package com.yishuifengxiao.tool.personalkit.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/8 23:16
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserRoleVo implements Serializable {
    private String id;
    private String name;


    private String description;

    private String homeUrl;
}
