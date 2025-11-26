package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.guava.EventPublisher;
import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFile;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFolder;
import com.yishuifengxiao.tool.personalkit.domain.entity.sys.SysUser;
import com.yishuifengxiao.tool.personalkit.domain.request.FileMoveReq;
import com.yishuifengxiao.tool.personalkit.domain.request.IdListReq;
import com.yishuifengxiao.tool.personalkit.support.UploadClient;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
