<template>
  <div class="page-content-wrapper">
    <!-- 上部搜索条件区域 -->
    <div class="search-area">
      <a-form
        layout="inline"
        name="basic"
        autocomplete="off"
        :model="formState"
        @finish="handleFinish"
        :label-col="labelCol"
      >
        <a-form-item label="搜索关键词" name="keyword" class="input">
          <a-input allowClear v-model:value="formState.keyword" placeholder="搜索关键词，模糊查询"></a-input>
        </a-form-item>

        <a-form-item label="用户名称" name="userName" class="input">
          <a-input allowClear v-model:value="formState.userName" placeholder="用户名称，模糊查询"></a-input>
        </a-form-item>

        <a-form-item label="搜索时间" name="searchTime" class="input">
          <a-range-picker allowClear
            v-model:value="formState['rangetimepicker']"
            :placeholder="['开始时间', '结束时间']"
            show-time
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </a-form-item>

        <a-space>
          <a-button type="primary" html-type="submit"> 搜索 </a-button>
          <a-button @click="handleReset"> 重置 </a-button>
          <a-button type="primary" @click="handleExport"> 导出 </a-button>
        </a-space>
      </a-form>
    </div>

    <!-- 统计信息区域 -->
    <div class="statistics-area" v-if="statistics.totalSearchCount > 0">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-card>
            <a-statistic
              title="总搜索次数"
              :value="statistics.totalSearchCount"
              :value-style="{ color: '#3f8600' }"
            >
              <template #prefix>
                <SearchOutlined />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic
              title="平均准确率"
              :value="statistics.avgAccuracy"
              :precision="1"
              suffix="%"
              :value-style="{ color: '#1890ff' }"
            >
              <template #prefix>
                <CheckCircleOutlined />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic
              title="平均精确率"
              :value="statistics.avgPrecision"
              :precision="1"
              suffix="%"
              :value-style="{ color: '#fa8c16' }"
            >
              <template #prefix>
                <AimOutlined />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic
              title="平均召回率"
              :value="statistics.avgRecall"
              :precision="1"
              suffix="%"
              :value-style="{ color: '#722ed1' }"
            >
              <template #prefix>
                <ReloadOutlined />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 中间内容区域 -->
    <div class="content-min-height">
      <!-- 表格区 -->
      <a-table 
        :columns="columns" 
        :data-source="tableData" 
        :pagination="false" 
        :scroll="{ x: 1500 }" 
        size="small"
        rowKey="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'clickedResult'">
            <a-tag :color="record.clickedResult ? 'green' : 'red'">
              {{ record.clickedResult ? '已点击' : '未点击' }}
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'accuracy'">
            <a-progress
              :percent="record.accuracy"
              :stroke-color="{
                '0%': '#108ee9',
                '100%': '#87d068',
              }"
              size="small"
            />
          </template>
          <template v-if="column.dataIndex === 'precision'">
            <a-progress
              :percent="record.precision"
              :stroke-color="{
                '0%': '#fa8c16',
                '100%': '#fa541c',
              }"
              size="small"
            />
          </template>
          <template v-if="column.dataIndex === 'recall'">
            <a-progress
              :percent="record.recall"
              :stroke-color="{
                '0%': '#722ed1',
                '100%': '#eb2f96',
              }"
              size="small"
            />
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a-space :size="2">
              <a-button type="link" @click="showDetailModal(record)">详情</a-button>
              <a-popconfirm
                title="确定要删除这条记录吗？"
                @confirm="handleDelete(record.id)"
                okText="确定"
                cancelText="取消"
              >
                <a-button type="link" danger>删除</a-button>
              </a-popconfirm>
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
    </div>
    <!-- 中间内容区域 -->
  </div>

  <!-- 详情弹窗 -->
  <a-modal
    v-model:open="detailVisible"
    title="搜索分析详情"
    @cancel="detailVisible = false"
    width="600px"
    :footer="null"
  >
    <a-descriptions :column="2" bordered>
      <a-descriptions-item label="用户名称">{{ currentDetail.userName }}</a-descriptions-item>
      <a-descriptions-item label="搜索时间">{{ currentDetail.searchTime }}</a-descriptions-item>
      <a-descriptions-item label="搜索关键词" :span="2">{{ currentDetail.keyword }}</a-descriptions-item>
      <a-descriptions-item label="搜索结果总数">{{ currentDetail.totalResults }}</a-descriptions-item>
      <a-descriptions-item label="点击位置">{{ currentDetail.clickedPosition || '-' }}</a-descriptions-item>
      <a-descriptions-item label="点击标题" :span="2">{{ currentDetail.clickedTitle || '-' }}</a-descriptions-item>
      <a-descriptions-item label="是否点击">
        <a-tag :color="currentDetail.clickedResult ? 'green' : 'red'">
          {{ currentDetail.clickedResult ? '已点击' : '未点击' }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="准确率">{{ currentDetail.accuracy }}%</a-descriptions-item>
      <a-descriptions-item label="精确率">{{ currentDetail.precision }}%</a-descriptions-item>
      <a-descriptions-item label="召回率">{{ currentDetail.recall }}%</a-descriptions-item>
    </a-descriptions>
  </a-modal>
</template>

<script>
import { reactive, defineComponent, ref } from 'vue'
import { 
  SearchOutlined, 
  CheckCircleOutlined, 
  AimOutlined, 
  ReloadOutlined,
  DownOutlined 
} from '@ant-design/icons-vue'
import { mapState } from 'pinia'
import { useUserStore } from '@/stores/user'

export default defineComponent({
  components: {
    SearchOutlined,
    CheckCircleOutlined,
    AimOutlined,
    ReloadOutlined,
    DownOutlined
  },
  props: {
    id: {
      type: [String, Number],
      default: undefined
    }
  },
  data() {
    const formState = reactive({
      keyword: '',
      userName: '',
      startTime: '',
      endTime: '',
      rangetimepicker: []
    })
    
    const data = reactive([])
    
    // 表格列定义
    const columns = [
      {
        title: '用户名称',
        dataIndex: 'userName',
        key: 'userName',
        width: 120,
        ellipsis: true
      },
      {
        title: '搜索关键词',
        dataIndex: 'keyword',
        key: 'keyword',
        width: 150,
        ellipsis: true
      },
      {
        title: '搜索时间',
        dataIndex: 'searchTime',
        key: 'searchTime',
        width: 160
      },
      {
        title: '结果总数',
        dataIndex: 'totalResults',
        key: 'totalResults',
        width: 90,
        align: 'center'
      },
      {
        title: '点击位置',
        dataIndex: 'clickedPosition',
        key: 'clickedPosition',
        width: 90,
        align: 'center',
        customRender: ({ text }) => text || '-'
      },
      {
        title: '点击标题',
        dataIndex: 'clickedTitle',
        key: 'clickedTitle',
        width: 200,
        ellipsis: true,
        customRender: ({ text }) => text || '-'
      },
      {
        title: '是否点击',
        dataIndex: 'clickedResult',
        key: 'clickedResult',
        width: 90,
        align: 'center'
      },
      {
        title: '准确率',
        dataIndex: 'accuracy',
        key: 'accuracy',
        width: 120,
        align: 'center'
      },
      {
        title: '精确率',
        dataIndex: 'precision',
        key: 'precision',
        width: 120,
        align: 'center'
      },
      {
        title: '召回率',
        dataIndex: 'recall',
        key: 'recall',
        width: 120,
        align: 'center'
      },
      {
        title: '操作',
        dataIndex: 'action',
        key: 'action',
        width: 120,
        align: 'center',
        fixed: 'right'
      }
    ]
    
    // 分页配置
    const pagination = reactive({
      current: 1,
      pageSize: 10,
      total: 0
    })
    
    // 表单布局配置
    const labelCol = reactive({ span: 8 })
    
    // 详情弹窗数据
    const detailVisible = ref(false)
    const currentDetail = reactive({
      id: '',
      userName: '',
      keyword: '',
      searchTime: '',
      totalResults: 0,
      clickedPosition: null,
      clickedTitle: '',
      clickedResult: false,
      accuracy: 0,
      precision: 0,
      recall: 0
    })
    
    // 统计数据
    const statistics = reactive({
      totalSearchCount: 0,
      avgAccuracy: 0,
      avgPrecision: 0,
      avgRecall: 0
    })
    
    return { 
      formState, 
      data,
      columns,
      pagination,
      labelCol,
      detailVisible,
      currentDetail,
      statistics
    }
  },
  computed: {
    ...mapState(useUserStore, ['tokenVal']),
    tableData: function () {
      return this.data
    },
    headers: function () {
      return { Authorization: 'xtoken ' + this.tokenVal }
    }
  },
  methods: {
    handleFinish() {
      // 处理时间范围选择器的数据
      if (this.formState.rangetimepicker && this.formState.rangetimepicker.length === 2) {
        this.formState.startTime = this.formState.rangetimepicker[0]
        this.formState.endTime = this.formState.rangetimepicker[1]
      } else {
        this.formState.startTime = ''
        this.formState.endTime = ''
      }
      this.query()
    },
    
    handleReset() {
      this.formState.keyword = ''
      this.formState.userName = ''
      this.formState.startTime = ''
      this.formState.endTime = ''
      this.formState.rangetimepicker = []
      this.query()
    },
    
    handleExport() {
      const params = {
        keyword: this.formState.keyword,
        userName: this.formState.userName,
        startTime: this.formState.startTime,
        endTime: this.formState.endTime
      }
      
      this.$http.request({
        url: this.$cfg.rootUrl() + '/personkit/search/analyze/export',
        method: 'post',
        data: params,
        responseType: 'blob'
      }).then(res => {
        // 创建下载链接
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `搜索分析数据_${new Date().toLocaleDateString()}.xlsx`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        this.$message.success('导出成功')
      }).catch(err => {
        this.$message.error('导出失败: ' + err)
      })
    },
    
    showDetailModal(record) {
      Object.assign(this.currentDetail, record)
      this.detailVisible = true
    },
    
    handleDelete(id) {
      this.$http.request({
        url: this.$cfg.rootUrl() + '/personkit/search/analyze/delete/' + id,
        method: 'delete'
      }).then(() => {
        this.$message.success('删除成功')
        this.query()
      }).catch(err => {
        this.$message.error('删除失败: ' + err)
      })
    },
    
    onPaginationChange(page) {
      this.pagination.current = page
      this.query()
    },
    
    query() {
      const params = {
        num: this.pagination.current,
        size: this.pagination.pageSize,
        query: {
          keyword: this.formState.keyword,
          userName: this.formState.userName,
          startTime: this.formState.startTime,
          endTime: this.formState.endTime
        }
      }
      
      this.$http.request({
        url: this.$cfg.rootUrl() + '/personkit/search/analyze/page',
        method: 'post',
        data: params
      }).then(res => {
        this.data.splice(0, this.data.length, ...res.data)
        this.pagination.total = res.total
        if (res.statistics) {
          Object.assign(this.statistics, res.statistics)
        }
      }).catch(err => {
        this.$message.error('查询失败: ' + err)
      })
    }
  },
  
  created() {
    this.query()
  }
})
</script>

<style scoped>
.page-content-wrapper {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 120px);
}

.search-area {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 6px;
}

.statistics-area {
  margin-bottom: 20px;
}

.content-min-height {
  min-height: 400px;
}

.input {
  margin-right: 10px;
  margin-bottom: 10px;
}

:deep(.ant-form-item) {
  margin-bottom: 10px;
}

:deep(.ant-card) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.ant-progress) {
  width: 80px;
}
</style>