<template>
  <div>
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
        <a-button type="primary" @click="addOnt"> 添加本体 </a-button>
      </a-form-item>
    </a-form>
    <!-- 上部搜索条件区域 -->
    <a-divider dashed />
    <!-- 中间内容区域 -->
    <div style="max-height: 65vh; min-height: 55vh; overflow-y: auto">
      <a-flex wrap="wrap" gap="small">
        <a-card hoverable style="width: 300px" v-for="item in tableData" :key="item">
          <template #cover>
            <img alt="example" :src="card_bg_url" />
          </template>
          <template #actions>
 
            <edit-outlined key="edit" @click="doEdit(item)" />
            <delete-outlined key="ellipsis" @click="doDelete(item)" />
          </template>

          <a-card-meta :title="item.ontologyName" :description="item.description + item.createTime">
            <template #avatar>
              <a-avatar src="" />
            </template>
          </a-card-meta>
        </a-card>
      </a-flex>
    </div>

    <!-- 中间内容区域 -->
    <!-- 分页区 -->
    <div style="margin-top: 15px; float: right">
      <a-pagination
        v-model:current="result.num"
        :total="result.total"
        :show-total="(total) => `共 ${total} 条数据`"
        @change="onPaginationChange"
      />
    </div>
    <!-- 分页区 -->
  </div>
</template>

<script>
import { reactive, defineComponent } from 'vue'
import { EditOutlined, SettingOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import card_bg_url from '@/assets/images/graph/card_bg.png'

export default defineComponent({
  data() {
    const formState = reactive({
      name: ''
    })

    const result = reactive({})
    return { formState, card_bg_url, result }
  },
  computed: {
    tableData: function () {
      return this.result.data
    }
  },
  methods: {
    handleFinish(val) {
      this.query()
    },
    addOnt() {
      sessionStorage.setItem('ontId', '')
      this.$router.push({ name: 'ontology_detail', query: { isAdd: true } })
    },
    query() {
      this.$http
        .request({
          url: '/personkit/graph/ont/page',
          data: {
            num: 1,
            query: {
              name: this.formState.name
            },
            size: 10
          }
        })
        .then((res) => {
          this.result = reactive(res)
        })
        .catch((err) => console.log(err))
    },
    // 添加成功
    onAddOk() {
      this.query()
    },

    //分页导航栏发生变化
    onPaginationChange(page, pageSize) {
      this.pagination.current = page
      this.query()
    },
    //触发编辑操作
    doEdit(val) {
      const id = val.id
      sessionStorage.setItem('ontId', id)

      this.$router.push({ name: 'ontology_detail', query: { isAdd: false, id: id } })
    },
    //删除本体
    doDelete(item) {
      this.$http
        .request({
          url: '/personkit/graph/ont/delete',
          data: {
            id: item.id
          }
        })
        .then((res) => {
          message.success('操作成功')
          this.query();
        })
        .catch((err) => console.log(err))
    }
  },
  mounted() {
    console.log('-------------- ont')
    this.query()
  },
  components: {
    EditOutlined,
    SettingOutlined,
    DeleteOutlined
  },
  setup() {}
})
</script>

<style lang="less" scoped></style>
