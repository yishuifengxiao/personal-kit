package com.yishuifengxiao.tool.personalkit.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2023/11/9-18:58
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class LoginVo implements Serializable {

    private String userId;

    private String username;

    private String nickName;

    private String token;

    private List<Role> roles;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Role implements Serializable {
        private String id;
        private String name;


        private String description;

        private String homeUrl;
    }

}
