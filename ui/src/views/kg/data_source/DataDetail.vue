<template>
  <div>
    <!-- 上部内容展示区 -->
    <div>
      当前文件名称:
      <a-select v-model:value="currentFile" style="width: 20rem">
        <a-select-option
          :value="item.virtualFileId"
          v-for="item in viewFiles"
          :key="item.virtualFileId"
          >{{ item.virtualFileName }}</a-select-option
        >
      </a-select>
      <a-space style="margin-left: 10rem; float: right">
        <span>上传数据总数:{{ currentItem.uploadNum }} </span>
        <span>有效数据总数:{{ currentItem.actualTotalNum }} </span
        ><span>上传时间 {{ currentRoot.createTime }}</span></a-space
      >
    </div>
    <a-divider />
    fileId========={{ currentFile }}
    <!-- 上部内容展示区 -->
  </div>
</template>

<script>
import { reactive, defineComponent, ref } from 'vue'
export default defineComponent({
  props: {
    record: String
  },
  computed: {
    //所有的总数据
    currentRoot: function () {
      return JSON.parse(sessionStorage.getItem('current_view_file'))
    },
    // 所有的子文件
    viewFiles: function () {
      return this.currentRoot.files
    },

    //当前选择的文件
    currentItem: {
      get() {
        const tmp = this.viewFiles
        debugger
        return tmp[0]
      },
      // setter
      set(newValue) {
        this.openKeys = newValue
      }
    },
    //当前选择的文件
    currentFile: {
      get() {
        return this.currentItem.virtualFileId
      },
      // setter
      set(newValue) {
        this.openKeys = newValue
      }
    }
  }
})
</script>

<style lang="less" scoped></style>
