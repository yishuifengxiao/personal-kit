<template>
  <div>
    <!-- 上部内容展示区 -->
    <div>
      当前文件名称:
      <a-select v-model:value="currentFileId" style="width: 20rem;" @change="onSelectChange">
        <a-select-option
          :value="item.virtualFileId"
          v-for="item in allRecord.files"
          :key="item.virtualFileId"
          >{{ item.virtualFileName }}</a-select-option
        >
      </a-select>
      <a-space style="margin-left: 10rem; float: right;">
        <span>上传数据总数:{{ currentFile.uploadNum }} </span>
        <span>有效数据总数:{{ currentFile.actualTotalNum }} </span
        ><span>上传时间 {{ currentFile.createTime }}</span></a-space
      >
    </div>
    <a-divider />
    {{ currentFile }}
    <!-- 上部内容展示区 -->
    <!-- 中间内容区 -->
    <!-- 表格区 -->
    <a-table :columns="colDefines" :data-source="tableData" :pagination="false">
      <template #bodyCell="{ column, text }">
        <template v-if="column.dataIndex === 'name'">
          <a>{{ text }}</a>
        </template>
      </template>
    </a-table>
    <!-- 表格区 -->
    <!-- 分页区 -->
    <div style="margin-top: 15px; float: right;">
      <a-pagination
        v-model:current="result.num"
        :total="result.total"
        :show-total="(total) => `共 ${total} 条数据`"
        @change="onPaginationChange"
      />
    </div>
    <!-- 分页区 -->
    <!-- 中间内容区 -->
  </div>
</template>

<script>
import { reactive, defineComponent, ref } from 'vue'
export default defineComponent({
  props: {
    record: String
  },
  data() {
    const fileStrcuts = []
    const result = {
      num: 1,
      total: 0,
      data: []
    }
    const currentFileId = this.currentFile.virtualFileId
    return { fileStrcuts, result, currentFileId }
  },
  computed: {
    //列名定义
    colDefines: function () {
      return this.fileStrcuts.map((v) => {
        return {
          title: v.name,
          dataIndex: v.name,
          key: v.name,
          align: 'left'
        }
      })
    },
    //表格数据
    tableData: function () {
      const data = this.result.data
      if (typeof data === 'undefined' || null === data) {
        return []
      }
      return data.map((v) => {
        let tmp = {}
        for (let item of v.cells) {
          tmp[item.columnName] = item.text
        }
        tmp.___data = v
        return tmp
      })
    }
  },
  methods: {
    // 查询文件结构
    loadStruct() {
      this.$http
        .request({
          url: '/personkit/data/virtuallyFile/findVirtuallyFileDefine',
          data: {
            id: this.currentFileId
          }
        })
        .then((res) => {
          this.fileStrcuts = res
          this.query()
        })
        .catch((err) => console.log(err))
    },
    //查询数据
    query() {
      this.$http
        .request({
          url: '/personkit/data/virtuallyFile/findPageVirtuallyRow',
          data: {
            num: this.result.num,
            query: {
              virtuallyFileId: this.currentFileId
            },
            size: 10
          }
        })
        .then((res) => {
          this.result = res
        })
        .catch((err) => console.log(err))
    },
    //分页变化
    onPaginationChange(page, pageSize) {
      this.result.num = page
      this.query()
    },
    //下拉选项发生变化
    onSelectChange(value) {
      this.currentFileId = value
      const files = this.allRecord.files

      this.currentFile = files.filter((v) => {
        return v.virtualFileId == value
      })[0]
      this.query()
    }
  },
  mounted() {
    this.loadStruct()
  },
  setup() {
    const allRecord = JSON.parse(sessionStorage.getItem('current_view_file'))
    const currentFile = allRecord.files[0]

    return { allRecord, currentFile }
  }
})
</script>

<style lang="less" scoped></style>
