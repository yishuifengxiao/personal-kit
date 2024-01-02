<template>
  <div>
    <!-- 上部内容展示区 -->
    <div>
      当前文件名称:
      <a-select v-model:value="currentFileId" style="width: 20rem" @change="onSelectChange">
        <a-select-option
          :value="item.virtualFileId"
          v-for="item in allRecord.files"
          :key="item.virtualFileId"
          >{{ item.virtualFileName }}</a-select-option
        >
      </a-select>
      <a-space style="margin-left: 10rem; float: right">
        <span>上传数据总数:{{ currentFile.uploadNum }} </span>
        <span>有效数据总数:{{ currentFile.actualTotalNum }} </span
        ><span>上传时间 {{ currentFile.createTime }}</span></a-space
      >
    </div>
    <a-divider />

    <!-- 上部内容展示区 -->
    <!-- 中间内容区 -->
    <!-- 表格区 -->
    <a-table
      :columns="colDefines"
      :data-source="tableData"
      :pagination="false"
      v-if="colDefines.length > 0"
    >
      <template #bodyCell="{ column, text }">
        <template v-if="typeof column.dataIndex != 'undefined' && column.dataIndex === 'name'">
          <a>{{ text }}</a>
        </template>
      </template>
    </a-table>
    <!-- 表格区 -->
    <!-- 分页区 -->
    <div style="margin-top: 15px; float: right">
      <a-pagination
        v-model:current="result.page.num"
        :total="result.page.total"
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
    const result = {
      headers: [],
      page: {
        data: [],
        empty: true,
        num: 1,
        pages: 0,
        size: 10,
        total: 0
      }
    }
    return { result }
  },
  computed: {
    //列名定义
    colDefines: function () {
      return this.result.headers.map((v) => {
        const name = null === v.name ? '' : v.name
        return {
          title: name,
          dataIndex: name,
          key: name,
          align: 'left'
        }
      })
    },
    //表格数据
    tableData: function () {
      const data = this.result.page.data
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
    //查询文件结构和内容
    query() {
      this.$http
        .request({
          url: '/personkit/data/center/view',
          data: {
            num: this.result.page.num,
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
      this.result.page.num = page
      this.query()
    },
    //下拉选项发生变化,即预览文件发生变化
    onSelectChange(value) {
      this.currentFileId = value
      this.currentFile = this.allRecord.files.filter((v) => {
        return v.virtualFileId === this.currentFileId
      })[0]
      this.query()
    }
  },
  mounted() {
    console.log('--------------------- 2222222 ')
    this.query()
  },
  setup() {
    console.log('--------------------- 111111 ')
    const allRecord = JSON.parse(sessionStorage.getItem('current_view_file'))
    const currentFile = allRecord.files[0]
    const currentFileId = allRecord.files[0].virtualFileId
    return { allRecord, currentFile, currentFileId }
  }
})
</script>

<style lang="less" scoped></style>
