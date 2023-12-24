package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
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

            DiskFile file = JdbcUtil.jdbcHelper().findOne(new DiskFile().setUploadId(v.getId()));
            if (null == file) {
                return null;
            }
            VirtuallyFile virtuallyFile = mongoDao.findVirtuallyFileByFileId(file.getId());
            if (null == virtuallyFile) {
                return null;
            }
            Long maxRowIndexByVirtuallyFileId = mongoDao.findMaxRowIndexByVirtuallyFileId(virtuallyFile.getId());
            Long count = mongoDao.countByVirtuallyFileId(virtuallyFile.getId());

            DiskUploadRecordVo vo = BeanUtil.copy(v, new DiskUploadRecordVo());
            vo.setActualTotalNum(count).setUploadNum(maxRowIndexByVirtuallyFileId)
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
