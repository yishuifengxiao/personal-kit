<template>
  <div class="detail-page">
    <a-page-header
      title="数据详情"
      @back="goBack"
    />
    
    <div class="detail-container">
      <!-- 左侧区域 -->
      <div class="left-section">
        <!-- 上部：数据标签 -->
        <div class="tags-section">
          <h3>数据标签</h3>
          <div class="tags-container">
            <a-tag
              v-for="(tag, index) in record.tags"
              :key="index"
              :color="getTagColor(tag)"
              class="tag-item"
            >
              {{ tag }}
            </a-tag>
          </div>
        </div>
        
        <!-- 下部：数据源网页内容 -->
        <div class="web-content-section">
          <h3>数据源内容</h3>
          <div class="web-content-container">
            <iframe 
              v-if="record.url" 
              :src="record.url" 
              frameborder="0"
              style="width: 100%; height: 600px; border: 1px solid #d9d9d9; border-radius: 4px;"
              sandbox="allow-same-origin allow-scripts allow-popups allow-forms"
            ></iframe>
            <a-empty v-else description="暂无网页内容" />
          </div>
        </div>
      </div>
      
      <!-- 右侧区域 -->
      <div class="right-section">
        <!-- 上部：操作按钮 -->
        <div class="action-buttons">
          <div class="compact-actions">
            <a-button type="primary" size="small" @click="handleEdit">编辑</a-button>
            <a-button size="small" @click="handleSync">同步</a-button>
            <a-button size="small" @click="showModifyTagsModal">标签</a-button>
            <a-button size="small" @click="showModifyStatusModal">状态</a-button>
            <a-button size="small" danger @click="handleDelete">删除</a-button>
          </div>
        </div>
        
        <!-- 中部：基本信息 -->
        <div class="info-section">
          <h3>基本信息</h3>
          <div class="info-content">
            <div class="info-item">
              <span class="info-label">标题：</span>
              <span class="info-value">{{ record.title }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">数据来源：</span>
              <a-tag :color="getDataSourceColor(record.dataSource)" size="small">
                {{ getDataSourceLabel(record.dataSource) }}
              </a-tag>
            </div>
            <div class="info-item">
              <span class="info-label">数据状态：</span>
              <a-tag :color="getDataStatusColor(record.dataStatus)" size="small">
                {{ getDataStatusText(record.dataStatus) }}
              </a-tag>
            </div>
            <div class="info-item">
              <span class="info-label">同步状态：</span>
              <a-badge :status="getSyncStatusStatus(record.syncStatus)" :text="getSyncStatusLabel(record.syncStatus)" />
            </div>
            <div class="info-item">
              <span class="info-label">同步时间：</span>
              <span class="info-value">{{ record.syncTime }}</span>
            </div>
          </div>
        </div>
        
        <!-- 下部：数据图谱 -->
        <div class="graph-section">
          <h3>数据图谱</h3>
          <div class="graph-placeholder">
            <a-empty description="数据图谱区域" />
          </div>
        </div>
      </div>
    </div>
    
    <!-- 修改标签弹窗 - 复制Index.vue风格 -->
    <a-modal
      v-model:open="modifyTagsModalVisible"
      title="修改标签"
      @ok="handleModifyTagsOk"
      @cancel="handleModifyTagsCancel"
      width="600px"
    >
      <div class="modify-tags-content">
        <div class="current-tags">
          <h4>当前标签</h4>
          <div class="tags-display">
            <a-tag
              v-for="(tag, index) in currentRecord.tags"
              :key="index"
              :color="getTagColor(tag)"
              closable
              @close="removeTag(index)"
            >
              {{ tag }}
            </a-tag>
          </div>
        </div>
        
        <div class="add-tag-section">
          <h4>添加标签</h4>
          <div class="tag-input-group">
            <a-input
              v-model:value="newTag"
              placeholder="请输入标签名称"
              @pressEnter="addTag"
              style="width: 200px"
            />
            <a-button type="primary" @click="addTag">添加</a-button>
          </div>
        </div>
      </div>
    </a-modal>
    
    <!-- 修改状态弹窗 -->
    <a-modal
      v-model:open="modifyStatusModalVisible"
      title="修改数据状态"
      @ok="handleModifyStatusOk"
      @cancel="handleModifyStatusCancel"
    >
      <a-form>
        <a-form-item label="数据状态">
          <a-radio-group v-model:value="modifyStatusValue">
            <a-radio value="published">已发布</a-radio>
            <a-radio value="unpublished">未发布</a-radio>
            <a-radio value="deleted">已删除</a-radio>
            <a-radio value="abnormal">异常</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { message } from 'ant-design-vue'

export default {
  name: 'SearchSourceDetail',
  data() {
    return {
      record: {
        id: 1,
        title: '示例数据标题',
        url: 'https://example.com',
        description: '这是一个示例数据的描述信息',
        dataSource: 'api-import',
        tags: ['标签1', '标签2', '标签3'],
        dataStatus: 'published',
        syncStatus: 'normal',
        syncTime: '2024-01-01 12:00:00'
      },
      currentRecord: null, // 用于修改标签的临时记录
      modifyTagsModalVisible: false,
      newTag: '', // 新增标签输入
      modifyStatusModalVisible: false,
      modifyStatusValue: 'published'
    }
  },
  mounted() {
    // 从路由参数获取数据ID并加载数据
    const id = this.$route.params.id
    if (id) {
      this.loadRecord(id)
    }
  },
  methods: {
    loadRecord(id) {
      // 模拟加载数据
      // 实际项目中这里应该调用API获取数据
      console.log('Loading record with ID:', id)
    },
    goBack() {
      this.$router.push('/view/my_search')
    },
    handleEdit() {
      // 跳转到编辑页面
      this.$router.push(`/view/my_search/edit/${this.record.id}`)
    },
    handleSync() {
      message.success('同步操作已触发')
    },
    handleDelete() {
      this.$msg.confirm({
        title: '确认删除',
        content: '确定要删除这条数据吗？',
        onOk: () => {
          this.$msg.success('删除成功')
          this.goBack()
        }
      })
    },
    showModifyTagsModal() {
      this.currentRecord = { ...this.record }
      this.newTag = ''
      this.modifyTagsModalVisible = true
    },
    handleModifyTagsOk() {
      this.record.tags = [...this.currentRecord.tags]
      this.modifyTagsModalVisible = false
      this.currentRecord = null
      message.success('标签修改成功')
    },
    handleModifyTagsCancel() {
      this.modifyTagsModalVisible = false
      this.currentRecord = null
      this.newTag = ''
    },
    addTag() {
      if (this.newTag.trim() && this.currentRecord) {
        if (!this.currentRecord.tags) {
          this.currentRecord.tags = []
        }
        if (!this.currentRecord.tags.includes(this.newTag.trim())) {
          this.currentRecord.tags.push(this.newTag.trim())
        }
        this.newTag = ''
      }
    },
    removeTag(index) {
      if (this.currentRecord && this.currentRecord.tags) {
        this.currentRecord.tags.splice(index, 1)
      }
    },
    showModifyStatusModal() {
      this.modifyStatusValue = this.record.dataStatus
      this.modifyStatusModalVisible = true
    },
    handleModifyStatusOk() {
      this.record.dataStatus = this.modifyStatusValue
      this.modifyStatusModalVisible = false
      message.success('状态修改成功')
    },
    handleModifyStatusCancel() {
      this.modifyStatusModalVisible = false
    },
    // 映射方法
    getTagColor(tag) {
      const colors = ['blue', 'green', 'orange', 'purple', 'red', 'cyan']
      return colors[tag.length % colors.length]
    },
    getDataSourceColor(dataSource) {
      const colors = {
        'api-import': 'blue',
        'manual': 'green',
        'third-party': 'orange',
        'crawler': 'purple'
      }
      return colors[dataSource] || 'default'
    },
    getDataSourceLabel(dataSource) {
      const labels = {
        'api-import': 'API导入',
        'manual': '手动创建',
        'third-party': '第三方链接',
        'crawler': '数据爬虫'
      }
      return labels[dataSource] || dataSource
    },
    getDataStatusColor(dataStatus) {
      const colors = {
        'published': 'success',
        'unpublished': 'warning',
        'deleted': 'default',
        'abnormal': 'error'
      }
      return colors[dataStatus] || 'default'
    },
    getDataStatusText(dataStatus) {
      const texts = {
        'published': '已发布',
        'unpublished': '未发布',
        'deleted': '已删除',
        'abnormal': '异常'
      }
      return texts[dataStatus] || dataStatus
    },
    getSyncStatusStatus(syncStatus) {
      const statuses = {
        'normal': 'success',
        'abnormal': 'error',
        'not-synced': 'default',
        'syncing': 'processing'
      }
      return statuses[syncStatus] || 'default'
    },
    getSyncStatusLabel(syncStatus) {
      const labels = {
        'normal': '正常',
        'abnormal': '异常',
        'not-synced': '未同步',
        'syncing': '同步中'
      }
      return labels[syncStatus] || syncStatus
    }
  }
}
</script>

<style scoped>
.detail-page {
  height: 100%;
  background-color: #f5f5f5;
}

.detail-container {
  display: flex;
  gap: 16px;
  padding: 16px;
  height: calc(100% - 64px);
}

.left-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.right-section {
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.tags-section, .web-content-section, .action-buttons, .info-section, .graph-section {
  background: white;
  padding: 16px;
  border-radius: 6px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.tag-item {
  margin: 0;
}

.action-buttons {
  padding: 16px;
}

.compact-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.compact-actions .ant-btn {
  flex: 1;
  min-width: 60px;
}

.info-content {
  margin-top: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}

.info-label {
  font-weight: 500;
  color: #666;
  width: 80px;
  flex-shrink: 0;
}

.info-value {
  flex: 1;
  color: #333;
}

.web-content-container {
  margin-top: 12px;
}

.modify-tags-content {
  padding: 16px 0;
}

.current-tags, .add-tag-section {
  margin-bottom: 24px;
}

.current-tags h4, .add-tag-section h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.tags-display {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-input-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

.graph-placeholder, .timeline-placeholder {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 12px;
}

h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 500;
}
</style>