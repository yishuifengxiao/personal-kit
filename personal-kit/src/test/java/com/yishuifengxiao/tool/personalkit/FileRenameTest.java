package com.yishuifengxiao.tool.personalkit;


import com.yishuifengxiao.common.tool.text.RegexUtil;

import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2024/3/15 21:21
 * @since 1.0.0
 */
public class FileRenameTest {
    static String text = "ttps://oss-audio.xiaobien.com/cloud_dick/0b1b6b592e9e42f5a10cc608ff77d5e9.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/3b925dda1f0d4512ab5f24db131757f0.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/ff6b70af69e14669aaf81913d7d6b29a.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/7a88e59e67ac4a588e63c93ebd65ab09.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/e00b95cb42d44ee99b1162def54255d5.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/08c06618e1ce4889a0db3f275c6d147d.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/fe1e22c9e37c4ee28e0af75aeb45bb89.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/1ac8e97da826450b8c80ba3e3fd91a91.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/7a142d23bbe44d58ac732fe7f385002d.mp3\n" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/91a59f8798f34eab9c1eaa96c33fd04a.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/fd0bad100d2b4e16adb5847acb2634af.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/054d23b08bc3412da887916ca33ecd93.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/0066cc58956648a1880c94d61ce0825c.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/e7f81b980ab0490c90f575d933f6d797.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/33e3947b31de49569fb9ee34ae322ae6.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/6852b02fa5f8497f81592edc2b059e35.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/3d328c0495314fb3a34e4ade4751d223.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/2353ab9b0875403f84d8e3c5414154d4.mp3\n" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/c97ca082f3fa49c48574e9c9b07f51a2.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/faf7f3a2997b49aa8d3ba6b1e45e4e77.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/dcf41f61b6b64ee9bc1a5255f3565a16.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/0cd8cdfb2bff4eb0831d1a2a60f96f04.mp3" +
            "ttps://oss-audio.xiaobien.com/cloud_dick/db240c38a09044148dd675470c4e808b.mp3\n";

    public static void main(String[] args) {
        List<String> urls = RegexUtil.extractAll("cloud_dick/[a-z0-9]+\\.mp3", text);
        urls.forEach(System.out::println);
    }
}
