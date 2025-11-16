<template>
  <div class="edit-spider-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <a-button @click="$router.back()" class="back-btn">
          <arrow-left-outlined />
          返回
        </a-button>
        <span class="page-title">编辑爬虫 - {{ spiderName }}</span>
      </div>
      <div class="header-actions">
        <a-button @click="handlePreview" type="dashed">
          <eye-outlined />
          预览配置
        </a-button>
      </div>
    </div>

    <!-- 表单区域 -->
    <div class="form-container">
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 16 }"
        size="large"
      >
        <!-- 基本信息 -->
        <a-divider orientation="left">基本信息</a-divider>
        
        <a-form-item label="爬虫ID" name="id">
          <a-input v-model:value="formData.id" disabled />
        </a-form-item>

        <a-form-item label="爬虫名称" name="name">
          <a-input v-model:value="formData.name" placeholder="请输入爬虫名称" />
        </a-form-item>

        <a-form-item label="爬虫描述" name="description">
          <a-textarea v-model:value="formData.description" placeholder="请输入爬虫描述" :rows="3" />
        </a-form-item>

        <a-form-item label="目标网站" name="targetUrl">
          <a-input v-model:value="formData.targetUrl" placeholder="请输入目标网站URL" />
        </a-form-item>

        <a-form-item label="数据类型" name="dataType">
          <a-select v-model:value="formData.dataType" placeholder="请选择数据类型">
            <a-select-option value="article">文章</a-select-option>
            <a-select-option value="image">图片</a-select-option>
            <a-select-option value="video">视频</a-select-option>
            <a-select-option value="product">商品</a-select-option>
            <a-select-option value="news">新闻</a-select-option>
            <a-select-option value="social">社交媒体</a-select-option>
          </a-select>
        </a-form-item>

        <!-- 爬取配置 -->
        <a-divider orientation="left">爬取配置</a-divider>

        <a-form-item label="爬取模式" name="crawlMode">
          <a-radio-group v-model:value="formData.crawlMode">
            <a-radio value="single">单页面</a-radio>
            <a-radio value="multi">多页面</a-radio>
            <a-radio value="recursive">递归爬取</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="爬取深度" name="depth">
          <a-input-number v-model:value="formData.depth" :min="1" :max="10" />
          <span class="help-text">递归爬取时的最大深度</span>
        </a-form-item>

        <a-form-item label="并发数量" name="concurrent">
          <a-input-number v-model:value="formData.concurrent" :min="1" :max="50" />
          <span class="help-text">同时进行的爬取任务数量</span>
        </a-form-item>

        <a-form-item label="请求间隔" name="interval">
          <a-input-number v-model:value="formData.interval" :min="0" :max="60" />
          <span class="help-text">每次请求之间的间隔时间（秒）</span>
        </a-form-item>

        <a-form-item label="超时时间" name="timeout">
          <a-input-number v-model:value="formData.timeout" :min="5" :max="300" />
          <span class="help-text">单个请求的超时时间（秒）</span>
        </a-form-item>

        <!-- 数据提取配置 -->
        <a-divider orientation="left">数据提取配置</a-divider>

        <a-form-item label="选择器配置" name="selectors">
          <div class="selector-config">
            <div v-for="(selector, index) in formData.selectors" :key="index" class="selector-item">
              <a-row :gutter="8">
                <a-col :span="6">
                  <a-input v-model:value="selector.name" placeholder="字段名称" />
                </a-col>
                <a-col :span="8">
                  <a-select v-model:value="selector.type" placeholder="选择器类型">
                    <a-select-option value="css">CSS选择器</a-select-option>
                    <a-select-option value="xpath">XPath</a-select-option>
                    <a-select-option value="regex">正则表达式</a-select-option>
                  </a-select>
                </a-col>
                <a-col :span="8">
                  <a-input v-model:value="selector.value" placeholder="选择器值" />
                </a-col>
                <a-col :span="2">
                  <a-button type="link" danger @click="removeSelector(index)" v-if="formData.selectors.length > 1">
                    <delete-outlined />
                  </a-button>
                </a-col>
              </a-row>
            </div>
            <a-button type="dashed" @click="addSelector" style="width: 100%">
              <plus-outlined />
              添加选择器
            </a-button>
          </div>
        </a-form-item>

        <a-form-item label="数据清洗" name="dataClean">
          <a-checkbox-group v-model:value="formData.dataClean">
            <a-checkbox value="trim">去除空白字符</a-checkbox>
            <a-checkbox value="html">去除HTML标签</a-checkbox>
            <a-checkbox value="special">去除特殊字符</a-checkbox>
          </a-checkbox-group>
        </a-form-item>

        <!-- 存储配置 -->
        <a-divider orientation="left">存储配置</a-divider>

        <a-form-item label="存储方式" name="storageType">
          <a-radio-group v-model:value="formData.storageType">
            <a-radio value="database">数据库存储</a-radio>
            <a-radio value="file">文件存储</a-radio>
            <a-radio value="api">API接口</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="数据库表" name="tableName" v-if="formData.storageType === 'database'">
          <a-input v-model:value="formData.tableName" placeholder="请输入数据库表名" />
        </a-form-item>

        <a-form-item label="文件格式" name="fileFormat" v-if="formData.storageType === 'file'">
          <a-select v-model:value="formData.fileFormat" placeholder="请选择文件格式">
            <a-select-option value="json">JSON</a-select-option>
            <a-select-option value="csv">CSV</a-select-option>
            <a-select-option value="xml">XML</a-select-option>
          </a-select>
        </a-form-item>

        <!-- 高级配置 -->
        <a-divider orientation="left">高级配置</a-divider>

        <a-form-item label="用户代理" name="userAgent">
          <a-textarea v-model:value="formData.userAgent" placeholder="请输入用户代理字符串" :rows="2" />
        </a-form-item>

        <a-form-item label="请求头" name="headers">
          <a-textarea v-model:value="formData.headers" placeholder="请输入自定义请求头，格式：key: value" :rows="3" />
        </a-form-item>

        <a-form-item label="Cookie" name="cookies">
          <a-textarea v-model:value="formData.cookies" placeholder="请输入Cookie字符串" :rows="2" />
        </a-form-item>

        <a-form-item label="代理设置" name="proxy">
          <a-input v-model:value="formData.proxy" placeholder="请输入代理服务器地址，如：http://proxy:8080" />
        </a-form-item>

        <!-- 提交按钮 -->
        <a-form-item :wrapper-col="{ span: 16, offset: 4 }">
          <a-space>
            <a-button type="primary" @click="handleSubmit" :loading="submitLoading">
              保存修改
            </a-button>
            <a-button @click="handleTest">测试连接</a-button>
            <a-button @click="handleReset">重置表单</a-button>
            <a-button @click="handleCancel">取消</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </div>

    <!-- 配置预览弹窗 -->
    <a-modal v-model:open="showPreviewModal" title="配置预览" width="800px" @cancel="showPreviewModal = false">
      <pre class="config-preview">{{ JSON.stringify(formData, null, 2) }}</pre>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  ArrowLeftOutlined,
  DeleteOutlined,
  PlusOutlined,
  EyeOutlined
} from '@ant-design/icons-vue'

export default defineComponent({
  name: 'EditSpider',
  components: {
    ArrowLeftOutlined,
    DeleteOutlined,
    PlusOutlined,
    EyeOutlined
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const formRef = ref()
    const submitLoading = ref(false)
    const showPreviewModal = ref(false)
    
    const spiderId = route.params.spiderId
    const spiderName = ref('')

    // 表单数据
    const formData = reactive({
      id: '',
      name: '',
      description: '',
      targetUrl: '',
      dataType: '',
      crawlMode: 'single',
      depth: 1,
      concurrent: 5,
      interval: 1,
      timeout: 30,
      selectors: [
        { name: 'title', type: 'css', value: 'h1' },
        { name: 'content', type: 'css', value: '.content' }
      ],
      dataClean: ['trim'],
      storageType: 'database',
      tableName: '',
      fileFormat: 'json',
      userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
      headers: '',
      cookies: '',
      proxy: ''
    })

    // 表单验证规则
    const formRules = {
      name: [
        { required: true, message: '请输入爬虫名称', trigger: 'blur' },
        { min: 2, max: 50, message: '名称长度应在2-50个字符之间', trigger: 'blur' }
      ],
      description: [
        { required: true, message: '请输入爬虫描述', trigger: 'blur' }
      ],
      targetUrl: [
        { required: true, message: '请输入目标网站URL', trigger: 'blur' },
        { pattern: /^https?:\/\/.+/, message: '请输入有效的URL地址', trigger: 'blur' }
      ],
      dataType: [
        { required: true, message: '请选择数据类型', trigger: 'change' }
      ],
      crawlMode: [
        { required: true, message: '请选择爬取模式', trigger: 'change' }
      ],
      depth: [
        { required: true, message: '请输入爬取深度', trigger: 'blur' }
      ],
      concurrent: [
        { required: true, message: '请输入并发数量', trigger: 'blur' }
      ],
      interval: [
        { required: true, message: '请输入请求间隔', trigger: 'blur' }
      ],
      timeout: [
        { required: true, message: '请输入超时时间', trigger: 'blur' }
      ],
      storageType: [
        { required: true, message: '请选择存储方式', trigger: 'change' }
      ],
      tableName: [
        { required: true, message: '请输入数据库表名', trigger: 'blur' }
      ]
    }

    // 模拟加载爬虫数据
    const loadSpiderData = () => {
      // 模拟API调用
      setTimeout(() => {
        const mockData = {
          id: spiderId,
          name: '新闻爬虫示例',
          description: '这是一个用于抓取新闻网站内容的新闻爬虫',
          targetUrl: 'https://news.example.com',
          dataType: 'news',
          crawlMode: 'multi',
          depth: 3,
          concurrent: 10,
          interval: 2,
          timeout: 60,
          selectors: [
            { name: 'title', type: 'css', value: 'h1.title' },
            { name: 'content', type: 'css', value: '.article-content' },
            { name: 'author', type: 'css', value: '.author-name' },
            { name: 'publish_time', type: 'css', value: '.publish-date' }
          ],
          dataClean: ['trim', 'html'],
          storageType: 'database',
          tableName: 'news_articles',
          fileFormat: 'json',
          userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36',
          headers: 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
          cookies: 'session_id=abc123; user_pref=zh-CN',
          proxy: ''
        }
        
        // 填充表单数据
        Object.keys(mockData).forEach(key => {
          if (formData.hasOwnProperty(key)) {
            formData[key] = mockData[key]
          }
        })
        
        spiderName.value = mockData.name
      }, 500)
    }

    // 添加选择器
    const addSelector = () => {
      formData.selectors.push({ name: '', type: 'css', value: '' })
    }

    // 删除选择器
    const removeSelector = (index) => {
      formData.selectors.splice(index, 1)
    }

    // 提交表单
    const handleSubmit = async () => {
      try {
        await formRef.value.validate()
        submitLoading.value = true
        
        // 模拟提交过程
        setTimeout(() => {
          message.success('爬虫修改成功')
          submitLoading.value = false
          // 返回爬虫列表页面
          router.push('/view/all_spider')
        }, 2000)
      } catch (error) {
        console.error('表单验证失败:', error)
      }
    }

    // 测试连接
    const handleTest = async () => {
      try {
        await formRef.value.validateFields(['targetUrl'])
        message.loading('正在测试连接...', 0)
        
        // 模拟测试过程
        setTimeout(() => {
          message.destroy()
          message.success('连接测试成功')
        }, 1500)
      } catch (error) {
        message.error('请先填写目标网站URL')
      }
    }

    // 重置表单
    const handleReset = () => {
      loadSpiderData()
      message.success('表单已重置')
    }

    // 取消编辑
    const handleCancel = () => {
      router.back()
    }

    // 预览配置
    const handlePreview = () => {
      showPreviewModal.value = true
    }

    // 组件挂载时加载数据
    onMounted(() => {
      loadSpiderData()
    })

    return {
      formRef,
      formData,
      formRules,
      submitLoading,
      showPreviewModal,
      spiderName,
      addSelector,
      removeSelector,
      handleSubmit,
      handleTest,
      handleReset,
      handleCancel,
      handlePreview
    }
  }
})
</script>

<style scoped>
.edit-spider-container {
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

.form-container {
  padding: 24px;
  max-width: 800px;
}

.selector-config {
  background-color: #fafafa;
  padding: 16px;
  border-radius: 6px;
  border: 1px solid #d9d9d9;
}

.selector-item {
  margin-bottom: 8px;
}

.help-text {
  margin-left: 8px;
  color: #8c8c8c;
  font-size: 12px;
}

.config-preview {
  background-color: #f5f5f5;
  padding: 16px;
  border-radius: 6px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.5;
  max-height: 400px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
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
  
  .form-container {
    padding: 16px;
  }
  
  .selector-config {
    padding: 12px;
  }
}
</style>