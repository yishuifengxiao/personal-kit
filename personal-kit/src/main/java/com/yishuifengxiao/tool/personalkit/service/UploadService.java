package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.guava.EventPublisher;
import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFile;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFolder;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.entity.UploadRecord;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadMode;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadStat;
import com.yishuifengxiao.tool.personalkit.listener.event.FileAnalysisEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class UploadService {

    @Value("${file.upload.path:/tmp/upload}")
    private String uploadPath;
    @Autowired
    private EventPublisher eventPublisher;

    public String upload(HttpServletRequest request, SysUser sysUser, String folderName, UploadMode uploadMode, MultipartFile multipartFile, String traceId) {
        // 1）若文件夹folderName名字为空，则使用默认文件夹root,给当前用户创建一个名为root的文件夹记录DiskFolder。
        // 若文件夹名字不为空则查询当前用户名是否有名字为folderName的文件夹记录DiskFolder，
        // 若没有则创建一个名为folderName的文件夹记录DiskFolder，并将该文件夹的文件夹设置为root的文件夹记录DiskFolder的id。

        String userId = sysUser.getId();
        String finalFolderName = StringUtils.isBlank(folderName) ? "root" : folderName.trim();

        // 查找或创建root文件夹
        DiskFolder rootFolder = findOrCreateFolder(userId, "root", null);

        // 查找或创建目标文件夹
        DiskFolder targetFolder;
        if ("root".equals(finalFolderName)) {
            targetFolder = rootFolder;
        } else {
            targetFolder = findOrCreateFolder(userId, finalFolderName, rootFolder.getId());
        }

        // 2）接收文件，并保存到本地。生成一条上传记录UploadRecord。
        String uploadRecordId = IdWorker.snowflakeStringId();
        String diskFileId = IdWorker.snowflakeStringId();

        // 保存文件到本地
        File savedFile = saveFileToLocal(multipartFile, uploadRecordId);
        if (savedFile == null) {
            throw new RuntimeException("文件保存失败");
        }

        // 创建上传记录
        UploadRecord uploadRecord = new UploadRecord()
                .setId(uploadRecordId)
                .setFileName(multipartFile.getOriginalFilename())
                .setFolderName(finalFolderName)
                .setUserId(userId)
                .setDiskFileId(diskFileId)
                .setStat(UploadStat.UPLOAD_SUCCESS.getCode())
                .setUploadMode(uploadMode.getCode())
                .setCreateTime(LocalDateTime.now())
                .setFinishTime(LocalDateTime.now())
                .setMsg("上传成功");

        // 3）生成一条文件记录DiskFile，将次文件记录DiskFile关联到上传记录UploadRecord，
        // 同时将文件记录DiskFile关联到文件夹记录DiskFolder，将DiskFolder记录关联到上传记录UploadRecord
        DiskFile diskFile = new DiskFile()
                .setId(diskFileId)
                .setFileName(multipartFile.getOriginalFilename())
                .setOriginalFileName(multipartFile.getOriginalFilename())
                .setFolderId(targetFolder.getId())
                .setUserId(userId)
                .setUploadRecordId(uploadRecordId)
                .setLocalPath(savedFile.getAbsolutePath())
                .setSize(multipartFile.getSize())
                .setMode(uploadMode.getCode())
                .setCreateTime(LocalDateTime.now());

        // 获取文件后缀
        String originalFilename = multipartFile.getOriginalFilename();
        if (StringUtils.isNotBlank(originalFilename) && originalFilename.contains(".")) {
            diskFile.setSuffix(originalFilename.substring(originalFilename.lastIndexOf(".") + 1));
        }

        // 保存记录到数据库
        JdbcUtil.jdbcHelper().insert(uploadRecord);
        JdbcUtil.jdbcHelper().insert(diskFile);

        // 4）发送一个文件上传成功的事件，将上传记录UploadRecord发送给所有用户
        eventPublisher.post(new FileAnalysisEvent(diskFile, sysUser, uploadMode, uploadRecord));

        // 5) 返回上传记录的id
        return uploadRecordId;
    }

    /**
     * 查找或创建文件夹
     */
    private DiskFolder findOrCreateFolder(String userId, String folderName, String parentId) {
        // 查询是否已存在该文件夹
        String sql = "SELECT * FROM disk_folder WHERE user_id = :userId AND folder_name = :folderName";
        if (StringUtils.isNotBlank(parentId)) {
            sql += " AND parent_id = :parentId";
            List<DiskFolder> diskFolders = JdbcUtil.jdbcHelper().find(DiskFolder.class, sql, Map.of("userId", userId, "folderName", folderName, "parentId", parentId));
            if (CollUtil.isNotEmpty(diskFolders)) {
                return diskFolders.get(0);
            }
        } else {
            List<DiskFolder> diskFolders = JdbcUtil.jdbcHelper().find(DiskFolder.class, sql, Map.of("userId", userId, "folderName", folderName));
            if (CollUtil.isNotEmpty(diskFolders)) {
                return diskFolders.get(0);
            }
        }

        // 创建新文件夹
        DiskFolder newFolder = new DiskFolder()
                .setId(IdWorker.snowflakeStringId())
                .setFolderName(folderName)
                .setParentId(parentId)
                .setUserId(userId)
                .setCreateTime(LocalDateTime.now());

        JdbcUtil.jdbcHelper().insert(newFolder);
        return newFolder;
    }

    /**
     * 保存文件到本地
     */
    private File saveFileToLocal(MultipartFile multipartFile, String uploadRecordId) {
        try {
            // 创建上传目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成文件名
            String originalFilename = multipartFile.getOriginalFilename();
            String fileExtension = "";
            if (StringUtils.isNotBlank(originalFilename) && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = uploadRecordId + fileExtension;
            File destFile = new File(uploadDir, fileName);

            // 保存文件
            multipartFile.transferTo(destFile);

            // 返回相对路径
            return destFile;
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败", e);
        }
    }
}