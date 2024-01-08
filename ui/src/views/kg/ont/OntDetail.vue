<template>
  <div>
    <!-- 参见 https://www.relation-graph.com/#/docs/start-vue3 -->
    <div id="mountNode" style="border: red solid thin; height: 80vh; width: 60vw">
      <RelationGraph ref="graphRef" :options="options"></RelationGraph>
    </div>
  </div>
</template>

<script>
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
    return { options }
  },
  components: {
    RelationGraph
  }
}
</script>

<style></style>
