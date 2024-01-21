package com.yishuifengxiao.tool.personalkit.tool;

import com.alibaba.fastjson2.JSONArray;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.collections.JsonUtil;
import com.yishuifengxiao.tool.personalkit.domain.bo.GraphData;
import com.yishuifengxiao.tool.personalkit.domain.mongo.Ontology;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2024/1/17 11:38
 * @since 1.0.0
 */
public class OntHelper {

    public static Ontology convert(Map<String, Object> graphData) {
        String id = MapUtils.getString(graphData, "id");
        String graphName = MapUtils.getString(graphData, "graphName");
        String description = MapUtils.getString(graphData, "description");

        Ontology ontology = new Ontology();
        ontology.setOntologyName(graphName).setDescription(description).setId(id);
        List<GraphData.Node> graphNodes = JSONArray.parseArray(JSONArray.toJSONString(graphData.get("nodes")),
                GraphData.Node.class);
        List<GraphData.Line> lines = JSONArray.parseArray(JSONArray.toJSONString(graphData.get("lines")),
                GraphData.Line.class);
        List<Ontology.Node> nodes = DataUtil.stream(graphNodes).map(v -> {
            String nodeName = v.getText();
            List<Ontology.NodeProperty> nodeProperties = JsonUtil.str2List(MapUtils.getString(v.getData(),
                    GraphData.NODE_PROPERTIES), Ontology.NodeProperty.class);
            List<Ontology.NodeProperty> properties =
                    DataUtil.stream(nodeProperties).filter(Objects::nonNull).filter(s -> StringUtils.isNotBlank(s.getNodePropertyName()) && null != s.getDataType())
                            .distinct().collect(Collectors.toList());
            return new Ontology.Node(nodeName, properties);
        }).filter(Objects::nonNull).collect(Collectors.toList());

        List<Ontology.Edge> edges = DataUtil.stream(lines).map(v -> {
            String edgeName = v.getText();
            String fromNodeName = DataUtil.stream(graphNodes).filter(s -> StringUtils.equals(s.getId(),
                    v.getFrom())).findFirst().map(GraphData.Node::getText).orElse(null);
            String toNodeName = DataUtil.stream(graphNodes).filter(s -> StringUtils.equals(s.getId(),
                    v.getTo())).findFirst().map(GraphData.Node::getText).orElse(null);
            if (StringUtils.isNoneBlank(edgeName, fromNodeName, toNodeName)) {
                return null;
            }
            return new Ontology.Edge(edgeName, fromNodeName, toNodeName);
        }).filter(Objects::nonNull).collect(Collectors.toList());
        ontology.setNodes(nodes);
        ontology.setEdges(edges);
        ontology.setText(JsonUtil.toJSONString(graphData));
        return ontology;
    }

    private String randomColor() {
        StringBuilder sb = new StringBuilder("#");
        IntStream.range(0, 3).mapToObj(v -> Integer.toHexString(RandomUtils.nextInt(0, 15))).forEach(sb::append);
        String color = sb.toString();
        return color;
    }
}
