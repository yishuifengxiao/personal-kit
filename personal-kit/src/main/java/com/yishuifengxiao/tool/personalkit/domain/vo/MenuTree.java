package com.yishuifengxiao.tool.personalkit.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/9 16:36
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MenuTree implements Serializable {

    private String id;

    private String name;

    private String description;

    private String parentId;

    private Integer auth;

    private Integer idx;

    private String routerName;

    private String routerPath;

    private String icon;

    private List<MenuTree> childrens;
}
