<template>
  <div class="sync-records-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>同步记录</h2>
      <a-button @click="goBack">
        <LeftOutlined />
        返回
      </a-button>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-area">
      <a-form layout="inline">
        <a-form-item label="数据源">
          <a-select v-model:value="searchForm.dataSource" placeholder="请选择数据源" style="width: 150px">
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="api-import">API导入</a-select-option>
            <a-select-option value="manual">手动添加</a-select-option>
            <a-select-option value="third-party">第三方</a-select-option>
            <a-select-option value="crawler">爬虫</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="同步状态">
          <a-select v-model:value="searchForm.syncStatus" placeholder="请选择状态" style="width: 120px">
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="success">成功</a-select-option>
            <a-select-option value="failed">失败</a-select-option>
            <a-select-option value="syncing">同步中</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="时间范围">
          <a-range-picker v-model:value="searchForm.dateRange" style="width: 240px" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            <SearchOutlined />
            搜索
          </a-button>
        </a-form-item>
        <a-form-item>
          <a-button @click="handleReset">
            <ClearOutlined />
            重置
          </a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-card>
            <div class="stat-item">
              <div class="stat-label">总同步次数</div>
              <div class="stat-value">{{ statistics.total }}</div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <div class="stat-item">
              <div class="stat-label">成功次数</div>
              <div class="stat-value success">{{ statistics.success }}</div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <div class="stat-item">
              <div class="stat-label">失败次数</div>
              <div class="stat-value error">{{ statistics.failed }}</div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <div class="stat-item">
              <div class="stat-label">成功率</div>
              <div class="stat-value">{{ statistics.successRate }}%</div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 同步记录表格 -->
    <div class="table-container">
      <a-table
        :columns="columns"
        :data-source="syncRecords"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'syncStatus'">
            <a-tag :color="getSyncStatusColor(record.syncStatus)">
              {{ getSyncStatusText(record.syncStatus) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewDetails(record)">
                详情
              </a-button>
              <a-button 
                type="link" 
                size="small" 
                @click="retrySync(record)"
                :disabled="record.syncStatus !== 'failed'"
              >
                重试
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script>
import { message } from 'ant-design-vue'
import {
  LeftOutlined,
  SearchOutlined,
  ClearOutlined
} from '@ant-design/icons-vue'

export default {
  name: 'SyncRecords',
  components: {
    LeftOutlined,
    SearchOutlined,
    ClearOutlined
  },
  data() {
    return {
      loading: false,
      searchForm: {
        dataSource: '',
        syncStatus: '',
        dateRange: null
      },
      statistics: {
        total: 0,
        success: 0,
        failed: 0,
        successRate: 0
      },
      syncRecords: [],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: total => `共 ${total} 条记录`
      },
      columns: [
        {
          title: 'ID',
          dataIndex: 'id',
          key: 'id',
          width: 80
        },
        {
          title: '数据标题',
          dataIndex: 'title',
          key: 'title',
          ellipsis: true
        },
        {
          title: '数据源',
          dataIndex: 'dataSource',
          key: 'dataSource',
          width: 120,
          customRender: ({ text }) => {
            const sourceMap = {
              'api-import': 'API导入',
              'manual': '手动添加',
              'third-party': '第三方',
              'crawler': '爬虫'
            }
            return sourceMap[text] || text
          }
        },
        {
          title: '同步状态',
          dataIndex: 'syncStatus',
          key: 'syncStatus',
          width: 100
        },
        {
          title: '开始时间',
          dataIndex: 'startTime',
          key: 'startTime',
          width: 180
        },
        {
          title: '结束时间',
          dataIndex: 'endTime',
          key: 'endTime',
          width: 180
        },
        {
          title: '耗时',
          dataIndex: 'duration',
          key: 'duration',
          width: 100
        },
        {
          title: '错误信息',
          dataIndex: 'errorMessage',
          key: 'errorMessage',
          ellipsis: true
        },
        {
          title: '操作',
          key: 'action',
          width: 120,
          fixed: 'right'
        }
      ]
    }
  },
  mounted() {
    this.loadSyncRecords()
  },
  methods: {
    goBack() {
      this.$router.push('/view/my_search')
    },

    handleSearch() {
      this.pagination.current = 1
      this.loadSyncRecords()
    },

    handleReset() {
      this.searchForm = {
        dataSource: '',
        syncStatus: '',
        dateRange: null
      }
      this.handleSearch()
    },

    handleTableChange(pagination) {
      this.pagination = pagination
      this.loadSyncRecords()
    },

    getSyncStatusColor(status) {
      const colors = {
        'success': 'green',
        'failed': 'red',
        'syncing': 'blue'
      }
      return colors[status] || 'default'
    },

    getSyncStatusText(status) {
      const texts = {
        'success': '成功',
        'failed': '失败',
        'syncing': '同步中'
      }
      return texts[status] || status
    },

    viewDetails(record) {
      message.info(`查看同步详情: ${record.title}`)
      // 这里可以跳转到详细的同步日志页面
    },

    retrySync(record) {
      message.loading(`正在重试同步: ${record.title}`, 0)
      setTimeout(() => {
        message.destroy()
        // 模拟重试成功
        const success = Math.random() > 0.3
        if (success) {
          record.syncStatus = 'success'
          record.endTime = new Date().toLocaleString('zh-CN')
          record.errorMessage = ''
          message.success(`重试成功: ${record.title}`)
        } else {
          record.syncStatus = 'failed'
          record.errorMessage = '网络超时，请稍后重试'
          message.error(`重试失败: ${record.title}`)
        }
        this.updateStatistics()
      }, 2000)
    },

    updateStatistics() {
      const total = this.syncRecords.length
      const success = this.syncRecords.filter(r => r.syncStatus === 'success').length
      const failed = this.syncRecords.filter(r => r.syncStatus === 'failed').length
      
      this.statistics = {
        total,
        success,
        failed,
        successRate: total > 0 ? Math.round((success / total) * 100) : 0
      }
    },

    loadSyncRecords() {
      this.loading = true
      
      // 模拟API调用延迟
      setTimeout(() => {
        // 模拟同步记录数据
        const mockRecords = Array.from({ length: 25 }, (_, i) => {
          const status = ['success', 'failed', 'syncing'][Math.floor(Math.random() * 3)]
          const dataSources = ['api-import', 'manual', 'third-party', 'crawler']
          const startTime = new Date(Date.now() - Math.random() * 86400000 * 7)
          const duration = Math.floor(Math.random() * 300) + 10 // 10-310秒
          
          return {
            id: i + 1,
            title: `数据标题${i + 1}`,
            dataSource: dataSources[Math.floor(Math.random() * dataSources.length)],
            syncStatus: status,
            startTime: startTime.toLocaleString('zh-CN'),
            endTime: status === 'syncing' ? '-' : new Date(startTime.getTime() + duration * 1000).toLocaleString('zh-CN'),
            duration: status === 'syncing' ? '-' : `${Math.floor(duration / 60)}分${duration % 60}秒`,
            errorMessage: status === 'failed' ? ['网络超时', '数据源不可用', '认证失败', '数据格式错误'][Math.floor(Math.random() * 4)] : ''
          }
        })

        // 应用筛选条件
        let filteredRecords = mockRecords
        
        if (this.searchForm.dataSource) {
          filteredRecords = filteredRecords.filter(r => r.dataSource === this.searchForm.dataSource)
        }
        
        if (this.searchForm.syncStatus) {
          filteredRecords = filteredRecords.filter(r => r.syncStatus === this.searchForm.syncStatus)
        }
        
        if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
          const [startDate, endDate] = this.searchForm.dateRange
          filteredRecords = filteredRecords.filter(r => {
            const recordDate = new Date(r.startTime)
            return recordDate >= startDate && recordDate <= endDate
          })
        }

        this.syncRecords = filteredRecords
        this.pagination.total = filteredRecords.length
        this.updateStatistics()
        this.loading = false
      }, 500)
    }
  }
}
</script>

<style scoped>
.sync-records-page {
  padding: 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 64px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: #fff;
  padding: 16px 24px;
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

.page-header h2 {
  margin: 0;
  color: #262626;
  font-size: 20px;
  font-weight: 500;
}

.search-area {
  background: #fff;
  padding: 24px;
  margin-bottom: 24px;
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

.stat-cards {
  margin-bottom: 24px;
}

.stat-item {
  text-align: center;
  padding: 16px;
}

.stat-label {
  color: #8c8c8c;
  font-size: 14px;
  margin-bottom: 8px;
}

.stat-value {
  color: #262626;
  font-size: 28px;
  font-weight: 600;
}

.stat-value.success {
  color: #52c41a;
}

.stat-value.error {
  color: #ff4d4f;
}

.table-container {
  background: #fff;
  padding: 24px;
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}
</style>