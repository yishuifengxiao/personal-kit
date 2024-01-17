package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.tool.lang.NumberUtil;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.dao.mongo.GraphDefineDao;
import com.yishuifengxiao.tool.personalkit.dao.mongo.repository.*;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFile;
import com.yishuifengxiao.tool.personalkit.domain.mongo.DataSet;
import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphBuildRecord;
import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphDefine;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.support.ContextUser;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/2 19:33
 * @since 1.0.0
 */
@Component
@Transactional(rollbackOn = {Exception.class})
public class GraphService {


    @Autowired
    private GraphDefineRepository graphDefineRepository;
    @Autowired
    private GraphDefineDao graphDefineDao;
    @Autowired
    private GraphBuildRecordRepository graphBuildRecordRepository;
    @Autowired
    private OntologyRepository ontologyRepository;
    @Autowired
    private DataSetRepository dataSetRepository;
    @Autowired
    private VirtuallyFileRepository virtuallyFileRepository;

    public Page<GraphDefine> findPage(PageQuery<GraphDefine> param) {
        GraphDefine dataSet = param.query().orElse(new GraphDefine());
        dataSet.setCreateUserId(ContextUser.currentUserId());
        param.setQuery(dataSet);
        return graphDefineDao.findPage(param);
    }

    public void save(GraphDefine param) {
        Assert.lteZero("已经存在相同名称的图谱", graphDefineRepository.countAllByGraphNameAndCreateUserId(param.getGraphName(),
                ContextUser.currentUserId()));
        param.setCreateUserId(ContextUser.currentUserId()).setCreateTime(LocalDateTime.now()).setVersion(NumberUtil.ZERO.intValue()).setMaster(true);
        graphDefineRepository.save(param);
    }

    public void update(GraphDefine param) {

        GraphDefine graphDefine = validate(param.getId());
        if (!StringUtils.equalsIgnoreCase(graphDefine.getGraphName(), param.getGraphName())) {
            Assert.lteZero("已经存在相同名称的图谱",
                    graphDefineRepository.countAllByGraphNameAndCreateUserId(param.getGraphName(),
                            graphDefine.getId()));
        }
        graphDefine.setGraphName(param.getGraphName()).setDescription(param.getDescription()).setOntologyId(param.getOntologyId());
        graphDefineRepository.save(graphDefine);
    }

    public void delete(IdReq param) {
        GraphDefine graphDefine = validate(param.getId());
        Assert.lteZero("该图谱已存在构建记录，不能删除", graphBuildRecordRepository.countByGraphId(param.getId()));
        graphDefineRepository.deleteById(param.getId());
    }

    public void updateDataSource(GraphDefine.DataSource dataSource) {
        GraphDefine graphDefine = validate(dataSource.getId());
        if (StringUtils.isNotBlank(dataSource.getDataSetId())) {
            DataSet dataSet =
                    dataSetRepository.findById(dataSource.getDataSetId()).orElseThrow(() -> UncheckedException.of(
                            "数据集不存在"));
            if (null != dataSource.getSourceItems()) {
                DataUtil.stream(dataSource.getSourceItems()).map(v -> {
                    DiskFile diskFile = JdbcUtil.jdbcHelper().findByPrimaryKey(DiskFile.class, v.getDiskFileId());
                    Assert.isNotNull("记录文件不存在", diskFile);
                    VirtuallyFile virtuallyFile =
                            virtuallyFileRepository.findById(v.getVirtuallyFileId()).orElseThrow(() -> UncheckedException.of(
                                    "数据源文件不存在"));
                    return null;
                });
            }
        } else {
            dataSource = new GraphDefine.DataSource().setSourceItems(Collections.emptyList());
        }

        graphDefine.setDataSource(dataSource);
        graphDefineRepository.save(graphDefine);
    }

    private GraphDefine validate(String id) {
        GraphDefine graphDefine =
                graphDefineRepository.findById(id).orElseThrow(() -> new UncheckedException("记录不存在"));
        Assert.lteZero("该图谱已存在构建记录，不能更新", graphBuildRecordRepository.countByGraphIdAndBuildstat(graphDefine.getId(),
                GraphBuildRecord.BuildStat.BUILD_SUC));
        Assert.lteZero("该图谱正在构建中，不能更新", graphBuildRecordRepository.countByGraphIdAndBuildstat(graphDefine.getId(),
                GraphBuildRecord.BuildStat.BUILDING));

        return graphDefine;

    }
}
