package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.guava.GuavaCache;
import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.utils.TokenUtil;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.codec.DES;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.dao.repository.SysUserRepository;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.enums.UserStat;
import com.yishuifengxiao.tool.personalkit.domain.query.LoginQuery;
import com.yishuifengxiao.tool.personalkit.domain.request.ResetPwdReq;
import com.yishuifengxiao.tool.personalkit.domain.request.UpdatePwdReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.LoginVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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


    private final SecurityPropertyResource securityPropertyResource;

    public LoginVo login(HttpServletRequest request, HttpServletResponse response, LoginQuery query) throws CustomException {

        SysUser sysUser = sysUserDao.findActiveSysUser(query.getUsername().trim()).orElseThrow(() -> new UncheckedException("账号不存在"));
        Assert.isTrue("账号已过期", UserStat.ACCOUNT_EXPIRED.getCode() != sysUser.getStat());
        Assert.isTrue("密码已过期", UserStat.CREDENTIALS_EXPIRED.getCode() != sysUser.getStat());
        Assert.isTrue("账号已锁定", UserStat.ACCOUNT_LOCKED.getCode() != sysUser.getStat());
        Assert.isTrue("账号未启用", UserStat.ACCOUNT_ENABLE.getCode() == sysUser.getStat());

        Assert.isTrue("密码错误", StringUtils.equals(DES.encrypt(sysUser.getSalt(), query.getPassword()),
                sysUser.getPwd()));

        List<SysRole> roles = sysUserDao.findAllRoleByUserId(sysUser.getId());
        //判断权限
        Assert.isNotEmpty("暂无此权限", roles);
        //获取token
        SecurityToken token = TokenUtil.createUnsafe(request, query.getUsername().trim());

        String requestParameter = securityPropertyResource.security().getToken().getRequestParameter();
        if (StringUtils.isBlank(requestParameter)) {
            requestParameter = TokenConstant.TOKEN_REQUEST_PARAM;
        }
        request.getSession().setAttribute(requestParameter, token.getValue());


        return new LoginVo(sysUser.getId(), sysUser.getUsername(), sysUser.getNickname(), token.getValue(), roles, token);

    }

    public UserInfo userInfo(String id) {
        SysUser sysUser = sysUserRepository.findById(id).orElseThrow(() -> UncheckedException.of("记录不存在"));
        UserInfo userInfo = BeanUtil.copy(sysUser, new UserInfo());
        String sql = String.format("SELECT r.* from sys_user_role ur,sys_role r where ur.role_id=r.id and ur.user_id='%s'", id);
        return userInfo.setRoles(JdbcUtil.jdbcHelper().query(SysRole.class, sql).orElse(Collections.EMPTY_LIST));
    }

    public void updatePwd(UpdatePwdReq req) {
        SysUser sysUser = sysUserRepository.findById(req.getId().trim()).orElseThrow(() -> UncheckedException.of("记录不存在"));
        Assert.isTrue("旧密码不正确", StringUtils.equals(DES.encrypt(sysUser.getSalt(), req.getOldPwd().trim()),
                sysUser.getPwd()));
        sysUser.setPwd(DES.encrypt(sysUser.getSalt(), req.getNewPwd().trim()));
        sysUserRepository.saveAndFlush(sysUser);
        GuavaCache.remove(sysUser.getUsername());
    }

    public void updateUser(SysUser sysUser) {
        SysUser user = sysUserRepository.findById(sysUser.getId().trim()).orElseThrow(() -> UncheckedException.of("记录不存在"));
        if (StringUtils.isNotBlank(sysUser.getNickname())) {
            user.setNickname(sysUser.getNickname().trim());
        }
        if (StringUtils.isNotBlank(sysUser.getPhone())) {
            user.setPhone(sysUser.getPhone().trim());
        }
        if (StringUtils.isNotBlank(sysUser.getEmail())) {
            user.setEmail(sysUser.getEmail().trim());
        }
        if (StringUtils.isNotBlank(sysUser.getCertNo())) {
            user.setCertNo(sysUser.getCertNo().trim());
        }
        if (null != sysUser.getStat()) {
            UserStat.code(sysUser.getStat()).orElseThrow(() -> new UncheckedException("请选择一个正确的状态"));
            user.setStat(sysUser.getStat());
        }
        user.setLastUpdateTime(LocalDateTime.now());
        sysUserRepository.saveAndFlush(sysUser);
        GuavaCache.remove(sysUser.getUsername());
    }

    public void updateResetPwd(ResetPwdReq req) {
        SysUser sysUser = sysUserDao.findActiveSysUser(req.getUsername().trim()).orElseThrow(() -> new UsernameNotFoundException(String.format("用户名%s不存在", req.getUsername().trim())));
        Assert.isTrue("邮箱不匹配", StringUtils.equalsIgnoreCase(sysUser.getEmail(), req.getEmail()));
        sysUser.setPwd(DES.encrypt(sysUser.getSalt(), Constant.DEFAULT_PWD));
        sysUserRepository.saveAndFlush(sysUser);
        GuavaCache.remove(sysUser.getUsername());
    }


}
