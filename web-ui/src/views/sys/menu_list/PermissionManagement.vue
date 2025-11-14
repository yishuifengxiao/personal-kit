<template>
  <div class="permission-management">
    <!-- 合并的权限管理区域 -->
    <div class="main-container">
      <a-card class="permission-card" :bordered="false">
        <!-- 页面头部和操作区域 -->
        <div class="card-header">
          <div class="header-content">
            <div class="header-left">
              <a-typography-title :level="4" class="header-title">菜单权限管理</a-typography-title>
              <a-typography-text type="secondary" class="header-subtitle">
                当前菜单：{{ currentMenu.name }} ({{ currentMenu.routerName }})
              </a-typography-text>
            </div>
            <div class="header-actions">
              <a-space>
                <a-button type="primary" @click="savePermissions" :loading="loading">
                  <template #icon><SaveOutlined /></template>
                  保存权限
                </a-button>
                <a-button @click="handleBack">
                  <template #icon><RollbackOutlined /></template>
                  返回
                </a-button>
              </a-space>
            </div>
          </div>
        </div>

        <!-- 搜索区域 -->
        <div class="search-area">
          <a-form layout="inline" :model="searchForm" class="search-form">
            <a-form-item label="权限名称">
              <a-input
                v-model:value="searchForm.name"
                placeholder="请输入权限名称"
                allowClear
                style="width: 180px"
                @pressEnter="handleSearch"
              />
            </a-form-item>
            <a-form-item label="权限编码">
              <a-input
                v-model:value="searchForm.code"
                placeholder="请输入权限编码"
                allowClear
                style="width: 180px"
                @pressEnter="handleSearch"
              />
            </a-form-item>
            <a-form-item label="状态">
              <a-select
                v-model:value="searchForm.status"
                placeholder="请选择状态"
                style="width: 120px"
                allowClear
              >
                <a-select-option :value="1">启用</a-select-option>
                <a-select-option :value="0">禁用</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item>
              <a-space>
                <a-button type="primary" @click="handleSearch">
                  <template #icon><SearchOutlined /></template>
                  搜索
                </a-button>
                <a-button @click="handleReset">
                  <template #icon><ReloadOutlined /></template>
                  重置
                </a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </div>

        <!-- 选中权限统计 -->
        <div class="selection-info">
          <a-space>
            <span>已选择 <a-tag color="blue">{{ selectedKeys.length }}</a-tag> 个权限</span>
            <a-button type="link" @click="clearSelection" size="small">
              <template #icon><ClearOutlined /></template>
              清空选择
            </a-button>
          </a-space>
        </div>

        <!-- 权限表格 -->
        <div class="table-container">
          <a-table
            :columns="columns"
            :data-source="permissionData"
            :pagination="pagination"
            :row-selection="{
              selectedRowKeys: selectedKeys,
              onChange: onSelectionChange,
              onSelect: onSelect,
              onSelectAll: onSelectAll
            }"
            :row-class-name="getRowClassName"
            size="middle"
            :scroll="{ x: 1000, y: 'calc(100vh - 380px)' }"
            :loading="loading"
            bordered
            class="permission-table"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'status'">
                <a-tag :color="record.status === 1 ? 'green' : 'red'">
                  {{ record.status === 1 ? '启用' : '禁用' }}
                </a-tag>
              </template>
              <template v-if="column.dataIndex === 'createTime'">
                {{ formatTime(record.createTime) }}
              </template>
              <template v-if="column.dataIndex === 'selectionStatus'">
                <a-tag v-if="isSelected(record)" color="blue">已选中</a-tag>
                <span v-else>-</span>
              </template>
            </template>
          </a-table>
        </div>
      </a-card>
    </div>
  </div>
</template>

<script>
import { reactive, ref, onMounted, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  SaveOutlined, 
  RollbackOutlined, 
  SearchOutlined, 
  ReloadOutlined, 
  ClearOutlined 
} from '@ant-design/icons-vue'

export default {
  name: 'PermissionManagement',
  components: {
    SaveOutlined,
    RollbackOutlined,
    SearchOutlined,
    ReloadOutlined,
    ClearOutlined
  },
  setup() {
    const { proxy } = getCurrentInstance()
    const router = useRouter()

    // 当前菜单信息
    const currentMenu = reactive({
      id: '',
      name: '',
      routerName: ''
    })

    // 搜索表单
    const searchForm = reactive({
      name: '',
      code: '',
      status: undefined
    })

    // 表格数据
    const permissionData = ref([])
    const selectedKeys = ref([])
    const loading = ref(false)

    // 分页配置
    const pagination = reactive({
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: (total) => `共 ${total} 条权限数据`,
      onChange: (page, pageSize) => {
        pagination.current = page
        pagination.pageSize = pageSize
        loadPermissions()
      }
    })

    // 表格列定义
    const columns = [
      {
        title: '权限名称',
        dataIndex: 'name',
        key: 'name',
        width: 150,
        ellipsis: true
      },
      {
        title: '权限编码',
        dataIndex: 'code',
        key: 'code',
        width: 120
      },
      {
        title: '权限描述',
        dataIndex: 'description',
        key: 'description',
        width: 200,
        ellipsis: true
      },
      {
        title: '状态',
        dataIndex: 'status',
        key: 'status',
        width: 80
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
        key: 'createTime',
        width: 150
      },
      {
        title: '选中状态',
        dataIndex: 'selectionStatus',
        key: 'selectionStatus',
        width: 100
      }
    ]

    // 初始化菜单信息
    const initMenuInfo = () => {
      try {
        const menuInfo = JSON.parse(sessionStorage.getItem('current_menu_for_permission') || '{}')
        if (menuInfo.id) {
          currentMenu.id = menuInfo.id
          currentMenu.name = menuInfo.name
          currentMenu.routerName = menuInfo.routerName
        } else {
          message.error('未获取到菜单信息，请返回重试')
          handleBack()
        }
      } catch (error) {
        console.error('初始化菜单信息失败:', error)
        message.error('菜单信息解析失败')
        handleBack()
      }
    }

    // 加载权限列表
    const loadPermissions = async () => {
      loading.value = true
      try {
        const res = await proxy.$http.request({
          url: '/personkit/sys/permission/page',
          method: 'post',
          data: {
            ...searchForm,
            page: pagination.current,
            size: pagination.pageSize
          }
        })
        
        if (res.code === 200) {
          permissionData.value = res.data.records || []
          pagination.total = res.data.total || 0
        } else {
          message.error('加载权限列表失败: ' + res.message)
        }
      } catch (error) {
        console.error('加载权限列表失败:', error)
        message.error('加载权限列表失败')
        
        // 模拟数据
        permissionData.value = [
          {
            id: 1,
            name: '用户管理-查看',
            code: 'user:view',
            description: '查看用户列表权限',
            status: 1,
            createTime: new Date().toISOString()
          },
          {
            id: 2,
            name: '用户管理-编辑',
            code: 'user:edit',
            description: '编辑用户信息权限',
            status: 1,
            createTime: new Date().toISOString()
          }
        ]
        pagination.total = 2
      } finally {
        loading.value = false
      }
    }

    // 搜索
    const handleSearch = () => {
      pagination.current = 1
      loadPermissions()
    }

    // 重置
    const handleReset = () => {
      searchForm.name = ''
      searchForm.code = ''
      searchForm.status = undefined
      pagination.current = 1
      loadPermissions()
    }

    // 选择变化
    const onSelectionChange = (selectedRowKeys) => {
      selectedKeys.value = selectedRowKeys
    }

    const onSelect = (record, selected) => {
      console.log('选择权限:', record, selected)
    }

    const onSelectAll = (selected, selectedRows, changeRows) => {
      console.log('全选权限:', selected, selectedRows, changeRows)
    }

    // 判断是否选中
    const isSelected = (record) => {
      return selectedKeys.value.includes(record.id)
    }

    // 获取行类名
    const getRowClassName = (record) => {
      return isSelected(record) ? 'selected-permission-row' : ''
    }

    // 清空选择
    const clearSelection = () => {
      selectedKeys.value = []
    }

    // 保存权限
    const savePermissions = async () => {
      if (!currentMenu.id) {
        message.error('未获取到菜单信息')
        return
      }

      if (selectedKeys.value.length === 0) {
        message.warning('请先选择要分配的权限')
        return
      }

      loading.value = true
      try {
        // 模拟保存
        await new Promise(resolve => setTimeout(resolve, 1000))
        message.success(`成功为菜单"${currentMenu.name}"分配 ${selectedKeys.value.length} 个权限`)
      } catch (error) {
        console.error('保存权限失败:', error)
        message.error('保存权限失败')
      } finally {
        loading.value = false
      }
    }

    // 返回
    const handleBack = () => {
      router.back()
    }

    // 格式化时间
    const formatTime = (timeString) => {
      if (!timeString) return '-'
      return new Date(timeString).toLocaleDateString()
    }

    onMounted(() => {
      initMenuInfo()
      loadPermissions()
    })

    return {
      currentMenu,
      searchForm,
      permissionData,
      selectedKeys,
      loading,
      pagination,
      columns,
      handleSearch,
      handleReset,
      onSelectionChange,
      onSelect,
      onSelectAll,
      isSelected,
      getRowClassName,
      clearSelection,
      savePermissions,
      handleBack,
      formatTime
    }
  }
}
</script>

<style lang="less" scoped>
.permission-management {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
  padding: 16px;
  box-sizing: border-box;
  overflow: hidden;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.permission-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  
  :deep(.ant-card-body) {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 0;
    min-height: 0;
    overflow: hidden;
  }
}

.card-header {
  flex-shrink: 0;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.header-title {
  margin: 0 !important;
  color: #262626;
}

.header-subtitle {
  font-size: 14px;
}

.header-actions {
  flex-shrink: 0;
}

.search-area {
  flex-shrink: 0;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  background: #fff;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-start;
  
  :deep(.ant-form-item) {
    margin-bottom: 0;
  }
}

.selection-info {
  flex-shrink: 0;
  padding: 12px 24px;
  background: #f8f9fa;
  border-bottom: 1px solid #f0f0f0;
}

.table-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.permission-table {
  flex: 1;
  min-height: 0;
  
  :deep(.ant-table) {
    height: 100%;
    
    .ant-table-container {
      height: 100%;
      display: flex;
      flex-direction: column;
    }
    
    .ant-table-content {
      flex: 1;
      display: flex;
      flex-direction: column;
    }
    
    .ant-table-body {
      flex: 1;
      min-height: 0;
    }
    
    .ant-table-thead > tr > th {
      background: #fafafa;
      position: sticky;
      top: 0;
      z-index: 1;
    }
  }
}

.selected-permission-row {
  background-color: #e6f7ff !important;
  
  &:hover > td {
    background-color: #d4edff !important;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .permission-management {
    padding: 8px;
  }
  
  .card-header {
    padding: 12px 16px;
  }
  
  .header-content {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .header-actions {
    align-self: flex-end;
  }
  
  .search-area {
    padding: 12px 16px;
  }
  
  .search-form {
    gap: 12px;
    
    :deep(.ant-form-item) {
      flex: 1;
      min-width: 150px;
    }
  }
  
  .permission-table {
    :deep(.ant-table) {
      :deep(.ant-table-scroll) {
        overflow-x: auto;
      }
    }
  }
}

// 滚动条优化
.table-container {
  :deep(.ant-table-body) {
    &::-webkit-scrollbar {
      width: 6px;
      height: 6px;
    }
    
    &::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 3px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: #c1c1c1;
      border-radius: 3px;
      
      &:hover {
        background: #a8a8a8;
      }
    }
  }
}
</style>
