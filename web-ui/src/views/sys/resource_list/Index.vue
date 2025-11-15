<template>
  <div>
    <!-- 上部搜索条件区域 -->
    <a-form
      layout="inline"
      name="basic"
      autocomplete="off"
      :model="formState"
      @finish="handleFinish"
      :label-col="labelCol"
    >
      <a-form-item label="应用名称" name="applicationName" class="input">
        <a-input
          allowClear
          v-model:value="formState.applicationName"
          placeholder="应用名称，模糊查询"
        >
        </a-input>
      </a-form-item>

      <a-form-item label="模块名称" name="模块名称" class="input">
        <a-input allowClear v-model:value="formState.module" placeholder="模块名称，模糊查询">
        </a-input>
      </a-form-item>

      <a-form-item label="资源名称" name="name" class="input">
        <a-input allowClear v-model:value="formState.name" placeholder="资源名称，模糊查询">
        </a-input>
      </a-form-item>
      <a-form-item label="资源路径" name="url" class="input">
        <a-input allowClear v-model:value="formState.url" placeholder="资源路径，模糊查询">
        </a-input>
      </a-form-item>
      <a-space class="input">
        <a-button type="primary" html-type="submit"> 搜索 </a-button>
        <a-button @click="handleReset"> 重置 </a-button>
      </a-space>
    </a-form>

    <!-- 上部搜索条件区域 -->
    <a-divider dashed />
    <!-- 中间内容区域 -->
    <!-- 表格容器，添加固定高度和滚动 -->
    <div class="table-container">
      <!-- 表格区 -->
      <a-table
        :columns="columns"
        :data-source="tableData"
        :pagination="false"
        :scroll="{ x: 1500, y: tableHeight }"
        size="small"
        :row-selection="rowSelection"
      >
        <!-- <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'action'">
            <a-space>
              <a-button
                type="link"
                @click="showDetail(record)"
                :disabled="record.stat != 2 || record.actualTotalNum === 0"
                >详情</a-button
              >
              <a>删除</a> <a>修改</a> <a>修改状态</a></a-space
            >
          </template>
</template> -->
      </a-table>
      <!-- 表格区 -->
    </div>

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
import { reactive, defineComponent, ref ,getCurrentInstance } from 'vue'
import { UserOutlined } from '@ant-design/icons-vue'
import { mapState } from 'pinia'
import { useUserStore } from '@/stores/user'
export default defineComponent({
  data() {
    const formState = reactive({})
    const data = reactive([])
    const roleSource = reactive([])
    const tableHeight = ref(400) // 表格初始高度

    const rowSelection = ref({
      checkStrictly: false,
      onChange: (selectedRowKeys, selectedRows) => {
        console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows)
      },
      onSelect: (record, selected, selectedRows) => {
        console.log(record, selected, selectedRows)
      },
      onSelectAll: (selected, selectedRows, changeRows) => {
        console.log(selected, selectedRows, changeRows)
      }
    })

    return {
      formState,
      data,
      roleSource,
      rowSelection,
      tableHeight
    }
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
      return this.$cfg.rootUrl() + '/personkit/disk/file/import'
    }
  },
  methods: {
    handleFinish() {
      this.query()
    },

    /**
     * 重置搜索条件
     */
    handleReset() {
      // 重置表单数据
      this.formState = reactive({})
      // 重置分页到第一页
      this.pagination.current = 1
      // 重新查询数据
      this.query()
    },

    /**
     * 查询数据
     */
    query() {
      this.$http
        .request({
          url: '/personkit/sys/permission/page',
          data: {
            num: this.pagination.current,
            query: this.formState,
            size: this.pagination.pageSize
          }
        })
        .then((res) => {
          this.pagination.current = res.num
          this.pagination.total = res.total

          // 直接使用返回的数据，不需要树形处理
          this.data = reactive(res.data)
        })
        .catch((err) => console.log(err))
    },

    /**
     * 计算表格高度
     */
    calculateTableHeight() {
      // 获取窗口高度
      const windowHeight = window.innerHeight
      // 计算表格容器可用高度（减去搜索区域、分页区域等）
      const searchHeight = 120 // 搜索区域高度
      const paginationHeight = 60 // 分页区域高度
      const margins = 40 // 边距

      const availableHeight = windowHeight - searchHeight - paginationHeight - margins

      // 设置最小高度和最大高度
      this.tableHeight = Math.max(300, Math.min(600, availableHeight))
    },

    /**
     * 窗口大小变化监听
     */
    handleResize() {
      this.calculateTableHeight()
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
    // 初始化表格高度
    this.calculateTableHeight()
    // 监听窗口大小变化
    window.addEventListener('resize', this.handleResize)
  },
  beforeUnmount() {
    // 移除事件监听
    window.removeEventListener('resize', this.handleResize)
  },
  setup() {
  
    const columns = [
      {
        title: '应用名称',
        dataIndex: 'applicationName',
        key: 'applicationName',
        align: 'center'
      },
      {
        title: '资源路径',
        dataIndex: 'url',
        key: 'url',
        ellipsis: true,
        align: 'center'
      },
      {
        title: '请求方法',
        dataIndex: 'httpMethod',
        key: 'httpMethod',
        ellipsis: true,

        align: 'center'
      },
      {
        title: '模块名称',
        dataIndex: 'module',
        key: 'module',
        align: 'center'
      },

      {
        title: '资源名称',
        dataIndex: 'name',
        key: 'name',
        ellipsis: true,
        align: 'center'
      },

      {
        title: '描述',
        dataIndex: 'description',
        key: 'description',

        ellipsis: true,
        align: 'center'
      }
    ]
    const pagination = {
      current: 1,
      total: 0,
      pageSize: 10
    }
    const fileList = ref([])
    const labelCol = { style: { width: '80px' } }
    const wrapperCol = { span: 14 }
    const statusOptions = reactive([
      { label: '需要鉴权', value: 1 },
      { label: '无需鉴权', value: 0 }
    ])
    return { columns, pagination, fileList, labelCol, wrapperCol, statusOptions }
  }
})
</script>

<style lang="less" scoped>
.input {
  margin: 5px 5px 10px 5px;
  padding-right: 10px;
}

.table-container {
  // 添加边框和圆角
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  // 添加内边距
  padding: 8px;
  // 设置背景色
  background-color: #fff;
  // 添加阴影效果
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.03),
    0 1px 6px -1px rgba(0, 0, 0, 0.02),
    0 2px 4px rgba(0, 0, 0, 0.02);

  // 确保表格在容器内正确显示
  :deep(.ant-table) {
    border-radius: 4px;
  }

  :deep(.ant-table-container) {
    border-radius: 4px;
  }
}

// 响应式设计
@media (max-height: 700px) {
  .table-container {
    max-height: 400px;
  }
}

@media (max-height: 500px) {
  .table-container {
    max-height: 250px;
  }
}
</style>
