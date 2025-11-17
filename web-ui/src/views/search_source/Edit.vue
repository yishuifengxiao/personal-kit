<template>
  <div class="edit-page">
    <a-page-header
      :title="pageTitle"
      @back="goBack"
    />
    
    <!-- 详情模式 -->
    <div v-if="isDetail" class="detail-container">
      <!-- 左侧区域 -->
      <div class="left-section">
        <!-- 上部：数据标签 -->
        <div class="tags-section">
          <h3>数据标签</h3>
          <div class="tags-container">
            <a-tag
              v-for="(tag, index) in formData.tags"
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
            <a-descriptions-item label="标题">{{ formData.title }}</a-descriptions-item>
            <a-descriptions-item label="URL">
              <a :href="formData.url" target="_blank">{{ formData.url }}</a>
            </a-descriptions-item>
            <a-descriptions-item label="描述">{{ formData.description }}</a-descriptions-item>
            <a-descriptions-item label="数据来源">
              <a-tag :color="getDataSourceColor(formData.dataSource)">
                {{ getDataSourceLabel(formData.dataSource) }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="数据状态">
              <a-tag :color="getDataStatusColor(formData.dataStatus)">
                {{ getDataStatusText(formData.dataStatus) }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="同步状态">
              <a-badge :status="getSyncStatusStatus(formData.syncStatus)" :text="getSyncStatusLabel(formData.syncStatus)" />
            </a-descriptions-item>
            <a-descriptions-item label="同步时间">{{ formData.syncTime }}</a-descriptions-item>
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
            {{ formData.description || '暂无描述信息' }}
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
    
    <!-- 编辑/新增模式 -->
    <div v-else class="edit-container">
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
        class="edit-form"
      >
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="标题" name="title">
              <a-input v-model:value="formData.title" placeholder="请输入标题" :disabled="isDetail" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="URL" name="url">
              <a-input v-model:value="formData.url" placeholder="请输入URL" :disabled="isDetail" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="数据来源" name="dataSource">
              <a-select v-model:value="formData.dataSource" placeholder="请选择数据来源" :disabled="isDetail">
                <a-select-option value="api-import">API导入</a-select-option>
                <a-select-option value="manual">手动创建</a-select-option>
                <a-select-option value="third-party">第三方链接</a-select-option>
                <a-select-option value="crawler">数据爬虫</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="数据状态" name="dataStatus">
              <a-radio-group v-model:value="formData.dataStatus" :disabled="isDetail">
                <a-radio value="published">已发布</a-radio>
                <a-radio value="unpublished">未发布</a-radio>
                <a-radio value="deleted">已删除</a-radio>
                <a-radio value="abnormal">异常</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="同步状态" name="syncStatus">
              <a-select v-model:value="formData.syncStatus" placeholder="请选择同步状态" :disabled="isDetail">
                <a-select-option value="normal">正常</a-select-option>
                <a-select-option value="abnormal">异常</a-select-option>
                <a-select-option value="not-synced">未同步</a-select-option>
                <a-select-option value="syncing">同步中</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="同步时间" name="syncTime">
              <a-date-picker 
                v-model:value="formData.syncTime" 
                show-time 
                placeholder="请选择同步时间"
                style="width: 100%"
                :disabled="isDetail"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item label="描述" name="description">
          <a-textarea 
            v-model:value="formData.description" 
            placeholder="请输入描述"
            :rows="4"
            :disabled="isDetail"
          />
        </a-form-item>
        
        <a-form-item label="标签" name="tags">
          <a-select
            mode="tags"
            v-model:value="formData.tags"
            placeholder="请输入标签"
            style="width: 100%"
            :disabled="isDetail"
          />
        </a-form-item>
      </a-form>
      
      <!-- 操作按钮 -->
      <div v-if="!isDetail" class="action-buttons">
        <a-space>
          <a-button type="primary" @click="handleSubmit">保存</a-button>
          <a-button @click="handleReset">重置</a-button>
          <a-button @click="goBack">取消</a-button>
        </a-space>
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
import dayjs from 'dayjs'

export default {
  name: 'SearchSourceEdit',
  data() {
    return {
      isEdit: false,
      isDetail: false,
      formData: {
        title: '',
        url: '',
        description: '',
        dataSource: 'manual',
        dataStatus: 'unpublished',
        syncStatus: 'not-synced',
        syncTime: null,
        tags: []
      },
      formRules: {
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' }
        ],
        url: [
          { required: true, message: '请输入URL', trigger: 'blur' },
          { type: 'url', message: '请输入有效的URL', trigger: 'blur' }
        ],
        dataSource: [
          { required: true, message: '请选择数据来源', trigger: 'change' }
        ],
        dataStatus: [
          { required: true, message: '请选择数据状态', trigger: 'change' }
        ],
        syncStatus: [
          { required: true, message: '请选择同步状态', trigger: 'change' }
        ]
      },
      modifyTagsModalVisible: false,
      modifyTagsValue: [],
      modifyStatusModalVisible: false,
      modifyStatusValue: 'published'
    }
  },
  computed: {
    pageTitle() {
      if (this.isDetail) return '数据详情'
      return this.isEdit ? '编辑数据' : '新增数据'
    }
  },
  mounted() {
    // 检查模式：详情、编辑或新增
    const id = this.$route.params.id
    const path = this.$route.path
    
    if (path.includes('/detail/')) {
      // 详情模式
      this.isDetail = true
      if (id) {
        this.loadRecord(id)
      }
    } else if (id) {
      // 编辑模式
      this.isEdit = true
      this.loadRecord(id)
    }
    // 新增模式不需要额外处理
  },
  methods: {
    loadRecord(id) {
      // 模拟加载数据
      // 实际项目中这里应该调用API获取数据
      console.log('Loading record with ID:', id)
      
      // 模拟数据
      this.formData = {
        title: '示例数据标题',
        url: 'https://example.com',
        description: '这是一个示例数据的描述信息',
        dataSource: 'api-import',
        dataStatus: 'published',
        syncStatus: 'normal',
        syncTime: dayjs('2024-01-01 12:00:00'),
        tags: ['标签1', '标签2', '标签3']
      }
    },
    goBack() {
      this.$router.push('/view/my_search')
    },
    handleSubmit() {
      this.$refs.formRef.validate().then(() => {
        // 处理提交逻辑
        const action = this.isEdit ? '编辑' : '新增'
        message.success(`${action}成功`)
        this.goBack()
      }).catch(() => {
        message.error('请检查表单数据')
      })
    },
    handleReset() {
      if (this.isEdit) {
        // 重新加载数据
        const id = this.$route.params.id
        this.loadRecord(id)
      } else {
        // 重置为默认值
        this.$refs.formRef.resetFields()
      }
    },
    // 详情模式的方法
    handleEdit() {
      // 跳转到编辑页面
      const id = this.$route.params.id
      this.$router.push(`/view/my_search/edit/${id}`)
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
      this.modifyTagsValue = [...this.formData.tags]
      this.modifyTagsModalVisible = true
    },
    handleModifyTags() {
      this.formData.tags = [...this.modifyTagsValue]
      this.modifyTagsModalVisible = false
      message.success('标签修改成功')
    },
    handleModifyTagsCancel() {
      this.modifyTagsModalVisible = false
    },
    showModifyStatusModal() {
      this.modifyStatusValue = this.formData.dataStatus
      this.modifyStatusModalVisible = true
    },
    handleModifyStatus() {
      this.formData.dataStatus = this.modifyStatusValue
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
.edit-page {
  height: 100%;
  background-color: #f5f5f5;
}

/* 详情模式样式 */
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

.edit-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.edit-form {
  background: white;
  padding: 24px;
  border-radius: 6px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

.action-buttons {
  margin-top: 24px;
  text-align: center;
}
</style>