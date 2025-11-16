<template>
  <div class="page-content-wrapper">
    <!-- 上部搜索条件区域 -->
    <div class="search-area">
      <a-form
        layout="inline"
        name="basic"
        autocomplete="off"
        :model="searchForm"
        @finish="handleFinish"
      >
        <a-form-item label="数据源名称" name="name" class="input">
          <a-input allowClear v-model:value="searchForm.name" placeholder="数据源名称，模糊查询"> </a-input>
        </a-form-item>

        <a-form-item label="数据源类型" name="type" class="input">
          <a-select allowClear
            style="width: 180px"
            v-model:value="searchForm.type"
            placeholder="请选择类型"
          >
            <a-select-option value="file">文件</a-select-option>
            <a-select-option value="database">数据库</a-select-option>
            <a-select-option value="api">API接口</a-select-option>
          </a-select>
        </a-form-item>

        <a-space>
          <a-button type="primary" html-type="submit"> 搜索 </a-button>
          <a-button @click="handleReset"> 重置 </a-button>
          <a-button type="primary" @click="showAddModal"> 新增数据源 </a-button>
        </a-space>
      </a-form>
    </div>

    <!-- 中间内容区域 -->
    <div class="content-min-height">
      <!-- 表格区 -->
      <a-table :columns="columns" :data-source="dataSource" :pagination="false" :scroll="{ x: 1000 }" size="small">
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
            <a-space :size="2">
              <a-button type="link" size="small" @click="showEditModal(record)">编辑</a-button>
              <a-dropdown>
                <template #overlay>
                  <a-menu @click="({ key }) => handleOperationMenu(record, key)">
                    <a-menu-item key="test">测试连接</a-menu-item>
                    <a-menu-item key="delete" style="color: #ff4d4f;">删除</a-menu-item>
                  </a-menu>
                </template>
                <a-button type="link" size="small">
                  操作 <DownOutlined />
                </a-button>
              </a-dropdown>
            </a-space>
          </template>
        </template>
      </a-table>
      <!-- 表格区 -->

      <!-- 分页区 -->
      <div style="margin-top: 15px; float: right">
        <a-pagination
          v-model:current="pagination.current"
          :total="pagination.total"
          :show-total="(total) => `共 ${total} 条数据`"
          @change="onPaginationChange"
        />
      </div>
    </div>
    <!-- 中间内容区域 -->

    <!-- 新增/编辑数据源弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
      width="600px"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
        :rules="formRules"
      >
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
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
  ClearOutlined,
  DownOutlined
} from '@ant-design/icons-vue'

export default {
  name: 'SearchSourceManagement',
  components: {
    PlusOutlined,
    ReloadOutlined,
    SearchOutlined,
    ClearOutlined,
    DownOutlined
  },
  data() {
    return {
      formRef: null,
      loading: false,
      modalVisible: false,
      modalLoading: false,
      testModalVisible: false,
      testLoading: false,
      isEditMode: false,
      testRecord: null,
      testResult: null,
      searchForm: {
        name: '',
        type: undefined
      },
      formData: {
        name: '',
        type: 'file',
        config: '',
        description: '',
        status: 1
      },
      formRules: {
        name: [{ required: true, message: '请输入数据源名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择数据源类型', trigger: 'change' }],
        config: [
          { required: true, message: '请输入连接配置', trigger: 'blur' },
          { validator: this.validateJsonFormat, trigger: 'blur' }
        ]
      },
      dataSource: [],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
    computed: {
      modalTitle() {
        return this.isEditMode ? '编辑数据源' : '新增数据源'
      },
      columns() {
        return [
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
      }
    },
    mounted() {
      this.loadData()
    },
    methods: {

      validateJsonFormat(rule, value) {
        if (!value) return Promise.resolve()
        try {
          JSON.parse(value)
          return Promise.resolve()
        } catch (error) {
          return Promise.reject('JSON格式不正确')
        }
      },

      validateJson() {
        if (this.formData.config) {
          try {
            JSON.parse(this.formData.config)
          } catch (error) {
            message.error('JSON格式不正确')
          }
        }
      },

      getTypeColor(type) {
        const colors = {
          file: 'blue',
          database: 'green',
          api: 'orange'
        }
        return colors[type] || 'default'
      },

      getTypeLabel(type) {
        const labels = {
          file: '文件',
          database: '数据库',
          api: 'API接口'
        }
        return labels[type] || type
      },

      handleFinish() {
        this.pagination.current = 1
        this.loadData()
      },

      handleReset() {
        this.searchForm.name = ''
        this.searchForm.type = undefined
        this.pagination.current = 1
        this.loadData()
      },

      onPaginationChange(page, pageSize) {
        this.pagination.current = page
        this.pagination.pageSize = pageSize
        this.loadData()
      },

      handleTypeChange() {
        // 根据类型设置默认配置
        const defaultConfigs = {
          file: '{"path": "/path/to/files", "extensions": [".txt", ".pdf", ".doc"]}',
          database: '{"host": "localhost", "port": 3306, "database": "search_db", "username": "root"}',
          api: '{"url": "https://api.example.com", "method": "GET", "headers": {}}'
        }
        this.formData.config = defaultConfigs[this.formData.type] || ''
      },

      showAddModal() {
        this.isEditMode = false
        this.resetForm()
        this.modalVisible = true
      },

      showEditModal(record) {
        this.isEditMode = true
        Object.assign(this.formData, record)
        this.modalVisible = true
      },

      showTestModal(record) {
        this.testRecord = record
        this.testResult = null
        this.testModalVisible = true
      },

      resetForm() {
        this.formData.name = ''
        this.formData.type = 'file'
        this.formData.config = ''
        this.formData.description = ''
        this.formData.status = 1
      },

      handleModalOk() {
        this.$refs.formRef.validate().then(() => {
          if (this.isEditMode) {
            this.updateData()
          } else {
            this.addData()
          }
        }).catch(error => {
          console.error('表单验证失败:', error)
        })
      },

      handleModalCancel() {
        this.modalVisible = false
        this.resetForm()
      },

      handleTestOk() {
        this.testLoading = true
        // 模拟测试连接
        setTimeout(() => {
          this.testResult = {
            success: Math.random() > 0.3,
            message: Math.random() > 0.3 ? '连接成功' : '连接失败，请检查配置'
          }
          this.testLoading = false
        }, 1500)
      },

      handleTestCancel() {
        this.testModalVisible = false
        this.testRecord = null
        this.testResult = null
      },

      handleOperationMenu(record, key) {
        switch (key) {
          case 'test':
            this.showTestModal(record)
            break
          case 'delete':
            this.handleDelete(record)
            break
        }
      },

      handleDelete(record) {
        // 模拟删除
        const index = this.dataSource.findIndex(item => item.id === record.id)
        if (index > -1) {
          this.dataSource.splice(index, 1)
          message.success('删除成功')
        }
      },

      loadData() {
        this.loading = true
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
          if (this.searchForm.name) {
            filteredData = filteredData.filter(item => 
              item.name.toLowerCase().includes(this.searchForm.name.toLowerCase())
            )
          }
          if (this.searchForm.type) {
            filteredData = filteredData.filter(item => item.type === this.searchForm.type)
          }

          this.dataSource = filteredData
          this.pagination.total = filteredData.length
          this.loading = false
        }, 500)
      },

      addData() {
        this.modalLoading = true
        setTimeout(() => {
          const newData = {
            ...this.formData,
            id: Date.now(),
            createTime: new Date().toLocaleString('zh-CN')
          }
          this.dataSource.unshift(newData)
          this.modalVisible = false
          this.modalLoading = false
          message.success('新增成功')
        }, 1000)
      },

      updateData() {
        this.modalLoading = true
        setTimeout(() => {
          const index = this.dataSource.findIndex(item => item.id === this.formData.id)
          if (index > -1) {
            this.dataSource[index] = { ...this.formData }
          }
          this.modalVisible = false
          this.modalLoading = false
          message.success('更新成功')
        }, 1000)
      }
    }
  }
</script>

<style scoped>
.status-tag {
  min-width: 60px;
  text-align: center;
}

.config-preview {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.test-result {
  margin-top: 16px;
  padding: 12px;
  border-radius: 4px;
}

.test-result.success {
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
  color: #52c41a;
}

.test-result.error {
  background-color: #fff2f0;
  border: 1px solid #ffccc7;
  color: #ff4d4f;
}
</style>