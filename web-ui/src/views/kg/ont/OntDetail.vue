<template>
  <div class="ont-detail-container">
    <a-row class="detail-row" :gutter="16">
      <!-- 左侧图谱区 -->
      <a-col :span="14" class="graph-col">
        <div class="graph-container">
          <RelationGraph
            ref="graphRef"
            :options="graphOptions"
            :on-node-click="onNodeClick"
            :on-line-click="onLineClick"
            :on-contextmenu="onContextmenu"
            :on-canvas-click="onCanvasClick"
            class="relation-graph"
          >
            <template #node="{ node }">
              <div class="node-container">
                <div
                  class="c-my-rg-node"
                  @click="showNodeMenus(node, $event)"
                  @contextmenu.prevent.stop="showNodeMenus(node, $event)"
                  @mouseover="nodeSlotOver(node, $event)"
                  @mouseout="nodeSlotOut(node, $event)"
                >
                  {{ node.text.charAt(0) }}
                </div>
                <div class="node-label">
                  {{ node.text }}
                </div>
              </div>
            </template>
          </RelationGraph>
        </div>
      </a-col>
      <!-- 右侧配置区 -->
      <a-col :span="10" class="config-col">
        <div class="config-panel">
          <a-tabs v-model:activeKey="activeKey" class="config-tabs">
            <!-- 节点属性配置区 -->
            <a-tab-pane key="1" tab="普通模式配置">
              <div class="basic-config">
                <a-form
                  :model="graph_json_data"
                  :label-col="{ span: 4 }"
                  :wrapper-col="{ span: 20 }"
                  name="basic"
                  autocomplete="off"
                  class="compact-form"
                >
                  <a-form-item
                    label="名称"
                    name="graphName"
                    :rules="[{ required: true, message: '请输入本体名称!' }]"
                  >
                    <a-input v-model:value="graph_json_data.graphName" allowClear placeholder="请输入本体名称" />
                  </a-form-item>

                  <a-form-item label="描述" name="description">
                    <a-textarea
                      v-model:value="graph_json_data.description"
                      allowClear
                      showCount
                      :autoSize="{ minRows: 3, maxRows: 5 }"
                      placeholder="请输入本体描述"
                    />
                  </a-form-item>
                </a-form>
              </div>

              <div class="node-config">
                <div class="config-header">
                  <a-button type="primary" size="small" @click="addNodeAction">
                    <PlusOutlined />
                    添加概念
                  </a-button>
                </div>
                
                <a-divider class="compact-divider" />
                
                <div class="node-properties">
                  <div class="concept-info-row">
                    <div class="concept-name-group">
                      <span class="property-label">概念名称:</span>
                      <a-input
                        placeholder="请输入概念名称"
                        v-model:value="currentNode.text"
                        allowClear
                        @change="onNodeNameChange"
                        class="name-input"
                      />
                    </div>
                    <div class="concept-color-group">
                      <span class="property-label">概念颜色:</span>
                      <a-input
                        v-model:value="currentNode.color"
                        type="color"
                        @change="onNodeColorChange"
                        class="color-input"
                      />
                    </div>
                  </div>

                  <div class="properties-list">
                    <a-form
                      ref="dynamic_form_nest_item"
                      name="dynamic_form_nest_item"
                      :model="currentNode"
                      class="properties-form"
                    >
                      <div class="property-item" v-for="(nodeProperty, index) in currentNode.nodeProperties" :key="index">
                        <a-space align="baseline" class="property-space">
                          <a-form-item
                            :name="['nodeProperties', index, 'nodePropertyName']"
                            :rules="{
                              required: true,
                              message: '节点属性名字不能为空'
                            }"
                            class="property-name-item"
                          >
                            <a-input
                              v-model:value="nodeProperty.nodePropertyName"
                              placeholder="属性名称"
                              class="property-input"
                              @change="onNodePropertyChange"
                            />
                          </a-form-item>
                          <a-form-item
                            :name="['nodeProperties', index, 'dataType']"
                            :rules="{
                              required: true,
                              message: '属性数据类型不能为空'
                            }"
                            class="property-type-item"
                          >
                            <a-select
                              v-model:value="nodeProperty.dataType"
                              :options="selectOptions"
                              placeholder="数据类型"
                              class="type-select"
                              @change="onNodePropertyChange"
                            ></a-select>
                          </a-form-item>
                          <a-button type="text" danger size="small" @click="removeUser(nodeProperty)" class="remove-btn">
                            <MinusCircleOutlined />
                          </a-button>
                        </a-space>
                      </div>
                      <div class="add-property-btn">
                        <a-button type="dashed" block @click="addUser" size="small">
                          <PlusOutlined />
                          添加属性
                        </a-button>
                      </div>
                    </a-form>
                  </div>
                </div>
              </div>
            </a-tab-pane>

            <!-- 代码编辑区 -->
            <a-tab-pane key="3" tab="代码模式配置">
              <div class="code-editor-container">
                <JsonEditorVue class="editor" v-model="code" style="height: 45vh"></JsonEditorVue>
              </div>
            </a-tab-pane>
          </a-tabs>
          
          <div class="save-section">
            <a-divider class="compact-divider" />
            <a-button type="primary" @click="onSaveAction" class="save-btn">
              保存本体
            </a-button>
          </div>
        </div>
      </a-col>
    </a-row>

    <!-- 弹窗区 -->
    <div
      v-show="isShowNodeOperateDialog"
      :style="{ left: nodeMenuPanelPosition.x + 'px', top: nodeMenuPanelPosition.y + 'px' }"
      class="context-menu"
      @mouseleave="isShowNodeOperateDialog = false"
    >
      <div class="context-menu-title">对这个节点进行操作：</div>
      <div class="context-menu-item" @click.stop="doAction('editNode')">编辑</div>
      <div class="context-menu-item" @click.stop="doAction('addRelation')">添加关系</div>
      <div class="context-menu-item" @click.stop="doAction('deleteNode')">删除节点</div>
    </div>
    <!-- 鼠标悬浮时的弹窗 -->
    <div
      v-if="isShowNodeTipsPanel"
      :style="{ left: nodeMenuPanelPosition.x + 'px', top: nodeMenuPanelPosition.y + 'px' }"
      class="node-tooltip"
    >
      <div class="tooltip-title">节点名称：{{ currentNode.text }}</div>
      <div class="tooltip-item">id: {{ currentNode.id }}</div>
      <div class="tooltip-item">名称: {{ currentNode.text }}</div>
      <div class="tooltip-item">图标: {{ currentNode.data.myicon }}</div>
    </div>
    <!-- 当在图谱中点击右键时 -->
    <div
      v-if="isShowTipsPanel"
      :style="{ left: nodeMenuPanelPosition.x + 'px', top: nodeMenuPanelPosition.y + 'px' }"
      class="context-menu"
    >
      <div class="context-menu-item">添加节点</div>
    </div>
    <!-- 添加关系弹窗 -->
    <div
      v-if="isSHowAddRelationDialog"
      :style="{ left: nodeMenuPanelPosition.x + 'px', top: nodeMenuPanelPosition.y + 'px' }"
      class="relation-dialog"
    >
      <div class="relation-form">
        <div class="form-group">
          <span class="form-label">关系名称</span>
          <a-input v-model:value="currentLine.text" placeholder="请输入关系名称" class="relation-input" />
        </div>
        <div class="form-group">
          <span class="form-label">尾概念</span>
          <a-select v-model:value="currentLine.to" placeholder="选择目标概念" class="concept-select">
            <a-select-option :value="item.id" v-for="item in relationNodes" :key="item.id">
              {{ item.text }}
            </a-select-option>
          </a-select>
        </div>
        <div class="form-actions">
          <a-button type="primary" @click="doAddRelation" size="small">确认</a-button>
        </div>
      </div>
    </div>
    <!-- 删除关系弹窗 -->
    <div
      v-if="isSHowDeleteRelationDialog"
      :style="{
        left: nodeMenuPanelPosition.x - 100 + 'px',
        top: nodeMenuPanelPosition.y - 100 + 'px'
      }"
      class="context-menu"
      @mouseleave="isSHowDeleteRelationDialog = false"
    >
      <div class="context-menu-item" @click.stop="deleteRelation('deleteRelation')">删除此关系</div>
    </div>
  </div>
</template>

<script>
import { reactive, ref } from 'vue'
// https://www.relation-graph.com/#/docs/start-vue3
import RelationGraph from 'relation-graph/vue3'
import { MinusCircleOutlined, PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue'
// https://github.com/guyue88/json-editor-vue3
import JsonEditorVue from 'json-editor-vue3'

function jsonNode(json_data) {
  var cache = []
  var json_str = JSON.stringify(json_data, function (key, value) {
    if (typeof value === 'object' && value !== null) {
      if (cache.indexOf(value) !== -1) {
        return
      }
      cache.push(value)
    }
    return value
  })
  cache = null //释放cache
  return json_str
}

function color16() {
  //十六进制颜色随机

  var color =
    '#' +
    Math.floor(Math.random() * 15).toString(16) +
    Math.floor(Math.random() * 15).toString(16) +
    Math.floor(Math.random() * 15).toString(16) +
    Math.floor(Math.random() * 15).toString(16) +
    Math.floor(Math.random() * 15).toString(16) +
    Math.floor(Math.random() * 15).toString(16)
  console.log('-------------------> color = ' + color)
  return color
}

export default {
  props: {
    id: String,
    isAdd: String
  },
  data() {
    const __graph_json_data = {
      rootId: '2',
      graphName: '',
      description: '',
      nodes: [{ id: 'root', opacity: 0, text: '', isHide: true }],
      lines: []
    }
    return {
      graph_json_data: __graph_json_data,
      isShowCodePanel: false,
      isShowNodeOperateDialog: false, //节点操作弹窗
      isShowNodeTipsPanel: false,
      isShowTipsPanel: false,
      isSHowAddRelationDialog: false, //添加关系弹窗
      isSHowDeleteRelationDialog: false, // 删除关系弹窗
      currentNode: reactive({ nodeProperties: [], color: '#ffffff' }),
      currentLine: reactive({ from: 'a', to: 'c', text: 'line 2' }),
      activeKey: '1',
      nodeMenuPanelPosition: { x: 0, y: 0 },
      isUpdate: false
    }
  },
  computed: {
    code: {
      get() {
        return this.graph_json_data
      },
      set(newVal) {
        this.graph_json_data = JSON.parse(newVal)
      }
    },
    //可选的关系目标节点
    relationNodes: function () {
      return this.graph_json_data.nodes.filter((v) => v.text.length > 0)
    },
    ontId: function () {
      const ontId = sessionStorage.getItem('ontId')
      if (typeof ontId != 'undefined' && ontId.length > 0) {
        return ontId
      }
      return typeof this.id != 'undefined' ? this.id : ''
    },
    mode: function () {
      return this.ontId.length > 0
    }
  },
  methods: {
    render(nodeId) {
      this.$refs.graphRef.setJsonData(this.graph_json_data, (graphInstance) => {
        // 这些写上当图谱初始化完成后需要执行的代码
        if (typeof nodeId != 'undefined' && nodeId.length > 0) {
          //   //根据节点id在图谱中选中该节点并居中;
          // graphInstance.focusNodeById(nodeId)
        }
      })
    },
    onContextmenu(nodeObject, $event) {
      // 右键点击节点时显示操作菜单
      console.log('onContextmenu:', nodeObject)
      this.showNodeMenus(nodeObject, $event)
    },
    onNodeClick(nodeObject, $event) {
      console.log('onNodeClick:', nodeObject)
      // 将点击的节点数据回显到右侧编辑区
      const nodeData = JSON.parse(jsonNode(nodeObject))
      // 确保节点有完整的属性结构
      if (!nodeData.nodeProperties) {
        nodeData.nodeProperties = []
      }
      if (!nodeData.data) {
        nodeData.data = {}
      }
      // 优先从 data.nodeProperties 中读取属性数据，如果不存在则使用 nodeProperties
      if (nodeData.data.nodeProperties && Array.isArray(nodeData.data.nodeProperties)) {
        nodeData.nodeProperties = nodeData.data.nodeProperties
      } else if (!nodeData.data.nodeProperties) {
        nodeData.data.nodeProperties = nodeData.nodeProperties
      }
      this.currentNode = reactive(nodeData)
      // 切换到普通模式配置标签页（key=1）
      this.activeKey = '1'
    },
    // 点击线条事件
    onLineClick(lineObject, linkObject, $event) {
      this.isShowNodeOperateDialog = false
      this.currentLine = lineObject
      this.isSHowDeleteRelationDialog = true
    },
    nodeSlotOver(nodeObject, $event) {
      // console.log('nodeSlotOver:', nodeObject)
      // const {id,text,styleClass,nodeShape,data}=
      const nodeData = JSON.parse(jsonNode(nodeObject))
      // 确保节点有完整的属性结构
      if (!nodeData.nodeProperties) {
        nodeData.nodeProperties = []
      }
      if (!nodeData.data) {
        nodeData.data = {}
      }
      // 优先从 data.nodeProperties 中读取属性数据，如果不存在则使用 nodeProperties
      if (nodeData.data.nodeProperties && Array.isArray(nodeData.data.nodeProperties)) {
        nodeData.nodeProperties = nodeData.data.nodeProperties
      } else if (!nodeData.data.nodeProperties) {
        nodeData.data.nodeProperties = nodeData.nodeProperties
      }
      this.currentNode = reactive(nodeData)
      this.isShowNodeOperateDialog = false
      this.isShowNodeTipsPanel = true

      this.nodeMenuPanelPosition.x = $event.clientX
      this.nodeMenuPanelPosition.y = $event.clientY
    },
    nodeSlotOut(nodeObject, $event) {
      // console.log('nodeSlotOut:', nodeObject)
      this.isShowNodeTipsPanel = false
    },
    showNodeMenus(nodeObject, $event) {
      // const _base_position = this.$refs.myPage.getBoundingClientRect()
      console.log('showNodeMenus triggered:', nodeObject, $event)
      const nodeData = JSON.parse(jsonNode(nodeObject))
      // 确保节点有完整的属性结构
      if (!nodeData.nodeProperties) {
        nodeData.nodeProperties = []
      }
      if (!nodeData.data) {
        nodeData.data = {}
      }
      // 优先从 data.nodeProperties 中读取属性数据，如果不存在则使用 nodeProperties
      if (nodeData.data.nodeProperties && Array.isArray(nodeData.data.nodeProperties)) {
        nodeData.nodeProperties = nodeData.data.nodeProperties
      } else if (!nodeData.data.nodeProperties) {
        nodeData.data.nodeProperties = nodeData.nodeProperties
      }
      this.currentNode = reactive(nodeData)
      this.nodeMenuPanelPosition.x = $event.clientX + 10
      this.nodeMenuPanelPosition.y = $event.clientY
      this.isShowNodeTipsPanel = false
      this.isShowNodeOperateDialog = true
    },
    doAction(actionName) {
      this.isShowNodeOperateDialog = false
      const currentNode = this.currentNode
      if ('deleteNode' === actionName) {
        alert('删除当前节点')
        const nodes = this.graph_json_data.nodes
        const results = nodes.filter((v) => v.text !== currentNode.text)
        this.graph_json_data.nodes = results
        this.render()
      } else if ('editNode' === actionName) {
        //编辑节点 - 只需要关闭弹窗，节点信息已经在currentNode中
        this.isShowNodeOperateDialog = false
      } else if ('addRelation' === actionName) {
        //添加关系
        this.isShowNodeOperateDialog = false
        this.isSHowAddRelationDialog = true
        this.currentLine = reactive({
          from: currentNode.id,
          to: '',
          text: '关系' + new Date().getTime()
        })
      }
    },
    //点击画布事件
    onCanvasClick(e) {
      // debugger
    },
    // 添加节点按钮
    addNodeAction() {
      const random = Math.floor(Math.random() * 100000)
      const nodeId = random + ''
      const currentNode = {
        id: nodeId,
        text: '节点-' + random,
        isHide: false,
        color: color16(),
        data: { myicon: 'el-icon-star-on', nodeProperties: [] }
      }
      this.graph_json_data.nodes.push(currentNode)
      this.currentNode = reactive(currentNode)
      this.render(nodeId)
    },
    // 节点名称变化
    onNodeNameChange() {
      const currentNode = this.currentNode
      const nodes = this.graph_json_data.nodes.filter((v) => v.id != currentNode.id)
      nodes.push(currentNode)
      this.graph_json_data.nodes = nodes
      this.render(currentNode.id)
    },
    // 节点颜色变化
    onNodeColorChange() {
      this.onNodeNameChange()
    },
    // 节点属性变化
    onNodePropertyChange() {
      this.onNodeNameChange()
    },
    // 动态移除一个表单项
    removeUser(item) {
      const index = this.currentNode.nodeProperties.indexOf(item)
      if (index !== -1) {
        this.currentNode.nodeProperties.splice(index, 1)
      }
      // 同步更新到图谱数据
      this.onNodeNameChange()
    },
    //动态增加一个表单项
    addUser() {
      const currentNode = this.currentNode
      if (typeof currentNode.nodeProperties === 'undefined') {
        currentNode.nodeProperties = []
      }
      currentNode.nodeProperties.push({
        nodePropertyName: '属性' + new Date().getTime(),
        dataType: 'TEXT',
        id: Date.now()
      })
      this.currentNode = currentNode
      // 同步更新到图谱数据
      this.onNodeNameChange()
    },
    // 确定保存操作
    onSaveAction() {
      if (!this.graph_json_data.graphName) {
        this.$msg.error('本体名称不能为空')
        return false
      }
      if (this.mode) {
        this.graph_json_data.id = this.ontId
      }
      const currentNode = this.currentNode
      if (!currentNode.id) {
        currentNode.id = new Date().getTime() + ''
      }
      const nodes = this.graph_json_data.nodes.filter((v) => v.text != currentNode.text)
      nodes.push(currentNode)
      this.graph_json_data.nodes = nodes
      this.render()
      const url = this.mode ? '/personkit/graph/ont/update' : '/personkit/graph/ont/save'
      this.$http
        .request({
          url: url,
          data: this.graph_json_data
        })
        .then((res) => {
          sessionStorage.setItem('ontId', res)
          this.$msg.success('保存成功')
        })
        .catch((err) => console.log(err))
    },
    //确认添加关系
    doAddRelation() {
      const currentLine = this.currentLine
      const index = this.graph_json_data.lines.indexOf(currentLine)
      if (index !== -1) {
        this.graph_json_data.lines.splice(index, 1)
      }
      this.graph_json_data.lines.push(currentLine)
      this.isSHowAddRelationDialog = false
      this.render()
    },
    // 确认删除关系
    deleteRelation() {
      const currentLine = this.currentLine
      const lines = this.graph_json_data.lines.filter(
        (v) => v.from != currentLine.from || v.to != currentLine.to || v.text != currentLine.text
      )
      this.graph_json_data.lines = lines
      this.render()
    },
    //加载数据
    load() {
      if (this.mode) {
        //更新操作
        this.$http
          .request({
            url: '/personkit/graph/ont/detail',
            data: { id: this.ontId }
          })
          .then((res) => {
            this.graph_json_data = JSON.parse(res)
            this.render()
          })
          .catch((err) => console.log(err))
      }
    }
  },
  mounted() {
    this.load()
  },
  setup() {
    const graphOptions = {
      backgroundImageNoRepeat: true,
      backgrounImageNoRepeat: true,
      isMoveByParentNode: true,
      defaultExpandHolderPosition: 'top',
      defaultLineShape: 3,
      defaultJunctionPoint: 'ltrb',
      placeOtherGroup: true,
      allowSwitchLineShape: true,
      allowSwitchJunctionPoint: true,
      moveToCenterWhenRefresh: true,
      layouts: [
        {
          label: '中心',
          layoutName: 'center',
          distance_coefficient: 1
        }
      ]
    }

    const selectOptions = ref([
      { value: 'TEXT', label: '字符串' },
      { value: 'LONG', label: '整数' },
      { value: 'DOUBLE', label: '小数' },
      { value: 'DATE', label: '日期' },
      { value: 'DATE_TIME', label: '日期时间' },
      { value: 'TIME', label: '时间' }
    ])

    return {
      graphOptions,
      selectOptions
    }
  },
  components: {
    RelationGraph,
    MinusCircleOutlined,
    PlusOutlined,
    JsonEditorVue
  }
}
</script>

<style lang="less" scoped>
.ont-detail-container {
  height: calc(100vh - 64px - 48px - 48px); /* 视口高度 - header - padding - breadcrumb */
  max-height: calc(100vh - 64px - 48px - 48px);
  overflow: hidden;
  background: #f5f5f5;
  padding: 16px;
  margin: 0;
  display: flex;
  flex-direction: column;
}

.detail-row {
  height: 100%;
  margin: 0 !important;
  flex: 1;
  display: flex;
}

.graph-col {
  height: 100%;
  padding: 0 !important;
}

.graph-container {
  height: 100%;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 0;
  overflow: hidden;
  position: relative;
}

.relation-graph {
  width: 100% !important;
  height: 100% !important;
  min-height: 100% !important;
}

.config-col {
  height: 100%;
  padding: 0 !important;
}

.config-panel {
  height: 100%;
  background: white;
  border: 1px solid #e8e8e8;
  border-left: none;
  display: flex;
  flex-direction: column;
  position: relative;
}

.config-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  
  :deep(.ant-tabs-nav) {
    padding-right: 140px; /* 为保存按钮留出更多空间 */
    margin-bottom: 0;
    padding-top: 8px; /* 增加顶部内边距 */
  }
  
  :deep(.ant-tabs-content) {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
  }
  
  :deep(.ant-tabs-content-holder) {
    flex: 1;
    overflow: auto;
    max-height: calc(100vh - 64px - 48px - 48px - 56px); /* 减去标签栏高度 */
  }
  
  :deep(.ant-tabs-tabpane) {
    height: 100%;
    overflow-y: auto;
  }
}

.basic-config {
  margin-bottom: 16px;
}

.compact-form {
  :deep(.ant-form-item) {
    margin-bottom: 12px;
  }
  
  :deep(.ant-form-item-label) {
    padding-bottom: 0;
    font-weight: 500;
  }
}

.node-config {
  background: #fafafa;
  border-radius: 6px;
  padding: 16px;
}

.config-header {
  margin-bottom: 12px;
}

.compact-divider {
  margin: 12px 0;
}

.node-properties {
  
}

.concept-info-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  padding: 12px;
  background: white;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
}

.concept-name-group {
  display: flex;
  align-items: center;
  flex: 1;
}

.concept-color-group {
  display: flex;
  align-items: center;
}

.property-row {
  margin-bottom: 16px;
  align-items: center;
}

.property-label {
  font-size: 13px;
  font-weight: 500;
  color: #595959;
  margin-right: 8px;
  white-space: nowrap;
}

.name-input {
  flex: 1;
  min-width: 120px;
}

.color-input {
  width: 40px;
  height: 28px;
  padding: 2px;
  border-radius: 4px;
  cursor: pointer;
}

.properties-list {
  max-height: 250px; /* 固定最大高度 */
  overflow-y: auto;
  padding-right: 8px;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #d9d9d9;
    border-radius: 2px;
  }
}

.properties-form {
  
}

.property-item {
  margin-bottom: 8px;
  padding: 8px;
  background: white;
  border-radius: 4px;
  border: 1px solid #f0f0f0;
}

.property-space {
  width: 100%;
  align-items: center;
}

.property-name-item {
  flex: 1;
  margin-bottom: 0;
}

.property-type-item {
  width: 120px;
  margin-bottom: 0;
  margin-left: 8px;
}

.property-input {
  
}

.type-select {
  width: 120px;
}

.remove-btn {
  margin-left: 8px;
  min-width: 24px;
  width: 24px;
  height: 24px;
  padding: 0;
}

.add-property-btn {
  margin-top: 12px;
}

.code-editor-container {
  height: calc(100vh - 64px - 48px - 48px - 280px); /* 进一步降低编辑器高度 */
  max-height: calc(100vh - 64px - 48px - 48px - 280px);
  min-height: 200px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  overflow: hidden;
  
  .editor {
    height: 100% !important;
    border: none !important;
  }
}

.save-section {
  padding: 16px;
  background: white;
  border-top: 1px solid #f0f0f0;
  position: absolute;
  top: 4px;
  right: 16px;
  left: auto;
  width: auto;
  border-top: none;
  padding: 0;
  background: transparent;
  z-index: 10;
}

.save-btn {
  width: auto;
  min-width: 100px;
}

.node-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.node-label {
  color: forestgreen;
  font-size: 14px;
  position: absolute;
  width: 140px;
  height: 24px;
  line-height: 24px;
  margin-top: 5px;
  margin-left: -48px;
  text-align: center;
  background-color: rgba(66, 187, 66, 0.2);
  border-radius: 4px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.c-my-rg-node {
  height: 80px;
  line-height: 80px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  place-items: center;
  justify-content: center;
  background-color: rgba(66, 187, 66, 0.8);
  color: white;
  font-weight: bold;
  font-size: 24px;
  border: 2px solid #42bb42;
}

.c-node-menu-item {
  line-height: 30px;
  padding-left: 10px;
  cursor: pointer;
  color: #444444;
  font-size: 14px;
  border-top: #efefef solid 1px;
}

.c-node-menu-item:hover {
  background-color: rgba(66, 187, 66, 0.2);
}

/* 弹窗样式优化 */
.context-menu {
  z-index: 999;
  padding: 8px 0;
  background-color: #ffffff;
  border: 1px solid #e8e8e8;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  position: absolute;
  border-radius: 6px;
  min-width: 140px;
}

.context-menu-title {
  line-height: 24px;
  padding: 4px 12px;
  color: #8c8c8c;
  font-size: 11px;
  font-weight: 500;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 4px;
}

.context-menu-item {
  line-height: 28px;
  padding: 0 12px;
  cursor: pointer;
  color: #262626;
  font-size: 13px;
  transition: background-color 0.2s ease;
  
  &:hover {
    background-color: #f5f5f5;
  }
}

.node-tooltip {
  z-index: 999;
  padding: 8px 12px;
  background-color: #ffffff;
  border: 1px solid #e8e8e8;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  position: absolute;
  border-radius: 6px;
  min-width: 160px;
}

.tooltip-title {
  line-height: 20px;
  color: #8c8c8c;
  font-size: 11px;
  font-weight: 500;
  margin-bottom: 4px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 4px;
}

.tooltip-item {
  line-height: 20px;
  color: #262626;
  font-size: 12px;
  padding: 2px 0;
}

.relation-dialog {
  z-index: 999;
  padding: 16px;
  background-color: #ffffff;
  border: 1px solid #e8e8e8;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  position: absolute;
  border-radius: 6px;
  min-width: 280px;
}

.relation-form {
  
}

.form-group {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-label {
  font-size: 13px;
  font-weight: 500;
  color: #595959;
  min-width: 60px;
}

.relation-input {
  flex: 1;
}

.concept-select {
  flex: 1;
}

.form-actions {
  text-align: right;
  margin-top: 8px;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .ont-detail-container {
    height: auto;
    min-height: 100vh;
  }
  
  .detail-row {
    height: auto;
  }
  
  .graph-col,
  .config-col {
    height: auto;
    min-height: 50vh;
  }
  
  .graph-container,
  .config-panel {
    height: auto;
    min-height: 50vh;
  }
}
</style>
