package com.yishuifengxiao.tool.personalkit.security;

import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.encoder.DES;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.enums.UserStat;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/6-16:02
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class SimpleUserDetailsService implements UserDetailsService {

    private final SysUserDao sysUserDao;

    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // @formatter:off
        SysUser sysUser = sysUserDao.findActiveSysUser(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("用户名%s不存在", username)));

        Assert.isFalse("账户已禁用，请稍候一段时候再重试",null!=sysUser.getLockTime()&&sysUser.getLockTime().isAfter(LocalDateTime.now()));

        List<SysRole> roles = sysUserDao.findAllRoleByUserId(sysUser.getId());

        String password=passwordEncoder.encode(DES.decrypt(sysUser.getSalt(),sysUser.getPwd()));

        return new User(username, password,
                UserStat.ACCOUNT_ENABLE.getCode()==sysUser.getStat(),
                UserStat.ACCOUNT_EXPIRED.getCode()!=sysUser.getStat(),
                UserStat.CREDENTIALS_EXPIRED.getCode()!=sysUser.getStat(),
                UserStat.ACCOUNT_LOCKED.getCode()!=sysUser.getStat(),
                AuthorityUtils.commaSeparatedStringToAuthorityList(
                        DataUtil.stream(roles).map(SysRole::getName)
                                .filter(StringUtils::isNotBlank)
                                .distinct().collect(Collectors.joining(","))));
    }
}
