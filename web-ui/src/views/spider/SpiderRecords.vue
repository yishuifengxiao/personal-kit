<template>
  <div class="spider-records-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <a-button @click="$router.back()" class="back-btn">
          <arrow-left-outlined />
          返回
        </a-button>
        <span class="page-title">{{ spiderName }} - 操作记录</span>
      </div>
      <div class="header-actions">
        <a-popconfirm title="确定要清空所有操作记录吗？" @confirm="clearAllRecords" okText="确定" cancelText="取消">
          <a-button danger>
            <delete-outlined />
            清空记录
          </a-button>
        </a-popconfirm>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="操作时间" name="dateRange">
          <a-range-picker v-model:value="searchForm.dateRange" class="date-picker" />
        </a-form-item>
        <a-form-item label="操作人" name="operator">
          <a-select v-model:value="searchForm.operator" placeholder="请选择操作人" allowClear class="operator-select">
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="admin">admin</a-select-option>
            <a-select-option value="user1">user1</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="操作类型" name="action">
          <a-select v-model:value="searchForm.action" placeholder="请选择操作类型" allowClear class="action-select">
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="start">启动</a-select-option>
            <a-select-option value="stop">停止</a-select-option>
            <a-select-option value="pause">暂停</a-select-option>
            <a-select-option value="edit">编辑</a-select-option>
            <a-select-option value="delete">删除</a-select-option>
          </a-select>
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

    <!-- 记录表格 -->
    <div class="table-container">
      <a-table 
        :columns="columns" 
        :data-source="recordsData" 
        :pagination="paginationConfig"
        :loading="loading"
        rowKey="id"
        size="middle"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'result'">
            <a-tag :color="record.result === '成功' ? 'success' : 'error'">
              {{ record.result }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewDetail(record)">
                详情
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

    <!-- 记录详情弹窗 -->
    <a-modal v-model:open="showDetailModal" title="操作详情" width="600px" @cancel="showDetailModal = false">
      <a-descriptions :column="1" size="small" bordered v-if="currentRecord">
        <a-descriptions-item label="记录ID">{{ currentRecord.id }}</a-descriptions-item>
        <a-descriptions-item label="操作时间">{{ currentRecord.time }}</a-descriptions-item>
        <a-descriptions-item label="操作人">{{ currentRecord.operator }}</a-descriptions-item>
        <a-descriptions-item label="操作类型">{{ currentRecord.action }}</a-descriptions-item>
        <a-descriptions-item label="操作内容">{{ currentRecord.content }}</a-descriptions-item>
        <a-descriptions-item label="操作结果">
          <a-tag :color="currentRecord.result === '成功' ? 'success' : 'error'">
            {{ currentRecord.result }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="IP地址" v-if="currentRecord.ip">{{ currentRecord.ip }}</a-descriptions-item>
        <a-descriptions-item label="用户代理" v-if="currentRecord.userAgent">
          <div class="user-agent">{{ currentRecord.userAgent }}</div>
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
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
  DeleteOutlined
} from '@ant-design/icons-vue'

export default defineComponent({
  name: 'SpiderRecords',
  components: {
    ArrowLeftOutlined,
    SearchOutlined,
    ReloadOutlined,
    DeleteOutlined
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    const spiderId = route.params.spiderId
    const spiderName = route.params.spiderName || '未知爬虫'

    // 搜索表单
    const searchForm = reactive({
      dateRange: [],
      operator: '',
      action: ''
    })

    // 表格列配置
    const columns = [
      {
        title: '操作时间',
        dataIndex: 'time',
        key: 'time',
        width: 160,
        sorter: true
      },
      {
        title: '操作人',
        dataIndex: 'operator',
        key: 'operator',
        width: 120,
        filters: [
          { text: 'admin', value: 'admin' },
          { text: 'user1', value: 'user1' }
        ]
      },
      {
        title: '操作类型',
        dataIndex: 'action',
        key: 'action',
        width: 100,
        filters: [
          { text: '启动', value: 'start' },
          { text: '停止', value: 'stop' },
          { text: '暂停', value: 'pause' },
          { text: '编辑', value: 'edit' },
          { text: '删除', value: 'delete' }
        ]
      },
      {
        title: '操作内容',
        dataIndex: 'content',
        key: 'content',
        ellipsis: true
      },
      {
        title: '操作结果',
        dataIndex: 'result',
        key: 'result',
        width: 80
      },
      {
        title: '操作',
        key: 'action',
        width: 120,
        fixed: 'right'
      }
    ]

    // 模拟数据
    const recordsData = ref([])
    const loading = ref(false)
    const showDetailModal = ref(false)
    const currentRecord = ref(null)

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

    // 生成模拟数据
    const generateMockData = () => {
      const actions = ['start', 'stop', 'pause', 'edit', 'delete']
      const actionTexts = {
        start: '启动爬虫',
        stop: '停止爬虫',
        pause: '暂停爬虫',
        edit: '编辑爬虫配置',
        delete: '删除爬虫'
      }
      const operators = ['admin', 'user1', 'user2']
      
      const data = []
      for (let i = 1; i <= 50; i++) {
        const action = actions[Math.floor(Math.random() * actions.length)]
        const operator = operators[Math.floor(Math.random() * operators.length)]
        data.push({
          id: `record_${i}`,
          time: `2024-01-${String(Math.floor(Math.random() * 28) + 1).padStart(2, '0')} ${String(Math.floor(Math.random() * 24)).padStart(2, '0')}:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}`,
          operator,
          action,
          content: `${actionTexts[action]} - ${operator}`,
          result: Math.random() > 0.1 ? '成功' : '失败',
          ip: `192.168.${Math.floor(Math.random() * 255)}.${Math.floor(Math.random() * 255)}`,
          userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
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
      searchForm.operator = ''
      searchForm.action = ''
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
      currentRecord.value = record
      showDetailModal.value = true
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
        content: '确定要清空所有操作记录吗？此操作不可恢复。',
        onOk() {
          recordsData.value = []
          pagination.total = 0
          message.success('所有记录已清空')
        }
      })
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
      paginationConfig,
      handleSearch,
      handleReset,
      handleTableChange,
      viewDetail,
      deleteRecord,
      clearAllRecords
    }
  }
})
</script>

<style scoped>
.spider-records-container {
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

.operator-select,
.action-select {
  width: 120px;
}

.table-container {
  padding: 16px 24px;
}

.user-agent {
  max-width: 300px;
  word-break: break-all;
  font-size: 12px;
  color: #8c8c8c;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .search-form {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .date-picker,
  .operator-select,
  .action-select {
    width: 100% !important;
  }
  
  .table-container {
    padding: 16px;
  }
}
</style>