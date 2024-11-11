package com.yishuifengxiao.tool.personalkit.security;

import com.yishuifengxiao.common.guava.GuavaCache;
import com.yishuifengxiao.common.security.user.CurrentUserDetails;
import com.yishuifengxiao.common.tool.codec.DES;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.enums.UserStat;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.AuthorityUtils;
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
        SysUser sysUser = GuavaCache.get(username, ()-> sysUserDao.findActiveSysUser(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("用户名%s不存在", username))));

        Assert.isFalse("账户已禁用，请稍候一段时候再重试",null!=sysUser.getLockTime()&&sysUser.getLockTime().isAfter(LocalDateTime.now()));

        List<SysRole> roles = sysUserDao.findAllRoleByUserId(sysUser.getId());

        String password=passwordEncoder.encode(DES.decrypt(sysUser.getSalt(),sysUser.getPwd()));

        return new CurrentUserDetails(username, password,
                UserStat.ACCOUNT_ENABLE.code()==sysUser.getStat(),
                UserStat.ACCOUNT_EXPIRED.code()!=sysUser.getStat(),
                UserStat.CREDENTIALS_EXPIRED.code()!=sysUser.getStat(),
                UserStat.ACCOUNT_LOCKED.code()!=sysUser.getStat(),
                AuthorityUtils.commaSeparatedStringToAuthorityList(
                        CollUtil.stream(roles).map(SysRole::getName)
                                .filter(StringUtils::isNotBlank)
                                .distinct().collect(Collectors.joining(","))))
                .setCurrentUser(sysUser);
    }
}
