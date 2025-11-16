<template>
  <div class="search-source-management">
    <a-card title="搜索数据源管理" class="source-card">
      <template #extra>
        <a-space>
          <a-button type="primary" @click="showAddModal">
            <template #icon><PlusOutlined /></template>
            新增数据源
          </a-button>
          <a-button @click="refreshData">
            <template #icon><ReloadOutlined /></template>
            刷新
          </a-button>
        </a-space>
      </template>

      <!-- 搜索区域 -->
      <div class="search-area">
        <a-form layout="inline" :model="searchForm">
          <a-form-item label="数据源名称">
            <a-input v-model:value="searchForm.name" placeholder="请输入数据源名称" allowClear />
          </a-form-item>
          <a-form-item label="数据源类型">
            <a-select v-model:value="searchForm.type" placeholder="请选择类型" allowClear style="width: 150px">
              <a-select-option value="file">文件</a-select-option>
              <a-select-option value="database">数据库</a-select-option>
              <a-select-option value="api">API接口</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item>
            <a-space>
              <a-button type="primary" @click="handleSearch">
                <template #icon><SearchOutlined /></template>
                搜索
              </a-button>
              <a-button @click="handleReset">
                <template #icon><ClearOutlined /></template>
                重置
              </a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </div>

      <!-- 表格 -->
      <a-table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'type'">
            <a-tag :color="getTypeColor(record.type)">
              {{ getTypeLabel(record.type) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-badge :status="record.status === 1 ? 'success' : 'error'" :text="record.status === 1 ? '启用' : '禁用'" />
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="showEditModal(record)">编辑</a-button>
              <a-button type="link" size="small" danger @click="handleDelete(record)">删除</a-button>
              <a-button type="link" size="small" @click="showTestModal(record)">测试</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑模态框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      width="600px"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
      :confirmLoading="modalLoading"
    >
      <a-form ref="formRef" :model="formData" :rules="formRules" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="数据源名称" name="name">
          <a-input v-model:value="formData.name" placeholder="请输入数据源名称" />
        </a-form-item>
        <a-form-item label="数据源类型" name="type">
          <a-select v-model:value="formData.type" placeholder="请选择数据源类型" @change="handleTypeChange">
            <a-select-option value="file">文件</a-select-option>
            <a-select-option value="database">数据库</a-select-option>
            <a-select-option value="api">API接口</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="连接配置" name="config">
          <a-textarea 
            v-model:value="formData.config" 
            placeholder="请输入连接配置（JSON格式）"
            :rows="4"
            @blur="validateJson"
          />
        </a-form-item>
        <a-form-item label="描述" name="description">
          <a-textarea v-model:value="formData.description" placeholder="请输入数据源描述" :rows="3" />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="formData.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 测试连接模态框 -->
    <a-modal
      v-model:open="testModalVisible"
      title="测试数据源连接"
      width="400px"
      @ok="handleTestOk"
      @cancel="handleTestCancel"
      :confirmLoading="testLoading"
    >
      <div class="test-content">
        <p>数据源：{{ testRecord?.name }}</p>
        <p>类型：{{ getTypeLabel(testRecord?.type) }}</p>
        <div v-if="testResult" class="test-result">
          <a-alert
            :message="testResult.success ? '连接成功' : '连接失败'"
            :description="testResult.message"
            :type="testResult.success ? 'success' : 'error'"
            show-icon
          />
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script>
import { reactive, ref, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
  ClearOutlined
} from '@ant-design/icons-vue'

export default {
  name: 'SearchSourceManagement',
  components: {
    PlusOutlined,
    ReloadOutlined,
    SearchOutlined,
    ClearOutlined
  },
  setup() {
    const formRef = ref()
    const loading = ref(false)
    const modalVisible = ref(false)
    const modalLoading = ref(false)
    const testModalVisible = ref(false)
    const testLoading = ref(false)
    const isEditMode = ref(false)
    const testRecord = ref(null)
    const testResult = ref(null)

    const searchForm = reactive({
      name: '',
      type: undefined
    })

    const formData = reactive({
      name: '',
      type: 'file',
      config: '',
      description: '',
      status: 1
    })

    const formRules = {
      name: [{ required: true, message: '请输入数据源名称', trigger: 'blur' }],
      type: [{ required: true, message: '请选择数据源类型', trigger: 'change' }],
      config: [
        { required: true, message: '请输入连接配置', trigger: 'blur' },
        { validator: validateJsonFormat, trigger: 'blur' }
      ]
    }

    const columns = [
      {
        title: '数据源名称',
        dataIndex: 'name',
        key: 'name',
        width: '20%'
      },
      {
        title: '数据源类型',
        dataIndex: 'type',
        key: 'type',
        width: '15%'
      },
      {
        title: '描述',
        dataIndex: 'description',
        key: 'description',
        ellipsis: true
      },
      {
        title: '状态',
        dataIndex: 'status',
        key: 'status',
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
        width: '20%'
      }
    ]

    const dataSource = ref([])

    const pagination = reactive({
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: (total) => `共 ${total} 条数据`
    })

    const modalTitle = computed(() => isEditMode.value ? '编辑数据源' : '新增数据源')

    function validateJsonFormat(rule, value) {
      if (!value) return Promise.resolve()
      try {
        JSON.parse(value)
        return Promise.resolve()
      } catch (error) {
        return Promise.reject('JSON格式不正确')
      }
    }

    function validateJson() {
      if (formData.config) {
        try {
          JSON.parse(formData.config)
        } catch (error) {
          message.error('JSON格式不正确')
        }
      }
    }

    function getTypeColor(type) {
      const colors = {
        file: 'blue',
        database: 'green',
        api: 'orange'
      }
      return colors[type] || 'default'
    }

    function getTypeLabel(type) {
      const labels = {
        file: '文件',
        database: '数据库',
        api: 'API接口'
      }
      return labels[type] || type
    }

    function handleTypeChange() {
      // 根据类型设置默认配置
      const defaultConfigs = {
        file: '{"path": "/path/to/files", "extensions": [".txt", ".pdf", ".doc"]}',
        database: '{"host": "localhost", "port": 3306, "database": "search_db", "username": "root"}',
        api: '{"url": "https://api.example.com", "method": "GET", "headers": {}}'
      }
      formData.config = defaultConfigs[formData.type] || ''
    }

    function handleSearch() {
      pagination.current = 1
      loadData()
    }

    function handleReset() {
      searchForm.name = ''
      searchForm.type = undefined
      handleSearch()
    }

    function handleTableChange(paginationInfo) {
      pagination.current = paginationInfo.current
      pagination.pageSize = paginationInfo.pageSize
      loadData()
    }

    function showAddModal() {
      isEditMode.value = false
      resetForm()
      modalVisible.value = true
    }

    function showEditModal(record) {
      isEditMode.value = true
      Object.assign(formData, record)
      modalVisible.value = true
    }

    function showTestModal(record) {
      testRecord.value = record
      testResult.value = null
      testModalVisible.value = true
    }

    function resetForm() {
      formData.name = ''
      formData.type = 'file'
      formData.config = ''
      formData.description = ''
      formData.status = 1
    }

    function handleModalOk() {
      formRef.value.validate().then(() => {
        if (isEditMode.value) {
          updateData()
        } else {
          addData()
        }
      }).catch(error => {
        console.error('表单验证失败:', error)
      })
    }

    function handleModalCancel() {
      modalVisible.value = false
      resetForm()
    }

    function handleTestOk() {
      testLoading.value = true
      // 模拟测试连接
      setTimeout(() => {
        testResult.value = {
          success: Math.random() > 0.3,
          message: Math.random() > 0.3 ? '连接成功' : '连接失败，请检查配置'
        }
        testLoading.value = false
      }, 1500)
    }

    function handleTestCancel() {
      testModalVisible.value = false
      testRecord.value = null
      testResult.value = null
    }

    function handleDelete(record) {
      // 模拟删除
      const index = dataSource.value.findIndex(item => item.id === record.id)
      if (index > -1) {
        dataSource.value.splice(index, 1)
        message.success('删除成功')
      }
    }

    function refreshData() {
      loadData()
    }

    function loadData() {
      loading.value = true
      // 模拟数据
      setTimeout(() => {
        const mockData = [
          {
            id: 1,
            name: '本地文档库',
            type: 'file',
            description: '本地文档文件搜索数据源',
            config: '{"path": "/data/documents", "extensions": [".txt", ".pdf", ".doc", ".docx"]}',
            status: 1,
            createTime: '2024-01-15 10:30:00'
          },
          {
            id: 2,
            name: '知识库数据库',
            type: 'database',
            description: '知识图谱数据库搜索数据源',
            config: '{"host": "localhost", "port": 3306, "database": "knowledge_base", "username": "root"}',
            status: 1,
            createTime: '2024-01-16 14:20:00'
          },
          {
            id: 3,
            name: '外部API接口',
            type: 'api',
            description: '外部系统API搜索数据源',
            config: '{"url": "https://api.example.com/search", "method": "POST", "headers": {"Authorization": "Bearer token"}}',
            status: 0,
            createTime: '2024-01-17 09:15:00'
          }
        ]

        // 搜索过滤
        let filteredData = mockData
        if (searchForm.name) {
          filteredData = filteredData.filter(item => 
            item.name.toLowerCase().includes(searchForm.name.toLowerCase())
          )
        }
        if (searchForm.type) {
          filteredData = filteredData.filter(item => item.type === searchForm.type)
        }

        dataSource.value = filteredData
        pagination.total = filteredData.length
        loading.value = false
      }, 500)
    }

    function addData() {
      modalLoading.value = true
      setTimeout(() => {
        const newData = {
          ...formData,
          id: Date.now(),
          createTime: new Date().toLocaleString('zh-CN')
        }
        dataSource.value.unshift(newData)
        modalVisible.value = false
        modalLoading.value = false
        message.success('新增成功')
      }, 1000)
    }

    function updateData() {
      modalLoading.value = true
      setTimeout(() => {
        const index = dataSource.value.findIndex(item => item.id === formData.id)
        if (index > -1) {
          dataSource.value[index] = { ...formData }
        }
        modalVisible.value = false
        modalLoading.value = false
        message.success('更新成功')
      }, 1000)
    }

    onMounted(() => {
      loadData()
    })

    return {
      formRef,
      loading,
      modalVisible,
      modalLoading,
      testModalVisible,
      testLoading,
      isEditMode,
      testRecord,
      testResult,
      searchForm,
      formData,
      formRules,
      columns,
      dataSource,
      pagination,
      modalTitle,
      getTypeColor,
      getTypeLabel,
      handleSearch,
      handleReset,
      handleTableChange,
      showAddModal,
      showEditModal,
      showTestModal,
      handleModalOk,
      handleModalCancel,
      handleTestOk,
      handleTestCancel,
      handleDelete,
      refreshData,
      validateJson
    }
  }
}
</script>

<style scoped>
.search-source-management {
  height: 100%;
  padding: 24px;
  background: #f0f2f5;
}

.source-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.search-area {
  margin-bottom: 24px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
}

.test-content {
  text-align: center;
}

.test-result {
  margin-top: 16px;
}

:deep(.ant-card-body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

:deep(.ant-table-wrapper) {
  flex: 1;
}
</style>