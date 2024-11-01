package com.yishuifengxiao.tool.personalkit.utils;

import org.springframework.beans.BeanUtils;

public class BeanUtil {

    public static <T> T copy(Object src, T dist) {
        if (null == src || null == dist) {
            return dist;
        }
        BeanUtils.copyProperties(src, dist);

        return dist;
    }
}
