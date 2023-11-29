package com.yishuifengxiao.tool.personalkit.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@ApiModel(description = "资源信息")
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceVo implements Serializable {
    /**
     * 当前文件夹的id
     */
    private String currentFolder;

    /**
     * 当前文件夹的父级文件夹id
     */
    private String parentFolder;

    private boolean root;

    /**
     * 当前文件夹的所在的全部路径
     */
    private List<Item> paths;

    /**
     * 当前文件夹下面所有的子文件夹
     */
    private List<Item> folders;

    /**
     * 当前文件夹下面所有的文件
     */
    private List<Item> files;

    @ApiModel(description = "资源信息-资源项")
    @Accessors(chain = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item implements Serializable {

        /**
         * 资源项id
         */
        private String id;
        /**
         * 资源项名字
         */
        private String name;
    }
}
