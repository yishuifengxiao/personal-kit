<template>
  <div class="my-share-container">
    <div class="toolbar">
      <div class="toolbar-left">
        <div class="search-item">
          <span class="search-label">名称：</span>
          <a-input
            v-model:value="searchName"
            placeholder="请输入分享名称"
            style="width: 200px"
            @pressEnter="handleSearch"
          />
        </div>
        
        <div class="search-item">
          <span class="search-label">状态：</span>
          <a-select
            v-model:value="searchStatus"
            placeholder="请选择状态"
            style="width: 150px"
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="active">有效</a-select-option>
            <a-select-option value="expired">失效</a-select-option>
            <a-select-option value="cancelled">取消</a-select-option>
          </a-select>
        </div>

        <div class="search-buttons">
          <a-button type="primary" @click="handleSearch">
            <SearchOutlined />
            确定
          </a-button>
          <a-button @click="handleReset" style="margin-left: 8px">
            重置
          </a-button>
        </div>
      </div>
      
      <div class="toolbar-right">
        <a-button type="link" danger @click="handleClearAll">
          <DeleteOutlined />
          清空记录
        </a-button>
      </div>
    </div>

    <div class="share-list">
      <a-table
        :columns="columns"
        :data-source="filteredData"
        :pagination="paginationConfig"
        :loading="loading"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'name'">
            <div class="share-name">
              <LinkOutlined class="share-icon" />
              <span>{{ record.name }}</span>
            </div>
          </template>
          
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          
          <template v-else-if="column.key === 'createTime'">
            <span>{{ record.createTime }}</span>
          </template>
          
          <template v-else-if="column.key === 'expireTime'">
            <span>{{ record.expireTime }}</span>
          </template>
          
          <template v-else-if="column.key === 'action'">
            <div class="action-buttons">
              <a-button
                v-if="record.status === 'active'"
                type="link"
                size="small"
                @click="handleCancel(record)"
              >
                <CloseCircleOutlined />
                取消分享
              </a-button>
              
              <a-button
                type="link"
                size="small"
                @click="handleCopyLink(record)"
              >
                <CopyOutlined />
                复制链接
              </a-button>
              
              <a-button
                type="link"
                danger
                size="small"
                @click="handleDelete(record)"
              >
                <DeleteOutlined />
                删除记录
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
  LinkOutlined,
  CopyOutlined,
  DeleteOutlined,
  CloseCircleOutlined
} from '@ant-design/icons-vue'

export default {
  name: 'MyShare',
  components: {
    SearchOutlined,
    LinkOutlined,
    CopyOutlined,
    DeleteOutlined,
    CloseCircleOutlined
  },
  setup() {
    const loading = ref(false)
    const searchName = ref('')
    const searchStatus = ref('')
    
    // 模拟数据
    const shareData = ref([
      {
        id: 1,
        name: '项目文档分享',
        status: 'active',
        createTime: '2024-01-15 14:30:00',
        expireTime: '2024-02-15 14:30:00'
      },
      {
        id: 2,
        name: '会议记录分享',
        status: 'expired',
        createTime: '2024-01-10 10:20:00',
        expireTime: '2024-01-17 10:20:00'
      },
      {
        id: 3,
        name: '设计稿分享',
        status: 'active',
        createTime: '2024-01-12 16:45:00',
        expireTime: '2024-02-12 16:45:00'
      },
      {
        id: 4,
        name: '代码示例分享',
        status: 'cancelled',
        createTime: '2024-01-08 09:15:00',
        expireTime: '2024-02-08 09:15:00'
      },
      {
        id: 5,
        name: '培训资料分享',
        status: 'active',
        createTime: '2024-01-14 13:00:00',
        expireTime: '2024-02-14 13:00:00'
      }
    ])

    const columns = [
      {
        title: '分享名称',
        dataIndex: 'name',
        key: 'name',
        width: '30%'
      },
      {
        title: '状态',
        dataIndex: 'status',
        key: 'status',
        width: '15%'
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
        key: 'createTime',
        width: '20%'
      },
      {
        title: '失效时间',
        dataIndex: 'expireTime',
        key: 'expireTime',
        width: '20%'
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
      showQuickJumper: true,
      showTotal: (total, range) => `${range[0]}-${range[1]} 共 ${total} 条`
    })

    const filteredData = computed(() => {
      let filtered = shareData.value

      if (searchName.value) {
        filtered = filtered.filter(item => 
          item.name.toLowerCase().includes(searchName.value.toLowerCase())
        )
      }

      if (searchStatus.value) {
        filtered = filtered.filter(item => item.status === searchStatus.value)
      }

      paginationConfig.total = filtered.length
      return filtered
    })

    const getStatusColor = (status) => {
      const colorMap = {
        active: 'green',
        expired: 'red',
        cancelled: 'orange'
      }
      return colorMap[status] || 'default'
    }

    const getStatusText = (status) => {
      const textMap = {
        active: '有效',
        expired: '失效',
        cancelled: '取消'
      }
      return textMap[status] || status
    }

    const handleSearch = () => {
      paginationConfig.current = 1
      message.success('搜索完成')
    }

    const handleReset = () => {
      searchName.value = ''
      searchStatus.value = ''
      paginationConfig.current = 1
      message.success('搜索条件已重置')
    }

    const handleCancel = (record) => {
      Modal.confirm({
        title: '确认取消分享',
        content: `确定要取消分享 "${record.name}" 吗？`,
        onOk() {
          const index = shareData.value.findIndex(item => item.id === record.id)
          if (index !== -1) {
            shareData.value[index].status = 'cancelled'
            message.success('分享已取消')
          }
        }
      })
    }

    const handleCopyLink = (record) => {
      const shareLink = `${window.location.origin}/share/${record.id}`
      navigator.clipboard.writeText(shareLink).then(() => {
        message.success('分享链接已复制到剪贴板')
      }).catch(() => {
        message.error('复制链接失败')
      })
    }

    const handleDelete = (record) => {
      Modal.confirm({
        title: '确认删除',
        content: `确定要删除分享记录 "${record.name}" 吗？`,
        onOk() {
          const index = shareData.value.findIndex(item => item.id === record.id)
          if (index !== -1) {
            shareData.value.splice(index, 1)
            message.success('分享记录已删除')
          }
        }
      })
    }

    const handleClearAll = () => {
      Modal.confirm({
        title: '确认清空记录',
        content: '确定要清空所有分享记录吗？此操作不可恢复。',
        onOk() {
          shareData.value = []
          message.success('分享记录已清空')
        }
      })
    }

    return {
      loading,
      searchName,
      searchStatus,
      shareData,
      columns,
      paginationConfig,
      filteredData,
      getStatusColor,
      getStatusText,
      handleSearch,
      handleReset,
      handleCancel,
      handleCopyLink,
      handleDelete,
      handleClearAll
    }
  }
}
</script>

<style scoped>
.my-share-container {
  padding: 24px;
  background: #fff;
  min-height: 100%;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 6px;
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

.share-list {
  background: #fff;
}

.share-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.share-icon {
  color: #1890ff;
  font-size: 16px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-buttons .ant-btn {
  padding: 0 4px;
  height: auto;
  line-height: 1;
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .toolbar-left {
    width: 100%;
  }
  
  .search-item {
    width: 100%;
  }
  
  .search-item .ant-input,
  .search-item .ant-select {
    width: 100% !important;
  }
  
  .search-buttons {
    width: 100%;
    margin-left: 0;
    margin-top: 8px;
  }
  
  .toolbar-right {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>