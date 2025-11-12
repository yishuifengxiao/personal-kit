package com.yishuifengxiao.tool.personalkit.security;

import com.yishuifengxiao.common.jdbc.JdbcHelper;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.lang.HexUtil;
import com.yishuifengxiao.tool.personalkit.domain.entity.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class JdbcTokenHolder implements TokenHolder {

    @Autowired
    private JdbcHelper jdbcHelper;


    @Override
    public SecurityToken get(String username, String deviceId) {
        UserToken token = jdbcHelper.findOne(new UserToken().setUserName(username).setDeviceId(deviceId), false);
        if (null == token) {
            return null;
        }
        return BeanUtil.byteToObject(HexUtil.hexToBytes(token.getValue()), SecurityToken.class);
    }

    @Override
    public List<SecurityToken> getAll(String username) {
        List<UserToken> tokens = jdbcHelper.findAll(new UserToken().setUserName(username), false);
        return tokens.stream().map(token -> {
            return BeanUtil.byteToObject(HexUtil.hexToBytes(token.getValue()), SecurityToken.class);
        }).toList();
    }


    @Override
    public void save(SecurityToken token) throws CustomException {
        String tokenJson = HexUtil.bytesToHex(BeanUtil.objectToByte(token));
        UserToken userToken = new UserToken(null, token.getValue(), Objects.toString(token.getPrincipal()), token.getDeviceId(),
                tokenJson, token.getIssueAt(), token.getExpireAt());
        jdbcHelper.insert(userToken);
    }

    @Override
    public void remove(SecurityToken token) {
        List<UserToken> tokens = jdbcHelper.findAll(new UserToken().setToken(token.getValue()), false);
        if (tokens.isEmpty()) {
            return;
        }
        tokens.forEach(v -> jdbcHelper.deleteByPrimaryKey(UserToken.class, v.getId()));
    }

    @Override
    public SecurityToken loadByTokenValue(String tokenValue) {
        List<UserToken> tokens = jdbcHelper.findAll(new UserToken().setToken(tokenValue), false);
        if (tokens.isEmpty()) {
            return null;
        }
        return BeanUtil.byteToObject(HexUtil.hexToBytes(tokens.get(0).getValue()), SecurityToken.class);
    }
}
