package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.TokenUtil;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.entity.BoolStat;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.tool.lang.BoolUtil;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.dao.repository.SysUserRepository;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.enums.UserStat;
import com.yishuifengxiao.tool.personalkit.domain.query.LoginQuery;
import com.yishuifengxiao.tool.personalkit.domain.vo.LoginVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.UserInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/9-18:50
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final SysUserDao sysUserDao;

    private final SysUserRepository sysUserRepository;

    private final PasswordEncoder passwordEncoder;

    public LoginVo login(HttpServletRequest request, LoginQuery query) throws CustomException {

        SysUser sysUser = sysUserDao.findActiveSysUser(query.getUsername().trim()).orElseThrow(() -> new UncheckedException(String.format("用户名%s不存在", query.getUsername())));
        Assert.isTrue("账号已过期", UserStat.ACCOUNT_EXPIRED.getCode() != sysUser.getStat());
        Assert.isTrue("密码已过期", UserStat.CREDENTIALS_EXPIRED.getCode() != sysUser.getStat());
        Assert.isTrue("账号已锁定", UserStat.ACCOUNT_LOCKED.getCode() != sysUser.getStat());
        Assert.isTrue("账号未启用", UserStat.ACCOUNT_ENABLE.getCode() == sysUser.getStat());
        Assert.isTrue("密码错误", passwordEncoder.matches(query.getPassword(), sysUser.getPwd()));

        List<SysRole> roles = sysUserDao.findAllRoleByUserId(sysUser.getId());
        //判断权限
        Assert.isNotEmpty("暂无此权限", roles);
        //获取token
        SecurityToken token = TokenUtil.createUnsafe(request, query.getUsername().trim());

        return new LoginVo(sysUser.getId(), sysUser.getUsername(), sysUser.getNickname(), token.getValue(), roles.stream()
                .map(v -> new LoginVo.Role(v.getId(), v.getName(), v.getDescription(), v.getHomeUrl())).collect(Collectors.toList()));

    }

    public UserInfo userInfo(String id) {
        SysUser sysUser = sysUserRepository.findById(id).orElseThrow(() -> UncheckedException.of("记录不存在"));
        UserInfo userInfo = BeanUtil.copy(sysUser, new UserInfo());
        String sql = String.format("SELECT r.* from sys_relation_user_role ur,sys_role r where ur.role_id=r.id and ur.user_id='%s'", id);
        return userInfo.setRoles(JdbcUtil.jdbcHelper().query(SysRole.class, sql).orElse(Collections.EMPTY_LIST));
    }
}
