package com.yishuifengxiao.tool.personalkit.tool;

import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.tool.io.IoUtil;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.tool.personalkit.config.MinioProperties;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/30 23:02
 * @see https://min.io/docs/minio/linux/developers/java/minio-java.html#
 * @since 1.0.0
 */
@Component
@Slf4j
public class UploadClient {


    @Autowired
    private MinioProperties minioProperties;


    public String upload(SysUser sysUser, File file) {
        // https://www.bookstack.cn/read/MinioCookbookZH/21.md
        try {
            // 使用Minio服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = MinioClient.builder().endpoint(minioProperties.getUrl()).credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey()).build();
            String bucket = "bucket" + sysUser.getId() + sysUser.getUsername();
            // 检查存储桶是否已经存在
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
            // 使用putObject上传一个文件到存储桶中。
            String objectName = IdWorker.snowflakeStringId() + "." + IoUtil.suffix(file);
            ObjectWriteResponse writeResponse = minioClient.uploadObject(UploadObjectArgs.builder().bucket(bucket).object(objectName).filename(file.getAbsolutePath()).build());
            return objectName;
        } catch (Exception e) {
            log.warn("---------> 用户 {} 上传文件 {} 失败 ，失败的原因为 {}", sysUser.getUsername(), file.getAbsolutePath(), e);
            throw new UncheckedException("文件保存失败");
        }
    }

    public String share(String bucket, String objectName) {
        try {
            // 使用Minio服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = MinioClient.builder().endpoint(minioProperties.getUrl()).credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey()).build();
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder().bucket(bucket).object(objectName).build();
            return minioClient.getPresignedObjectUrl(args);
        } catch (Exception e) {
            log.warn("---------> 获取 bucket ={} ,objectName ={} getPresignedObjectUrl 的 失败 ，失败的原因为 {}", bucket,
                    objectName, e);
            throw new UncheckedException("文件url获取失败");
        }
    }
}
