package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.dao.mongo.DataSourceDao;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFile;
import com.yishuifengxiao.tool.personalkit.domain.entity.UploadRecord;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadMode;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyRow;
import com.yishuifengxiao.tool.personalkit.domain.vo.UploadRecordVo;
import com.yishuifengxiao.tool.personalkit.domain.vo.VirtuallyFileVo;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 21:39
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class DataCenterService {

    @Autowired
    private DataSourceDao dataSourceDao;
    @Autowired
    private SysUserDao sysUserDao;


    /**
     * 查询出所有的文件上传记录
     *
     * @param pageQuery
     * @return
     */
    public Page<UploadRecordVo> findPageDataRecord(PageQuery<UploadRecord> pageQuery) {
        UploadRecord uploadRecord = pageQuery.query().orElse(new UploadRecord());
        uploadRecord.setUploadMode(UploadMode.ANALYSIS.getCode());
//        return JdbcUtil.jdbcHelper().findPage(pageQuery, false,
//                Order.desc("create_time")).map(v -> {
//
//            //上传记录关联的全部文件
//            List<DiskFile> files = JdbcUtil.jdbcHelper().findAll(new DiskFile().setUploadId(v.getId()), false);
//            List<VirtuallyFile> virtuallyFiles =
//                    CollUtil.stream(files).map(diskFile -> dataSourceDao.findVirtuallyFileByFileId(diskFile.getId())).filter(Objects::nonNull)
//                            //
//                            .flatMap(Collection::stream).filter(Objects::nonNull).distinct().collect(Collectors.toList());
//
//
//            List<UploadRecordVo.FileItem> items = virtuallyFiles.stream().map(virtuallyFile -> {
//                Long uploadNum = dataSourceDao.findMaxRowIndexByVirtuallyFileId(virtuallyFile.getId());
//                Long actualTotalNum = dataSourceDao.countByVirtuallyFileId(virtuallyFile.getId());
//
//                return new UploadRecordVo.FileItem(actualTotalNum, uploadNum, virtuallyFile.getFileId(),
//                        //
//                        files.stream().filter(diskFile -> diskFile.getId().equals(virtuallyFile.getFileId())).findFirst().map(DiskFile::getFileName).orElse(null),
//                        virtuallyFile.getId(), virtuallyFile.getSheetName()
//                );
//            }).collect(Collectors.toList());
//
//            //实际数据的数量
//            Long actualTotalNum =
//                    items.stream().filter(Objects::nonNull).mapToLong(s -> Optional.ofNullable(s.getActualTotalNum()).orElse(0L)).filter(Objects::nonNull).sum();
//
//            //全部数据数量
//            Long uploadNum =
//                    items.stream().filter(Objects::nonNull).mapToLong(s -> Optional.ofNullable(s.getUploadNum()).orElse(0L)).filter(Objects::nonNull).sum();
//
//            UploadRecordVo vo = BeanUtil.copy(v, new UploadRecordVo());
//            vo.setActualTotalNum(actualTotalNum).setUploadNum(uploadNum).setFiles(items)
//                    //
//                    .setStatName(UploadStat.code(v.getStat()).orElse(UploadStat.UPLOAD_HANDING).getName())
//                    //
//                    .setUserName(sysUserDao.findUserNameById(v.getUserId()))
//            //
//            ;
//            return vo;
//        });
        return null;
    }

    /**
     * 查询虚拟文件定义
     *
     * @param virtuallyFileId
     * @return
     */
    public List<VirtuallyFile.VirtuallyHeader> findVirtuallyFileDefine(String virtuallyFileId) {
        VirtuallyFile virtuallyFile = dataSourceDao.findVirtuallyFileById(virtuallyFileId);
        return null == virtuallyFile || null == virtuallyFile.getHeaders() ? Collections.EMPTY_LIST :
                virtuallyFile.getHeaders();
    }

    public Page<VirtuallyRow> findPageVirtuallyRow(PageQuery<VirtuallyRow> pageQuery) {
        return dataSourceDao.findPageVirtuallyRow(pageQuery);
    }


    public VirtuallyFileVo findPageVirtuallyData(PageQuery<VirtuallyRow> pageQuery) {
        String virtuallyFileId =
                pageQuery.query().map(VirtuallyRow::getVirtuallyFileId).orElseThrow(() -> new UncheckedException(
                        "请选择一个文件"));

        return new VirtuallyFileVo(findVirtuallyFileDefine(virtuallyFileId), findPageVirtuallyRow(pageQuery));
    }

    public Page<DiskFile> findPageDiskFile(PageQuery<DiskFile> pageQuery) {
        DiskFile diskFile = pageQuery.query().orElse(new DiskFile());
        diskFile.setUserId(ContextCache.currentUserId());
        return JdbcUtil.jdbcHelper().findPage(diskFile, false, pageQuery, Order.desc("create_time"));
    }
}
