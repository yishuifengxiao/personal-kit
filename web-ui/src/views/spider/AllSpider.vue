<template>
  <div class="spider-container">
    <!-- 搜索条件区域 -->
    <div class="search-header">
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="名称" name="name">
          <a-input v-model:value="searchForm.name" placeholder="请输入爬虫名称" allowClear class="search-input" />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-select v-model:value="searchForm.status" placeholder="请选择状态" allowClear class="search-select" style="width: 120px">
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="running">运行中</a-select-option>
            <a-select-option value="stopped">停止</a-select-option>
            <a-select-option value="paused">暂停</a-select-option>
            <a-select-option value="error">异常</a-select-option>
            <a-select-option value="completed">完成</a-select-option>
            <a-select-option value="failed">失败</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch" class="search-btn">确认</a-button>
        </a-form-item>
        <a-form-item>
          <a-button @click="handleReset" class="reset-btn">重置</a-button>
        </a-form-item>
      </a-form>
      <a-button type="primary" @click="showAddSpiderModal = true" class="add-btn-float">
        <plus-outlined />
        添加爬虫
      </a-button>
    </div>

    <!-- 爬虫卡片网格 -->
    <div class="content-area-compact">
      <a-row :gutter="[16, 16]" class="spider-grid-compact">
        <a-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-for="spider in spiderData" :key="spider.id">
          <a-card hoverable class="spider-card-compact">
            <template #cover>
              <div class="card-cover-compact">
                <div class="spider-icon-container">
                  <a-avatar :size="48" class="spider-avatar" :style="{ backgroundColor: getStatusColor(spider.status) }">
                    <component :is="getStatusIcon(spider.status)" />
                  </a-avatar>
                </div>
                <!-- 状态标签 -->
                <div class="status-badge" :style="{ backgroundColor: getStatusColor(spider.status) }">
                  {{ getStatusText(spider.status) }}
                </div>
                <!-- 操作按钮覆盖层 -->
                <div class="card-actions-overlay">
                  <a-dropdown :trigger="['click']" placement="bottomRight">
                    <template #overlay>
                      <a-menu @click="({ key }) => handleSpiderAction(key, spider)">
                        <a-menu-item key="pause" v-if="spider.status === 'running'">
                          <pause-circle-outlined />
                          暂停
                        </a-menu-item>
                        <a-menu-item key="resume" v-if="spider.status === 'paused'">
                          <play-circle-outlined />
                          继续
                        </a-menu-item>
                        <a-menu-item key="stop" v-if="['running', 'paused'].includes(spider.status)">
                          <stop-outlined />
                          停止
                        </a-menu-item>
                        <a-menu-item key="restart">
                          <reload-outlined />
                          重启
                        </a-menu-item>
                        <a-menu-divider />
                        <a-menu-item key="records">
                          <history-outlined />
                          操作记录
                        </a-menu-item>
                        <a-menu-item key="crawlRecords">
                          <file-text-outlined />
                          抓取记录
                        </a-menu-item>
                        <a-menu-item key="logs">
                          <file-text-outlined />
                          运行日志
                        </a-menu-item>
                        <a-menu-item key="data">
                          <database-outlined />
                          抓取数据
                        </a-menu-item>
                        <a-menu-divider />
                        <a-menu-item key="edit">
                          <edit-outlined />
                          编辑
                        </a-menu-item>
                        <a-menu-item key="delete">
                          <delete-outlined />
                          删除
                        </a-menu-item>
                      </a-menu>
                    </template>
                    <a-button type="primary" size="small" class="action-menu-btn">
                      <setting-outlined />
                      操作
                    </a-button>
                  </a-dropdown>
                </div>
              </div>
            </template>

            <a-card-meta>
              <template #title>
                <div class="card-title-compact" :title="spider.name">{{ spider.name }}</div>
              </template>
              <template #description>
                <div class="card-description-compact">
                  <div class="spider-desc">{{ spider.description }}</div>
                  <div class="spider-stats">
                    <div class="stat-item">
                      <clock-circle-outlined />
                      <span>{{ formatTime(spider.lastRunTime) }}</span>
                    </div>
                    <div class="stat-item">
                      <database-outlined />
                      <span>{{ spider.dataCount }} 条</span>
                    </div>
                  </div>
                </div>
              </template>
            </a-card-meta>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 分页 -->
    <div class="pagination-container-compact">
      <a-pagination 
        v-model:current="pagination.current" 
        :total="pagination.total" 
        :show-total="(total) => `共 ${total} 个爬虫`"
        @change="onPaginationChange"
        size="small"
        class="pagination-compact" />
    </div>

    <!-- 操作记录、抓取记录、添加爬虫、编辑功能已改为独立页面 -->


  </div>
</template>

<script>
import { defineComponent, reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  PlusOutlined,
  SettingOutlined,
  EditOutlined,
  DeleteOutlined,
  PlayCircleOutlined,
  PauseCircleOutlined,
  StopOutlined,
  ReloadOutlined,
  HistoryOutlined,
  FileTextOutlined,
  DatabaseOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ExclamationCircleOutlined,
  LoadingOutlined,
  DownloadOutlined
} from '@ant-design/icons-vue'

export default defineComponent({
  name: 'AllSpider',
  components: {
    PlusOutlined,
    SettingOutlined,
    EditOutlined,
    DeleteOutlined,
    PlayCircleOutlined,
    PauseCircleOutlined,
    StopOutlined,
    ReloadOutlined,
    HistoryOutlined,
    FileTextOutlined,
    DatabaseOutlined,
    ClockCircleOutlined,
    DownloadOutlined
  },
  setup() {
    // 路由
    const router = useRouter()
    
    // 搜索表单
    const searchForm = reactive({
      name: '',
      status: ''
    })

    // 分页配置
    const pagination = reactive({
      current: 1,
      total: 100,
      pageSize: 12
    })

    // 表单数据 - 已移除，改为独立页面

    // 弹窗状态 - 已移除，改为独立页面

    // 当前操作的数据
    const currentSpider = ref(null)
    const currentDataDetail = ref({})

    // 模拟爬虫数据
    const spiderData = ref([
      {
        id: 1,
        name: '新闻网站爬虫',
        description: '抓取各大新闻网站的最新资讯',
        status: 'running',
        lastRunTime: '2024-01-15 14:30:00',
        dataCount: 1250
      },
      {
        id: 2,
        name: '电商商品爬虫',
        description: '抓取电商平台商品信息和价格',
        status: 'stopped',
        lastRunTime: '2024-01-14 16:45:00',
        dataCount: 3420
      },
      {
        id: 3,
        name: '社交媒体爬虫',
        description: '抓取社交媒体平台的公开数据',
        status: 'paused',
        lastRunTime: '2024-01-15 12:20:00',
        dataCount: 890
      },
      {
        id: 4,
        name: '股票数据爬虫',
        description: '实时抓取股票行情数据',
        status: 'error',
        lastRunTime: '2024-01-15 10:15:00',
        dataCount: 560
      },
      {
        id: 5,
        name: '招聘网站爬虫',
        description: '抓取招聘信息和企业数据',
        status: 'completed',
        lastRunTime: '2024-01-13 09:30:00',
        dataCount: 2100
      },
      {
        id: 6,
        name: '天气数据爬虫',
        description: '抓取全国各地天气信息',
        status: 'failed',
        lastRunTime: '2024-01-12 18:00:00',
        dataCount: 0
      }
    ])

    // 操作记录数据
    const operationRecords = ref([
      {
        time: '2024-01-15 14:30:00',
        operator: 'admin',
        action: '启动爬虫',
        result: '成功'
      },
      {
        time: '2024-01-15 14:25:00',
        operator: 'admin',
        action: '暂停爬虫',
        result: '成功'
      },
      {
        time: '2024-01-15 14:20:00',
        operator: 'user1',
        action: '编辑爬虫配置',
        result: '成功'
      }
    ])

    // 爬虫数据列表
    const spiderDataList = ref([
      {
        id: 'data_001',
        crawlTime: '2024-01-15 14:30:00',
        sourceUrl: 'https://example.com/news/123',
        status: 'success',
        content: { title: '新闻标题', content: '新闻内容...' }
      },
      {
        id: 'data_002',
        crawlTime: '2024-01-15 14:29:00',
        sourceUrl: 'https://example.com/news/124',
        status: 'failed',
        content: { error: '网络超时' }
      }
    ])

    // 数据分页
    const dataPagination = reactive({
      current: 1,
      total: 50,
      pageSize: 10
    })

    // 获取状态颜色
    const getStatusColor = (status) => {
      const colorMap = {
        running: '#52c41a',
        stopped: '#8c8c8c',
        paused: '#faad14',
        error: '#ff4d4f',
        completed: '#1890ff',
        failed: '#ff4d4f'
      }
      return colorMap[status] || '#8c8c8c'
    }

    // 获取状态图标
    const getStatusIcon = (status) => {
      const iconMap = {
        running: LoadingOutlined,
        stopped: StopOutlined,
        paused: PauseCircleOutlined,
        error: ExclamationCircleOutlined,
        completed: CheckCircleOutlined,
        failed: CloseCircleOutlined
      }
      return iconMap[status] || StopOutlined
    }

    // 获取状态文本
    const getStatusText = (status) => {
      const textMap = {
        running: '运行中',
        stopped: '停止',
        paused: '暂停',
        error: '异常',
        completed: '完成',
        failed: '失败'
      }
      return textMap[status] || status
    }

    // 获取数据状态颜色
    const getDataStatusColor = (status) => {
      const colorMap = {
        success: 'success',
        failed: 'error',
        pending: 'warning'
      }
      return colorMap[status] || 'default'
    }

    // 获取数据状态文本
    const getDataStatusText = (status) => {
      const textMap = {
        success: '成功',
        failed: '失败',
        pending: '处理中'
      }
      return textMap[status] || status
    }

    // 格式化时间
    const formatTime = (time) => {
      if (!time) return '-'
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      const hours = Math.floor(diff / (1000 * 60 * 60))
      const minutes = Math.floor(diff / (1000 * 60))
      
      if (days > 0) return `${days}天前`
      if (hours > 0) return `${hours}小时前`
      if (minutes > 0) return `${minutes}分钟前`
      return '刚刚'
    }

    // 搜索功能
    const handleSearch = () => {
      pagination.current = 1
      message.success('搜索完成')
      // 这里可以实现实际的搜索逻辑
    }

    // 重置搜索
    const handleReset = () => {
      searchForm.name = ''
      searchForm.status = ''
      pagination.current = 1
      message.success('搜索条件已重置')
    }

    // 爬虫操作处理
    const handleSpiderAction = (action, spider) => {
      currentSpider.value = spider
      switch (action) {
        case 'pause':
          spider.status = 'paused'
          message.success(`爬虫 ${spider.name} 已暂停`)
          break
        case 'resume':
          spider.status = 'running'
          message.success(`爬虫 ${spider.name} 已继续运行`)
          break
        case 'stop':
          spider.status = 'stopped'
          message.success(`爬虫 ${spider.name} 已停止`)
          break
        case 'restart':
          spider.status = 'running'
          message.success(`爬虫 ${spider.name} 已重启`)
          break
        case 'records':
            router.push({
              path: '/view/spider_records',
              query: { spiderId: spider.id, spiderName: spider.name }
            })
            break
          case 'crawlRecords':
            router.push({
              path: `/view/crawl_records/${spider.id}`,
              query: { spiderName: spider.name }
            })
            break
        case 'logs':
          message.info('运行日志功能开发中')
          break
        case 'data':
          router.push({
            path: `/view/crawl_data/${spider.id}`,
            query: { spiderName: spider.name }
          })
          break
        case 'edit':
          router.push({
            path: '/view/edit_spider',
            query: { id: spider.id }
          })
          break
        case 'delete':
          Modal.confirm({
            title: '确认删除',
            content: `确定要删除爬虫 ${spider.name} 吗？`,
            onOk() {
              const index = spiderData.value.findIndex(item => item.id === spider.id)
              if (index > -1) {
                spiderData.value.splice(index, 1)
                message.success('爬虫已删除')
              }
            }
          })
          break
      }
    }

    // 添加爬虫 - 已改为独立页面
    const handleAddSpider = () => {
      router.push('/view/add_spider')
    }

    // 编辑爬虫 - 已改为独立页面，通过路由跳转实现

    // 查看数据详情 - 已改为独立页面

    // 数据操作相关 - 已改为独立页面

    // 分页变化
    const onPaginationChange = (page) => {
      pagination.current = page
    }

    // 表格列配置 - 已移除，改为独立页面

    return {
      router,
      searchForm,
      pagination,


      spiderData,
      operationRecords,
      spiderDataList,
      dataPagination,
      currentDataDetail,
      getStatusColor,
      getStatusIcon,
      getStatusText,
      getDataStatusColor,
      getDataStatusText,
      formatTime,
      handleSearch,
      handleReset,
      handleSpiderAction,
      handleAddSpider,
      onPaginationChange,
    }
  }
})
</script>

<style scoped>
.spider-container {
  background: #fff;
  min-height: 100%;
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.search-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.search-input {
  width: 200px;
}

.search-select {
  width: 120px;
}

.search-btn,
.reset-btn {
  min-width: 60px;
}

.add-btn-float {
  align-self: center;
}

.content-area-compact {
  background: white;
  padding: 24px;
  min-height: calc(100vh - 200px);
  overflow-y: auto;
}

.spider-grid-compact {
  margin: 0 !important;
}

.spider-card-compact {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #e8e8e8;
  position: relative;
}

.spider-card-compact:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-color: #1890ff;
}

.card-cover-compact {
  height: 80px;
  overflow: hidden;
  position: relative;
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.spider-icon-container {
  display: flex;
  align-items: center;
  justify-content: center;
}

.spider-avatar {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.status-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  color: white;
  font-weight: 500;
}

.card-actions-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.spider-card-compact:hover .card-actions-overlay {
  opacity: 1;
}

.action-menu-btn {
  min-width: 80px;
}

.card-title-compact {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.card-description-compact {
  font-size: 12px;
  color: #8c8c8c;
}

.spider-desc {
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  height: 32px;
}

.spider-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #8c8c8c;
}

.pagination-container-compact {
  display: flex;
  justify-content: flex-end;
  padding: 16px 24px;
  background: white;
  border-top: 1px solid #f0f0f0;
}

.data-modal-header {
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-end;
}

.data-detail-content {
  max-height: 400px;
  overflow-y: auto;
}

.data-content-section {
  margin-top: 16px;
}

.section-title {
  font-weight: 500;
  margin-bottom: 8px;
  color: #262626;
}

.data-content {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.5;
}

.data-content pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .search-form {
    width: 100%;
  }
  
  .search-input,
  .search-select {
    width: 100% !important;
  }
  
  .add-btn-float {
    align-self: flex-end;
  }
  
  .content-area-compact {
    padding: 16px;
  }
  
  .spider-card-compact {
    height: 180px;
  }
  
  .card-cover-compact {
    height: 70px;
  }
}
</style>