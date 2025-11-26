package com.yishuifengxiao.tool.personalkit.listener;

import com.google.common.eventbus.Subscribe;
import com.yishuifengxiao.common.guava.EventPublisher;
import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.tool.personalkit.domain.constant.Constant;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFolder;
import com.yishuifengxiao.tool.personalkit.domain.entity.sys.SysUser;
import com.yishuifengxiao.tool.personalkit.listener.event.UserCreateEvent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserCreateEventListener {
    @Autowired
    private EventPublisher eventPublisher;

    @Subscribe
    public void onUserCreateEvent(UserCreateEvent event) {
        SysUser sysUser = event.getUser();
        DiskFolder folder = new DiskFolder(sysUser.getId(), sysUser.getUsername(),
                Constant.DEFAULT_ROOT_ID,
                sysUser.getId(), LocalDateTime.now());
        JdbcUtil.jdbcHelper().saveOrUpdate(folder);
    }

    @PostConstruct
    public void init() {
        eventPublisher.eventBus().register(this);
    }

}
