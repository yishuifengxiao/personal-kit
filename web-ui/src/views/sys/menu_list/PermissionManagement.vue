<template>
  <div class="permission-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <a-page-header
        title="菜单权限管理"
        :sub-title="`当前菜单：${currentMenu.name} (${currentMenu.routerName})`"
        @back="handleBack"
      >
        <template #extra>
          <a-space>
            <a-button type="primary" @click="savePermissions" :loading="loading">
              保存权限
            </a-button>
            <a-button @click="handleBack">返回</a-button>
          </a-space>
        </template>
      </a-page-header>
    </div>

    <!-- 权限搜索区域 -->
    <div class="search-section">
      <a-card title="权限搜索" size="small">
        <a-form layout="inline" :model="searchForm">
          <a-form-item label="权限名称">
            <a-input
              v-model:value="searchForm.name"
              placeholder="请输入权限名称"
              allowClear
              style="width: 200px"
              @pressEnter="handleSearch"
            />
          </a-form-item>
          <a-form-item label="权限编码">
            <a-input
              v-model:value="searchForm.code"
              placeholder="请输入权限编码"
              allowClear
              style="width: 200px"
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
            <a-button type="primary" @click="handleSearch">搜索</a-button>
            <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
          </a-form-item>
        </a-form>
      </a-card>
    </div>

    <!-- 权限表格区域 -->
    <div class="table-section">
      <a-card title="权限列表" size="small">
        <!-- 选中权限统计 -->
        <div class="selection-info">
          <a-space>
            <span
              >已选择 <a-tag color="blue">{{ selectedKeys.length }}</a-tag> 个权限</span
            >
            <a-button type="link" @click="clearSelection" size="small">清空选择</a-button>
          </a-space>
        </div>

        <!-- 权限表格 -->
        <div class="permission-table-container">
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
            size="small"
            :scroll="{ x: 1000, y: 500 }"
            :loading="loading"
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
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

export default {
  name: 'PermissionManagement',
  setup() {
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
    const selectedKeys = ref([]) // 选中的权限ID
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
        this.$http
          .request({
            url: '/personkit/sys/permission/page',
            data: {
              num: pagination.current,
              query: formState,
              size: pagination.pageSize
            }
          })
          .then((res) => {
            pagination.current = res.num
            pagination.total = res.total

            // 直接使用返回的数据，不需要树形处理
             permissionData.value = reactive(res.data)
          })
          .catch((err) => console.log(err))


        // 加载当前菜单已选中的权限
        loadSelectedPermissions()
      } catch (error) {
        console.error('加载权限列表失败:', error)
        message.error('加载权限列表失败')
        permissionData.value = []
        pagination.total = 0
      } finally {
        loading.value = false
      }
    }

    // 加载已选中的权限
    const loadSelectedPermissions = () => {
      try {
        // 模拟API调用 - 获取当前菜单已选中的权限
        // 实际项目中需要调用类似 /personkit/sys/menu/permissions?menuId=xxx 的接口
        const mockSelectedPermissions = [1, 3, 6] // 模拟已选中的权限ID
        selectedKeys.value = mockSelectedPermissions
      } catch (error) {
        console.error('加载已选中权限失败:', error)
        selectedKeys.value = []
      }
    }

    // 搜索权限
    const handleSearch = () => {
      pagination.current = 1
      loadPermissions()
    }

    // 重置搜索
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

    // 单个选择
    const onSelect = (record, selected) => {
      console.log('选择权限:', record, selected)
    }

    // 全选
    const onSelectAll = (selected, selectedRows, changeRows) => {
      console.log('全选权限:', selected, selectedRows, changeRows)
    }

    // 判断权限是否已选中
    const isSelected = (record) => {
      return selectedKeys.value.includes(record.id)
    }

    // 获取行样式类名
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

      loading.value = true
      try {
        // 模拟API调用 - 实际项目中需要替换为真实的API
        // await this.$http.request({
        //   url: '/personkit/sys/menu/update-permissions',
        //   method: 'POST',
        //   data: {
        //     menuId: currentMenu.id,
        //     permissionIds: selectedKeys.value
        //   }
        // })

        // 模拟保存成功
        await new Promise((resolve) => setTimeout(resolve, 1000))

        message.success('权限保存成功')
        console.log('保存的权限ID:', selectedKeys.value)
      } catch (error) {
        console.error('保存权限失败:', error)
        message.error('保存权限失败')
      } finally {
        loading.value = false
      }
    }

    // 返回上一页
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
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;

  :deep(.ant-page-header) {
    background: #fff;
    border-radius: 6px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  }
}

.search-section {
  margin-bottom: 20px;

  :deep(.ant-card) {
    border-radius: 6px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  }
}

.table-section {
  :deep(.ant-card) {
    border-radius: 6px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  }
}

.selection-info {
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 4px;
  border-left: 4px solid #1890ff;
}

.permission-table-container {
  border: 1px solid #f0f0f0;
  border-radius: 4px;

  // 已选中权限行的样式
  :deep(.selected-permission-row) {
    background-color: #f0f9ff !important;

    &:hover {
      background-color: #e6f7ff !important;
    }

    td {
      border-bottom: 2px solid #1890ff !important;
    }
  }

  // 选中行的复选框样式
  :deep(.ant-table-selection-column) {
    .ant-checkbox-checked .ant-checkbox-inner {
      background-color: #1890ff;
      border-color: #1890ff;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .permission-management {
    padding: 10px;
  }

  .search-section {
    :deep(.ant-form) {
      flex-direction: column;
      align-items: flex-start;
    }

    :deep(.ant-form-item) {
      margin-bottom: 8px;
    }
  }
}
</style>
