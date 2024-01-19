package com.yishuifengxiao.tool.personalkit.service;

import com.yishuifengxiao.common.guava.EventPublisher;
import com.yishuifengxiao.common.tool.exception.UncheckedException;
import com.yishuifengxiao.common.tool.utils.Assert;
import com.yishuifengxiao.tool.personalkit.dao.mongo.repository.GraphBuildRecordRepository;
import com.yishuifengxiao.tool.personalkit.dao.mongo.repository.GraphDefineRepository;
import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphBuildRecord;
import com.yishuifengxiao.tool.personalkit.domain.mongo.GraphDefine;
import com.yishuifengxiao.tool.personalkit.support.ContextUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/18 16:45
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class GraphBuildService {

    private final GraphBuildRecordRepository graphBuildRecordRepository;


    private final GraphDefineRepository graphDefineRepository;

    private final EventPublisher eventPublisher;

    public void start(String id) {
        GraphDefine define = graphDefineRepository.findById(id).orElseThrow(() -> UncheckedException.of("记录不存在"));
        GraphBuildRecord.BuildStat buildStat =
                graphBuildRecordRepository.findTop1ByGraphIdOrderByCreateTimeDesc(id).map(GraphBuildRecord::getBuildstat).orElse(GraphBuildRecord.BuildStat.NO_BUILD);
        Assert.isFalse("图谱正在构建中，不能重复构建", GraphBuildRecord.BuildStat.BUILDING.equals(buildStat));
        // 发送构建事件
        eventPublisher.post(define);

        GraphBuildRecord buildRecord =
                new GraphBuildRecord().setGraphId(define.getId()).setBuildstat(GraphBuildRecord.BuildStat.BUILDING).setCreateTime(LocalDateTime.now()).setCreateUserId(ContextUser.currentUserId());
        graphBuildRecordRepository.save(buildRecord);
    }

    public void stop(String id) {
        GraphDefine define = graphDefineRepository.findById(id).orElseThrow(() -> UncheckedException.of("记录不存在"));
        GraphBuildRecord.BuildStat buildStat =
                graphBuildRecordRepository.findTop1ByGraphIdOrderByCreateTimeDesc(id).map(GraphBuildRecord::getBuildstat).orElse(GraphBuildRecord.BuildStat.NO_BUILD);
        Assert.isTrue("图谱不在构建中，不能重复构建", GraphBuildRecord.BuildStat.BUILDING.equals(buildStat));
    }
}
