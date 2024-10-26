package com.yishuifengxiao.tool.personalkit;

import com.yishuifengxiao.common.tool.codec.DES;
import com.yishuifengxiao.common.tool.random.IdWorker;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/23 11:31
 * @since 1.0.0
 */
public class DemoMain {

    public static void main(String[] args) {
        System.out.println(IdWorker.snowflakeStringId().length());
        String des = DES.encrypt(IdWorker.snowflakeStringId(), "01234567890123456789");
        System.out.println(des);
        System.out.println(des.length());
    }
}
