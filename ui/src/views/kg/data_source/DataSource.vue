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
      <a-form-item label="文件名" name="fileName">
        <a-input v-model:value="formState.fileName" placeholder="文件名，模糊查询"> </a-input>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit"> 搜索 </a-button>
      </a-form-item>
      <a-space>
        <a-upload
          v-model:file-list="fileList"
          name="file"
          :action="uploadUrl"
          :headers="headers"
          @change="handleChange"
          :showUploadList="false"
        >
          <a-button type="primary">上传文件</a-button>
        </a-upload>
        <a-button type="primary" danger>删除文件</a-button></a-space
      >
    </a-form>

    <!-- 上部搜索条件区域 -->
    <a-divider dashed />
    <!-- 中间内容区域 -->
    <!-- 表格区 -->
    <a-table :columns="columns" :data-source="tableData" :pagination="false">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <a-space>
            <a href="javascript:void(0);" @click="showDetail(record)">详情</a> <a>删除</a></a-space
          >
        </template>
      </template>
    </a-table>
    <!-- 表格区 -->

    <!-- 分页区 -->
    <div style="margin-top: 15px; float: right">
      <a-pagination
        v-model:current="pagination.current"
        :total="pagination.total"
        :show-total="(total) => `共 ${total} 条数据`"
        @change="onPaginationChange"
      />
    </div>
    <!-- 分页区 -->
    <!-- 中间内容区域 -->
  </div>
</template>

<script>
import { reactive, defineComponent, ref } from 'vue'
import { UserOutlined } from '@ant-design/icons-vue'
import { mapState } from 'pinia'
import { useUserStore } from '@/stores/user'
export default defineComponent({
  data() {
    const formState = reactive({
      fileName: ''
    })
    const data = reactive([])
    return { formState, data }
  },
  computed: {
    ...mapState(useUserStore, ['tokenVal']),
    tableData: function () {
      return this.data
    },
    headers: function () {
      return { Authorization: 'xtoken ' + this.tokenVal }
    },
    uploadUrl: function () {
      return this.$cfg.rootUrl() + '/personkit/file/import'
    }
  },
  methods: {
    handleFinish() {
      this.query()
    },
    /**
     * 查询数据
     */
    query() {
      this.$http
        .request({
          url: '/personkit/data/data/record/page',
          data: {
            num: this.pagination.current,
            query: this.formState,
            size: this.pagination.pageSize
          }
        })
        .then((res) => {
          this.pagination.current = res.num
          this.pagination.total = res.total
          this.data = reactive(res.data)
        })
        .catch((err) => console.log(err))
    },

    handleChange(info) {
      if (info.file.status !== 'uploading') {
        console.log(info.file, info.fileList)
      }
      if (info.file.status === 'done') {
        this.$msg.success(`${info.file.name} file uploaded successfully`)
      } else if (info.file.status === 'error') {
        this.$msg.error(`${info.file.name} file upload failed.`)
      }
      this.query()
    },
    //导航栏发生变化
    onPaginationChange(page, pageSize) {
      this.pagination.current = page
      this.query()
    },
    //查看详情
    showDetail(record) {
      sessionStorage.setItem('current_view_file', JSON.stringify(record))

      this.$router.push({ name: 'data_source_detail', query: { record: record.id } })
    }
  },
  components: {
    UserOutlined
  },
  mounted() {
    this.query()
  },
  setup() {
    const columns = [
      {
        title: '文件名称',
        dataIndex: 'fileName',
        key: 'fileName',
        align: 'center'
      },
      {
        title: '上传时间',
        dataIndex: 'createTime',
        key: 'createTime',
        align: 'center'
      },
      {
        title: '处理状态',
        dataIndex: 'statName',
        key: 'statName',
        ellipsis: true,
        align: 'center'
      },
      {
        title: '上传数据总数',
        dataIndex: 'uploadNum',
        key: 'uploadNum',
        ellipsis: true,
        align: 'center'
      },
      {
        title: '有效数据总数',
        dataIndex: 'actualTotalNum',
        key: 'actualTotalNum',
        ellipsis: true,
        align: 'center'
      },
      {
        title: '完成时间',
        dataIndex: 'finishTime',
        key: 'finishTime',
        ellipsis: true,
        align: 'center'
      },

      {
        title: '上传者',
        dataIndex: 'userName',
        key: 'userName',
        ellipsis: true,
        align: 'center'
      },
      {
        title: '操作',
        dataIndex: 'action',
        key: 'action',
        align: 'center'
      }
    ]
    const pagination = {
      current: 1,
      total: 0,
      pageSize: 10
    }
    const fileList = ref([])
    return { columns, pagination, fileList }
  }
})
</script>

<style lang="less" scoped></style>
