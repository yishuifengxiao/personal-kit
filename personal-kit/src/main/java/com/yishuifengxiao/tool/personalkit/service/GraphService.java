package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.tool.lang.NumberUtil;
import com.yishuifengxiao.common.tool.random.IdWorker;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.dao.mongo.GraphDefineDao;
import com.yishuifengxiao.tool.personalkit.dao.mongo.repository.*;
import com.yishuifengxiao.tool.personalkit.domain.entity.DiskFile;
import com.yishuifengxiao.tool.personalkit.domain.mongo.DataSet;
import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphBuildRecord;
import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphDefine;
import com.yishuifengxiao.tool.personalkit.domain.mongo.VirtuallyFile;
import com.yishuifengxiao.tool.personalkit.domain.request.IdReq;
import com.yishuifengxiao.tool.personalkit.domain.vo.GraphDefineVo;
import com.yishuifengxiao.tool.personalkit.support.ContextCache;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Page<GraphDefineVo> findPage(PageQuery<GraphDefine> param) {
        GraphDefine dataSet = param.query().orElse(new GraphDefine());
        dataSet.setCreateUserId(ContextCache.currentUserId());
        param.setQuery(dataSet);
        return graphDefineDao.findPage(param).map(v -> {
            GraphBuildRecord.BuildStat buildStat =
                    graphBuildRecordRepository.findTop1ByGraphIdOrderByCreateTimeDesc(v.getId()).map(GraphBuildRecord::getBuildstat).orElse(GraphBuildRecord.BuildStat.NO_BUILD);

            return new GraphDefineVo(v.getId(), v.getGraphName(), v.getDescription(), v.getCreateTime(), buildStat);
        });
    }

    public String save(GraphDefine param) {
        Assert.lteZero("已经存在相同名称的图谱", graphDefineRepository.countAllByGraphNameAndCreateUserId(param.getGraphName(),
                ContextCache.currentUserId()));
        param.setCreateUserId(ContextCache.currentUserId()).setCreateTime(LocalDateTime.now()).setVersion(NumberUtil.ZERO.intValue()).setMaster(true);
        param.setId(IdWorker.snowflakeStringId());
        graphDefineRepository.save(param);
        return param.getId();
    }

    public String update(GraphDefine param) {

        GraphDefine graphDefine = validate(param.getId());
        if (!StringUtils.equalsIgnoreCase(graphDefine.getGraphName(), param.getGraphName())) {
            Assert.lteZero("已经存在相同名称的图谱",
                    graphDefineRepository.countAllByGraphNameAndCreateUserId(param.getGraphName(),
                            graphDefine.getId()));
        }
        graphDefine.setGraphName(param.getGraphName()).setDescription(param.getDescription()).setOntologyId(param.getOntologyId());
        graphDefineRepository.save(graphDefine);
        return param.getId();
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
                            virtuallyFileRepository.findById(v.getVirtuallyFileId()).orElseThrow(() -> UncheckedException.of("数据源文件不存在"));
                    return null;
                });
            }
        } else {
            dataSource = new GraphDefine.DataSource().setSourceItems(Collections.emptyList());
        }

        graphDefine.setDataSource(dataSource);
        graphDefineRepository.save(graphDefine);
    }

    public GraphDefine.DataSource dataSource(String id) {
        GraphDefine graphDefine = validate(id);
        GraphDefine.DataSource dataSource = graphDefine.getDataSource();
        if (null == dataSource) {
            return new GraphDefine.DataSource();
        }
        DataSet dataSet =
                dataSetRepository.findById(dataSource.getDataSetId()).orElseThrow((() -> UncheckedException.of(
                        "数据源已被删除")));
        dataSource.setDataSetName(dataSet.getName());
        if (null == dataSource.getSourceItems()) {
            dataSource.setSourceItems(Collections.emptyList());
        } else {
            List<GraphDefine.SourceItem> items = dataSource.getSourceItems().stream().map(v -> {
                v.setDiskFileName(Optional.ofNullable(JdbcUtil.jdbcHelper().findByPrimaryKey(DiskFile.class,
                        v.getDiskFileId())).map(DiskFile::getFileName).orElseThrow(() -> UncheckedException.of(
                        "数据文件不存在")));
                v.setVirtuallyFileName(virtuallyFileRepository.findById(v.getVirtuallyFileId()).map(VirtuallyFile::getSheetName).orElseThrow(() -> UncheckedException.of("数据文件不存在")));
                return v;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            dataSource.setSourceItems(items);
        }

        return dataSource;
    }


    public void updateNodeMapping(GraphDefine.NodeMapping nodeMapping) {
        GraphDefine graphDefine = validate(nodeMapping.getId());
        List<GraphDefine.NodeMapping> mappings =
                DataUtil.stream(graphDefine.getNodeMappings()).filter(Objects::nonNull).filter(v -> StringUtils.equals(v.getEdgeName(), nodeMapping.getEdgeName())).collect(Collectors.toList());
        mappings.add(nodeMapping);
        graphDefine.setNodeMappings(mappings);
        graphDefineRepository.save(graphDefine);
    }

    public List<GraphDefine.NodeMapping> nodeMapping(String id) {
        GraphDefine graphDefine = validate(id);
        return graphDefine.getNodeMappings();
    }

    private GraphDefine validate(String id) {
        GraphDefine graphDefine = graphDefineRepository.findById(id).orElseThrow(() -> new UncheckedException("记录不存在"));
        Assert.lteZero("该图谱已存在构建记录，不能更新", graphBuildRecordRepository.countByGraphIdAndBuildstat(graphDefine.getId(),
                GraphBuildRecord.BuildStat.BUILD_SUC));
        Assert.lteZero("该图谱正在构建中，不能更新", graphBuildRecordRepository.countByGraphIdAndBuildstat(graphDefine.getId(),
                GraphBuildRecord.BuildStat.BUILDING));

        return graphDefine;

    }


    public GraphDefine detail(IdReq param) {
        GraphDefine graphDefine = graphDefineRepository.findById(param.getId()).orElseThrow(() -> new UncheckedException(
                "记录不存在"));
        graphDefine.setDataSource(null).setNodeMappings(Collections.EMPTY_LIST).setDataFusions(Collections.EMPTY_LIST);
        return graphDefine;
    }
}
