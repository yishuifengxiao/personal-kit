<template>
  <div>
    <!-- 上部搜索条件区域 -->
    <a-form
      layout="inline"
      name="basic"
      autocomplete="off"
      :model="formState"
      @finish="handleFinish"
      @finishFailed="handleFinishFailed"
    >
      <a-form-item label="Username" name="username">
        <a-input v-model:value="formState.user" placeholder="Username">
          <template #prefix><UserOutlined style="color: rgba(0, 0, 0, 0.25)" /></template>
        </a-input>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit"> 搜索 </a-button>
      </a-form-item>
    </a-form>
    <!-- 上部搜索条件区域 -->
    <a-divider dashed />
    <!-- 中间内容区域 -->
    <a-table :columns="columns" :data-source="tableData">
      <template #bodyCell="{ column, text }">
        <template v-if="column.dataIndex === 'name'">
          <a>{{ text }}</a>
        </template>
      </template>
    </a-table>
    <!-- 中间内容区域 -->
  </div>
</template>

<script>
import { reactive, defineComponent } from 'vue'
import { UserOutlined } from '@ant-design/icons-vue'
export default defineComponent({
  data() {
    const formState = reactive({
      user: '',
      password: ''
    })
    return { formState }
  },
  computed: {
    tableData: function () {
      return []
    }
  },
  methods: {
    handleFinish() {},
    handleFinishFailed() {}
  },
  components: {
    UserOutlined
  },
  setup() {
    const columns = [
      {
        title: 'Name',
        dataIndex: 'name',
        key: 'name'
      },
      {
        title: 'Age',
        dataIndex: 'age',
        key: 'age',
        width: 80
      },
      {
        title: 'Address',
        dataIndex: 'address',
        key: 'address 1',
        ellipsis: true
      },
      {
        title: 'Long Column Long Column Long Column',
        dataIndex: 'address',
        key: 'address 2',
        ellipsis: true
      },
      {
        title: 'Long Column Long Column',
        dataIndex: 'address',
        key: 'address 3',
        ellipsis: true
      },
      {
        title: 'Long Column',
        dataIndex: 'address',
        key: 'address 4',
        ellipsis: true
      }
    ]
    return { columns }
  }
})
</script>

<style lang="less" scoped></style>
