package com.yishuifengxiao.tool.personalkit;

import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.vo.CurrentUser;

import java.time.LocalDateTime;

public class BeanDemo {

    public static void main(String[] args) {
        SysUser u =
                new SysUser().setId(IdWorker.snowflakeStringId()).setCreateTime(LocalDateTime.now());
        CurrentUser currentUser = BeanUtil.copy(u, new CurrentUser());
        System.out.println(currentUser);
    }
}
