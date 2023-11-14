package com.yishuifengxiao.tool.personalkit;

import com.yishuifengxiao.tool.personalkit.dao.repository.SysUserRepository;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import org.springframework.data.domain.Example;

import java.util.function.Function;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/6-17:00
 * @since 1.0.0
 */
public class SysUserRepositoryTest {

    public static void main(String[] args) {

        Function<SysUser, String> getUsername1 = SysUser::getUsername;

        Function<SysUser, String> getUsername2 = SysUser::getUsername;
        System.out.println(getUsername1);
        System.out.println(getUsername2);
        System.out.println(getUsername2.getClass().isAssignableFrom(getUsername1.getClass()));
        System.out.println(getUsername2 == getUsername1);
    }
}
