package com.yishuifengxiao.tool.personalkit;

import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.vo.UserInfo;

import java.time.LocalDateTime;

public class BeanDemo {

    public static void main(String[] args) {
        SysUser u =
                new SysUser().setId(IdWorker.snowflakeStringId()).setCreateTime(LocalDateTime.now());
        UserInfo userInfo = BeanUtil.copy(u, new UserInfo());
        System.out.println(userInfo);
    }
}
