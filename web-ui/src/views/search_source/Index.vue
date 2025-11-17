<template>
  <div class="page-content-wrapper">
    <!-- 上部搜索条件区域 -->
    <div class="search-area">
      <div class="search-container">
        <a-form
          layout="inline"
          name="basic"
          autocomplete="off"
          :model="searchForm"
          @finish="handleFinish"
          class="search-form"
        >
          <a-form-item label="标题" name="title" class="input">
            <a-input allowClear v-model:value="searchForm.title" placeholder="请输入标题，模糊查询"> </a-input>
          </a-form-item>

          <a-form-item label="描述" name="description" class="input">
            <a-input allowClear v-model:value="searchForm.description" placeholder="请输入描述，模糊查询"> </a-input>
          </a-form-item>

          <a-form-item label="垂直搜索" name="verticalSearch" class="input">
            <a-select allowClear
              style="width: 180px"
              v-model:value="searchForm.verticalSearch"
              placeholder="请选择垂直搜索"
            >
              <a-select-option value="web">网页搜索</a-select-option>
              <a-select-option value="image">图片搜索</a-select-option>
              <a-select-option value="video">视频搜索</a-select-option>
              <a-select-option value="news">新闻搜索</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item>
            <a-space>
              <a-button type="primary" html-type="submit"> 搜索 </a-button>
              <a-button @click="handleReset"> 重置 </a-button>
            </a-space>
          </a-form-item>
        </a-form>
        
        <div class="action-buttons">
          <a-space>
            <a-button type="primary" @click="showAddModal">
              <PlusOutlined />
              新增数据
            </a-button>
            <a-button @click="handleBatchEdit">
              <EditOutlined />
              批量编辑
            </a-button>
            <a-button @click="handleBatchDelete">
              <DeleteOutlined />
              批量删除
            </a-button>
            <a-dropdown>
              <template #overlay>
                <a-menu @click="handleBatchOperation">
                  <a-menu-item key="batchSync">批量同步</a-menu-item>
                  <a-menu-item key="syncRecords">同步记录</a-menu-item>
                  <a-menu-item key="batchDelete">批量删除</a-menu-item>
                  <a-menu-item key="batchModifyTags">批量修改标签</a-menu-item>
                  <a-menu-item key="batchModifyStatus">批量修改数据状态</a-menu-item>
                </a-menu>
              </template>
              <a-button>
                批量操作 <DownOutlined />
              </a-button>
            </a-dropdown>
          </a-space>
        </div>
      </div>
    </div>

    <!-- 中间内容区域 -->
    <div class="content-min-height">
      <!-- 表格区 -->
      <a-table 
        :columns="columns" 
        :data-source="dataSource" 
        :pagination="pagination"
        :scroll="{ x: 1200 }" 
        size="small"
        :row-selection="rowSelection"
        :loading="loading"
        @change="handleTableChange"
        row-key="id"
        class="table-container"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'dataSource'">
            <a-tag :color="getDataSourceColor(record.dataSource)">
              {{ getDataSourceLabel(record.dataSource) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'tags'">
            <div class="tags-container" @mouseenter="showAllTags(record)" @mouseleave="hideAllTags(record)">
              <a-tag v-for="(tag, index) in getDisplayTags(record.tags)" :key="index" class="tag-item">
                {{ tag }}
              </a-tag>
              <span v-if="record.tags && record.tags.length > 3" class="tag-more">...</span>
              <div v-if="record.showAllTags" class="tag-tooltip">
                <a-tag v-for="(tag, index) in record.tags" :key="index">
                  {{ tag }}
                </a-tag>
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'dataStatus'">
            <a-tag :color="getDataStatusColor(record.dataStatus)">
              {{ getDataStatusText(record.dataStatus) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'syncStatus'">
            <a-badge :status="getSyncStatusStatus(record.syncStatus)" :text="getSyncStatusLabel(record.syncStatus)" />
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space :size="2">
              <a-button type="link" size="small" @click="showDetailsModal(record)">详情</a-button>
              <a-button type="link" size="small" @click="handleSync(record)">同步</a-button>
              <a-dropdown>
                <template #overlay>
                  <a-menu @click="({ key }) => handleOperationMenu(record, key)">
                    <a-menu-item key="edit">编辑</a-menu-item>
                    <a-menu-item key="modifyTags">修改标签</a-menu-item>
                    <a-menu-item key="modifyStatus">修改数据状态</a-menu-item>
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
  EditOutlined,
  DeleteOutlined,
  ReloadOutlined,
  SearchOutlined,
  ClearOutlined,
  DownOutlined
} from '@ant-design/icons-vue'

export default {
  name: 'SearchSourceManagement',
  components: {
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
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
        title: '',
        description: '',
        verticalSearch: undefined
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
      selectedRows: [],
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
            title: '标题',
            dataIndex: 'title',
            key: 'title',
            width: '20%',
            ellipsis: true
          },
          {
            title: 'URL',
            dataIndex: 'url',
            key: 'url',
            width: '20%',
            ellipsis: true
          },
          {
            title: '描述',
            dataIndex: 'description',
            key: 'description',
            width: '25%',
            ellipsis: true
          },
          {
            title: '数据来源',
            dataIndex: 'dataSource',
            key: 'dataSource',
            width: '12%'
          },
          {
            title: '标签',
            dataIndex: 'tags',
            key: 'tags',
            width: '15%'
          },
          {
            title: '数据状态',
            dataIndex: 'dataStatus',
            key: 'dataStatus',
            width: '8%'
          },
          {
            title: '同步状态',
            dataIndex: 'syncStatus',
            key: 'syncStatus',
            width: '8%'
          },
          {
            title: '同步时间',
            dataIndex: 'syncTime',
            key: 'syncTime',
            width: '12%'
          },
          {
            title: '操作',
            key: 'action',
            width: '15%',
            fixed: 'right'
          }
        ]
      },
      rowSelection() {
        return {
          selectedRowKeys: this.selectedRows.map(row => row.id),
          onChange: (selectedRowKeys, selectedRows) => {
            this.selectedRows = selectedRows
          },
          getCheckboxProps: record => ({
            disabled: record.dataStatus === 'deleted',
            name: record.title,
          }),
          hideDefaultSelections: true,
          selections: false
        }
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

      // 数据来源映射
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

      // 数据状态映射
      getDataStatusColor(dataStatus) {
        const colorMap = {
          'published': 'success',
          'unpublished': 'warning',
          'deleted': 'default',
          'abnormal': 'error'
        }
        return colorMap[dataStatus] || 'default'
      },

      getDataStatusText(dataStatus) {
        const textMap = {
          'published': '已发布',
          'unpublished': '未发布',
          'deleted': '已删除',
          'abnormal': '异常'
        }
        return textMap[dataStatus] || dataStatus
      },

      // 同步状态映射
      getSyncStatusStatus(syncStatus) {
        const statusMap = {
          'normal': 'success',
          'abnormal': 'error',
          'not-synced': 'warning',
          'syncing': 'processing'
        }
        return statusMap[syncStatus] || 'default'
      },

      getSyncStatusLabel(syncStatus) {
        const labelMap = {
          'normal': '正常',
          'abnormal': '异常',
          'not-synced': '未同步',
          'syncing': '同步中'
        }
        return labelMap[syncStatus] || syncStatus
      },

      // 标签处理
      getDisplayTags(tags) {
        if (!tags || tags.length === 0) return []
        return tags.slice(0, 3)
      },

      showAllTags(record) {
        record.showAllTags = true
      },

      hideAllTags(record) {
        record.showAllTags = false
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

      handleTableChange(pagination, filters, sorter) {
        // 处理表格变化事件
        if (pagination) {
          this.pagination.current = pagination.current
          this.pagination.pageSize = pagination.pageSize
        }
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

      showDetailsModal(record) {
        this.$msg.info(`查看详情: ${record.title}`)
        // 这里可以打开详情弹窗或跳转到详情页面
        // 例如: this.$router.push({ name: 'search_source_detail', params: { id: record.id } })
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
        this.$msg.confirm({
          title: '确认删除',
          content: `确定要删除数据"${record.title}"吗？`,
          onOk: () => {
            const index = this.dataSource.findIndex(item => item.id === record.id)
            if (index !== -1) {
              this.dataSource.splice(index, 1)
              this.pagination.total--
              this.$msg.success('删除成功')
            }
          }
        })
      },

      handleDetails(record) {
        this.$msg.info(`查看详情: ${record.title}`)
        // 这里可以打开详情弹窗或跳转到详情页面
      },

      handleSync(record) {
        this.$msg.loading(`正在同步: ${record.title}`, 0)
        // 模拟同步操作
        setTimeout(() => {
          this.$msg.destroy()
          this.$msg.success(`同步成功: ${record.title}`)
          // 更新同步状态
          record.syncStatus = 'normal'
          record.syncTime = new Date().toISOString()
        }, 2000)
      },

      handleModifyTags(record) {
        this.$msg.info(`修改标签: ${record.title}`)
        // 这里可以打开标签编辑弹窗
      },

      handleModifyDataStatus(record) {
        this.$msg.info(`修改数据状态: ${record.title}`)
        // 这里可以打开数据状态修改弹窗
      },

      // 页面导航方法
      handleViewDetail(record) {
        this.$router.push({
          name: 'search_source_detail',
          params: { id: record.id }
        })
      },
      
      handleEdit(record) {
        this.$router.push({
          name: 'search_source_edit',
          params: { id: record.id }
        })
      },
      
      handleAdd() {
        this.$router.push({
          name: 'search_source_add'
        })
      },

      // 批量操作
      handleBatchSync() {
        if (this.selectedRows.length === 0) {
          this.$msg.warning('请选择要同步的数据')
          return
        }
        this.$msg.loading(`正在批量同步 ${this.selectedRows.length} 条数据`, 0)
        setTimeout(() => {
          this.$msg.destroy()
          this.$msg.success(`批量同步成功 ${this.selectedRows.length} 条数据`)
          // 更新选中数据的同步状态
          this.selectedRows.forEach(row => {
            row.syncStatus = 'normal'
            row.syncTime = new Date().toISOString()
          })
        }, 3000)
      },

      handleBatchEdit() {
        if (this.selectedRows.length === 0) {
          this.$msg.warning('请选择要编辑的数据')
          return
        }
        if (this.selectedRows.length > 1) {
          this.$msg.warning('批量编辑功能暂不支持多选')
          return
        }
        this.showEditModal(this.selectedRows[0])
      },

      handleBatchDelete() {
        if (this.selectedRows.length === 0) {
          this.$msg.warning('请选择要删除的数据')
          return
        }
        this.$msg.confirm({
          title: '确认批量删除',
          content: `确定要删除选中的 ${this.selectedRows.length} 条数据吗？`,
          onOk: () => {
            this.selectedRows.forEach(row => {
              const index = this.dataSource.findIndex(item => item.id === row.id)
              if (index !== -1) {
                this.dataSource.splice(index, 1)
                this.pagination.total--
              }
            })
            this.$msg.success(`批量删除成功 ${this.selectedRows.length} 条数据`)
            this.selectedRows = []
          }
        })
      },

      handleBatchModifyTags() {
        if (this.selectedRows.length === 0) {
          this.$msg.warning('请选择要修改标签的数据')
          return
        }
        this.$msg.info(`批量修改标签: ${this.selectedRows.length} 条数据`)
        // 这里可以打开批量标签编辑弹窗
      },

      handleBatchModifyDataStatus() {
        if (this.selectedRows.length === 0) {
          this.$msg.warning('请选择要修改数据状态的数据')
          return
        }
        this.$msg.info(`批量修改数据状态: ${this.selectedRows.length} 条数据`)
        // 这里可以打开批量数据状态修改弹窗
      },

      handleSyncRecords() {
        this.$msg.info('查看同步记录')
        // 这里可以打开同步记录弹窗或页面
      },

      handleBatchOperation({ key }) {
        switch (key) {
          case 'batchSync':
            this.handleBatchSync()
            break
          case 'syncRecords':
            this.handleSyncRecords()
            break
          case 'batchDelete':
            this.handleBatchDelete()
            break
          case 'batchModifyTags':
            this.handleBatchModifyTags()
            break
          case 'batchModifyStatus':
            this.handleBatchModifyDataStatus()
            break
        }
      },

      handleOperationMenu(record, key) {
        switch (key) {
          case 'edit':
            this.showEditModal(record)
            break
          case 'modifyTags':
            this.handleModifyTags(record)
            break
          case 'modifyStatus':
            this.handleModifyDataStatus(record)
            break
          case 'delete':
            this.handleDelete(record)
            break
        }
      },

      loadData() {
        this.loading = true
        // 模拟数据
        setTimeout(() => {
          const mockData = Array.from({ length: 50 }, (_, i) => ({
            id: i + 1,
            title: `数据标题${i + 1}`,
            url: `https://example.com/data/${i + 1}`,
            description: `这是数据${i + 1}的描述信息，包含详细的说明内容`,
            dataSource: ['api-import', 'manual', 'third-party', 'crawler'][i % 4],
            sourceDataId: `SD${1000 + i}`,
            dataIdentifier: `DI${2000 + i}`,
            tags: i % 3 === 0 ? ['标签1', '标签2', '标签3', '标签4'] : i % 2 === 0 ? ['标签1', '标签2'] : ['标签1'],
            dataStatus: i % 6 === 0 ? 'deleted' : i % 4 === 0 ? 'abnormal' : i % 3 === 0 ? 'unpublished' : 'published',
            syncStatus: i % 5 === 0 ? 'syncing' : i % 3 === 0 ? 'abnormal' : i % 2 === 0 ? 'not-synced' : 'normal',
            createTime: new Date(Date.now() - Math.random() * 86400000 * 30).toISOString(),
            syncTime: new Date(Date.now() - Math.random() * 86400000 * 7).toISOString(),
            config: JSON.stringify({ host: 'localhost', port: 3306, database: `db${i + 1}` }, null, 2),
            status: i % 5 === 0 ? 'disabled' : 'enabled',
            name: `数据源${i + 1}`,
            type: ['file', 'database', 'api'][i % 3]
          }))

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

/* 标签容器样式 */
.tags-container {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.tag-more {
  color: #999;
  font-size: 12px;
  margin-left: 4px;
}

.tag-tooltip {
  position: absolute;
  top: 100%;
  left: 0;
  z-index: 1000;
  background: white;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  min-width: 200px;
  margin-top: 4px;
}

/* 按钮组右侧浮动 */
.button-group-right {
  margin-left: auto;
}

/* 搜索区域样式 */
.search-area {
  background: #fff;
  padding: 16px;
  margin-bottom: 16px;
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

.search-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: nowrap;
}

.search-form {
  display: flex;
  align-items: center;
  flex: 1;
  flex-wrap: nowrap;
}

.action-buttons {
  display: flex;
  align-items: center;
  margin-left: 16px;
  flex-shrink: 0;
}

/* Tag item styles - remove borders */
.tag-item {
  border: none !important;
  background-color: #f0f2f5;
  margin-right: 4px;
  margin-bottom: 2px;
}

/* Enhanced tag container for hover functionality */
.tags-container {
  position: relative;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  max-height: 24px;
  overflow: hidden;
  transition: max-height 0.3s ease;
}

.tags-container:hover {
  max-height: none;
  overflow: visible;
  z-index: 10;
}

.tags-container:hover .tag-item {
  margin-bottom: 4px;
}

.tag-more, .tag-tooltip {
  color: #8c8c8c;
  font-size: 12px;
  margin-left: 4px;
  cursor: pointer;
}

.tags-container:hover .tag-more,
.tags-container:hover .tag-tooltip {
  display: none;
}
</style>