package com.yishuifengxiao.tool.personalkit.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yishuifengxiao.common.tool.entity.StringKeyValue;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/13-12:45
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageUser extends SysUser {

    private List<StringKeyValue> roles;

    private String statName;

    private String roleName;


}
