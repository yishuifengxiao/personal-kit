<template>
  <div>
    <a-drawer
      :width="768"
      :title="title"
      :placement="placement"
      :open="open"
      @close="onClose"
      :closable="false"
    >
      <template #extra>
        <a-button style="margin-right: 8px" @click="onClose">取消</a-button>
        <a-button type="primary" @click="onClose">确认</a-button>
      </template>
      <p>
        选择数据源:
        <a-select
          v-model:value="value"
          show-search
          placeholder="input search text"
          style="width: 400px"
          :default-active-first-option="false"
          :show-arrow="false"
          :filter-option="false"
          :not-found-content="null"
          :options="data"
          @search="handleSearch"
          @change="handleChange"
        ></a-select>
      </p>
      <a-divider orientation="left">已选择的数据源</a-divider>
      <p><span>Some contents...</span><span>已选择</span><span>已提交</span></p>
    </a-drawer>
  </div>
</template>
<script>
import { ref, defineComponent, reactive } from 'vue'

export default defineComponent({
  computed: {
    selections: function () {
      return reactive([])
    }
  },
  setup() {
    const placement = ref('left')
    const open = ref(true)
    let title = reactive('添加数据集')
    const showDrawer = (param) => {
      open.value = true
      title = typeof param === 'undefined' ? reactive('添加数据集') : reactive('编辑数据集')
    }
    const onClose = () => {
      open.value = false
    }

    const data = ref([])
    const value = ref()
    const handleSearch = (val) => {
      fetch(val, (d) => (data.value = d))
    }
    const handleChange = (val) => {
      console.log(val)
      value.value = val
      fetch(val, (d) => (data.value = d))
    }

    const values = reactive([])

    return {
      placement,
      open,
      showDrawer,
      onClose,
      title,
      data,
      value,
      handleSearch,
      handleChange,
      values
    }
  }
})
</script>
