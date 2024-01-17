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
                  {{ node.data.myicon }}
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
                :model="formState"
                :label-col="{ span: 4 }"
                :wrapper-col="{ span: 18 }"
                name="basic"
                autocomplete="off"
              >
                <a-form-item
                  label="名称"
                  name="username"
                  :rules="[{ required: true, message: 'Please input your username!' }]"
                >
                  <a-input v-model:value="formState.username" allowClear />
                </a-form-item>

                <a-form-item label="描述" name="password">
                  <a-textarea
                    v-model:value="formState.desc"
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
                ><a-space> <a-button type="primary">添加节点</a-button> </a-space></a-col
              >
              <!-- 按钮区 -->
              <a-col :span="20" :offset="4"> <a-divider /></a-col>
              <a-col :span="20" :offset="4">
                <span>节点名称</span> <a-input placeholder="节点名称" allowClear
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
            <JsonEditorVue  class="editor" v-model ="code" style="height: 50vh;"></JsonEditorVue>
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
    <!-- 悬浮图案 -->
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
      <div class="c-node-menu-item">id:{{ currentNode.text }}</div>
      <div class="c-node-menu-item">图标:{currentNode.data.myicon}</div>
    </div>
    <!-- 悬浮图案 -->
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

export default {
  data() {
    const __graph_json_data = {
      rootId: '2',
      nodes: [
        // 注意：在节点配置信息中，你的自定义属性需要像下面这样放到data标签中，否则数据会丢失
        { id: '1', text: '节点-1', data: { myicon: 'el-icon-star-on' } },
        { id: '2', text: '节点-2', data: { myicon: 'el-icon-setting' } },
        { id: '3', text: '节点-3', data: { myicon: 'el-icon-setting' } },
        { id: '4', text: '节点-4', data: { myicon: 'el-icon-star-on' } },
        { id: '6', text: '节点-6', data: { myicon: 'el-icon-setting' } },
        { id: '7', text: '节点-7', data: { myicon: 'el-icon-setting' } },
        { id: '8', text: '节点-8', data: { myicon: 'el-icon-star-on' } },
        { id: '9', text: '节点-9', data: { myicon: 'el-icon-headset' } },
        { id: '71', text: '节点-71', data: { myicon: 'el-icon-headset' } },
        { id: '72', text: '节点-72', data: { myicon: 'el-icon-s-tools' } },
        { id: '73', text: '节点-73', data: { myicon: 'el-icon-star-on' } },
        { id: '81', text: '节点-81', data: { myicon: 'el-icon-s-promotion' } },
        { id: '82', text: '节点-82', data: { myicon: 'el-icon-s-promotion' } },
        { id: '83', text: '节点-83', data: { myicon: 'el-icon-star-on' } },
        { id: '84', text: '节点-84', data: { myicon: 'el-icon-s-promotion' } },
        { id: '85', text: '节点-85', data: { myicon: 'el-icon-sunny' } },
        { id: '91', text: '节点-91', data: { myicon: 'el-icon-sunny' } },
        { id: '92', text: '节点-82', data: { myicon: 'el-icon-sunny' } },
        { id: '51', text: '节点-51', data: { myicon: 'el-icon-sunny' } },
        { id: '52', text: '节点-52', data: { myicon: 'el-icon-sunny' } },
        { id: '53', text: '节点-53', data: { myicon: 'el-icon-sunny' } },
        { id: '54', text: '节点-54', data: { myicon: 'el-icon-sunny' } },
        { id: '55', text: '节点-55', data: { myicon: 'el-icon-sunny' } },
        { id: '5', text: '节点-5', data: { myicon: 'el-icon-sunny' } }
      ],
      lines: [
        { from: '7', to: '71', text: '投资' },
        { from: '7', to: '72', text: '投资' },
        { from: '7', to: '73', text: '投资' },
        { from: '8', to: '81', text: '投资' },
        { from: '8', to: '82', text: '投资' },
        { from: '8', to: '83', text: '投资' },
        { from: '8', to: '84', text: '投资' },
        { from: '8', to: '85', text: '投资' },
        { from: '9', to: '91', text: '投资' },
        { from: '9', to: '92', text: '投资' },
        { from: '5', to: '51', text: '投资1' },
        { from: '5', to: '52', text: '投资' },
        { from: '5', to: '53', text: '投资3' },
        { from: '5', to: '54', text: '投资4' },
        { from: '5', to: '55', text: '投资' },
        { from: '1', to: '2', text: '投资' },
        { from: '3', to: '1', text: '高管' },
        { from: '4', to: '2', text: '高管' },
        { from: '6', to: '2', text: '高管' },
        { from: '7', to: '2', text: '高管' },
        { from: '8', to: '2', text: '高管' },
        { from: '9', to: '2', text: '高管' },
        { from: '1', to: '5', text: '投资' }
      ]
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
    render() {
      this.$refs.graphRef.setJsonData(this.graph_json_data, (graphInstance) => {
        // 这些写上当图谱初始化完成后需要执行的代码
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
