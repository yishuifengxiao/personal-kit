<template>
  <div class="spider-task-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">任务总览</h1>
        <span class="page-subtitle">爬虫运行状态与数据统计</span>
      </div>
      <div class="header-actions">
        <a-button type="primary" @click="refreshData">
          <reload-outlined />
          刷新数据
        </a-button>
        <a-range-picker v-model:value="dateRange" class="date-picker" @change="handleDateChange" />
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :sm="12" :md="6">
          <a-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon running">
                <play-circle-outlined />
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.runningCount }}</div>
                <div class="stat-label">运行中任务</div>
              </div>
            </div>
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon completed">
                <check-circle-outlined />
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.completedCount }}</div>
                <div class="stat-label">已完成任务</div>
              </div>
            </div>
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon failed">
                <close-circle-outlined />
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.failedCount }}</div>
                <div class="stat-label">失败任务</div>
              </div>
            </div>
          </a-card>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6">
          <a-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon data">
                <database-outlined />
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ formatNumber(stats.totalDataCount) }}</div>
                <div class="stat-label">总数据量</div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <a-row :gutter="[16, 16]">
        <!-- 任务运行趋势折线图 -->
        <a-col :xs="24" :lg="16">
          <a-card title="任务运行趋势" class="chart-card">
            <div ref="lineChartRef" class="chart-container" style="height: 400px;"></div>
          </a-card>
        </a-col>
        
        <!-- 任务状态分布饼图 -->
        <a-col :xs="24" :lg="8">
          <a-card title="任务状态分布" class="chart-card">
            <div ref="pieChartRef" class="chart-container" style="height: 400px;"></div>
          </a-card>
        </a-col>
      </a-row>

      <a-row :gutter="[16, 16]" style="margin-top: 16px;">
        <!-- 数据类型分布柱状图 -->
        <a-col :xs="24" :lg="12">
          <a-card title="数据类型分布" class="chart-card">
            <div ref="barChartRef" class="chart-container" style="height: 350px;"></div>
          </a-card>
        </a-col>

        <!-- 爬虫任务排行 -->
        <a-col :xs="24" :lg="12">
          <a-card title="爬虫任务排行" class="chart-card">
            <div ref="rankChartRef" class="chart-container" style="height: 350px;"></div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 最近任务列表 -->
    <div class="recent-tasks">
      <a-card title="最近任务" class="tasks-card">
        <a-table 
          :columns="taskColumns" 
          :data-source="recentTasks" 
          :pagination="false"
          :loading="loading"
          rowKey="id"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record.status)">
                {{ getStatusText(record.status) }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'dataType'">
              <a-tag :color="getDataTypeColor(record.dataType)">
                {{ getDataTypeText(record.dataType) }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'progress'">
              <a-progress 
                :percent="record.progress" 
                :status="getProgressStatus(record.status)"
                size="small"
              />
            </template>
          </template>
        </a-table>
      </a-card>
    </div>
  </div>
</template>

<script>
import { defineComponent, reactive, ref, onMounted, onUnmounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import * as echarts from 'echarts'
import {
  PlayCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  DatabaseOutlined,
  ReloadOutlined,
  ClockCircleOutlined,
  SettingOutlined
} from '@ant-design/icons-vue'

export default defineComponent({
  name: 'SpiderTask',
  components: {
    PlayCircleOutlined,
    CheckCircleOutlined,
    CloseCircleOutlined,
    DatabaseOutlined,
    ReloadOutlined,
    ClockCircleOutlined,
    SettingOutlined
  },
  setup() {
    const loading = ref(false)
    const dateRange = ref([])
    const lineChartRef = ref(null)
    const pieChartRef = ref(null)
    const barChartRef = ref(null)
    const rankChartRef = ref(null)
    
    let lineChart = null
    let pieChart = null
    let barChart = null
    let rankChart = null

    // 统计数据
    const stats = reactive({
      runningCount: 12,
      completedCount: 156,
      failedCount: 8,
      totalDataCount: 124580
    })

    // 最近任务数据
    const recentTasks = ref([
      {
        id: 1,
        name: '新闻爬虫任务-001',
        spiderName: '新闻爬虫',
        status: 'running',
        startTime: '2024-01-15 14:30:00',
        dataCount: 1250,
        dataType: 'text',
        progress: 65
      },
      {
        id: 2,
        name: '商品数据抓取-002',
        spiderName: '电商爬虫',
        status: 'completed',
        startTime: '2024-01-15 13:45:00',
        dataCount: 890,
        dataType: 'json',
        progress: 100
      },
      {
        id: 3,
        name: '社交媒体采集-003',
        spiderName: '社交媒体爬虫',
        status: 'failed',
        startTime: '2024-01-15 12:20:00',
        dataCount: 0,
        dataType: 'rich_text',
        progress: 0
      },
      {
        id: 4,
        name: '图片资源下载-004',
        spiderName: '图片爬虫',
        status: 'completed',
        startTime: '2024-01-15 11:15:00',
        dataCount: 456,
        dataType: 'file',
        progress: 100
      },
      {
        id: 5,
        name: '论坛数据抓取-005',
        spiderName: '论坛爬虫',
        status: 'running',
        startTime: '2024-01-15 10:30:00',
        dataCount: 678,
        dataType: 'text',
        progress: 78
      }
    ])

    // 任务表格列配置
    const taskColumns = [
      {
        title: '任务名称',
        dataIndex: 'name',
        key: 'name',
        width: 200,
        ellipsis: true
      },
      {
        title: '爬虫名称',
        dataIndex: 'spiderName',
        key: 'spiderName',
        width: 120
      },
      {
        title: '状态',
        dataIndex: 'status',
        key: 'status',
        width: 80
      },
      {
        title: '数据类型',
        dataIndex: 'dataType',
        key: 'dataType',
        width: 100
      },
      {
        title: '数据量',
        dataIndex: 'dataCount',
        key: 'dataCount',
        width: 80,
        align: 'right'
      },
      {
        title: '开始时间',
        dataIndex: 'startTime',
        key: 'startTime',
        width: 160
      },
      {
        title: '进度',
        dataIndex: 'progress',
        key: 'progress',
        width: 120
      }
    ]

    // 初始化折线图
    const initLineChart = () => {
      if (!lineChartRef.value) return
      
      lineChart = echarts.init(lineChartRef.value)
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: ['运行中', '已完成', '失败']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '24:00']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '运行中',
            type: 'line',
            stack: 'Total',
            areaStyle: {},
            emphasis: {
              focus: 'series'
            },
            data: [5, 8, 15, 12, 18, 10, 8]
          },
          {
            name: '已完成',
            type: 'line',
            stack: 'Total',
            areaStyle: {},
            emphasis: {
              focus: 'series'
            },
            data: [20, 25, 30, 35, 40, 45, 50]
          },
          {
            name: '失败',
            type: 'line',
            stack: 'Total',
            areaStyle: {},
            emphasis: {
              focus: 'series'
            },
            data: [2, 1, 3, 2, 1, 2, 1]
          }
        ]
      }
      lineChart.setOption(option)
    }

    // 初始化饼图
    const initPieChart = () => {
      if (!pieChartRef.value) return
      
      pieChart = echarts.init(pieChartRef.value)
      const option = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '任务状态',
            type: 'pie',
            radius: '50%',
            data: [
              { value: 12, name: '运行中', itemStyle: { color: '#52c41a' } },
              { value: 156, name: '已完成', itemStyle: { color: '#1890ff' } },
              { value: 8, name: '失败', itemStyle: { color: '#ff4d4f' } },
              { value: 24, name: '暂停', itemStyle: { color: '#faad14' } }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
      pieChart.setOption(option)
    }

    // 初始化柱状图
    const initBarChart = () => {
      if (!barChartRef.value) return
      
      barChart = echarts.init(barChartRef.value)
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['文本', 'JSON', '富文本', '文件', '图片', '视频']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '数据量',
            type: 'bar',
            data: [32000, 28000, 15000, 12000, 8000, 3000],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#83bff6' },
                { offset: 0.5, color: '#188df0' },
                { offset: 1, color: '#188df0' }
              ])
            }
          }
        ]
      }
      barChart.setOption(option)
    }

    // 初始化排行图
    const initRankChart = () => {
      if (!rankChartRef.value) return
      
      rankChart = echarts.init(rankChartRef.value)
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value'
        },
        yAxis: {
          type: 'category',
          data: ['新闻爬虫', '电商爬虫', '社交媒体爬虫', '论坛爬虫', '图片爬虫']
        },
        series: [
          {
            name: '抓取数量',
            type: 'bar',
            data: [4500, 3800, 3200, 2800, 2100],
            itemStyle: {
              color: '#722ed1'
            }
          }
        ]
      }
      rankChart.setOption(option)
    }

    // 刷新数据
    const refreshData = () => {
      loading.value = true
      // 模拟数据刷新
      setTimeout(() => {
        // 更新统计数据
        stats.runningCount = Math.floor(Math.random() * 20) + 5
        stats.completedCount = Math.floor(Math.random() * 50) + 100
        stats.failedCount = Math.floor(Math.random() * 10) + 2
        stats.totalDataCount = Math.floor(Math.random() * 50000) + 100000
        
        message.success('数据已刷新')
        loading.value = false
      }, 1000)
    }

    // 日期范围变化处理
    const handleDateChange = (dates) => {
      console.log('日期范围变化:', dates)
      // 这里可以根据日期范围重新加载数据
    }

    // 状态相关方法
    const getStatusColor = (status) => {
      const colorMap = {
        running: 'processing',
        completed: 'success',
        failed: 'error',
        paused: 'warning'
      }
      return colorMap[status] || 'default'
    }

    const getStatusText = (status) => {
      const textMap = {
        running: '运行中',
        completed: '已完成',
        failed: '失败',
        paused: '暂停'
      }
      return textMap[status] || status
    }

    const getProgressStatus = (status) => {
      return status === 'failed' ? 'exception' : status === 'completed' ? 'success' : 'active'
    }

    // 数据类型相关方法
    const getDataTypeColor = (type) => {
      const colorMap = {
        text: 'blue',
        json: 'green',
        rich_text: 'orange',
        file: 'purple'
      }
      return colorMap[type] || 'default'
    }

    const getDataTypeText = (type) => {
      const textMap = {
        text: '文本',
        json: 'JSON',
        rich_text: '富文本',
        file: '文件'
      }
      return textMap[type] || type
    }

    // 格式化数字
    const formatNumber = (num) => {
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万'
      }
      return num.toString()
    }

    // 窗口大小变化处理
    const handleResize = () => {
      lineChart?.resize()
      pieChart?.resize()
      barChart?.resize()
      rankChart?.resize()
    }

    onMounted(() => {
      initLineChart()
      initPieChart()
      initBarChart()
      initRankChart()
      
      window.addEventListener('resize', handleResize)
    })

    onUnmounted(() => {
      window.removeEventListener('resize', handleResize)
      lineChart?.dispose()
      pieChart?.dispose()
      barChart?.dispose()
      rankChart?.dispose()
    })

    return {
      loading,
      dateRange,
      stats,
      recentTasks,
      taskColumns,
      lineChartRef,
      pieChartRef,
      barChartRef,
      rankChartRef,
      refreshData,
      handleDateChange,
      getStatusColor,
      getStatusText,
      getProgressStatus,
      getDataTypeColor,
      getDataTypeText,
      formatNumber
    }
  }
})
</script>

<style scoped>
.spider-task-container {
  padding: 24px;
  background: #f0f2f5;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.page-subtitle {
  color: #8c8c8c;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.date-picker {
  width: 240px;
}

.stats-cards {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.stat-icon.running {
  background: linear-gradient(135deg, #52c41a, #73d13d);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #1890ff, #40a9ff);
}

.stat-icon.failed {
  background: linear-gradient(135deg, #ff4d4f, #ff7875);
}

.stat-icon.data {
  background: linear-gradient(135deg, #722ed1, #9254de);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #262626;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #8c8c8c;
}

.charts-container {
  margin-bottom: 24px;
}

.chart-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-container {
  width: 100%;
}

.recent-tasks {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tasks-card {
  border-radius: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .spider-task-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .header-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .date-picker {
    width: 100%;
  }
  
  .stat-content {
    padding: 16px;
  }
  
  .stat-icon {
    width: 50px;
    height: 50px;
    font-size: 24px;
  }
  
  .stat-number {
    font-size: 24px;
  }
}

/* 暗色主题支持 */
@media (prefers-color-scheme: dark) {
  .spider-task-container {
    background: #141414;
  }
  
  .page-header {
    background: #1f1f1f;
  }
  
  .page-title {
    color: #ffffff;
  }
  
  .stat-card,
  .chart-card,
  .tasks-card {
    background: #1f1f1f;
    color: #ffffff;
  }
  
  .stat-number {
    color: #ffffff;
  }
}
</style>