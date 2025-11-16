<template>
  <div class="spider-data-container">
    <!-- 搜索条件区域 -->
    <div class="search-header">
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="名称" name="name">
          <a-input v-model:value="searchForm.name" placeholder="请输入数据名称" allowClear class="search-input" />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-select v-model:value="searchForm.status" placeholder="请选择状态" allowClear class="search-select" style="width: 120px">
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="active">启用</a-select-option>
            <a-select-option value="inactive">停用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch" class="search-btn">确认</a-button>
        </a-form-item>
        <a-form-item>
          <a-button @click="handleReset" class="reset-btn">重置</a-button>
        </a-form-item>
      </a-form>
      <div class="header-actions">
        <a-button danger @click="handleBatchDelete" :disabled="selectedRowKeys.length === 0">
          <delete-outlined />
          批量删除
        </a-button>
        <a-button type="primary" @click="handleAddData" class="add-btn-float">
          <plus-outlined />
          添加数据
        </a-button>
      </div>
    </div>

    <!-- 数据表格 -->
  <div class="table-container">
    <a-table 
      :columns="columns" 
      :data-source="dataList" 
      :pagination="paginationConfig"
      :loading="loading"
      :row-selection="rowSelection"
      rowKey="id"
      size="middle"
      @change="handleTableChange"
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
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="handleEdit(record)">
              <edit-outlined />
              编辑
            </a-button>
            <a-button type="link" size="small" @click="handleDetail(record)">
              <eye-outlined />
              详情
            </a-button>
            <a-popconfirm title="确定要删除这条数据吗？" @confirm="handleDelete(record)" okText="确定" cancelText="取消">
              <a-button type="link" danger size="small">
                <delete-outlined />
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>

  <!-- 编辑/添加数据模态框 -->
  <a-modal 
    v-model:open="modalVisible" 
    :title="modalTitle" 
    width="800px" 
    @ok="handleModalOk"
    @cancel="handleModalCancel"
  >
    <a-form ref="dataFormRef" :model="dataForm" :rules="dataFormRules" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
      <a-form-item label="数据名称" name="name">
        <a-input v-model:value="dataForm.name" placeholder="请输入数据名称" />
      </a-form-item>
      <a-form-item label="数据来源" name="source">
        <a-input v-model:value="dataForm.source" placeholder="请输入数据来源" />
      </a-form-item>
      <a-form-item label="数据类型" name="dataType">
        <a-radio-group v-model:value="dataForm.dataType" @change="handleDataTypeChange" style="display: flex; flex-wrap: wrap; gap: 16px;">
          <a-radio value="text">文本</a-radio>
          <a-radio value="json">JSON</a-radio>
          <a-radio value="richtext">富文本</a-radio>
          <a-radio value="file">文件</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label="状态" name="status">
        <a-radio-group v-model:value="dataForm.status">
          <a-radio value="active">启用</a-radio>
          <a-radio value="inactive">停用</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label="描述" name="description">
        <a-textarea v-model:value="dataForm.description" placeholder="请输入数据描述" :rows="2" />
      </a-form-item>
      
      <!-- 文本数据内容 -->
      <a-form-item v-if="dataForm.dataType === 'text'" label="数据内容" name="content">
        <a-textarea v-model:value="dataForm.content" placeholder="请输入文本数据内容" :rows="6" />
      </a-form-item>
      
      <!-- JSON数据内容 -->
      <a-form-item v-if="dataForm.dataType === 'json'" label="JSON数据" name="content">
        <a-textarea v-model:value="dataForm.content" placeholder='请输入JSON格式数据，如：{"key": "value"}' :rows="8" />
      </a-form-item>
      
      <!-- 富文本数据内容 -->
      <a-form-item v-if="dataForm.dataType === 'richtext'" label="富文本内容" name="content">
        <a-textarea v-model:value="dataForm.content" placeholder="请输入富文本内容" :rows="8" />
        <div style="color: #999; font-size: 12px; margin-top: 4px;">支持HTML标签的富文本内容</div>
      </a-form-item>
      
      <!-- 文件数据内容 -->
      <a-form-item v-if="dataForm.dataType === 'file'" label="文件信息">
        <a-input v-model:value="dataForm.fileName" placeholder="请输入文件名称" />
        <div style="margin-top: 8px;">
          <a-input v-model:value="dataForm.fileUrl" placeholder="请输入文件链接地址" />
        </div>
        <div style="color: #999; font-size: 12px; margin-top: 4px;">支持音视频、图片、Excel、Word、PDF等文件类型的链接</div>
      </a-form-item>
    </a-form>
  </a-modal>

  <!-- 详情模态框 -->
  <a-modal 
    v-model:open="detailModalVisible" 
    title="数据详情" 
    width="800px" 
    :footer="null"
    @cancel="handleDetailModalCancel"
  >
    <a-descriptions :column="2" bordered>
      <a-descriptions-item label="数据ID">{{ currentRecord.id }}</a-descriptions-item>
      <a-descriptions-item label="数据名称">{{ currentRecord.name }}</a-descriptions-item>
      <a-descriptions-item label="创建时间">{{ currentRecord.createTime }}</a-descriptions-item>
      <a-descriptions-item label="数据来源">{{ currentRecord.source }}</a-descriptions-item>
      <a-descriptions-item label="数据类型">{{ getDataTypeText(currentRecord.dataType) }}</a-descriptions-item>
      <a-descriptions-item label="状态">
        <a-tag :color="getStatusColor(currentRecord.status)">
          {{ getStatusText(currentRecord.status) }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="描述" :span="2">
        {{ currentRecord.description || '暂无描述' }}
      </a-descriptions-item>
    </a-descriptions>
    
    <!-- 数据内容展示区域 -->
    <div style="margin-top: 16px;">
      <a-divider orientation="left">数据内容</a-divider>
      
      <!-- 文本数据 -->
      <div v-if="currentRecord.dataType === 'text'" class="data-content-area">
        <div class="content-label">文本内容：</div>
        <div class="text-content">{{ currentRecord.content || '暂无文本数据' }}</div>
      </div>
      
      <!-- JSON数据 -->
      <div v-else-if="currentRecord.dataType === 'json'" class="data-content-area">
        <div class="content-label">JSON数据：</div>
        <pre class="json-content">{{ formatJsonContent(currentRecord.content) || '暂无JSON数据' }}</pre>
      </div>
      
      <!-- 富文本数据 -->
      <div v-else-if="currentRecord.dataType === 'richtext'" class="data-content-area">
        <div class="content-label">富文本内容：</div>
        <div class="richtext-content" v-html="currentRecord.content || '<p>暂无富文本数据</p>'"></div>
      </div>
      
      <!-- 文件数据 -->
      <div v-else-if="currentRecord.dataType === 'file'" class="data-content-area">
        <div class="content-label">文件信息：</div>
        <div class="file-info">
          <div><strong>文件名称：</strong>{{ currentRecord.fileName || '未命名文件' }}</div>
          <div style="margin-top: 8px;">
            <strong>文件链接：</strong>
            <a v-if="currentRecord.fileUrl" :href="currentRecord.fileUrl" target="_blank" rel="noopener noreferrer">
              {{ currentRecord.fileUrl }}
            </a>
            <span v-else>暂无文件链接</span>
          </div>
          <div style="margin-top: 8px; color: #666; font-size: 12px;">
            支持音视频、图片、Excel、Word、PDF等文件类型
          </div>
        </div>
      </div>
      
      <!-- 未知类型 -->
      <div v-else class="data-content-area">
        <div class="content-label">数据内容：</div>
        <div style="color: #999; padding: 16px; background: #f5f5f5; border-radius: 4px;">
          暂无数据内容
        </div>
      </div>
    </div>
  </a-modal>
  </div>
</template>

<script>
import { defineComponent, reactive, ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  EyeOutlined,
  SearchOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'

export default defineComponent({
  name: 'SpiderData',
  components: {
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
    EyeOutlined,
    SearchOutlined,
    ReloadOutlined
  },
  setup() {
    const router = useRouter()
    
    // 搜索表单
    const searchForm = reactive({
      name: '',
      status: ''
    })

    // 选中的行
    const selectedRowKeys = ref([])

    // 表格列配置
    const columns = [
      {
        title: '数据ID',
        dataIndex: 'id',
        key: 'id',
        width: 120
      },
      {
        title: '数据名称',
        dataIndex: 'name',
        key: 'name',
        width: 200
      },
      {
        title: '数据类型',
        dataIndex: 'dataType',
        key: 'dataType',
        width: 100,
        filters: [
          { text: '文本', value: 'text' },
          { text: 'JSON', value: 'json' },
          { text: '富文本', value: 'richtext' },
          { text: '文件', value: 'file' }
        ]
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
        key: 'createTime',
        width: 160,
        sorter: true
      },
      {
        title: '数据来源',
        dataIndex: 'source',
        key: 'source',
        width: 150
      },
      {
        title: '状态',
        dataIndex: 'status',
        key: 'status',
        width: 100,
        filters: [
          { text: '启用', value: 'active' },
          { text: '停用', value: 'inactive' }
        ]
      },
      {
        title: '操作',
        key: 'action',
        width: 150,
        fixed: 'right'
      }
    ]

    // 数据列表
    const dataList = ref([])
    const loading = ref(false)

    // 分页配置
    const pagination = reactive({
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: total => `共 ${total} 条数据`
    })

    // 模态框状态
    const modalVisible = ref(false)
    const detailModalVisible = ref(false)
    const modalTitle = ref('')
    const currentRecord = ref({})
    const dataFormRef = ref()

    // 表单数据
    const dataForm = reactive({
      id: '',
      name: '',
      source: '',
      dataType: 'text',
      status: 'active',
      description: '',
      content: '',
      fileName: '',
      fileUrl: ''
    })

    // 表单验证规则
    const dataFormRules = {
      name: [{ required: true, message: '请输入数据名称', trigger: 'blur' }],
      source: [{ required: true, message: '请输入数据来源', trigger: 'blur' }],
      status: [{ required: true, message: '请选择状态', trigger: 'change' }]
    }

    // 行选择配置
    const rowSelection = computed(() => ({
      selectedRowKeys: selectedRowKeys.value,
      onChange: (keys) => {
        selectedRowKeys.value = keys
      }
    }))

    // 获取状态颜色
    const getStatusColor = (status) => {
      const colorMap = {
        'active': 'green',
        'inactive': 'red'
      }
      return colorMap[status] || 'default'
    }

    // 获取数据类型颜色
    const getDataTypeColor = (dataType) => {
      const colorMap = {
        text: 'blue',
        json: 'orange',
        richtext: 'purple',
        file: 'cyan'
      }
      return colorMap[dataType] || 'default'
    }

    // 获取状态文本
    const getStatusText = (status) => {
      const textMap = {
        'active': '启用',
        'inactive': '停用'
      }
      return textMap[status] || status
    }

    // 获取数据类型文字
    const getDataTypeText = (dataType) => {
      const typeMap = {
        text: '文本',
        json: 'JSON',
        richtext: '富文本',
        file: '文件'
      }
      return typeMap[dataType] || '未知'
    }

    // 处理数据类型变化
    const handleDataTypeChange = () => {
      // 清空之前的数据内容，使用nextTick避免触发验证
      nextTick(() => {
        dataForm.content = ''
        dataForm.fileName = ''
        dataForm.fileUrl = ''
      })
    }

    // 格式化JSON内容
    const formatJsonContent = (content) => {
      if (!content) return ''
      try {
        // 如果是JSON字符串，尝试格式化
        const parsed = JSON.parse(content)
        return JSON.stringify(parsed, null, 2)
      } catch {
        // 如果不是有效的JSON，返回原内容
        return content
      }
    }

    // 搜索
    const handleSearch = () => {
      pagination.current = 1
      loadData()
      message.success('搜索完成')
    }

    // 重置
    const handleReset = () => {
      searchForm.name = ''
      searchForm.status = ''
      pagination.current = 1
      loadData()
      message.success('搜索条件已重置')
    }

    // 表格变化
    const handleTableChange = (pag, filters, sorter) => {
      pagination.current = pag.current
      pagination.pageSize = pag.pageSize
      loadData()
    }

    // 添加数据
    const handleAddData = () => {
      modalTitle.value = '添加数据'
      // 重置表单
      Object.assign(dataForm, {
        id: '',
        name: '',
        source: '',
        dataType: 'text',
        status: 'active',
        description: '',
        content: '',
        fileName: '',
        fileUrl: ''
      })
      modalVisible.value = true
    }

    // 编辑
    const handleEdit = (record) => {
      modalTitle.value = '编辑数据'
      // 填充表单数据
      Object.assign(dataForm, {
        id: record.id,
        name: record.name,
        source: record.source,
        dataType: record.dataType || 'text',
        status: record.status,
        description: record.description || '',
        content: record.content || '',
        fileName: record.fileName || '',
        fileUrl: record.fileUrl || ''
      })
      modalVisible.value = true
    }

    // 详情
    const handleDetail = (record) => {
      currentRecord.value = record
      detailModalVisible.value = true
    }

    // 删除
    const handleDelete = (record) => {
      const index = dataList.value.findIndex(item => item.id === record.id)
      if (index > -1) {
        dataList.value.splice(index, 1)
        pagination.total--
        message.success('删除成功')
      }
    }

    // 批量删除
    const handleBatchDelete = () => {
      Modal.confirm({
        title: '确认批量删除',
        content: `确定要删除选中的 ${selectedRowKeys.value.length} 条数据吗？`,
        onOk() {
          dataList.value = dataList.value.filter(item => !selectedRowKeys.value.includes(item.id))
          pagination.total = dataList.value.length
          selectedRowKeys.value = []
          message.success('批量删除成功')
        }
      })
    }

    // 处理模态框确认
    const handleModalOk = () => {
      dataFormRef.value.validate().then(() => {
        if (dataForm.id) {
          // 编辑模式
          const index = dataList.value.findIndex(item => item.id === dataForm.id)
          if (index !== -1) {
            dataList.value[index] = {
              ...dataList.value[index],
              name: dataForm.name,
              source: dataForm.source,
              dataType: dataForm.dataType,
              status: dataForm.status,
              description: dataForm.description,
              content: dataForm.content,
              fileName: dataForm.fileName,
              fileUrl: dataForm.fileUrl
            }
          }
          message.success('编辑成功')
        } else {
          // 添加模式
          const newData = {
            id: `data_${Date.now()}`,
            name: dataForm.name,
            source: dataForm.source,
            dataType: dataForm.dataType,
            status: dataForm.status,
            description: dataForm.description,
            content: dataForm.content,
            fileName: dataForm.fileName,
            fileUrl: dataForm.fileUrl,
            createTime: new Date().toLocaleString()
          }
          dataList.value.unshift(newData)
          pagination.total++
          message.success('添加成功')
        }
        modalVisible.value = false
        // 重置表单
        dataFormRef.value.resetFields()
      }).catch(error => {
        console.log('表单验证失败:', error)
      })
    }

    // 处理模态框取消
    const handleModalCancel = () => {
      modalVisible.value = false
      dataFormRef.value.resetFields()
    }

    // 处理详情模态框取消
    const handleDetailModalCancel = () => {
      detailModalVisible.value = false
      currentRecord.value = {}
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
            id: `data_${i.toString().padStart(3, '0')}`,
            name: `数据项 ${i}`,
            createTime: new Date(Date.now() - Math.random() * 86400000 * 30).toLocaleString('zh-CN'),
            source: ['爬虫A', '爬虫B', '爬虫C', '手动导入'][Math.floor(Math.random() * 4)],
            status: Math.random() > 0.3 ? 'active' : 'inactive'
          })
        }
        
        // 根据搜索条件过滤
        let filteredData = mockData
        if (searchForm.name) {
          filteredData = filteredData.filter(item => item.name.includes(searchForm.name))
        }
        if (searchForm.status) {
          filteredData = filteredData.filter(item => item.status === searchForm.status)
        }
        
        dataList.value = filteredData
        pagination.total = filteredData.length
        
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
      searchForm,
      selectedRowKeys,
      columns,
      dataList,
      loading,
      paginationConfig: computed(() => ({
        ...pagination,
        current: pagination.current,
        pageSize: pagination.pageSize,
        total: pagination.total
      })),
      rowSelection,
      getStatusColor,
      getDataTypeColor,
      getStatusText,
      getDataTypeText,
      formatJsonContent,
      handleSearch,
      handleReset,
      handleTableChange,
      handleAddData,
      handleEdit,
      handleDetail,
      handleDelete,
      handleBatchDelete,
      handleDataTypeChange,
      // 模态框相关
      modalVisible,
      detailModalVisible,
      modalTitle,
      currentRecord,
      dataFormRef,
      dataForm,
      dataFormRules,
      handleModalOk,
      handleModalCancel,
      handleDetailModalCancel
    }
  }
})
</script>

<style scoped>
.spider-data-container {
  background: #fff;
  min-height: 100%;
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.search-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.search-input {
  width: 200px;
}

.search-select {
  width: 120px;
}

.search-btn,
.reset-btn {
  min-width: 60px;
}

.header-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.add-btn-float {
  align-self: center;
}

.table-container {
  padding: 16px 24px;
}

/* 数据内容展示样式 */
.data-content-area {
  margin-bottom: 16px;
}

.content-label {
  font-weight: 500;
  margin-bottom: 8px;
  color: #333;
}

.text-content {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 4px;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 300px;
  overflow-y: auto;
}

.json-content {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 14px;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 300px;
  overflow-y: auto;
}

.richtext-content {
  background: #fff;
  border: 1px solid #d9d9d9;
  padding: 16px;
  border-radius: 4px;
  max-height: 300px;
  overflow-y: auto;
}

.file-info {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 4px;
}

.file-info a {
  color: #1890ff;
  text-decoration: none;
}

.file-info a:hover {
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .header-actions {
    flex-wrap: wrap;
  }
  
  .search-form {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>