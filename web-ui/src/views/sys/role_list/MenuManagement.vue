<template>
  <div class="menu-management">
    <!-- 合并的菜单管理区域 -->
    <div class="main-container">
      <a-card class="menu-card" :bordered="false">
        <!-- 页面头部和操作区域 -->
        <div class="card-header">
          <div class="header-content">
            <div class="header-left">
              <a-typography-title :level="4" class="header-title">角色菜单配置</a-typography-title>
              <a-typography-text type="secondary" class="header-subtitle">
                当前角色：{{ currentRole.name }} ({{ currentRole.description }})
                <a-tag :color="currentRole.stat === 1 ? 'green' : 'red'" style="margin-left: 8px">
                  {{ currentRole.stat === 1 ? '启用' : '禁用' }}
                </a-tag>
              </a-typography-text>
            </div>
            <div class="header-actions">
              <a-space>
                <a-button type="primary" @click="saveMenus" :loading="loading" :disabled="currentRole.stat === 0">
                  <template #icon>
                    <SaveOutlined />
                  </template>
                  保存菜单
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

        <!-- 禁用状态提示 -->
        <a-alert v-if="currentRole.stat === 0" message="当前角色已禁用，无法保存菜单配置" type="warning" show-icon
          style="margin-bottom: 16px" />

        <!-- 搜索区域 -->
        <div class="search-area">
          <a-form layout="inline" :model="searchForm" class="search-form">
            <a-form-item label="菜单名称">
              <a-input v-model:value="searchForm.name" placeholder="请输入菜单名称" allowClear style="width: 180px"
                @pressEnter="handleSearch" />
            </a-form-item>
            <a-form-item label="路由名称">
              <a-input v-model:value="searchForm.routerName" placeholder="请输入路由名称" allowClear style="width: 180px"
                @pressEnter="handleSearch" />
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
        </div>

        <!-- 选中菜单统计 -->
        <div class="selection-info">
          <a-space>
            <span>已选择 <a-tag color="blue">{{ selectedKeys.length }}</a-tag> 个菜单</span>
            <a-button type="link" @click="clearSelection" size="small">
              <template #icon>
                <ClearOutlined />
              </template>
              清空选择
            </a-button>
          </a-space>
        </div>

        <!-- 菜单表格 -->
        <div class="table-container">
          <a-table :columns="columns" size="small" :data-source="menuData" :pagination="pagination" :row-selection="{
            selectedRowKeys: selectedKeys,
            onChange: onSelectionChange,
            onSelect: onSelect,
            onSelectAll: onSelectAll,
            checkStrictly: true
          }" :row-class-name="getRowClassName" :scroll="{ x: 1000, y: 'calc(100vh - 380px)' }" :loading="loading"
            :row-key="(record) => record.id" bordered class="menu-table">
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'selectionStatus'">
                <a-tag v-if="isSelected(record)" color="blue">已选中</a-tag>
                <span v-else>-</span>
              </template>
              <template v-else-if="column.dataIndex === 'isShow'">
                <a-tag :color="record.isShow === 1 ? 'green' : 'red'">
                  {{ record.isShow === 1 ? '显示' : '隐藏' }}
                </a-tag>
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
  name: 'MenuManagement',
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

    // 当前角色信息
    const currentRole = reactive({
      id: '',
      name: '',
      description: '',
      stat: 1 // 默认启用状态
    })

    // 搜索表单
    const searchForm = reactive({
      name: '',
      routerName: ''
    })

    // 表格数据
    const menuData = ref([])
    const selectedKeys = ref([])
    const loading = ref(false)

    // 分页配置
    const pagination = reactive({
      current: 1,
      pageSize: 10,
      total: 0,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: (total) => `共 ${total} 条菜单数据`,
      onChange: (page, pageSize) => {
        pagination.current = page
        pagination.pageSize = pageSize
        loadMenus()
      }
    })

    // 表格列定义
    const columns = [
      {
        title: '菜单名称',
        dataIndex: 'name',
        key: 'name',
        align: 'center'
      },
      {
        title: '路由名称',
        dataIndex: 'routerName',
        key: 'routerName',
        align: 'center'
      },
      {
        title: '父级菜单',
        dataIndex: 'parentName',
        key: 'parentName',
        align: 'center'
      },
      {
        title: '菜单位置',
        dataIndex: 'typeName',
        key: 'typeName',
        align: 'center'
      },
      {
        title: '是否显示',
        dataIndex: 'isShow',
        key: 'isShow',
        align: 'center',
        width: 80
      },
      {
        title: '描述',
        dataIndex: 'description',
        key: 'description',
        align: 'center'
      },
      {
        title: '选择状态',
        dataIndex: 'selectionStatus',
        key: 'selectionStatus',
        align: 'center',
        width: 100
      }
    ]

    // 初始化角色信息
    const initRoleInfo = () => {
      const roleInfo = sessionStorage.getItem('current_role_for_menu')
      if (roleInfo) {
        const role = JSON.parse(roleInfo)
        currentRole.id = role.id
        currentRole.name = role.name
        currentRole.description = role.description
        currentRole.stat = role.stat || 1 // 默认为启用状态
      } else {
        // 从路由参数获取
        const route = router.currentRoute.value
        if (route.query.roleId) {
          currentRole.id = route.query.roleId
          // 这里可以调用接口获取角色详情
          loadRoleDetail()
        }
      }
    }

    // 加载角色详情
    const loadRoleDetail = async () => {
      if (!currentRole.id) return

      try {
        const res = await proxy.$http.request({
          url: '/personkit/sys/role/detail',
          data: {
            roleId: currentRole.id
          }
        })

        if (res.code === 200 && res.data) {
          currentRole.name = res.data.name
          currentRole.description = res.data.description
          currentRole.stat = res.data.stat || 1
        }
      } catch (error) {
        console.error('加载角色详情失败:', error)
      }
    }

    // 加载菜单列表
    const loadMenus = async () => {
      loading.value = true
      try {
        const res = await proxy.$http.request({
          url: '/personkit/sys/menu/page',
          data: {
            num: pagination.current,
            query: searchForm,
            size: pagination.pageSize
          }
        })

        menuData.value = res.data || []
        pagination.total = res.total || 0

        // 加载当前角色已关联的菜单
        await loadRoleMenus()
      } catch (error) {
        console.error('加载菜单列表失败:', error)
        message.error('加载菜单列表失败')
      } finally {
        loading.value = false
      }
    }

    // 加载角色已关联的菜单
    const loadRoleMenus = async () => {
      if (!currentRole.id) return

      try {
        const res = await proxy.$http.request({
          url: '/personkit/sys/role/menu',
          data: {
            id: currentRole.id
          }
        })
        selectedKeys.value = res || []
      } catch (error) {
        console.error('加载角色菜单失败:', error)
      }
    }

    // 保存菜单配置
    const saveMenus = async () => {
      if (!currentRole.id) {
        message.warning('请先选择角色')
        return
      }

      if (currentRole.stat === 0) {
        message.warning('当前角色已禁用，无法保存菜单配置')
        return
      }

      loading.value = true
      try {
        const res = await proxy.$http.request({
          url: '/personkit/sys/role/updateMenu',
          method: 'post',
          data: {
            id: currentRole.id,
            menus: selectedKeys.value.map(v => ({ "id": v }))
          }
        })
        message.success('菜单配置保存成功')
      } catch (error) {
        console.error('保存菜单配置失败:', error)
        message.error('保存菜单配置失败')
      } finally {
        loading.value = false
      }
    }

    // 选择事件处理
    const onSelectionChange = (selectedRowKeys) => {
      selectedKeys.value = selectedRowKeys
    }

    const onSelect = (record, selected) => {
      console.log('选择菜单:', record, selected)
    }

    const onSelectAll = (selected, selectedRows, changeRows) => {
      console.log('全选菜单:', selected, selectedRows, changeRows)
    }

    // 判断是否选中
    const isSelected = (record) => {
      return selectedKeys.value.includes(record.id)
    }

    // 获取行类名
    const getRowClassName = (record) => {
      return isSelected(record) ? 'selected-row' : ''
    }

    // 搜索
    const handleSearch = () => {
      pagination.current = 1
      loadMenus()
    }

    // 重置搜索
    const handleReset = () => {
      searchForm.name = ''
      searchForm.routerName = ''
      pagination.current = 1
      loadMenus()
    }

    // 清空选择
    const clearSelection = () => {
      selectedKeys.value = []
    }

    // 返回
    const handleBack = () => {
      router.back()
    }

    onMounted(() => {
      initRoleInfo()
      loadMenus()
    })

    return {
      currentRole,
      searchForm,
      menuData,
      selectedKeys,
      loading,
      pagination,
      columns,
      loadMenus,
      saveMenus,
      onSelectionChange,
      onSelect,
      onSelectAll,
      isSelected,
      getRowClassName,
      handleSearch,
      handleReset,
      clearSelection,
      handleBack
    }
  }
}
</script>

<style scoped>
.menu-management {
  height: 100vh;
  background: #f0f2f5;
  padding: 20px;
}

.main-container {
  height: 100%;
}

.menu-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  margin: 0 !important;
}

.header-subtitle {
  margin-left: 10px;
}

.search-area {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.selection-info {
  margin-bottom: 16px;
}

.table-container {
  flex: 1;
  overflow: hidden;
}

.menu-table {
  height: 100%;
}

:deep(.selected-row) {
  background-color: #f0f7ff;
}

:deep(.ant-table-thead > tr > th) {
  background-color: #fafafa;
  font-weight: 600;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background-color: #f5f5f5;
}
</style>