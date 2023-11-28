package com.yishuifengxiao.tool.personalkit.domain.query;

import com.yishuifengxiao.tool.personalkit.domain.entity.SysRole;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/20 16:22
 * @since 1.0.0
 */
@Data
public class RoleQuery extends SysRole {

    @Column(name = "id")
    private String permissionId;

    @Column(name = "name")
    private String permissionName;

    private String url;

    @Column(name = "context_path")
    private String contextPath;

    @Column(name = "application_name")
    private String applicationName;
}
