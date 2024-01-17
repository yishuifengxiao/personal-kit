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
          >
            <template #node="{ node }">
              <div>
                <div
                  class="c-my-rg-node"
                  @click="showNodeMenus(node, $event)"
                  @contextmenu.prevent.stop="showNodeMenus(node, $event)"
                  @mouseover="nodeSlotOver(node, $event)"
                  @mouseout="nodeSlotOut(node, $event)"
                >
                  <i style="font-size: 30px" :class="node.data.myicon" />
                </div>
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
                :label-col="{ span: 4 }"
                :wrapper-col="{ span: 18 }"
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
              <a-col :span="20" :offset="4"
                ><a-space>
                  <a-button type="primary" @click="addNodeAction">添加概念</a-button>
                </a-space></a-col
              >
              <!-- 按钮区 -->
              <a-col :span="20" :offset="4"> <a-divider /></a-col>
              <a-col :span="20" :offset="4">
                <span>节点名称:</span>
     
                <a-input
                  placeholder="概念名称"
                  v-model:value="currentNode.text"
                  allowClear
                  style="width: 300px; margin-left: 10px"
              /></a-col>
              <a-col :span="20" :offset="4"> <a-divider /></a-col>

              <!-- 节点项配置 -->
              <a-col :span="20" :offset="4">
                <a-form
                  ref="dynamic_form_nest_item"
                  name="dynamic_form_nest_item"
                  :model="dynamicValidateForm"
                  @finish="onFinish"
                >
                  <a-space
                    v-for="(user, index) in dynamicValidateForm.users"
                    :key="user.id"
                    style="display: flex; margin-bottom: 8px"
                    align="baseline"
                  >
                    <a-form-item
                      :name="['users', index, 'nodePropertyName']"
                      :rules="{
                        required: true,
                        message: '节点属性名字不能为空'
                      }"
                    >
                      <a-input v-model:value="user.nodePropertyName" placeholder="节点属性名字" />
                    </a-form-item>
                    <a-form-item
                      :name="['users', index, 'dataType']"
                      :rules="{
                        required: true,
                        message: '属性数据类型不能为空'
                      }"
                    >
                      <a-select
                        ref="select"
                        style="width: 120px"
                        v-model:value="user.dataType"
                        :options="selectOptions"
                      ></a-select>
                    </a-form-item>
                    <MinusCircleOutlined @click="removeUser(user)" />
                  </a-space>
                  <a-form-item>
                    <a-button type="dashed" block @click="addUser">
                      <PlusOutlined />
                      Add user
                    </a-button>
                  </a-form-item>
                  <a-form-item>
                    <a-button style="visibility: hidden" type="primary" html-type="submit"
                      >Submit</a-button
                    >
                  </a-form-item>
                </a-form></a-col
              >
              <!-- 节点项配置 -->
            </a-row>
            <!-- 属性配置区 -->
          </a-tab-pane>

          <!-- 节点属性配置区 -->
          <!-- 代码编辑区 -->
          <a-tab-pane key="3" tab="代码模式配置">
            <!-- <a-textarea v-model:value="code" placeholder="配置代码" :rows="20" /> -->
            <JsonEditorVue class="editor" v-model="code" style="height: 50vh"></JsonEditorVue>
            <a-divider />
            <a-button type="primary" @click="render">确定</a-button>
          </a-tab-pane>
          <!-- 代码编辑区 -->
        </a-tabs>
      </a-col>
      <!-- 右侧配置区 -->
    </a-row>

    <!-- 弹窗区 -->
    <div
      v-show="isShowNodeMenuPanel"
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
    >
      <div style="line-height: 25px; padding-left: 10px; color: #888888; font-size: 12px">
        对这个节点进行操作：
      </div>
      <div class="c-node-menu-item" @click.stop="doAction('addRelation')">添加关系</div>
      <div class="c-node-menu-item" @click.stop="doAction('addProperty')">添加属性</div>
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
      <div class="c-node-menu-item">图标:{{currentNode.data.myicon}}</div>
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
  var r = Math.floor(Math.random() * 256)
  var g = Math.floor(Math.random() * 256)
  var b = Math.floor(Math.random() * 256)
  var color = '#' + r.toString(16) + g.toString(16) + b.toString(16)
  return color
}

export default {
  data() {
    const __graph_json_data = {
      rootId: '2',
      graphName: '',
      description: '',
      nodes: [{id:'root', opacity:0}],
      lines: []
    }
    return {
      graph_json_data: __graph_json_data,
      isShowCodePanel: false,
      isShowNodeMenuPanel: false,
      isShowNodeTipsPanel: false,
      isShowTipsPanel: false,
      currentNode: reactive({}),
      activeKey: '1',

      nodeMenuPanelPosition: { x: 0, y: 0 }
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
    onLineClick(lineObject, linkObject, $event) {
      console.log('onLineClick:', lineObject)
    },
    nodeSlotOver(nodeObject, $event) {
      console.log('nodeSlotOver:', nodeObject)
      // const {id,text,styleClass,nodeShape,data}=
      this.currentNode = reactive(JSON.parse(jsonNode(nodeObject)))
      this.isShowNodeMenuPanel = false
      this.isShowNodeTipsPanel = true

      this.nodeMenuPanelPosition.x = $event.clientX
      this.nodeMenuPanelPosition.y = $event.clientY
    },
    nodeSlotOut(nodeObject, $event) {
      console.log('nodeSlotOut:', nodeObject)
      this.isShowNodeTipsPanel = false
    },
    showNodeMenus(nodeObject, $event) {
      const _base_position = this.$refs.myPage.getBoundingClientRect()
      console.log('showNodeMenus:', $event, _base_position)
      this.nodeMenuPanelPosition.x = $event.clientX + 10
      this.nodeMenuPanelPosition.y = $event.clientY
      this.isShowNodeTipsPanel = false
      this.isShowNodeMenuPanel = true
    },
    doAction(actionName) {
      this.$notify({
        title: '提示',
        message: '对节点【' + this.currentNode.text + '】进行了：' + actionName,
        type: 'success'
      })
      this.isShowNodeMenuPanel = false
    },
    // 添加节点按钮
    addNodeAction() {
      const random = Math.floor(Math.random() * 100000)
      const nodeId = random + ''
      this.graph_json_data.nodes.push({
        id: nodeId,
        text: '节点-' + random,
        color: color16(),
        data: { myicon: 'el-icon-star-on' }
      })
      // this.$refs.graphRef.refresh();
      // console.log(this.graph_json_data)
      this.render(nodeId)
    }
  },
  mounted() {
    this.render()
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
      layouts: [
        {
          label: '中心',
          layoutName: 'center',
          distance_coefficient: 1
        }
      ]
    }

    const formRef = ref()
    const dynamicValidateForm = reactive({
      users: []
    })
    const removeUser = (item) => {
      const index = dynamicValidateForm.users.indexOf(item)
      if (index !== -1) {
        dynamicValidateForm.users.splice(index, 1)
      }
    }
    const addUser = () => {
      dynamicValidateForm.users.push({
        nodePropertyName: '',
        dataType: '',
        id: Date.now()
      })
    }
    const onFinish = (values) => {
      console.log('Received values of form:', values)
      console.log('dynamicValidateForm.users:', dynamicValidateForm.users)
    }

    const formState = reactive({
      username: '',
      password: '',
      remember: true
    })

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
      formState,
      formRef,
      removeUser,
      addUser,
      onFinish,
      dynamicValidateForm,
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
