<template>
  <div style="max-height: 78vh; overflow-y: auto">
    <!-- 上部搜索条件区域 -->
    <a-form
      layout="inline"
      name="basic"
      autocomplete="off"
      :model="formState"
      @finish="handleFinish"

    >
      <a-form-item label="名称" name="name">
        <a-input v-model:value="formState.name" placeholder="名称" allowClear> </a-input>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit"> 搜索 </a-button>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="addDataSet"> 添加数据集 </a-button>
      </a-form-item>
    </a-form>
    <!-- 上部搜索条件区域 -->
    <a-divider dashed />
    <!-- 中间内容区域 -->
    <a-flex wrap="wrap" gap="small">
      <a-card hoverable style="width: 300px" v-for="item in tableData" :key="item">
        <template #cover>
          <img alt="example" :src="card_bg_url" />
        </template>
        <template #actions>
          <setting-outlined key="setting" />
          <edit-outlined key="edit" />
          <delete-outlined key="ellipsis" />
        </template>

        <a-card-meta :title="item.name" :description="item.description + item.createTime">
          <template #avatar>
            <a-avatar src="" />
          </template>
        </a-card-meta>
      </a-card>
    </a-flex>
    <!-- 中间内容区域 -->
    <!-- 弹窗区域 -->
    <DataSetAdd ref="DataSetAdd" @ok="onAddOk"></DataSetAdd>
    <!-- 弹窗区域 -->
  </div>
</template>

<script>
import { reactive, defineComponent } from 'vue'
import { UserOutlined, EditOutlined, SettingOutlined, DeleteOutlined } from '@ant-design/icons-vue'

import card_bg_url from '@/assets/images/graph/card_bg.png'
import DataSetAdd from './DataSetAdd.vue'
export default defineComponent({
  data() {
    const formState = reactive({
      name: ''
    })

    const results = reactive({})
    return { formState, card_bg_url, results }
  },
  computed: {
    tableData: function () {
      return this.results.data
    }
  },
  methods: {
    handleFinish(val) {
      this.query();
    },
    addDataSet() {
      this.$refs.DataSetAdd.showDrawer()
    },
    query() {
      this.$http
        .request({
          url: '/personkit/data/dataSet/page',
          data: {
            num: 1,
            query: {
              name: this.formState.name
            },
            size: 100
          }
        })
        .then((res) => {
          this.results = reactive(res)
        })
        .catch((err) => console.log(err))
    },
    // 添加成功
    onAddOk() {
      this.query()
    }
  },
  mounted() {
    this.query()
  },
  components: {
    UserOutlined,
    EditOutlined,
    SettingOutlined,
    DeleteOutlined,
    DataSetAdd
  },
  setup() {}
})
</script>

<style lang="less" scoped></style>
