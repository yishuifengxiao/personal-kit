<template>
  <div class="crawl-data-detail-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <a-button @click="$router.back()" class="back-btn">
          <arrow-left-outlined />
          返回
        </a-button>
        <span class="page-title">抓取数据详情</span>
      </div>
      <div class="header-actions">
        <a-button type="primary" @click="exportData">
          <export-outlined />
          导出数据
        </a-button>
      </div>
    </div>

    <!-- 基本信息 -->
    <div class="basic-info">
      <a-descriptions :column="2" size="small" bordered>
        <a-descriptions-item label="记录ID">{{ recordId }}</a-descriptions-item>
        <a-descriptions-item label="抓取时间">{{ recordInfo.crawlTime }}</a-descriptions-item>
        <a-descriptions-item label="抓取状态">
          <a-tag :color="getStatusColor(recordInfo.status)">
            {{ getStatusText(recordInfo.status) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="数据类型">{{ recordInfo.dataType }}</a-descriptions-item>
        <a-descriptions-item label="抓取数量">{{ recordInfo.dataCount }} 条</a-descriptions-item>
        <a-descriptions-item label="耗时">{{ recordInfo.duration }} 秒</a-descriptions-item>
        <a-descriptions-item label="起始URL" :span="2">{{ recordInfo.startUrl }}</a-descriptions-item>
        <a-descriptions-item label="结束URL" :span="2">{{ recordInfo.endUrl }}</a-descriptions-item>
        <a-descriptions-item label="错误信息" :span="2" v-if="recordInfo.errorMsg">
          <div class="error-msg">{{ recordInfo.errorMsg }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="抓取日志" :span="2" v-if="recordInfo.log">
          <div class="crawl-log">{{ recordInfo.log }}</div>
        </a-descriptions-item>
      </a-descriptions>
    </div>

    <!-- 数据列表 -->
    <div class="data-list">
      <div class="list-header">
        <h3>抓取数据列表</h3>
        <span class="data-count">共 {{ dataList.length }} 条数据</span>
      </div>
      
      <a-table 
        :columns="columns" 
        :data-source="dataList" 
        :pagination="paginationConfig"
        :loading="loading"
        rowKey="id"
        size="middle"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'url'">
            <a-tooltip :title="record.url">
              <span class="url-text">{{ record.url }}</span>
            </a-tooltip>
          </template>
          <template v-else-if="column.key === 'content'">
            <a-tooltip :title="JSON.stringify(record.content, null, 2)">
              <span class="content-text">{{ formatContent(record.content) }}</span>
            </a-tooltip>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewDataDetail(record)">
                查看详情
              </a-button>
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
import { message } from 'ant-design-vue'
import {
  ArrowLeftOutlined,
  ExportOutlined
} from '@ant-design/icons-vue'

export default defineComponent({
  name: 'CrawlDataDetail',
  components: {
    ArrowLeftOutlined,
    ExportOutlined
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    const spiderId = route.params.spiderId
    const recordId = route.params.recordId

    // 记录信息
    const recordInfo = ref({
      crawlTime: '2024-01-15 14:30:00',
      status: 'success',
      dataType: '文章',
      dataCount: 125,
      duration: 180,
      startUrl: 'https://example.com/page1',
      endUrl: 'https://example.com/page125',
      errorMsg: '',
      log: ''
    })

    // 数据列表
    const dataList = ref([])
    const loading = ref(false)

    // 表格列配置
    const columns = [
      {
        title: '数据ID',
        dataIndex: 'id',
        key: 'id',
        width: 120
      },
      {
        title: '页面URL',
        dataIndex: 'url',
        key: 'url',
        width: 300,
        ellipsis: true
      },
      {
        title: '抓取时间',
        dataIndex: 'crawlTime',
        key: 'crawlTime',
        width: 160
      },
      {
        title: '数据内容',
        dataIndex: 'content',
        key: 'content',
        ellipsis: true
      },
      {
        title: '操作',
        key: 'action',
        width: 100,
        fixed: 'right'
      }
    ]

    // 分页配置
    const pagination = reactive({
      current: 1,
      pageSize: 20,
      total: 0,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: (total) => `共 ${total} 条数据`
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

    // 格式化内容
    const formatContent = (content) => {
      if (typeof content === 'string') {
        return content.length > 100 ? content.substring(0, 100) + '...' : content
      }
      return JSON.stringify(content).substring(0, 100) + '...'
    }

    // 生成模拟数据
    const generateMockData = () => {
      const data = []
      for (let i = 1; i <= 125; i++) {
        data.push({
          id: `data_${i}`,
          url: `https://example.com/article/${i}`,
          crawlTime: `2024-01-15 14:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}`,
          content: {
            title: `文章标题 ${i}`,
            author: `作者${i}`,
            publishTime: '2024-01-15',
            summary: '这是一篇测试文章的内容摘要，包含了文章的主要信息...'
          }
        })
      }
      return data
    }

    // 加载数据
    const loadData = () => {
      loading.value = true
      setTimeout(() => {
        const allData = generateMockData()
        dataList.value = allData.slice(
          (pagination.current - 1) * pagination.pageSize,
          pagination.current * pagination.pageSize
        )
        pagination.total = allData.length
        loading.value = false
      }, 500)
    }

    // 表格变化
    const handleTableChange = (pag) => {
      pagination.current = pag.current
      pagination.pageSize = pag.pageSize
      loadData()
    }

    // 查看数据详情
    const viewDataDetail = (record) => {
      message.info(`查看数据详情: ${record.id}`)
    }

    // 导出数据
    const exportData = () => {
      message.info('正在导出抓取数据...')
      setTimeout(() => {
        message.success('抓取数据导出成功')
      }, 1000)
    }

    // 初始化
    loadData()

    return {
      spiderId,
      recordId,
      recordInfo,
      dataList,
      loading,
      columns,
      paginationConfig,
      getStatusColor,
      getStatusText,
      formatContent,
      handleTableChange,
      viewDataDetail,
      exportData
    }
  }
})
</script>

<style scoped>
.crawl-data-detail-container {
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

.basic-info {
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.data-list {
  padding: 24px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.list-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.data-count {
  color: #8c8c8c;
  font-size: 14px;
}

.url-text {
  max-width: 280px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
}

.content-text {
  max-width: 400px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
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
  
  .basic-info,
  .data-list {
    padding: 16px;
  }
  
  .list-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>