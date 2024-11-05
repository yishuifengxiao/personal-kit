package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.guava.EventPublisher;
import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.common.tool.io.IoUtil;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.common.tool.utils.OsUtils;
import com.yishuifengxiao.tool.personalkit.domain.bo.FileAnalysisEvent;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFile;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFolder;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskUploadRecord;
import com.yishuifengxiao.tool.personalkit.domain.entity.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadMode;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadStat;
import com.yishuifengxiao.tool.personalkit.domain.request.FileMoveReq;
import com.yishuifengxiao.tool.personalkit.domain.request.IdListReq;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import com.yishuifengxiao.tool.personalkit.support.UploadClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.yishuifengxiao.tool.personalkit.domain.constant.Constant.DEFAULT_FOLDER_NAME;
import static com.yishuifengxiao.tool.personalkit.domain.constant.Constant.DEFAULT_PARENT_ROOT_ID;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/11/28 23:13
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class FileService {

    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private UploadClient uploadClient;

    public void uploads(HttpServletRequest request, SysUser sysUser, String folder, UploadMode uploadMode, MultipartFile[] files) {
        CollUtil.stream(files).forEach(v -> upload(request, sysUser, folder, uploadMode, v, v.getName()));
    }

    public String upload(HttpServletRequest request, SysUser sysUser, String folder, UploadMode uploadMode, MultipartFile multipartFile, String traceId) {
        uploadMode = null == uploadMode ? UploadMode.ANALYSIS : uploadMode;
        File root = new File(new File(OsUtils.currentWorkPath(), "tmp"), IdWorker.snowflakeStringId());
        if (!root.exists()) {
            root.mkdirs();
        }
        DiskFolder diskFolder = diskFolder(folder);
        File file = new File(root, multipartFile.getOriginalFilename());
        DiskUploadRecord uploadRecord = null;
        try {
            IoUtil.inputStream2File(multipartFile.getInputStream(), file);
            uploadRecord = new DiskUploadRecord(IdWorker.snowflakeStringId(), multipartFile.getOriginalFilename(), sysUser.getId(), UploadStat.UPLOAD_HANDING.getCode(),
                    uploadMode.getCode(),
                    null, LocalDateTime.now(), null);
            JdbcUtil.jdbcHelper().saveOrUpdate(uploadRecord);

            eventPublisher.post(new FileAnalysisEvent(diskFolder, sysUser, file.getAbsolutePath(), uploadMode, uploadRecord));
        } catch (IOException e) {
            if (null != file) {
                file.delete();
                file = null;
            }
            throw new RuntimeException(e);
        }

        return null == uploadRecord ? null : uploadRecord.getId();
    }

    public DiskFolder diskFolder(String folder) {
        if (StringUtils.isBlank(folder)) {
            DiskFolder diskFolder = new DiskFolder().setId(IdWorker.snowflakeStringId()).setFolderName(DEFAULT_FOLDER_NAME)
                    //
                    .setParentId(DEFAULT_PARENT_ROOT_ID).setUserId(ContextCache.currentUserId()).setCreateTime(LocalDateTime.now());
            //不存在则新增
            JdbcUtil.jdbcHelper().saveOrUpdate(diskFolder);


            return diskFolder;
        }
        DiskFolder diskFolder = JdbcUtil.jdbcHelper().findByPrimaryKey(DiskFolder.class, folder.trim());
        Assert.isNotNull("路径不存在", diskFolder);
        return diskFolder;
    }

    public void delete(IdListReq req) {

        for (String id : req.getIds()) {
            if (StringUtils.isBlank(id)) {
                continue;
            }
            DiskFile diskFile = JdbcUtil.jdbcHelper().findByPrimaryKey(DiskFile.class, id.trim());
            Assert.isNotNull("记录不存在", diskFile);
            int deleted = JdbcUtil.jdbcHelper().deleteByPrimaryKey(DiskFile.class, id.trim());
            Assert.gtZero("删除失败", deleted);
        }
    }

    public String share(String id) {
        DiskFile diskFile = JdbcUtil.jdbcHelper().findByPrimaryKey(DiskFile.class, id.trim());
        Assert.isNotNull("请选择一个正确的数据", diskFile);

        SysUser user = JdbcUtil.jdbcHelper().findByPrimaryKey(SysUser.class, diskFile.getUserId().trim());
        return uploadClient.share(user.getUsername(), diskFile.getOssId());
    }


    public void move(FileMoveReq req) {
        DiskFile diskFile = JdbcUtil.jdbcHelper().findByPrimaryKey(DiskFile.class, req.getId().trim());
        Assert.isNotNull("请选择一个正确的数据", diskFile);
        DiskFolder folder = JdbcUtil.jdbcHelper().findByPrimaryKey(DiskFolder.class, req.getFolder().trim());
        Assert.isNotNull("记录不存在", folder);
        Assert.isTrue("请选择一个正确的文件夹", StringUtils.equalsIgnoreCase(folder.getUserId(), folder.getUserId()));
        diskFile.setFolderId(folder.getId());
        JdbcUtil.jdbcHelper().updateByPrimaryKeySelective(diskFile);
    }
}
