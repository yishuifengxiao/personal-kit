<template>
  <div style="max-height: 78vh;overflow-y: auto;">
    <!-- 上部搜索条件区域 -->
    <a-form
      layout="inline"
      name="basic"
      autocomplete="off"
      :model="formState"
      @finish="handleFinish"
      @finishFailed="handleFinishFailed"
    >
      <a-form-item label="名称" name="username">
        <a-input v-model:value="formState.user" placeholder="名称"> </a-input>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit"> 搜索 </a-button>
      </a-form-item>
      <a-form-item>
        <a-button type="primary"> 添加数据集 </a-button>
      </a-form-item>
    </a-form>
    <!-- 上部搜索条件区域 -->
    <a-divider dashed />
    <!-- 中间内容区域 -->
    <a-flex wrap="wrap" gap="small">
      <a-card hoverable style="width: 300px" v-for="item in new Array(16)" :key="item">
        <template #cover>
          <img
            alt="example"
            :src="card_bg_url"
          />
        </template>
        <template #actions>
          <setting-outlined key="setting" />
          <edit-outlined key="edit" />
          <delete-outlined key="ellipsis" />
        </template>
        <a-card-meta title="Card title" description="This is the description">
          <template #avatar>
            <a-avatar src="https://joeschmoe.io/api/v1/random" />
          </template>
        </a-card-meta>
      </a-card>
    </a-flex>
    <!-- 中间内容区域 -->  
  </div>
</template>

<script>
import { reactive, defineComponent } from 'vue'
import {
  UserOutlined,
  EditOutlined,
  SettingOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'

import card_bg_url from "@/assets/images/graph/card_bg.png"
export default defineComponent({
  data() {
    const formState = reactive({
      user: '',
      password: ''
    })
    return { formState ,card_bg_url}
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
    UserOutlined,
    EditOutlined,
    SettingOutlined,
    DeleteOutlined
  },
  setup() {}
})
</script>

<style lang="less" scoped></style>
