<template>
  <div class="transport-list-container">
    <!-- 操作栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <div class="search-item">
          <span class="search-label">名称：</span>
          <a-input
            v-model:value="searchName"
            placeholder="搜索名称"
            style="width: 200px; margin-right: 12px"
          >
            <template #prefix>
              <SearchOutlined />
            </template>
          </a-input>
        </div>
        
        <div class="search-item">
          <span class="search-label">传输类型：</span>
          <a-select
            v-model:value="searchType"
            placeholder="传输类型"
            style="width: 120px; margin-right: 12px"
          >
            <a-select-option value="">全部类型</a-select-option>
            <a-select-option value="upload">上传</a-select-option>
            <a-select-option value="download">下载</a-select-option>
          </a-select>
        </div>
        
        <div class="search-item">
          <span class="search-label">传输状态：</span>
          <a-select
            v-model:value="searchStatus"
            placeholder="传输状态"
            style="width: 120px; margin-right: 12px"
          >
            <a-select-option value="">全部状态</a-select-option>
            <a-select-option value="pending">未开始</a-select-option>
            <a-select-option value="paused">暂停中</a-select-option>
            <a-select-option value="running">进行中</a-select-option>
            <a-select-option value="success">成功</a-select-option>
            <a-select-option value="failed">失败</a-select-option>
          </a-select>
        </div>
        
        <div class="search-buttons">
          <a-button type="primary" @click="handleSearch" style="margin-right: 8px">
            确定
          </a-button>
          <a-button @click="handleReset">
            重置
          </a-button>
        </div>
      </div>
      
      <div class="toolbar-right">
        <a-button type="primary" danger @click="handleClearAll">
          <template #icon>
            <DeleteOutlined />
          </template>
          清空记录
        </a-button>
      </div>
    </div>

    <!-- 传输列表 -->
    <div class="transport-list">
      <a-table
        :columns="columns"
        :data-source="filteredData"
        :pagination="paginationConfig"
        :loading="loading"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'name'">
            <div class="file-name">
              <FolderTwoTone v-if="isFolder(record.name)" class="file-icon folder-icon" two-tone-color="#1890ff" />
              <FileImageTwoTone v-else-if="isImage(record.name)" class="file-icon image-icon" two-tone-color="#52c41a" />
              <FileTextTwoTone v-else-if="isText(record.name)" class="file-icon text-icon" two-tone-color="#722ed1" />
              <FilePdfTwoTone v-else-if="isPDF(record.name)" class="file-icon pdf-icon" two-tone-color="#ff4d4f" />
              <FileTwoTone v-else class="file-icon file-icon" two-tone-color="#fa8c16" />
              <span>{{ record.name }}</span>
            </div>
          </template>
          
          <template v-else-if="column.key === 'type'">
            <a-tag :color="getTypeColor(record.type)">
              {{ record.type === 'upload' ? '上传' : '下载' }}
            </a-tag>
          </template>
          
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          
          <template v-else-if="column.key === 'progress'">
            <div class="progress-container">
              <a-progress
                :percent="record.progress"
                :status="getProgressStatus(record.status)"
                :show-info="false"
                size="small"
              />
              <span class="progress-text">{{ record.progress }}%</span>
            </div>
          </template>
          
          <template v-else-if="column.key === 'speed'">
            <span>{{ record.speed || '-' }}</span>
          </template>
          
          <template v-else-if="column.key === 'size'">
            <span>{{ formatFileSize(record.size) }}</span>
          </template>
          
          <template v-else-if="column.key === 'action'">
            <div class="action-buttons">
              <a-button
                v-if="record.status === 'running'"
                type="link"
                size="small"
                @click="handlePause(record)"
              >
                <PauseCircleOutlined />
                暂停
              </a-button>
              
              <a-button
                v-if="record.status === 'paused'"
                type="link"
                size="small"
                @click="handleResume(record)"
              >
                <PlayCircleOutlined />
                继续
              </a-button>
              
              <a-button
                v-if="record.status === 'failed'"
                type="link"
                size="small"
                @click="handleRetry(record)"
              >
                <ReloadOutlined />
                重试
              </a-button>
              
              <a-button
                type="link"
                danger
                size="small"
                @click="handleDelete(record)"
              >
                <DeleteOutlined />
                删除
              </a-button>
            </div>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script>
import { ref, computed, reactive } from 'vue'
import { Modal, message } from 'ant-design-vue'
import {
  SearchOutlined,
  FileOutlined,
  FolderOutlined,
  DeleteOutlined,
  PauseCircleOutlined,
  PlayCircleOutlined,
  ReloadOutlined,
  FileImageTwoTone,
  FileTextTwoTone,
  FilePdfTwoTone,
  FileTwoTone,
  FolderTwoTone
} from '@ant-design/icons-vue'

export default {
  name: 'TransportList',
  components: {
    SearchOutlined,
    FileOutlined,
    FolderOutlined,
    DeleteOutlined,
    PauseCircleOutlined,
    PlayCircleOutlined,
    ReloadOutlined,
    FileImageTwoTone,
    FileTextTwoTone,
    FilePdfTwoTone,
    FileTwoTone,
    FolderTwoTone
  },
  setup() {
    const loading = ref(false)
    const searchName = ref('')
    const searchType = ref('')
    const searchStatus = ref('')
    
    // 模拟数据
    const transportData = ref([
      {
        id: 1,
        name: 'document.pdf',
        type: 'download',
        status: 'running',
        progress: 65,
        speed: '2.5 MB/s',
        size: 10485760,
        createTime: '2024-01-15 14:30:00'
      },
      {
        id: 2,
        name: 'project_folder',
        type: 'upload',
        status: 'success',
        progress: 100,
        speed: '1.8 MB/s',
        size: 52428800,
        createTime: '2024-01-15 14:25:00'
      },
      {
        id: 3,
        name: 'vacation-photo.jpg',
        type: 'download',
        status: 'failed',
        progress: 45,
        speed: '0 MB/s',
        size: 3145728,
        createTime: '2024-01-15 14:20:00'
      },
      {
        id: 4,
        name: 'presentation.pptx',
        type: 'upload',
        status: 'paused',
        progress: 30,
        speed: '0 MB/s',
        size: 15728640,
        createTime: '2024-01-15 14:15:00'
      },
      {
        id: 5,
        name: 'video-tutorial.mp4',
        type: 'download',
        status: 'pending',
        progress: 0,
        speed: '0 MB/s',
        size: 104857600,
        createTime: '2024-01-15 14:10:00'
      },
      {
        id: 6,
        name: 'notes.txt',
        type: 'upload',
        status: 'running',
        progress: 80,
        speed: '1.2 MB/s',
        size: 524288,
        createTime: '2024-01-15 14:05:00'
      },
      {
        id: 7,
        name: 'archive.zip',
        type: 'download',
        status: 'success',
        progress: 100,
        speed: '3.0 MB/s',
        size: 52428800,
        createTime: '2024-01-15 14:00:00'
      }
    ])

    const columns = [
      {
        title: '名称',
        dataIndex: 'name',
        key: 'name',
        width: '25%'
      },
      {
        title: '类型',
        dataIndex: 'type',
        key: 'type',
        width: '8%'
      },
      {
        title: '状态',
        dataIndex: 'status',
        key: 'status',
        width: '10%'
      },
      {
        title: '进度',
        dataIndex: 'progress',
        key: 'progress',
        width: '15%'
      },
      {
        title: '速度',
        dataIndex: 'speed',
        key: 'speed',
        width: '12%'
      },
      {
        title: '大小',
        dataIndex: 'size',
        key: 'size',
        width: '10%'
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
        key: 'createTime',
        width: '15%'
      },
      {
        title: '操作',
        key: 'action',
        width: '15%'
      }
    ]

    const paginationConfig = reactive({
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      pageSizeOptions: ['10', '20', '50', '100'],
      showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条/总共 ${total} 条`
    })

    const filteredData = computed(() => {
      let data = transportData.value
      
      if (searchName.value) {
        data = data.filter(item => 
          item.name.toLowerCase().includes(searchName.value.toLowerCase())
        )
      }
      
      if (searchType.value) {
        data = data.filter(item => item.type === searchType.value)
      }
      
      if (searchStatus.value) {
        data = data.filter(item => item.status === searchStatus.value)
      }
      
      return data
    })

    const getTypeColor = (type) => {
      return type === 'upload' ? 'blue' : 'green'
    }

    const getStatusColor = (status) => {
      const colorMap = {
        pending: 'default',
        paused: 'warning',
        running: 'processing',
        success: 'success',
        failed: 'error'
      }
      return colorMap[status] || 'default'
    }

    const getStatusText = (status) => {
      const textMap = {
        pending: '未开始',
        paused: '暂停中',
        running: '进行中',
        success: '成功',
        failed: '失败'
      }
      return textMap[status] || status
    }

    const getProgressStatus = (status) => {
      if (status === 'success') return 'success'
      if (status === 'failed') return 'exception'
      return 'active'
    }

    const formatFileSize = (bytes) => {
      if (bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    }

    const isFolder = (filename) => {
      return !filename.includes('.')
    }

    const isImage = (filename) => {
      const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.svg', '.webp']
      return imageExtensions.some(ext => filename.toLowerCase().endsWith(ext))
    }

    const isText = (filename) => {
      const textExtensions = ['.txt', '.md', '.doc', '.docx', '.log']
      return textExtensions.some(ext => filename.toLowerCase().endsWith(ext))
    }

    const isPDF = (filename) => {
      return filename.toLowerCase().endsWith('.pdf')
    }

    const handleSearch = () => {
      // 搜索逻辑已通过 computed 实现
      // 这里可以添加额外的搜索逻辑
    }

    const handleReset = () => {
      searchName.value = ''
      searchType.value = ''
      searchStatus.value = ''
      message.success('搜索条件已重置')
    }

    const handlePause = (record) => {
      message.success(`已暂停 ${record.name}`)
      record.status = 'paused'
      record.speed = '0 MB/s'
    }

    const handleResume = (record) => {
      message.success(`已继续 ${record.name}`)
      record.status = 'running'
      record.speed = '2.5 MB/s'
    }

    const handleRetry = (record) => {
      message.success(`已重试 ${record.name}`)
      record.status = 'running'
      record.progress = 0
      record.speed = '1.5 MB/s'
    }

    const handleDelete = (record) => {
      Modal.confirm({
        title: '确认删除',
        content: `确定要删除传输记录 "${record.name}" 吗？`,
        onOk() {
          const index = transportData.value.findIndex(item => item.id === record.id)
          if (index > -1) {
            transportData.value.splice(index, 1)
            message.success('删除成功')
          }
        }
      })
    }

    const handleClearAll = () => {
      Modal.confirm({
        title: '确认清空',
        content: '确定要清空所有传输记录吗？此操作不可恢复。',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk() {
          transportData.value = []
          message.success('已清空所有传输记录')
        }
      })
    }

    return {
      loading,
      searchName,
      searchType,
      searchStatus,
      transportData,
      columns,
      paginationConfig,
      filteredData,
      getTypeColor,
      getStatusColor,
      getStatusText,
      getProgressStatus,
      formatFileSize,
      isFolder,
      isImage,
      isText,
      isPDF,
      handleSearch,
      handleReset,
      handlePause,
      handleResume,
      handleRetry,
      handleDelete,
      handleClearAll
    }
  }
}
</script>

<style scoped>
.transport-list-container {
  background: #fff;
  min-height: 100%;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.toolbar-left {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.search-item {
  display: flex;
  align-items: center;
}

.search-label {
  margin-right: 8px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
}

.search-buttons {
  display: flex;
  align-items: center;
  margin-left: 8px;
}

.toolbar-right {
  display: flex;
  align-items: center;
}

.file-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-icon {
  color: #fa8c16;
}

.folder-icon {
  color: #1890ff;
}

.progress-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-text {
  font-size: 12px;
  color: #666;
  min-width: 40px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.transport-list {
  background: #fff;
  padding: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    gap: 12px;
  }
  
  .toolbar-left {
    flex-wrap: wrap;
    width: 100%;
  }
  
  .toolbar-right {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>