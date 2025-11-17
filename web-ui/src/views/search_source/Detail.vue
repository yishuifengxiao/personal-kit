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
        
        <!-- 下部：数据详情 -->
        <div class="details-section">
          <h3>数据详情</h3>
          <a-descriptions :column="1" bordered>
            <a-descriptions-item label="标题">{{ record.title }}</a-descriptions-item>
            <a-descriptions-item label="URL">
              <a :href="record.url" target="_blank">{{ record.url }}</a>
            </a-descriptions-item>
            <a-descriptions-item label="描述">{{ record.description }}</a-descriptions-item>
            <a-descriptions-item label="数据来源">
              <a-tag :color="getDataSourceColor(record.dataSource)">
                {{ getDataSourceLabel(record.dataSource) }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="数据状态">
              <a-tag :color="getDataStatusColor(record.dataStatus)">
                {{ getDataStatusText(record.dataStatus) }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="同步状态">
              <a-badge :status="getSyncStatusStatus(record.syncStatus)" :text="getSyncStatusLabel(record.syncStatus)" />
            </a-descriptions-item>
            <a-descriptions-item label="同步时间">{{ record.syncTime }}</a-descriptions-item>
          </a-descriptions>
        </div>
      </div>
      
      <!-- 右侧区域 -->
      <div class="right-section">
        <!-- 上部：操作按钮 -->
        <div class="action-buttons">
          <a-space direction="vertical" style="width: 100%">
            <a-button type="primary" block @click="handleEdit">编辑</a-button>
            <a-button block @click="handleSync">同步</a-button>
            <a-button block @click="showModifyTagsModal">修改标签</a-button>
            <a-button block @click="showModifyStatusModal">修改状态</a-button>
            <a-button block danger @click="handleDelete">删除</a-button>
          </a-space>
        </div>
        
        <!-- 中部：页面描述 -->
        <div class="description-section">
          <h3>页面描述</h3>
          <div class="description-content">
            {{ record.description || '暂无描述信息' }}
          </div>
        </div>
        
        <!-- 下部：数据图谱 -->
        <div class="graph-section">
          <h3>数据图谱</h3>
          <div class="graph-placeholder">
            <a-empty description="数据图谱区域" />
          </div>
        </div>
        
        <!-- 底部：事件脉络 -->
        <div class="timeline-section">
          <h3>事件脉络</h3>
          <div class="timeline-placeholder">
            <a-empty description="事件脉络区域" />
          </div>
        </div>
      </div>
    </div>
    
    <!-- 修改标签弹窗 -->
    <a-modal
      v-model:open="modifyTagsModalVisible"
      title="修改标签"
      @ok="handleModifyTags"
      @cancel="handleModifyTagsCancel"
    >
      <a-form>
        <a-form-item label="标签">
          <a-select
            mode="tags"
            v-model:value="modifyTagsValue"
            placeholder="请输入标签"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>
    
    <!-- 修改状态弹窗 -->
    <a-modal
      v-model:open="modifyStatusModalVisible"
      title="修改数据状态"
      @ok="handleModifyStatus"
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
      modifyTagsModalVisible: false,
      modifyTagsValue: [],
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
      this.modifyTagsValue = [...this.record.tags]
      this.modifyTagsModalVisible = true
    },
    handleModifyTags() {
      this.record.tags = [...this.modifyTagsValue]
      this.modifyTagsModalVisible = false
      message.success('标签修改成功')
    },
    handleModifyTagsCancel() {
      this.modifyTagsModalVisible = false
    },
    showModifyStatusModal() {
      this.modifyStatusValue = this.record.dataStatus
      this.modifyStatusModalVisible = true
    },
    handleModifyStatus() {
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

.tags-section, .details-section, .action-buttons, .description-section, .graph-section, .timeline-section {
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

.description-content {
  margin-top: 12px;
  padding: 12px;
  background-color: #f5f5f5;
  border-radius: 4px;
  min-height: 80px;
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