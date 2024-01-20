<template>
  <div>
    <a-row>
      <!-- 左侧图谱区 -->
      <a-col :span="14">
        <!-- 参见 https://www.relation-graph.com/#/docs/start-vue3 -->
        <div
          id="mountNode"
          ref="myPage"
          style="border: gray dashed thin; height: 80vh; width: 50vw"
        >
          <RelationGraph
            ref="graphRef"
            :options="graphOptions"
            :on-node-click="onNodeClick"
            :on-line-click="onLineClick"
            :on-contextmenu="onContextmenu"
            :on-canvas-click="onCanvasClick"
          >
            <template #node="{ node }">
              <div>
                <div
                  class="c-my-rg-node"
                  @click="showNodeMenus(node, $event)"
                  @contextmenu.prevent.stop="showNodeMenus(node, $event)"
                  @mouseover="nodeSlotOver(node, $event)"
                  @mouseout="nodeSlotOut(node, $event)"
                ></div>
                <div
                  style="
                    color: forestgreen;
                    font-size: 16px;
                    position: absolute;
                    width: 160px;
                    height: 25px;
                    line-height: 25px;
                    margin-top: 5px;
                    margin-left: -48px;
                    text-align: center;
                    background-color: rgba(66, 187, 66, 0.2);
                  "
                >
                  {{ node.text }}
                  <!-- {{ node.data.myicon }} -->
                </div>
              </div>
            </template>
          </RelationGraph>
        </div>
      </a-col>
      <!-- 左侧图谱区 -->
      <!-- 右侧配置区 -->
      <a-col :span="9" :offset="1">
        <a-tabs v-model:activeKey="activeKey">
          <!-- 节点属性配置区 -->
          <a-tab-pane key="1" tab="普通模式配置">
            <div>
              <a-form
                :model="graph_json_data"
                :label-col="{ span: 3 }"
                :wrapper-col="{ span: 21 }"
                name="basic"
                autocomplete="off"
              >
                <a-form-item
                  label="名称"
                  name="graphName"
                  :rules="[{ required: true, message: 'Please input your graphName!' }]"
                >
                  <a-input v-model:value="graph_json_data.graphName" allowClear />
                </a-form-item>

                <a-form-item label="描述" name="description">
                  <a-textarea
                    v-model:value="graph_json_data.description"
                    allowClear
                    showCount
                    :autoSize="{ minRows: 4, maxRows: 6 }"
                  />
                </a-form-item>
              </a-form>
            </div>

            <!-- 属性配置区 -->
            <a-row>
              <!-- 按钮区 -->
              <a-col :span="20"
                ><a-space>
                  <a-button type="primary" @click="addNodeAction">添加概念</a-button>
                </a-space></a-col
              >
              <!-- 按钮区 -->
            </a-row>
            <a-row>
              <a-col :span="24"> <a-divider /></a-col>
            </a-row>
            <a-row>
              <a-col :span="16">
                <span>概念名称:</span>

                <a-input
                  placeholder="概念名称"
                  v-model:value="currentNode.text"
                  allowClear
                  @change="onNodeNameChange"
                  style="width: 13vw; margin-left: 10px"
              /></a-col>
              <a-col :span="8">
                <span>概念颜色:</span>

                <a-input
                  placeholder="概念颜色"
                  v-model:value="currentNode.color"
                  type="color"
                  @change="onNodeColorChange"
                  style="width: 3vw; margin-left: 10px"
              /></a-col>
            </a-row>
            <a-row
              style="margin-top: 10px; max-height: 200px; overflow-y: auto; overflow-x: hidden"
            >
              <!-- 节点属性项配置 -->
              <a-col :span="24">
                <a-form
                  ref="dynamic_form_nest_item"
                  name="dynamic_form_nest_item"
                  :model="currentNode"
                >
                  <a-space
                    v-for="(nodeProperty, index) in currentNode.nodeProperties"
                    :key="index"
                    style="display: flex"
                    align="baseline"
                  >
                    <a-form-item
                      :name="['nodeProperties', index, 'nodePropertyName']"
                      :rules="{
                        required: true,
                        message: '节点属性名字不能为空'
                      }"
                    >
                      <a-input
                        v-model:value="nodeProperty.nodePropertyName"
                        placeholder="节点属性名字"
                      />
                    </a-form-item>
                    <a-form-item
                      :name="['nodeProperties', index, 'dataType']"
                      :rules="{
                        required: true,
                        message: '属性数据类型不能为空'
                      }"
                    >
                      <a-select
                        ref="select"
                        style="width: 120px"
                        v-model:value="nodeProperty.dataType"
                        :options="selectOptions"
                      ></a-select>
                    </a-form-item>
                    <MinusCircleOutlined @click="removeUser(nodeProperty)" />
                  </a-space>
                  <a-form-item>
                    <a-button type="dashed" block @click="addUser">
                      <PlusOutlined />
                      添加属性
                    </a-button>
                  </a-form-item>
                  <a-form-item>
                    <a-button style="visibility: hidden" type="primary" html-type="submit"
                      >Submit</a-button
                    >
                  </a-form-item>
                </a-form></a-col
              >
              <!-- 节点属性项配置 -->
            </a-row>
            <!-- 属性配置区 -->
          </a-tab-pane>

          <!-- 节点属性配置区 -->
          <!-- 代码编辑区 -->
          <a-tab-pane key="3" tab="代码模式配置">
            <!-- <a-textarea v-model:value="code" placeholder="配置代码" :rows="20" /> -->
            <JsonEditorVue class="editor" v-model="code" style="height: 50vh"></JsonEditorVue>
          </a-tab-pane>
          <!-- 代码编辑区 -->
        </a-tabs>
        <div><a-divider /> <a-button type="primary" @click="onSaveAction">保存本体</a-button></div>
      </a-col>
      <!-- 右侧配置区 -->
    </a-row>

    <!-- 弹窗区 -->
    <div
      v-show="isShowNodeOperateDialog"
      :style="{ left: nodeMenuPanelPosition.x + 'px', top: nodeMenuPanelPosition.y + 'px' }"
      style="
        z-index: 999;
        padding: 10px;
        background-color: #ffffff;
        border: #eeeeee solid 1px;
        box-shadow: 0px 0px 8px #cccccc;
        position: absolute;
        border-radius: 10px;
      "
      @mouseleave="isShowNodeOperateDialog = false"
    >
      <div style="line-height: 25px; padding-left: 10px; color: #888888; font-size: 12px">
        对这个节点进行操作：
      </div>
      <div class="c-node-menu-item" @click.stop="doAction('editNode')">编辑</div>
      <div class="c-node-menu-item" @click.stop="doAction('addRelation')">添加关系</div>
      <div class="c-node-menu-item" @click.stop="doAction('deleteNode')">删除节点</div>
    </div>
    <!-- 弹窗区 -->
    <!-- 鼠标悬浮时的弹窗 -->
    <div
      v-if="isShowNodeTipsPanel"
      :style="{ left: nodeMenuPanelPosition.x + 'px', top: nodeMenuPanelPosition.y + 'px' }"
      style="
        z-index: 999;
        padding: 10px;
        background-color: #ffffff;
        border: #eeeeee solid 1px;
        box-shadow: 0px 0px 8px #cccccc;
        position: absolute;
      "
    >
      <div style="line-height: 25px; padding-left: 10px; color: #888888; font-size: 12px">
        节点名称：{{ currentNode.text }}
      </div>
      <div class="c-node-menu-item">id:{{ currentNode.id }}</div>
      <div class="c-node-menu-item">名称:{{ currentNode.text }}</div>
      <div class="c-node-menu-item">图标:{{ currentNode.data.myicon }}</div>
    </div>
    <!-- 鼠标悬浮时的弹窗 -->
    <!-- 当在图谱中点击右键时 -->
    <div
      v-if="isShowTipsPanel"
      :style="{ left: nodeMenuPanelPosition.x + 'px', top: nodeMenuPanelPosition.y + 'px' }"
      style="
        z-index: 999;
        padding: 10px;
        background-color: #ffffff;
        border: #eeeeee solid 1px;
        box-shadow: 0px 0px 8px #cccccc;
        position: absolute;
      "
    >
      <div class="c-node-menu-item">添加节点</div>
    </div>
    <!-- 当在图谱中点击右键时 -->
    <!-- 添加关系弹窗 -->
    <div
      v-if="isSHowAddRelationDialog"
      :style="{ left: nodeMenuPanelPosition.x + 'px', top: nodeMenuPanelPosition.y + 'px' }"
      style="
        z-index: 999;
        padding: 10px;
        background-color: #ffffff;
        border: #eeeeee solid 1px;
        box-shadow: 0px 0px 8px #cccccc;
        position: absolute;
      "
    >
      <div style="display: inline-block">
        <span style="display: inline-block">关系名称</span>
        <a-input style="width: 200px" v-model:value="currentLine.text" placeholder="关系名称" />
      </div>
      <div style="display: inline-block; margin-left: 10px">
        尾概念
        <a-select style="width: 120px" v-model:value="currentLine.to">
          <a-select-option :value="item.id" v-for="item in relationNodes" :key="item.id">{{
            item.text
          }}</a-select-option>
        </a-select>
      </div>
      <div style="display: inline-block; margin-left: 10px">
        <a-button type="primary" @click="doAddRelation">确认</a-button>
      </div>
    </div>
    <!-- 添加关系弹窗 -->
    <!-- 删除关系弹窗 -->
    <div
      v-if="isSHowDeleteRelationDialog"
      :style="{
        left: nodeMenuPanelPosition.x - 100 + 'px',
        top: nodeMenuPanelPosition.y - 100 + 'px'
      }"
      style="
        z-index: 999;
        padding: 10px;
        background-color: #ffffff;
        border: #eeeeee solid 1px;
        box-shadow: 0px 0px 8px #cccccc;
        position: absolute;
      "
      @mouseleave="isSHowDeleteRelationDialog = false"
    >
      <div class="c-node-menu-item" @click.stop="deleteRelation('deleteRelation')">删除此关系</div>
    </div>
    <!-- 删除关系弹窗 -->
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
  var r = Math.floor(Math.random() * 255)
  var g = Math.floor(Math.random() * 255)
  var b = Math.floor(Math.random() * 255)
  var color = '#' + r.toString(16) + g.toString(16) + b.toString(16)
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
      nodes: [{ id: 'root', opacity: 0, text: '' }],
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
      return sessionStorage.getItem('ontId')
    },
    mode: function () {
      const ontId = sessionStorage.getItem('ontId')
      return typeof ontId != 'undefined' && ontId.length > 0
    }
  },
  methods: {
    render(nodeId) {
      this.$refs.graphRef.setJsonData(this.graph_json_data, (graphInstance) => {
        // 这些写上当图谱初始化完成后需要执行的代码
        if (typeof nodeId != 'undefined' && nodeId.length > 0) {
          //   //根据节点id在图谱中选中该节点并居中;
          graphInstance.focusNodeById(nodeId)
        }
      })
    },
    onContextmenu($event) {},
    onNodeClick(nodeObject, $event) {
      console.log('onNodeClick:', nodeObject)
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
      this.currentNode = reactive(JSON.parse(jsonNode(nodeObject)))
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
      // console.log('showNodeMenus:', $event, _base_position)
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
    // 动态移除一个表单项
    removeUser(item) {
      const index = this.currentNode.nodeProperties.indexOf(item)
      if (index !== -1) {
        this.currentNode.nodeProperties.splice(index, 1)
      }
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

<style></style>

<style lang="css" scoped>
.c-my-rg-node {
  height: 80px;
  line-height: 80px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  place-items: center;
  justify-content: center;
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
</style>
