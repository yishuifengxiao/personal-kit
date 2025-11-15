<template>
  <div>
    <!-- 上部搜索条件区域 -->
    <div style="display: flex; justify-content: space-between; align-items: center;">
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
        <a-form-item>
          <a-button @click="handleReset"> 重置 </a-button>
        </a-form-item>
      </a-form>
      
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
        <a-button type="primary" @click="showUploadHistory">上传历史</a-button>
      </a-space>
    </div>

    <!-- 上部搜索条件区域 -->
    <a-divider dashed />
    <!-- 中间内容区域 -->
    <!-- 表格区 -->
    <a-table :columns="columns" :data-source="tableData" :pagination="false">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <a-space>
            <a-button
              type="link"
              @click="showDetail(record)"
              :disabled="record.stat != 2 || record.actualTotalNum === 0"
              >详情</a-button
            >
            <a-button
              type="link"
              danger
              @click="handleDelete(record)"
            >删除</a-button>
          </a-space>
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
import { message } from 'ant-design-vue'
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
      return this.$cfg.rootUrl() + '/personkit/file/upload'
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
          url: '/personkit/data/center/file/page',
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
        .catch((err) => {
          console.error('查询数据失败:', err)
          message.error('查询数据失败: ' + (err.message || '未知错误'))
        })
    },

    /**
     * 重置搜索条件
     */
    handleReset() {
      this.formState = reactive({
        fileName: ''
      })
      this.pagination.current = 1
      this.query()
    },

    /**
     * 跳转到上传历史页面
     */
    showUploadHistory() {
      this.$router.push({ name: 'UploadHistory' })
    },
    handleDelete(record) {
      this.$confirm({
        title: '确认删除',
        content: `确定要删除文件 "${record.originalFileName}" 吗？`,
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          const params = {
            fileId: record.id
          }
          this.$http
            .get('/personkit/data/center/file/delete', { params })
            .then(res => {
              if (res.code === 0) {
                this.$message.success('删除成功')
                this.query()
              } else {
                this.$message.error(res.message || '删除失败')
              }
            })
            .catch(err => {
              console.error('删除文件失败:', err)
              this.$message.error('删除文件失败')
            })
        }
      })
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
        dataIndex: 'originalFileName',
        key: 'originalFileName',
        align: 'center',
        ellipsis: true
      },
      {
        title: '文件大小',
        dataIndex: 'size',
        key: 'size',
        align: 'center',
        customRender: ({ text }) => {
          if (!text) return '0 B'
          const units = ['B', 'KB', 'MB', 'GB']
          let size = text
          let unitIndex = 0
          while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024
            unitIndex++
          }
          return `${size.toFixed(2)} ${units[unitIndex]}`
        }
      },
      {
        title: '文件类型',
        dataIndex: 'suffix',
        key: 'suffix',
        align: 'center'
      },
      {
        title: '模式',
        dataIndex: 'mode',
        key: 'mode',
        align: 'center',
        customRender: ({ text }) => {
          return text === 0 ? '仅上传' : text === 1 ? '上传且解析' : '未知'
        }
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
        key: 'createTime',
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
