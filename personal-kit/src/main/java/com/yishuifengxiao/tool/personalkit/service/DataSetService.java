package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.dao.mongo.DataSetDao;
import com.yishuifengxiao.tool.personalkit.dao.mongo.repository.DataSetRepository;
import com.yishuifengxiao.tool.personalkit.domain.mongo.DataSet;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.tool.ContextUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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


    public Page<DataSet> findPageDataSet(PageQuery<DataSet> param) {
        DataSet dataSet = param.query().orElse(new DataSet());
        dataSet.setCreateUserId(ContextUser.currentUserId());
        param.setQuery(dataSet);
        return dataSetDao.findPage(param);
    }

    public void save(DataSet param) {
        Assert.lteZero("已经存在相同名称的数据集", dataSetDao.countByNameAndUser(param.getName(), ContextUser.currentUserId()));
        param.setId(IdWorker.snowflakeStringId()).setCreateTime(LocalDateTime.now()).setCreateUserId(ContextUser.currentUserId());
        dataSetRepository.save(param);
    }

    public void update(DataSet param) {

        DataSet dataSet = dataSetRepository.findById(param.getId()).orElseThrow(() -> new UncheckedException("记录不存在"));
        if (!StringUtils.equals(param.getName(), dataSet.getName())) {
            Assert.lteZero("已经存在相同名称的数据集", dataSetDao.countByNameAndUser(param.getName(), dataSet.getCreateUserId()));
        }
        dataSet.setName(param.getName()).setDescription(param.getDescription())
                .setVirtuallyFileIds(param.getVirtuallyFileIds().stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
        dataSetRepository.save(param);
    }

    public void delete(IdReq req) {
        dataSetRepository.deleteById(req.getId());
    }
}
