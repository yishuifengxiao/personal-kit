<template>
  <div>
    <a-row>
      <a-col :span="14">
        <!-- 参见 https://www.relation-graph.com/#/docs/start-vue3 -->
        <div id="mountNode" style="border: red solid thin; height: 80vh; width: 50vw">
          <RelationGraph ref="graphRef" :options="options"></RelationGraph></div
      ></a-col>
      <a-col :span="10">
        <div>
          <a-form
            :model="formState"
            :label-col="{ span: 6 }"
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
        <div><a-space>
          
        </a-space></div
      ></a-col>
    </a-row>
  </div>
</template>

<script>
import { reactive, defineComponent } from 'vue'
import RelationGraph from 'relation-graph/vue3'

export default {
  data() {
    const data = {
      // 点集
      nodes: [
        {
          id: 'node1', // 节点的唯一标识
          x: 100, // 节点横坐标
          y: 200, // 节点纵坐标
          label: '起始点' // 节点文本
        },
        {
          id: 'node2',
          x: 300,
          y: 200,
          label: '目标点'
        }
      ],
      // 边集
      edges: [
        // 表示一条从 node1 节点连接到 node2 节点的边
        {
          source: 'node1', // 起始点 id
          target: 'node2', // 目标点 id
          label: '我是连线' // 边的文本
        }
      ]
    }
    return { data }
  },
  methods: {
    render() {
      const jsonData = {
        rootId: 'a',
        nodes: [
          { id: 'a', text: 'a' },
          { id: 'b', text: 'b' },
          { id: 'c', text: 'c' },
          { id: 'd', text: 'd' },
          { id: 'e', text: 'e' },
          { id: 'f', text: 'f' }
        ],
        lines: [
          { from: 'a', to: 'b' },
          { from: 'a', to: 'c' },
          { from: 'a', to: 'd' },
          { from: 'a', to: 'e' },
          { from: 'a', to: 'f' }
        ]
      }
      this.$refs.graphRef.setJsonData(jsonData)
    }
  },
  mounted() {
    this.render()
  },
  setup() {
    const options = {
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

    const formState = reactive({
      username: '',
      password: '',
      remember: true
    })
    return { options, formState }
  },
  components: {
    RelationGraph
  }
}
</script>

<style></style>
