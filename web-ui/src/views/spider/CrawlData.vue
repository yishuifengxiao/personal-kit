<template>
  <div class="crawl-data-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <a-button @click="goBack" style="margin-right: 16px;">
          <template #icon><left-outlined /></template>
          返回
        </a-button>
        <h2 class="page-title">抓取数据</h2>
      </div>
      <div class="header-actions">
        <a-space>
          <a-button type="primary" @click="exportData">
            <template #icon><download-outlined /></template>
            导出数据
          </a-button>
          <a-popconfirm title="确定要清空所有数据吗？" @confirm="clearAllData" okText="确定" cancelText="取消">
            <a-button danger>
              <template #icon><delete-outlined /></template>
              清空数据
            </a-button>
          </a-popconfirm>
        </a-space>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="stats-section">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-card>
            <a-statistic title="总数据量" :value="totalData" />
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic title="今日新增" :value="todayData" />
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic title="成功抓取" :value="successData" />
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic title="失败数量" :value="failedData" />
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 数据表格 -->
    <div class="data-table-section">
      <a-table 
        :columns="dataColumns" 
        :data-source="spiderDataList" 
        :pagination="dataPagination"
        :loading="loading"
        size="small"
        rowKey="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getDataStatusColor(record.status)">
              {{ getDataStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'sourceUrl'">
            <a :href="record.sourceUrl" target="_blank" style="max-width: 200px; display: inline-block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
              {{ record.sourceUrl }}
            </a>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewDataDetail(record)">
                查看详情
              </a-button>
              <a-popconfirm title="确定要删除这条数据吗？" @confirm="deleteData(record)" okText="确定" cancelText="取消">
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
import { defineComponent, ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { LeftOutlined, DownloadOutlined, DeleteOutlined } from '@ant-design/icons-vue'

export default defineComponent({
  name: 'CrawlData',
  components: {
    LeftOutlined,
    DownloadOutlined,
    DeleteOutlined
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    const spiderId = route.params.spiderId
    const spiderName = route.query.spiderName || '未知爬虫'
    
    // 统计数据
    const totalData = ref(0)
    const todayData = ref(0)
    const successData = ref(0)
    const failedData = ref(0)
    
    // 数据列表
    const spiderDataList = ref([])
    const loading = ref(false)
    
    // 表格列配置
    const dataColumns = [
      {
        title: '数据ID',
        dataIndex: 'id',
        key: 'id',
        width: 80
      },
      {
        title: '抓取时间',
        dataIndex: 'crawlTime',
        key: 'crawlTime',
        width: 150,
        customRender: ({ text }) => formatTime(text)
      },
      {
        title: '来源地址',
        dataIndex: 'sourceUrl',
        key: 'sourceUrl',
        ellipsis: true,
        customCell: () => ({
          style: {
            'max-width': '200px',
            'overflow': 'hidden',
            'text-overflow': 'ellipsis',
            'white-space': 'nowrap'
          }
        })
      },
      {
        title: '数据状态',
        dataIndex: 'status',
        key: 'status',
        width: 100
      },
      {
        title: '操作',
        key: 'action',
        width: 150,
        fixed: 'right'
      }
    ]
    
    // 分页配置
    const dataPagination = reactive({
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: total => `共 ${total} 条数据`
    })
    
    // 时间格式化
    const formatTime = (time) => {
      if (!time) return '-'
      return new Date(time).toLocaleString('zh-CN')
    }
    
    // 获取状态颜色
    const getDataStatusColor = (status) => {
      const statusMap = {
        'success': 'green',
        'failed': 'red',
        'pending': 'orange',
        'processing': 'blue'
      }
      return statusMap[status] || 'default'
    }
    
    // 获取状态文本
    const getDataStatusText = (status) => {
      const statusMap = {
        'success': '成功',
        'failed': '失败',
        'pending': '待处理',
        'processing': '处理中'
      }
      return statusMap[status] || status
    }
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    // 查看数据详情
    const viewDataDetail = (record) => {
      router.push({
        path: `/view/crawl_data/${spiderId}/${record.id}`,
        query: { spiderName: spiderName }
      })
    }
    
    // 删除数据
    const deleteData = (record) => {
      const index = spiderDataList.value.findIndex(item => item.id === record.id)
      if (index > -1) {
        spiderDataList.value.splice(index, 1)
        message.success('删除数据成功')
        loadData()
      }
    }
    
    // 导出数据
    const exportData = () => {
      const dataStr = JSON.stringify(spiderDataList.value, null, 2)
      const blob = new Blob([dataStr], { type: 'application/json' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `crawl_data_${spiderName}_${new Date().toISOString().slice(0, 10)}.json`
      link.click()
      URL.revokeObjectURL(url)
      message.success('数据导出成功')
    }
    
    // 清空所有数据
    const clearAllData = () => {
      spiderDataList.value = []
      message.success('已清空所有数据')
      loadData()
    }
    
    // 加载数据
    const loadData = async () => {
      loading.value = true
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 500))
        
        // 生成模拟数据
        const mockData = []
        for (let i = 1; i <= 50; i++) {
          mockData.push({
            id: `data_${spiderId}_${i}`,
            crawlTime: new Date(Date.now() - Math.random() * 86400000 * 30).toISOString(),
            sourceUrl: `https://example.com/page${i}.html`,
            status: ['success', 'failed', 'pending', 'processing'][Math.floor(Math.random() * 4)],
            content: {
              title: `页面标题 ${i}`,
              content: `这是第 ${i} 条抓取的数据内容`,
              url: `https://example.com/page${i}.html`
            }
          })
        }
        
        spiderDataList.value = mockData
        dataPagination.total = mockData.length
        
        // 更新统计数据
        totalData.value = mockData.length
        todayData.value = Math.floor(Math.random() * 20) + 5
        successData.value = mockData.filter(item => item.status === 'success').length
        failedData.value = mockData.filter(item => item.status === 'failed').length
        
      } catch (error) {
        message.error('加载数据失败')
      } finally {
        loading.value = false
      }
    }
    
    // 页面加载时获取数据
    onMounted(() => {
      loadData()
    })
    
    return {
      spiderName,
      totalData,
      todayData,
      successData,
      failedData,
      spiderDataList,
      loading,
      dataColumns,
      dataPagination,
      formatTime,
      getDataStatusColor,
      getDataStatusText,
      goBack,
      viewDataDetail,
      deleteData,
      exportData,
      clearAllData
    }
  }
})
</script>

<style scoped>
.crawl-data-container {
  background: #fff;
  min-height: 100vh;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #262626;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.stats-section {
  margin-bottom: 24px;
}

.data-table-section {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}
</style>