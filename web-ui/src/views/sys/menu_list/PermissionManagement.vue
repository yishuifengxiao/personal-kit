<template>
  <div class="page-content-wrapper">
    <!-- 合并的权限管理区域 -->
    <div class="main-container">
        <!-- 页面头部和操作区域 -->
        <div class="search-area">
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
                  <template #icon>
                    <SaveOutlined />
                  </template>
                  保存权限
                </a-button>
                <a-button @click="handleBack">
                  <template #icon>
                    <RollbackOutlined />
                  </template>
                  返回
                </a-button>
              </a-space>
            </div>
          </div>
        </div>

        <!-- 搜索区域 -->
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
            <a-form-item label="资源路径">
              <a-input
                v-model:value="searchForm.url"
                placeholder="请输入资源路径"
                allowClear
                style="width: 180px"
                @pressEnter="handleSearch"
              />
            </a-form-item>
      
            <a-form-item>
              <a-space>
                <a-button type="primary" @click="handleSearch">
                  <template #icon>
                    <SearchOutlined />
                  </template>
                  搜索
                </a-button>
                <a-button @click="handleReset">
                  <template #icon>
                    <ReloadOutlined />
                  </template>
                  重置
                </a-button>
              </a-space>
            </a-form-item>
          </a-form>

        <!-- 中间内容区域 -->
        <div class="content-min-height">
        <!-- 选中权限统计 -->
        <div class="selection-info">
          <a-space>
            <span
              >已选择 <a-tag color="blue">{{ selectedKeys.length }}</a-tag> 个权限</span
            >
            <a-button type="link" @click="clearSelection" size="small">
              <template #icon>
                <ClearOutlined />
              </template>
              清空选择
            </a-button>
          </a-space>
        </div>

        <!-- 权限表格 -->
        <div class="table-container">
          <a-table
            :columns="columns"
            size="small"
            :data-source="permissionData"
            :pagination="pagination"
            :row-selection="{
              selectedRowKeys: selectedKeys,
              onChange: onSelectionChange,
              onSelect: onSelect,
              onSelectAll: onSelectAll,
              checkStrictly: true
            }"
            :row-class-name="getRowClassName"
            :scroll="{ x: 1000, y: 'calc(100vh - 380px)' }"
            :loading="loading"
            :row-key="(record) => record.id"
            class="permission-table"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'selectionStatus'">
                <a-tag v-if="isSelected(record)" color="blue">已选中</a-tag>
                <span v-else>-</span>
              </template>
            </template>
          </a-table>
        </div>
        </div>
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
      url: ''
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
        title: '资源路径',
        dataIndex: 'url',
        key: 'url',
        align: 'center'
      },
      {
        title: '请求方法',
        dataIndex: 'httpMethod',
        key: 'httpMethod',
        align: 'center'
      },

      {
        title: '模块名称',
        dataIndex: 'module',
        key: 'module',
        ellipsis: true,

        align: 'center'
      },
      {
        title: '资源名称',
        dataIndex: 'name',
        key: 'name',
        ellipsis: true,

        align: 'center'
      },

      {
        title: '描述',
        dataIndex: 'description',
        key: 'description',

        ellipsis: true,
        align: 'center'
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
            query:searchForm,
            page: pagination.current,
            size: pagination.pageSize
          }
        })
        permissionData.value = res.data || []
        pagination.total = res.total || 0
        // 加载完成后，查询当前菜单已关联的权限并标记选中状态
        await loadMenuPermissions()
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
    // 查询当前菜单已关联的权限
    const loadMenuPermissions = async () => {
      if (!currentMenu.id) {
        console.warn('菜单ID为空，无法查询关联权限')
        return
      }

      try {
        const res = await proxy.$http.request({
          url: '/personkit/sys/menu/permission',
          method: 'get',
          params: {
            menuId: currentMenu.id
          }
        })
        
        // 直接使用返回的权限ID字符串数组
        selectedKeys.value = res
        
      } catch (error) {
        console.error('查询菜单关联权限失败:', error)
        // 模拟数据用于演示
        console.log('使用模拟数据演示关联权限功能')
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
      if (selected) {
        // 全选时，只选择当前页的数据
        selectedKeys.value = selectedRows.map((row) => row.id)
      } else {
        // 取消全选时，清空选择
        selectedKeys.value = []
      }
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

    await proxy.$http.request({
        url: '/personkit/sys/menu/updateMenuPermission',
        method: 'post',
        data: {
          id: currentMenu.id,
          permissionIds: selectedKeys.value
        }
      })
      message.success('权限分配成功')
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
.page-content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
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

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-start;

  :deep(.ant-form-item) {
    margin-bottom: 0;
  }
}

.content-min-height {
  flex: 1;
  min-height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
  gap: 16px;
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
