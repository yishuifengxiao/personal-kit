<template>
  <div class="edit-page">
    <a-page-header
      :title="isEdit ? '编辑数据' : '新增数据'"
      @back="goBack"
    />
    
    <div class="edit-container">
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
              <a-input v-model:value="formData.title" placeholder="请输入标题" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="URL" name="url">
              <a-input v-model:value="formData.url" placeholder="请输入URL" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="数据来源" name="dataSource">
              <a-select v-model:value="formData.dataSource" placeholder="请选择数据来源">
                <a-select-option value="api-import">API导入</a-select-option>
                <a-select-option value="manual">手动创建</a-select-option>
                <a-select-option value="third-party">第三方链接</a-select-option>
                <a-select-option value="crawler">数据爬虫</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="数据状态" name="dataStatus">
              <a-radio-group v-model:value="formData.dataStatus">
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
              <a-select v-model:value="formData.syncStatus" placeholder="请选择同步状态">
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
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item label="描述" name="description">
          <a-textarea 
            v-model:value="formData.description" 
            placeholder="请输入描述"
            :rows="4"
          />
        </a-form-item>
        
        <a-form-item label="标签" name="tags">
          <a-select
            mode="tags"
            v-model:value="formData.tags"
            placeholder="请输入标签"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
      
      <!-- 操作按钮 -->
      <div class="action-buttons">
        <a-space>
          <a-button type="primary" @click="handleSubmit">保存</a-button>
          <a-button @click="handleReset">重置</a-button>
          <a-button @click="goBack">取消</a-button>
        </a-space>
      </div>
    </div>
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
      }
    }
  },
  mounted() {
    // 检查是否是编辑模式
    const id = this.$route.params.id
    if (id) {
      this.isEdit = true
      this.loadRecord(id)
    }
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
    }
  }
}
</script>

<style scoped>
.edit-page {
  height: 100%;
  background-color: #f5f5f5;
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