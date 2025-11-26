package com.yishuifengxiao.tool.personalkit.listener.event;

import com.yishuifengxiao.tool.personalkit.domain.entity.sys.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateEvent implements Serializable {

    private SysUser user;
}
