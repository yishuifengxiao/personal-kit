package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.dao.mongo.DataSetDao;
import com.yishuifengxiao.tool.personalkit.dao.mongo.GraphDefineDao;
import com.yishuifengxiao.tool.personalkit.dao.mongo.repository.DataSetRepository;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFile;
import com.yishuifengxiao.tool.personalkit.domain.mongo.DataSet;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.DataSetDetail;
import com.yishuifengxiao.tool.personalkit.domain.vo.DataSetVo;
import com.yishuifengxiao.tool.personalkit.support.ContextUser;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 18:11
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class DataSetService {

    @Autowired
    private DataSetDao dataSetDao;
    @Autowired
    private DataSetRepository dataSetRepository;
    @Autowired
    private GraphDefineDao graphDefineDao;


    public Page<DataSetVo> findPageDataSet(PageQuery<DataSet> param) {
        DataSet dataSet = param.query().orElse(new DataSet());
        dataSet.setCreateUserId(ContextUser.currentUserId());
        param.setQuery(dataSet);
        return dataSetDao.findPage(param).map(v -> {

            DataSetVo vo = BeanUtil.copy(v, new DataSetVo());

            return vo;
        });
    }

    public void save(DataSet param) {
        Assert.lteZero("已经存在相同名称的数据集", dataSetDao.countByNameAndUser(param.getName(), ContextUser.currentUserId()));
        param.setId(IdWorker.snowflakeStringId())
                .setCreateTime(LocalDateTime.now())
                .setCreateUserId(ContextUser.currentUserId());
        dataSetRepository.save(param);
    }

    public void update(DataSet param) {

        DataSet dataSet = dataSetRepository.findById(param.getId()).orElseThrow(() -> new UncheckedException("记录不存在"));
        if (!StringUtils.equals(param.getName(), dataSet.getName())) {
            Assert.lteZero("已经存在相同名称的数据集", dataSetDao.countByNameAndUser(param.getName(), dataSet.getCreateUserId()));
        }
        Assert.lteZeroN("该数据集已被使用，不能更新", graphDefineDao.countByDatasetId(dataSet.getId()));

        dataSet.setName(param.getName()).setDescription(param.getDescription())
                .setDiskFiles(param.getDiskFiles().stream()
                        .filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
        dataSetRepository.save(param);
    }

    public void delete(IdReq req) {
        Assert.lteZeroN("该数据集已被使用，不能删除", graphDefineDao.countByDatasetId(req.getId()));
        dataSetRepository.deleteById(req.getId());
    }

    public DataSetDetail detail(IdReq param) {
        DataSet dataSet = dataSetRepository.findById(param.getId()).orElseThrow(() -> UncheckedException.of("记录不存在"));
        DataSetDetail detail = BeanUtil.copy(dataSet, new DataSetDetail());

        List<DataSetDetail.Item> items = dataSet.getDiskFiles().stream().map(s -> {
            DiskFile diskFile = JdbcUtil.jdbcHelper().findByPrimaryKey(DiskFile.class, s);
            if (null == diskFile) {
                return null;
            }
            return new DataSetDetail.Item(diskFile.getId(), diskFile.getFileName());
        }).filter(Objects::nonNull).collect(Collectors.toList());

        detail.setSources(items);
        return detail;
    }
}
