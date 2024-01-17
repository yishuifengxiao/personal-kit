package com.yishuifengxiao.tool.personalkit.tool;

import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.collections.JsonUtil;
import com.yishuifengxiao.tool.personalkit.domain.bo.GraphData;
import com.yishuifengxiao.tool.personalkit.domain.mongo.Ontology;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/17 11:38
 * @since 1.0.0
 */
public class OntHelper {

    public static Ontology convert(GraphData graphData) {
        Ontology ontology = new Ontology();
        ontology.setOntologyName(graphData.getGraphName()).setDescription(graphData.getDescription());

        List<Ontology.Node> nodes = DataUtil.stream(graphData.getNodes()).map(v -> {
            String nodeName = v.getText();
            List<Ontology.NodeProperty> nodeProperties = JsonUtil.str2List(MapUtils.getString(v.getData(),
                    GraphData.NODE_PROPERTIES), Ontology.NodeProperty.class);
            List<Ontology.NodeProperty> properties =
                    DataUtil.stream(nodeProperties).filter(Objects::nonNull).filter(s -> StringUtils.isNotBlank(s.getNodePropertyName()) && null != s.getDataType())
                            .distinct().collect(Collectors.toList());
            return new Ontology.Node(nodeName, properties);
        }).filter(Objects::nonNull).collect(Collectors.toList());

        List<Ontology.Edge> edges = DataUtil.stream(graphData.getLines()).map(v -> {
            String edgeName = v.getText();
            String fromNodeName = DataUtil.stream(graphData.getNodes()).filter(s -> StringUtils.equals(s.getText(),
                    v.getFrom())).findFirst().map(GraphData.Node::getText).orElse(null);
            String toNodeName = DataUtil.stream(graphData.getNodes()).filter(s -> StringUtils.equals(s.getText(),
                    v.getFrom())).findFirst().map(GraphData.Node::getText).orElse(null);
            return new Ontology.Edge(edgeName, fromNodeName, toNodeName);
        }).collect(Collectors.toList());
        ontology.setNodes(nodes);
        ontology.setEdges(edges);
        ontology.setText(JsonUtil.toJSONString(graphData));
        return ontology;
    }
}
