package com.yishuifengxiao.tool.personalkit.security;

import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.tool.personalkit.config.CoreProperties;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.enums.UserStat;
import com.yishuifengxiao.tool.personalkit.tool.CacheRateLimiter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

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

    private final CoreProperties coreProperties;


    private final CacheRateLimiter cacheRateLimiter;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // @formatter:off
        SysUser sysUser = sysUserDao.findActiveSysUser(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("用户名%s不存在", username)));

        if (cacheRateLimiter.get(sysUser.getId())){
            throw new UncheckedException(String.format("短时间内越权请求过多,请在%d秒后重新请求",coreProperties.getLimitTimeInSecond()));
        }

        List<SysRole> roles = sysUserDao.findAllRoleByUserId(sysUser.getId());

        return new User(username, sysUser.getPwd(),
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
