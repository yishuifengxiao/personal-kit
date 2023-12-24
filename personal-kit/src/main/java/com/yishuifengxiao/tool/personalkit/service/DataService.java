package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.dao.MongoDao;
import com.yishuifengxiao.tool.personalkit.dao.SysUserDao;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFile;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskUploadRecord;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadMode;
import com.yishuifengxiao.tool.personalkit.domain.enums.UploadStat;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.vo.DiskUploadRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2023/12/2 21:39
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class DataService {

    @Autowired
    private MongoDao mongoDao;
    @Autowired
    private SysUserDao sysUserDao;


    public Page<DiskUploadRecordVo> findPageDataRecord(PageQuery<DiskUploadRecord> pageQuery) {
        DiskUploadRecord uploadRecord = pageQuery.query().orElse(new DiskUploadRecord());
        uploadRecord.setUploadMode(UploadMode.ANALYSIS.getCode());
        return JdbcUtil.jdbcHelper().findPage(uploadRecord, pageQuery.size().intValue(), pageQuery.num().intValue()).map(v -> {

            //上传记录关联的全部文件
            List<DiskFile> files = JdbcUtil.jdbcHelper().findAll(new DiskFile().setUploadId(v.getId()));
            List<VirtuallyFile> virtuallyFiles = DataUtil.stream(files).map(diskFile -> mongoDao.findVirtuallyFileByFileId(diskFile.getId())).filter(Objects::nonNull)
                    //
                    .flatMap(Collection::stream).filter(Objects::nonNull).distinct().collect(Collectors.toList());


            List<DiskUploadRecordVo.FileItem> items = virtuallyFiles.stream().map(virtuallyFile -> {
                Long uploadNum = mongoDao.findMaxRowIndexByVirtuallyFileId(virtuallyFile.getId());
                Long actualTotalNum = mongoDao.countByVirtuallyFileId(virtuallyFile.getId());

                return new DiskUploadRecordVo.FileItem(actualTotalNum, uploadNum, virtuallyFile.getFileId(),
                        //
                        files.stream().filter(diskFile -> diskFile.getId().equals(virtuallyFile.getFileId())).findFirst().map(DiskFile::getFileName).orElse(null),
                        virtuallyFile.getId(), virtuallyFile.getSheetName()
                );
            }).collect(Collectors.toList());

            //实际数据的数量
            Long actualTotalNum=   items.stream().mapToLong(DiskUploadRecordVo.FileItem::getActualTotalNum).sum();

            //全部数据数量
            Long uploadNum=   items.stream().mapToLong(DiskUploadRecordVo.FileItem::getUploadNum).sum();

            DiskUploadRecordVo vo = BeanUtil.copy(v, new DiskUploadRecordVo());
            vo.setActualTotalNum(actualTotalNum).setUploadNum(uploadNum).setFiles(items)
                    //
                    .setStatName(UploadStat.code(v.getStat()).orElse(UploadStat.UPLOAD_HANDING).getName())
                    //
                    .setUserName(sysUserDao.findUserNameById(v.getUserId()))
            //
            ;
            return vo;
        });
    }


}
