package com.yishuifengxiao.tool.personalkit.dao.repository;

import com.yishuifengxiao.tool.personalkit.domain.entity.SysMenuPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/6-16:58
 * @since 1.0.0
 */
public interface SysRelationRolePermissionRepository extends JpaRepository<SysMenuPermission, String>, JpaSpecificationExecutor<SysMenuPermission> {


}
