<template>
  <div class="page-content-wrapper medium-data-table">
    <!-- 上部搜索条件区域 -->
    <div class="search-area">
    <a-form layout="inline" name="basic" autocomplete="off" :model="formState" @finish="handleFinish"
      :label-col="labelCol">
      <a-form-item label="菜单名称" name="name" class="input">
        <a-input allowClear v-model:value="formState.name" placeholder="菜单名称，模糊查询">
        </a-input>
      </a-form-item>

      <a-form-item label="父级菜单" name="父级菜单" class="input">
        <a-input allowClear v-model:value="formState.parentName" placeholder="父级菜单名称，模糊查询">
        </a-input>
      </a-form-item>

      <a-space class="input">
        <a-button type="primary" html-type="submit"> 搜索 </a-button>
        <a-button @click="handleReset">重置</a-button>
        <a-button type="primary" @click="showAddModal">增加菜单</a-button>
      </a-space>
    </a-form>
    </div>

    <!-- 中间内容区域 -->
    <div class="content-min-height">
    <!-- 表格容器，添加固定高度和滚动 -->
    <div class="table-container">
      <!-- 表格区 -->
      <a-table :columns="columns" :data-source="tableData" :pagination="false" :scroll="{ x: 1500, y: tableHeight }"
        size="small" :row-selection="rowSelection" :expandable="expandable" :row-key="(record) => record.id"
        :row-class-name="getRowClassName">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'action'">
            <!-- 只在叶子节点显示操作按钮 -->
            <template v-if="!record.children || record.children.length === 0">
              <a-space :size="2">
                <a-button type="link" @click="showEditModal(record)">编辑</a-button>
                <a-button type="link" @click="modifyPermissions(record)">修改权限</a-button>
                <a-button type="link" danger @click="handleDelete(record)">删除</a-button>
              </a-space>
            </template>
            <!-- 非叶子节点不显示操作按钮 -->
            <template v-else>
              <a-space :size="2">
                <a-button type="link" @click="showEditModal(record)">编辑</a-button>
                <a-button type="link" danger @click="handleDelete(record)">删除</a-button>
              </a-space>
            </template>
          </template>

          <!-- 状态列显示逻辑 -->
          <template v-else-if="column.dataIndex === 'stat'">
            <a-tag :color="record.stat === 1 ? 'green' : 'red'">
              {{ record.stat === 1 ? '启用' : '禁用' }}
            </a-tag>
          </template>

          <!-- 是否显示列显示逻辑 -->
          <template v-else-if="column.dataIndex === 'isShow'">
            <a-tag :color="record.isShow === 1 ? 'blue' : 'orange'">
              {{ record.isShow === 1 ? '显示' : '隐藏' }}
            </a-tag>
          </template>

          <!-- 所需权限列显示逻辑 -->
          <template v-else-if="column.dataIndex === 'permissions'">
            <template v-if="record.permissions && record.permissions.length > 0">
              <a-tooltip placement="topLeft">
                <template #title>
                  <div style="max-width: 300px">
                    <div v-for="(permission, index) in record.permissions" :key="permission.id">
                      {{ permission.httpMethod }} {{ permission.url }}
                    </div>
                  </div>
                </template>
                <span class="permission-urls">
                  {{record.permissions.map((p) => p.url).join(', ')}}
                </span>
              </a-tooltip>
            </template>
            <template v-else>
              <span style="color: #999">-</span>
            </template>
          </template>
        </template>
      </a-table>
      <!-- 表格区 -->
    </div>

    <!-- 分页区 -->
    <div class="pagination-wrapper">
      <a-pagination v-model:current="pagination.current" :total="pagination.total"
        :show-total="(total) => `共 ${total} 条数据`" @change="onPaginationChange" />
    </div>
    <!-- 分页区 -->
    </div>

    <!-- 新增/编辑菜单模态框 -->
    <a-modal v-model:open="modalVisible" :title="modalTitle" width="600px" @ok="handleModalOk"
      @cancel="handleModalCancel">
      <a-form ref="menuFormRef" :model="menuForm" :rules="menuFormRules" :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }" :key="formRenderKey">
        <a-form-item label="菜单名称" name="name">
          <a-input v-model:value="menuForm.name" placeholder="请输入菜单名称" />
        </a-form-item>
        <a-form-item label="父级菜单" name="parentId">
          <a-select v-model:value="menuForm.parentId" placeholder="请选择父级菜单" allowClear show-search
            :filter-option="false" :loading="menuLoading" @search="handleMenuSearch" @focus="handleMenuFocus">
            <a-select-option :value="0">
               顶级菜单
            </a-select-option>
            <a-select-option v-for="menu in filteredMenuList" :key="menu.id" :value="menu.id">
              {{ menu.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="路由名称" name="routerName">
          <a-input v-model:value="menuForm.routerName" placeholder="请输入路由名称" />
        </a-form-item>
        <a-form-item label="菜单位置" name="type">
          <a-select v-model:value="menuForm.type" placeholder="请选择菜单位置">
            <a-select-option :value="1">顶部菜单</a-select-option>
            <a-select-option :value="2">侧边菜单</a-select-option>
            <a-select-option :value="3">底部菜单</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="序号" name="idx">
          <a-input-number v-model:value="menuForm.idx" placeholder="请输入序号" :min="0" :precision="0"
            style="width: 100%" />
        </a-form-item>
        <a-form-item label="是否显示" name="isShow">
          <a-radio-group v-model:value="menuForm.isShow">
            <a-radio :value="1">显示</a-radio>
            <a-radio :value="0">隐藏</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="状态" name="stat">
          <a-radio-group v-model:value="menuForm.stat">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="描述" name="description">
          <a-textarea v-model:value="menuForm.description" placeholder="请输入菜单描述" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
    <!-- 中间内容区域 -->
  </div>
</template>

<script>
import { reactive, defineComponent, ref, onMounted, onUnmounted, h } from 'vue'
import { UserOutlined } from '@ant-design/icons-vue'
import { mapState } from 'pinia'
import { useUserStore } from '@/stores/user'

export default defineComponent({
  data() {
    const formState = reactive({})
    const data = reactive([])
    const roleSource = reactive([])
    const expandedRowKeys = ref([]) // 存储展开的行key
    const tableHeight = ref(400) // 表格初始高度
    const modalVisible = ref(false) // 模态框显示状态
    const isEditMode = ref(false) // 是否为编辑模式
    const currentEditId = ref(null) // 当前编辑的菜单ID
    const menuList = ref([]) // 完整菜单列表
    const filteredMenuList = ref([]) // 过滤后的菜单列表
    const menuLoading = ref(false) // 菜单加载状态
    const searchKeyword = ref('') // 搜索关键词

    const rowSelection = ref({
      checkStrictly: false,
      onChange: (selectedRowKeys, selectedRows) => {
        console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows)
      },
      onSelect: (record, selected, selectedRows) => {
        console.log(record, selected, selectedRows)
      },
      onSelectAll: (selected, selectedRows, changeRows) => {
        console.log(selected, selectedRows, changeRows)
      }
    })

    // 展开配置
    const expandable = ref({
      expandedRowKeys: expandedRowKeys.value,
      onExpand: (expanded, record) => {
        if (expanded) {
          // 展开当前行，收起其他行
          expandedRowKeys.value = [record.id]
        } else {
          // 收起当前行
          expandedRowKeys.value = []
        }
      },
      // 判断是否有子节点 - 只有非叶子节点才显示展开按钮
      rowExpandable: (record) => {
        return record.children && record.children.length > 0
      },
      // 自定义展开图标 - 对叶子节点不显示图标
      expandIcon: ({ record, expanded, expandable: iconExpandable, onExpand }) => {
        // 如果是叶子节点（没有子节点），不显示展开图标
        if (!record.children || record.children.length === 0) {
          return null // 返回null表示不渲染任何内容
        }
        // 对非叶子节点使用默认展开图标
        return null
      }
    })

    // 菜单表单数据
    const menuForm = reactive({
      idx: 0,
      name: '',
      parentId: 0,
      routerName: '',
      type: 1,
      isShow: 1,
      stat: 1,
      description: ''
    })
    
    // 强制重新渲染的key
    const formRenderKey = ref(0)

    // 菜单表单验证规则
    const menuFormRules = {
      idx: [{ required: true, message: '请输入序号', trigger: 'blur' }],
      name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
      routerName: [{ required: true, message: '请输入路由名称', trigger: 'blur' }],
      type: [{ required: true, message: '请选择菜单位置', trigger: 'change' }]
    }

    // 默认表单状态
    const defaultFormState = {
      name: '',
      parentName: ''
    }

    return {
      formState,
      data,
      roleSource,
      rowSelection,
      expandable,
      expandedRowKeys,
      tableHeight,
      modalVisible,
      isEditMode,
      currentEditId,
      menuList,
      filteredMenuList,
      menuLoading,
      searchKeyword,
      menuForm,
      menuFormRules,
      defaultFormState,
      formRenderKey
    }
  },
  computed: {
    ...mapState(useUserStore, ['tokenVal']),
    tableData: function () {
      return this.data
    },
    headers: function () {
      return { Authorization: 'xtoken ' + this.tokenVal }
    },
    uploadUrl: function () {
      return this.$cfg.rootUrl() + '/personkit/disk/file/import'
    },
    modalTitle: function () {
      return this.isEditMode ? '编辑菜单' : '新增菜单'
    }
  },
  methods: {
    handleFinish() {
      this.query()
    },

    /**
     * 重置搜索条件
     */
    handleReset() {
      Object.assign(this.formState, this.defaultFormState)
      this.pagination.current = 1
      this.query()
    },

    /**
     * 查询数据
     */
    query() {
      this.$http
        .request({
          url: '/personkit/sys/menu/page',
          data: {
            num: this.pagination.current,
            query: this.formState,
            size: this.pagination.pageSize
          }
        })
        .then((res) => {
          this.pagination.current = res.num
          this.pagination.total = res.total

          // 处理数据，确保有children字段用于树形展示
          this.data = reactive(this.processTreeData(res.data))

          // 更新菜单列表用于父级菜单选择
          this.menuList = this.getAllMenus(res.data)
        })
        .catch((err) => console.log(err))
    },

    /**
     * 加载菜单列表
     */
    loadMenuList() {
      this.menuLoading = true
      this.$http
        .request({
          url: '/personkit/sys/menu/list',
          data: {
            "name": ""
          }
        })
        .then((res) => {
          this.menuList = res || []
          this.filteredMenuList = res || []
          this.menuLoading = false
        })
        .catch((err) => {
          console.log(err)
          this.menuLoading = false
        })
    },

    /**
     * 处理菜单搜索
     */
    handleMenuSearch(value) {
      this.searchKeyword = value
      this.$http
        .request({
          url: '/personkit/sys/menu/list',
          data: {
            "name": value
          }
        })
        .then((res) => {

          this.menuList = res || []
          this.filteredMenuList = res || []
          this.menuLoading = false
        })
        .catch((err) => {
          console.log(err)
          this.menuLoading = false
        })

      // if (!value) {
      //   this.filteredMenuList = this.menuList
      //   return
      // }

      // this.filteredMenuList = this.menuList.filter(
      //   (menu) => menu.name && menu.name.toLowerCase().includes(value.toLowerCase())
      // )
    },

    /**
     * 处理菜单选择框获取焦点
     */
    handleMenuFocus() {
      if (this.menuList.length === 0) {
        this.loadMenuList()
      }
    },

    /**
     * 获取所有菜单用于父级菜单选择
     */
    getAllMenus(data) {
      const menus = []
      const traverse = (items) => {
        items.forEach((item) => {
          menus.push({
            id: item.id,
            name: item.name
          })
          if (item.children && item.children.length > 0) {
            traverse(item.children)
          }
        })
      }
      traverse(data)
      return menus
    },

    /**
     * 处理树形数据
     */
    processTreeData(data) {
      if (!data || !Array.isArray(data)) return []

      return data.map((item) => {
        // 确保每个item都有children字段，即使为空数组
        return {
          ...item,
          children: item.children || []
        }
      })
    },

    /**
     * 计算表格高度
     */
    calculateTableHeight() {
      // 获取窗口高度
      const windowHeight = window.innerHeight
      // 计算表格容器可用高度（减去搜索区域、分页区域等）
      const searchHeight = 120 // 搜索区域高度
      const paginationHeight = 60 // 分页区域高度
      const margins = 40 // 边距

      const availableHeight = windowHeight - searchHeight - paginationHeight - margins

      // 设置最小高度和最大高度
      this.tableHeight = Math.max(300, Math.min(600, availableHeight))
    },

    /**
     * 窗口大小变化监听
     */
    handleResize() {
      this.calculateTableHeight()
    },

    handleChange(info) {
      if (info.file.status !== 'uploading') {
        console.log(info.file, info.fileList)
      }
      if (info.file.status === 'done') {
        this.$msg.success(`${info.file.name} file uploaded successfully`)
      } else if (info.file.status === 'error') {
        this.$msg.error(`${info.file.name} file upload failed.`)
      }
      this.query()
    },
    //导航栏发生变化
    onPaginationChange(page, pageSize) {
      this.pagination.current = page
      this.query()
    },
    //查看详情
    showDetail(record) {
      sessionStorage.setItem('current_view_file', JSON.stringify(record))

      this.$router.push({ name: 'data_source_detail', query: { record: record.id } })
    },

    /**
     * 修改权限 - 跳转到权限管理页面
     */
    modifyPermissions(record) {
      // 将当前菜单信息存储到sessionStorage，供权限管理页面使用
      sessionStorage.setItem(
        'current_menu_for_permission',
        JSON.stringify({
          id: record.id,
          name: record.name,
          routerName: record.routerName
        })
      )

      // 跳转到权限管理页面
      this.$router.push({
        name: 'menu_permission_management',
        query: { menuId: record.id }
      })
    },

    /**
     * 显示新增菜单模态框
     */
    showAddModal() {
      // 预加载菜单列表
      this.loadMenuList()
      this.isEditMode = false
      this.currentEditId = null
      this.resetMenuForm()
      this.modalVisible = true

    },

    /**
     * 显示编辑菜单模态框
     */
    showEditModal(record) {
      // 预加载菜单列表
      this.loadMenuList()
      this.isEditMode = true
      this.currentEditId = record.id
      
      // 调试日志 - 检查原始数据
      console.log('编辑菜单数据:', {
        original: record,
        isShow: record.isShow,
        isShowType: typeof record.isShow,
        stat: record.stat,
        statType: typeof record.stat
      })
      
      // 确保数据类型正确，转换为数字类型
      const isShowValue = Number(record.isShow) || 0
      const statValue = Number(record.stat) || 0
      
      // 先重置表单，确保清空之前的值
      this.resetMenuForm()
      
      // 增加强制重新渲染的key
      this.formRenderKey += 1
      
      // 填充表单数据
      Object.assign(this.menuForm, {
        idx: record.idx || 0,
        name: record.name || '',
        parentId: record.parentId || 0,
        routerName: record.routerName || '',
        type: record.type || 1,
        isShow: isShowValue,
        stat: statValue,
        description: record.description || ''
      })
      
      // 强制触发响应式更新
      this.$nextTick(() => {
        console.log('表单数据回显后:', {
          menuForm: this.menuForm,
          isShow: this.menuForm.isShow,
          stat: this.menuForm.stat,
          formRenderKey: this.formRenderKey
        })
        
        // 强制更新radio组件
        if (this.$refs.menuFormRef) {
          this.$refs.menuFormRef.clearValidate(['isShow', 'stat'])
        }
      })
      
      this.modalVisible = true
    },

    /**
     * 重置菜单表单
     */
    resetMenuForm() {
      Object.assign(this.menuForm, {
        name: '',
        parentId: 0,
        routerName: '',
        type: 1,
        isShow: 1,
        stat: 1,
        description: ''
      })
    },

    /**
     * 模态框确定按钮
     */
    handleModalOk() {
      this.$refs.menuFormRef
        .validate()
        .then(() => {
          const apiUrl = this.isEditMode ? '/personkit/sys/menu/update' : '/personkit/sys/menu/save'
          const requestData = this.isEditMode
            ? { ...this.menuForm, id: this.currentEditId }
            : this.menuForm

          this.$http
            .request({
              url: apiUrl,
              data: requestData
            })
            .then((res) => {
              this.$msg.success(this.isEditMode ? '菜单更新成功' : '菜单新增成功')
              this.modalVisible = false
              this.query() // 刷新列表
            })
            .catch((err) => {
              console.log(err)
              this.$msg.error(this.isEditMode ? '菜单更新失败' : '菜单新增失败')
            })
        })
        .catch(() => {
          this.$msg.warning('请完善表单信息')
        })
    },

    /**
     * 模态框取消按钮
     */
    handleModalCancel() {
      this.modalVisible = false
      this.resetMenuForm()
    },

    /**
     * 切换菜单状态
     */
    handleToggleStatus(record, newStatus) {
      const actionText = newStatus === 1 ? '启用' : '禁用'
      this.$confirm({
        title: `确认${actionText}菜单`,
        content: `确定要${actionText}菜单 "${record.name}" 吗？`,
        onOk: () => {
          this.$http
            .request({
              url: '/personkit/sys/menu/update',
              data: {
                id: record.id,
                stat: newStatus
              }
            })
            .then((res) => {
              this.$msg.success(`菜单${actionText}成功`)
              this.query() // 刷新列表
            })
            .catch((err) => {
              console.log(err)
              this.$msg.error(`菜单${actionText}失败`)
            })
        }
      })
    },

    /**
     * 删除菜单
     */
    handleDelete(record) {
      this.$confirm({
        title: '确认删除菜单',
        content: `确定要删除菜单 "${record.name}" 吗？此操作不可恢复。`,
        okText: '确定',
        cancelText: '取消',
        okButtonProps: {
          danger: true
        },
        onOk: () => {
          this.$http
            .request({
              url: '/personkit/sys/menu/delete',
              data: {
                id: record.id
              }
            })
            .then((res) => {
              this.$msg.success('菜单删除成功')
              this.query() // 刷新列表
            })
            .catch((err) => {
              console.log(err)
              this.$msg.error('菜单删除失败')
            })
        }
      })
    },

    /**
     * 获取行类名 - 用于区分叶子节点和非叶子节点
     */
    getRowClassName(record) {
      // 如果是叶子节点（没有子节点），添加叶子节点样式类
      if (!record.children || record.children.length === 0) {
        return 'leaf-node-row'
      }
      return ''
    },

  },
  components: {
    UserOutlined
  },
  mounted() {
    this.query()
    // 初始化表格高度
    this.calculateTableHeight()
    // 监听窗口大小变化
    window.addEventListener('resize', this.handleResize)
    
    // 监听menuForm变化，确保radio组件正确响应
    this.$watch('menuForm', (newVal) => {
      console.log('menuForm变化:', {
        isShow: newVal.isShow,
        stat: newVal.stat,
        isShowType: typeof newVal.isShow,
        statType: typeof newVal.stat
      })
    }, { deep: true })
  },
  beforeUnmount() {
    // 移除事件监听
    window.removeEventListener('resize', this.handleResize)
  },
  setup() {
    const columns = [
      {
        title: '序号',
        dataIndex: 'idx',
        key: 'idx',
        align: 'center',
        width: 80
      },
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
        ellipsis: true,
        align: 'center'
      },
      {
        title: '菜单位置',
        dataIndex: 'typeName',
        key: 'typeName',
        ellipsis: true,
        align: 'center'
      },
      {
        title: '是否显示',
        dataIndex: 'isShow',
        key: 'isShow',
        ellipsis: true,
        align: 'center'
      },
      {
        title: '状态',
        dataIndex: 'stat',
        key: 'stat',
        align: 'center'
      },
      {
        title: '父级菜单',
        dataIndex: 'parentName',
        key: 'parentName',
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
        title: '所需权限',
        dataIndex: 'permissions',
        key: 'permissions',
        ellipsis: true,
        align: 'center'
      },
      {
        title: '操作',
        dataIndex: 'action',
        key: 'action',
        width: 300,
        align: 'center',
        fixed: 'right'
      }
    ]
    const pagination = {
      current: 1,
      total: 0,
      pageSize: 10
    }
    const fileList = ref([])
    const labelCol = { style: { width: '80px' } }
    const wrapperCol = { span: 14 }
    const statusOptions = reactive([
      { label: '需要鉴权', value: 1 },
      { label: '无需鉴权', value: 0 }
    ])
    return { columns, pagination, fileList, labelCol, wrapperCol, statusOptions }
  }
})
</script>
<style lang="less" scoped>
.input {
  margin: 5px 5px 10px 5px;
  padding-right: 10px;
}

.table-container {
  // 添加边框和圆角
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  // 添加内边距
  padding: 8px;
  // 设置背景色
  background-color: #fff;
  // 添加阴影效果
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.03),
    0 1px 6px -1px rgba(0, 0, 0, 0.02),
    0 2px 4px rgba(0, 0, 0, 0.02);

  // 确保表格在容器内正确显示
  :deep(.ant-table) {
    border-radius: 4px;
  }

  :deep(.ant-table-container) {
    border-radius: 4px;
  }

  // 所需权限列样式
  :deep(.permission-urls) {
    display: inline-block;
    max-width: 200px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
  }

  // 叶子节点行样式 - 使用淡蓝色背景进行区分
  :deep(.leaf-node-row) {
    background-color: #f0f9ff !important;  // 淡蓝色背景
    
    &:hover {
      background-color: #e6f7ff !important;  // 鼠标悬停时的稍深蓝色
    }
    
    // 确保子表格也继承样式
    td {
      background-color: transparent !important;
    }
  }

  // 隐藏叶子节点的展开图标
  :deep(.leaf-node-row) {
    // 隐藏展开图标按钮
    .ant-table-row-expand-icon {
      display: none !important;
    }
    // 隐藏展开图标单元格
    .ant-table-row-expand-icon-cell {
      width: 0 !important;
      padding: 0 !important;
      border: none !important;
    }
    // 隐藏第一列中的展开图标（某些版本的Ant Design）
    td:first-child .ant-table-row-expand-icon {
      display: none !important;
    }
  }
}

// 响应式设计
@media (max-height: 700px) {
  .table-container {
    max-height: 400px;
  }
}

@media (max-height: 500px) {
  .table-container {
    max-height: 250px;
  }
}
</style>
