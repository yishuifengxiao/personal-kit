package com.yishuifengxiao.tool.personalkit.tool;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.yishuifengxiao.common.tool.bean.JsonUtil;
import com.yishuifengxiao.common.tool.codec.Md5;
import com.yishuifengxiao.common.tool.collections.CollUtil;
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

        // 解析节点
        List<Ontology.Node> nodes =
                JSONArray.parseArray(JSONArray.toJSONString(graphData.get("nodes"))).stream().map(v -> {
                    JSONObject jsonObject = JSONObject.from(v);
                    String nodeId = jsonObject.getString("id");
                    if (StringUtils.equals(nodeId, "root")) {
                        return null;
                    }

                    String nodeName = jsonObject.getString("text");
                    if (StringUtils.isBlank(nodeName)) {
                        return null;
                    }
                    if (StringUtils.isBlank(nodeId)) {
                        nodeId = Md5.md5Short(nodeName);
                    }


                    Ontology.Node node = new Ontology.Node();
                    node.setNodeName(nodeName).setNodeId(nodeId);
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (null != data && null != data.getJSONArray("nodeProperties")) {
                        List<Ontology.NodeProperty> nodeProperties =
                                data.getJSONArray("nodeProperties").stream().map(s -> JSONObject.from(s).to(Ontology.NodeProperty.class)).collect(Collectors.toList());
                        node.setNodeProperties(nodeProperties);
                    }
                    return node;
                }).filter(Objects::nonNull).collect(Collectors.toList());

        List<Ontology.Edge> edges =
                JSONArray.parseArray(JSONArray.toJSONString(graphData.get("lines"))).stream().map(v -> {
                    JSONObject jsonObject = JSONObject.from(v);
                    String edgeName = jsonObject.getString("text");
                    String fromNodeName = CollUtil.stream(nodes).filter(s -> StringUtils.equals(s.getNodeId(),
                            jsonObject.getString("from"))).findFirst().map(Ontology.Node::getNodeName).orElse(null);
                    String toNodeName = CollUtil.stream(nodes).filter(s -> StringUtils.equals(s.getNodeId(),
                            jsonObject.getString("to"))).findFirst().map(Ontology.Node::getNodeName).orElse(null);
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
