<template>
  <div class="crawl-records-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <a-button @click="$router.back()" class="back-btn">
          <arrow-left-outlined />
          返回
        </a-button>
        <span class="page-title">{{ spiderName }} - 抓取记录</span>
      </div>
      <div class="header-actions">
        <a-popconfirm title="确定要清空所有抓取记录吗？" @confirm="clearAllRecords" okText="确定" cancelText="取消">
          <a-button danger>
            <delete-outlined />
            清空记录
          </a-button>
        </a-popconfirm>
        <a-button type="primary" @click="exportRecords">
          <export-outlined />
          导出数据
        </a-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="抓取时间" name="dateRange">
          <a-range-picker v-model:value="searchForm.dateRange" class="date-picker" />
        </a-form-item>
        <a-form-item label="抓取状态" name="status">
          <a-select v-model:value="searchForm.status" placeholder="请选择状态" allowClear class="status-select">
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="success">成功</a-select-option>
            <a-select-option value="failed">失败</a-select-option>
            <a-select-option value="partial">部分成功</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="数据类型" name="dataType">
          <a-select v-model:value="searchForm.dataType" placeholder="请选择数据类型" allowClear class="type-select">
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="article">文章</a-select-option>
            <a-select-option value="image">图片</a-select-option>
            <a-select-option value="video">视频</a-select-option>
            <a-select-option value="product">商品</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="关键词" name="keyword">
          <a-input v-model:value="searchForm.keyword" placeholder="请输入关键词" allowClear class="keyword-input" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            <search-outlined />
            搜索
          </a-button>
        </a-form-item>
        <a-form-item>
          <a-button @click="handleReset">
            <reload-outlined />
            重置
          </a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 统计信息 -->
    <div class="stats-area">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-statistic title="今日抓取" :value="todayCount" suffix="条" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="本周抓取" :value="weekCount" suffix="条" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="成功率" :value="successRate" suffix="%" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="总抓取量" :value="totalCount" suffix="条" />
        </a-col>
      </a-row>
    </div>

    <!-- 记录表格 -->
    <div class="table-container">
      <a-table 
        :columns="columns" 
        :data-source="recordsData" 
        :pagination="paginationConfig"
        :loading="loading"
        :scroll="tableScroll"
        rowKey="id"
        size="middle"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'dataCount'">
            <span class="data-count">
              <database-outlined />
              {{ record.dataCount }} 条
            </span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewDetail(record)">
                详情
              </a-button>
              <a-button type="link" size="small" @click="viewData(record)">
                查看数据
              </a-button>
              <a-popconfirm title="确定要删除这条记录吗？" @confirm="deleteRecord(record)" okText="确定" cancelText="取消">
                <a-button type="link" danger size="small">
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>


  </div>
</template>

<script>
import { defineComponent, reactive, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  ArrowLeftOutlined,
  SearchOutlined,
  ReloadOutlined,
  DeleteOutlined,
  ExportOutlined,
  DatabaseOutlined
} from '@ant-design/icons-vue'

export default defineComponent({
  name: 'CrawlRecords',
  components: {
    ArrowLeftOutlined,
    SearchOutlined,
    ReloadOutlined,
    DeleteOutlined,
    ExportOutlined,
    DatabaseOutlined
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    const spiderId = route.params.spiderId
    const spiderName = route.params.spiderName || '未知爬虫'

    // 搜索表单
    const searchForm = reactive({
      dateRange: [],
      status: '',
      dataType: '',
      keyword: ''
    })

    // 表格列配置
    const columns = [
      {
        title: '抓取时间',
        dataIndex: 'crawlTime',
        key: 'crawlTime',
        width: 160,
        sorter: true
      },
      {
        title: '数据类型',
        dataIndex: 'dataType',
        key: 'dataType',
        width: 100,
        filters: [
          { text: '文章', value: 'article' },
          { text: '图片', value: 'image' },
          { text: '视频', value: 'video' },
          { text: '商品', value: 'product' }
        ]
      },
      {
        title: '抓取状态',
        dataIndex: 'status',
        key: 'status',
        width: 100,
        filters: [
          { text: '成功', value: 'success' },
          { text: '失败', value: 'failed' },
          { text: '部分成功', value: 'partial' }
        ]
      },
      {
        title: '抓取数量',
        dataIndex: 'dataCount',
        key: 'dataCount',
        width: 100,
        sorter: true
      },
      {
        title: '耗时(秒)',
        dataIndex: 'duration',
        key: 'duration',
        width: 80,
        sorter: true
      },
      {
        title: '起始URL',
        dataIndex: 'startUrl',
        key: 'startUrl',
        width: 200,
        ellipsis: true,
        customCell: () => {
          return {
            style: {
              'max-width': '200px',
              'overflow': 'hidden',
              'text-overflow': 'ellipsis',
              'white-space': 'nowrap'
            }
          }
        }
      },
      {
        title: '操作',
        key: 'action',
        width: 150,
        fixed: 'right'
      }
    ]

    // 模拟数据
    const recordsData = ref([])
    const loading = ref(false)
    const showDetailModal = ref(false)
    const currentRecord = ref(null)
    
    // 移除滚动条 - 设置表格最大高度
    const tableScroll = {
      y: 'calc(100vh - 400px)'
    }

    // 统计数据
    const todayCount = ref(125)
    const weekCount = ref(856)
    const successRate = ref(92.5)
    const totalCount = ref(12580)

    // 分页配置
    const pagination = reactive({
      current: 1,
      pageSize: 20,
      total: 0,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: (total) => `共 ${total} 条记录`
    })

    const paginationConfig = computed(() => ({
      ...pagination,
      pageSizeOptions: ['10', '20', '50', '100']
    }))

    // 获取状态颜色
    const getStatusColor = (status) => {
      const colors = {
        success: 'success',
        failed: 'error',
        partial: 'warning'
      }
      return colors[status] || 'default'
    }

    // 获取状态文本
    const getStatusText = (status) => {
      const texts = {
        success: '成功',
        failed: '失败',
        partial: '部分成功'
      }
      return texts[status] || status
    }

    // 生成模拟数据
    const generateMockData = () => {
      const dataTypes = ['article', 'image', 'video', 'product']
      const dataTypeTexts = {
        article: '文章',
        image: '图片',
        video: '视频',
        product: '商品'
      }
      const statuses = ['success', 'failed', 'partial']
      
      const data = []
      for (let i = 1; i <= 50; i++) {
        const dataType = dataTypes[Math.floor(Math.random() * dataTypes.length)]
        const status = statuses[Math.floor(Math.random() * statuses.length)]
        data.push({
          id: `crawl_${i}`,
          crawlTime: `2024-01-${String(Math.floor(Math.random() * 28) + 1).padStart(2, '0')} ${String(Math.floor(Math.random() * 24)).padStart(2, '0')}:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}`,
          dataType: dataTypeTexts[dataType],
          status,
          dataCount: Math.floor(Math.random() * 1000) + 10,
          duration: Math.floor(Math.random() * 300) + 30,
          startUrl: `https://example.com/page${i}`,
          endUrl: `https://example.com/page${i + 1}`,
          errorMsg: status === 'failed' ? '网络连接超时，无法访问目标页面' : null,
          log: status === 'partial' ? '部分页面抓取成功，部分页面超时' : null
        })
      }
      return data
    }

    // 加载数据
    const loadData = () => {
      loading.value = true
      setTimeout(() => {
        const allData = generateMockData()
        recordsData.value = allData.slice(
          (pagination.current - 1) * pagination.pageSize,
          pagination.current * pagination.pageSize
        )
        pagination.total = allData.length
        loading.value = false
      }, 500)
    }

    // 搜索
    const handleSearch = () => {
      pagination.current = 1
      loadData()
      message.success('搜索完成')
    }

    // 重置
    const handleReset = () => {
      searchForm.dateRange = []
      searchForm.status = ''
      searchForm.dataType = ''
      searchForm.keyword = ''
      pagination.current = 1
      loadData()
      message.success('搜索条件已重置')
    }

    // 表格变化
    const handleTableChange = (pag, filters, sorter) => {
      pagination.current = pag.current
      pagination.pageSize = pag.pageSize
      loadData()
    }

    // 查看详情
    const viewDetail = (record) => {
      router.push(`/view/crawl_data/${spiderId}/${record.id}`)
    }

    // 查看数据
    const viewData = (record) => {
      router.push(`/view/crawl_data/${spiderId}`)
    }

    // 删除记录
    const deleteRecord = (record) => {
      const index = recordsData.value.findIndex(item => item.id === record.id)
      if (index > -1) {
        recordsData.value.splice(index, 1)
        pagination.total--
        message.success('记录删除成功')
      }
    }

    // 清空所有记录
    const clearAllRecords = () => {
      Modal.confirm({
        title: '确认清空',
        content: '确定要清空所有抓取记录吗？此操作不可恢复。',
        onOk() {
          recordsData.value = []
          pagination.total = 0
          message.success('所有记录已清空')
        }
      })
    }

    // 导出记录
    const exportRecords = () => {
      message.info('正在导出抓取记录...')
      setTimeout(() => {
        message.success('抓取记录导出成功')
      }, 1000)
    }

    // 初始化
    loadData()

    return {
      spiderName,
      searchForm,
      columns,
      recordsData,
      loading,
      showDetailModal,
      currentRecord,
      tableScroll,
      todayCount,
      weekCount,
      successRate,
      totalCount,
      paginationConfig,
      getStatusColor,
      getStatusText,
      handleSearch,
      handleReset,
      handleTableChange,
      viewDetail,
      viewData,
      deleteRecord,
      clearAllRecords,
      exportRecords
    }
  }
})
</script>

<style scoped>
.crawl-records-container {
  background: #fff;
  min-height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 4px;
}

.page-title {
  font-size: 16px;
  font-weight: 500;
  color: #262626;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.search-area {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.search-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.date-picker {
  width: 240px;
}

.status-select,
.type-select {
  width: 120px;
}

.keyword-input {
  width: 200px;
}

.stats-area {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.table-container {
  padding: 16px 24px;
}

.data-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.error-msg {
  color: #ff4d4f;
  font-size: 12px;
  max-height: 60px;
  overflow-y: auto;
}

.crawl-log {
  font-size: 12px;
  color: #595959;
  max-height: 100px;
  overflow-y: auto;
  background-color: #fafafa;
  padding: 8px;
  border-radius: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .header-actions {
    flex-wrap: wrap;
  }
  
  .search-form {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .date-picker,
  .status-select,
  .type-select,
  .keyword-input {
    width: 100% !important;
  }
  
  .stats-area {
    padding: 16px;
  }
  
  .table-container {
    padding: 16px;
  }
}
</style>