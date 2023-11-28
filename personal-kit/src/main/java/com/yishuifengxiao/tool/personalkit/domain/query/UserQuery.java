package com.yishuifengxiao.tool.personalkit.domain.query;

import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/26 21:52
 * @since 1.0.0
 */
@Data
public class UserQuery extends SysUser implements Serializable {

    @Column(name = "name")
    private String roleName;


    @Column(name = "id")
    private String roleId;
}
