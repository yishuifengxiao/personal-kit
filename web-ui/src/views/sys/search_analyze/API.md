# 搜索分析功能 API 接口文档

## 接口说明

搜索分析功能用于记录和分析用户的搜索行为，包括搜索关键词、搜索结果、用户点击行为等，以便计算搜索质量指标。

## 主要接口

### 1. 获取搜索分析数据列表

**接口地址**: `/personkit/search/analyze/page`

**请求方法**: POST

**请求参数**:
```json
{
  "num": 1,           // 页码
  "size": 10,         // 每页条数
  "query": {
    "keyword": "",      // 搜索关键词（模糊查询）
    "userName": "",     // 用户名称（模糊查询）
    "startTime": "",    // 开始时间（格式：YYYY-MM-DD HH:mm:ss）
    "endTime": ""       // 结束时间（格式：YYYY-MM-DD HH:mm:ss）
  }
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userName": "张三",
      "keyword": "人工智能",
      "searchTime": "2024-01-15 14:30:25",
      "totalResults": 156,
      "clickedPosition": 3,
      "clickedTitle": "人工智能的发展历程",
      "clickedResult": true,
      "accuracy": 85.5,
      "precision": 78.2,
      "recall": 92.1
    }
  ],
  "total": 100,
  "statistics": {
    "totalSearchCount": 1250,
    "avgAccuracy": 82.3,
    "avgPrecision": 76.8,
    "avgRecall": 89.2
  }
}
```

### 2. 导出搜索分析数据

**接口地址**: `/personkit/search/analyze/export`

**请求方法**: POST

**请求参数**: 同获取列表接口的查询参数

**响应数据**: Excel文件流（application/vnd.openxmlformats-officedocument.spreadsheetml.sheet）

### 3. 删除搜索分析记录

**接口地址**: `/personkit/search/analyze/delete/{id}`

**请求方法**: DELETE

**路径参数**:
- `id`: 记录ID

**响应数据**:
```json
{
  "code": 200,
  "message": "删除成功"
}
```

## 数据模型说明

### 搜索分析记录字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 记录ID |
| userName | String | 用户名称 |
| keyword | String | 搜索关键词 |
| searchTime | String | 搜索时间 |
| totalResults | Integer | 搜索结果总数 |
| clickedPosition | Integer | 用户点击的结果序号 |
| clickedTitle | String | 用户点击的结果标题 |
| clickedResult | Boolean | 是否点击了结果 |
| accuracy | Double | 准确率（百分比） |
| precision | Double | 精确率（百分比） |
| recall | Double | 召回率（百分比） |

### 质量指标计算公式

1. **准确率 (Accuracy)**: 正确预测的结果数 / 总预测结果数
2. **精确率 (Precision)**: 正确预测的正例数 / 预测为正例的总数
3. **召回率 (Recall)**: 正确预测的正例数 / 实际正例的总数

## 使用说明

1. **数据收集**: 需要在前端搜索功能中集成数据收集逻辑，记录用户的搜索行为和点击行为
2. **指标计算**: 后端根据收集的数据计算各项质量指标
3. **数据展示**: 前端页面展示统计数据和详细记录
4. **数据导出**: 支持将分析数据导出为Excel文件

## 注意事项

1. 时间参数需要使用北京时间格式（YYYY-MM-DD HH:mm:ss）
2. 所有百分比数据都以0-100的数值形式返回
3. 导出功能需要处理文件流响应
4. 删除操作需要管理员权限