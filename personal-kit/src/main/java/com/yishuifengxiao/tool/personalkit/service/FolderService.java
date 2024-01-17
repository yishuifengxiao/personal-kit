package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFile;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFolder;
import com.yishuifengxiao.tool.personalkit.domain.request.FolderCreateReq;
import com.yishuifengxiao.tool.personalkit.domain.request.FolderNameReq;
import com.yishuifengxiao.tool.personalkit.domain.request.FolderParentReq;
import com.yishuifengxiao.tool.personalkit.domain.request.IdListReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.ResourceVo;
import com.yishuifengxiao.tool.personalkit.support.ContextUser;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
public class FolderService {
    public void createFolder(FolderCreateReq req) {


        Long counted = JdbcUtil.jdbcHelper().countAll(new DiskFolder().setUserId(ContextUser.currentUserId()).setFolderName(req.getName().trim()).setParentId(req.getParent()));
        Assert.lteZeroN("已经存在相同名称的文件夹", counted);

        DiskFolder diskFolder = new DiskFolder().setUserId(ContextUser.currentUserId()).setFolderName(req.getName().trim()).setParentId(req.getParent()).setId(IdWorker.snowflakeStringId()).setCreateTime(LocalDateTime.now());
        JdbcUtil.jdbcHelper().insertSelective(diskFolder);
    }

    public void updateFolderName(FolderNameReq req) {

        DiskFolder folder = JdbcUtil.jdbcHelper().findByPrimaryKey(DiskFolder.class, req.getId());
        Assert.isNotNull("记录不存在", folder);
        Assert.isFalse("顶级文件夹不允许操作", StringUtils.equals(DEFAULT_PARENT_ROOT_ID, folder.getParentId()));
        if (!StringUtils.equals(req.getName().trim(), folder.getFolderName())) {
            Long counted = JdbcUtil.jdbcHelper().countAll(new DiskFolder().setUserId(ContextUser.currentUserId()).setFolderName(req.getName().trim()).setParentId(folder.getParentId()));
            Assert.lteZeroN("已经存在相同名称的文件夹", counted);
        }
        folder.setFolderName(req.getName().trim());
        JdbcUtil.jdbcHelper().updateByPrimaryKeySelective(folder);
    }

    public void updateFolderParent(FolderParentReq req) {

        DiskFolder folder = JdbcUtil.jdbcHelper().findByPrimaryKey(DiskFolder.class, req.getId());
        Assert.isNotNull("记录不存在", folder);
        Assert.isFalse("顶级文件夹不允许操作", StringUtils.equals(DEFAULT_PARENT_ROOT_ID, folder.getParentId()));
        if (!StringUtils.equals(req.getParent().trim(), folder.getParentId())) {
            Long counted = JdbcUtil.jdbcHelper().countAll(new DiskFolder().setUserId(ContextUser.currentUserId()).setParentId(req.getParent()));
            Assert.lteZeroN("已经存在相同名称的文件夹", counted);
        }
        DiskFolder parentFolder = JdbcUtil.jdbcHelper().findByPrimaryKey(DiskFolder.class, req.getParent());
        Assert.isNotNull("目标文件不存在", parentFolder);
        Assert.isTrue("请选择一个正确的文件夹", StringUtils.equalsIgnoreCase(folder.getUserId(), parentFolder.getUserId()));
        folder.setParentId(req.getParent());
        JdbcUtil.jdbcHelper().updateByPrimaryKeySelective(folder);
    }

    public void deleteById(IdListReq req) {

        for (String id : req.getIds()) {
            if (StringUtils.isBlank(id)) {
                continue;
            }
            DiskFolder folder = JdbcUtil.jdbcHelper().findByPrimaryKey(DiskFolder.class, id.trim());
            Assert.isNotNull("记录不存在", folder);
            JdbcUtil.jdbcHelper().deleteByPrimaryKey(DiskFolder.class, id.trim());
            JdbcUtil.jdbcHelper().delete(new DiskFile().setFolderId(id.trim()));
        }

    }

    public ResourceVo list(String folderId) {
        DiskFolder folder = null;
        if (StringUtils.isBlank(folderId)) {
            folder = JdbcUtil.jdbcHelper().findOne(new DiskFolder().setId(ContextUser.currentUserId()).setParentId(DEFAULT_PARENT_ROOT_ID));
            if (null == folder) {
                folder = new DiskFolder().setId(IdWorker.snowflakeStringId()).setFolderName(DEFAULT_FOLDER_NAME).setParentId(DEFAULT_PARENT_ROOT_ID).setUserId(ContextUser.currentUserId()).setCreateTime(LocalDateTime.now());

                JdbcUtil.jdbcHelper().insertSelective(folder);
            }
        } else {
            folder = JdbcUtil.jdbcHelper().findByPrimaryKey(DiskFolder.class, folderId.trim());
            Assert.isNotNull("记录不存在", folder);
        }
        List<DiskFolder> list = new ArrayList<>();
        findParent(list, folder);

        List<ResourceVo.Item> folders = DataUtil.stream(JdbcUtil.jdbcHelper().findAll(new DiskFolder().setParentId(folder.getId()))).map(v -> new ResourceVo.Item(v.getId(), v.getFolderName())).collect(Collectors.toList());
        List<ResourceVo.Item> files = DataUtil.stream(JdbcUtil.jdbcHelper().findAll(new DiskFile().setFolderId(folder.getId()))).map(v -> new ResourceVo.Item(v.getId(), v.getFileName())).collect(Collectors.toList());


        return new ResourceVo(folder.getId(), folder.getParentId(), DEFAULT_PARENT_ROOT_ID.equals(folder.getParentId()), list.stream().map(v -> new ResourceVo.Item(v.getId(), v.getFolderName())).collect(Collectors.toList()), folders, files);
    }

    private void findParent(List<DiskFolder> list, DiskFolder folder) {
        if (DEFAULT_PARENT_ROOT_ID.equals(folder.getParentId())) {
            list.add(folder);
            return;
        }
        folder = JdbcUtil.jdbcHelper().findByPrimaryKey(DiskFolder.class, folder.getParentId());
        findParent(list, folder);
    }

}
