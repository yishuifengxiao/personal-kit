<template>
  <div>
    <!-- 上部搜索条件区域 -->
    <a-form layout="inline" name="basic" autocomplete="off" :model="formState" @finish="handleFinish"
      :label-col="labelCol">
      <a-form-item label="角色名称" name="name" class="input">
        <a-input allowClear v-model:value="formState.name" placeholder="角色名称，模糊查询"> </a-input>
      </a-form-item>

      <a-form-item label="角色状态" name="stat" class="input">
        <a-select allowClear style="width: 180px" placeholder="请选择状态" v-model:value="formState.stat"
          :options="statusOptions"></a-select>
      </a-form-item>

      <a-space class="input">
        <a-button type="primary" html-type="submit"> 搜索 </a-button>
        <a-button @click="handleReset"> 重置 </a-button>
        <a-button type="primary" @click="showCreateModal"> 创建角色 </a-button>
      </a-space>
    </a-form>

    <!-- 上部搜索条件区域 -->
    <a-divider dashed />
    <!-- 中间内容区域 -->
    <!-- 表格区 -->
    <a-table :columns="columns" :data-source="tableData" :pagination="false" :scroll="{ x: 1500 }">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <a-space>
            <a-button type="link" danger @click="handleDelete(record)">删除</a-button>
            <a-button type="link" @click="showEditModal(record)">编辑</a-button>
            <a-button type="link" @click="modifyMenus(record)">修改菜单</a-button>
            <a-button type="link" @click="handleToggleStatus(record)" :type="record.stat === 1 ? 'danger' : 'primary'">
              {{ record.stat === 1 ? '禁用' : '启用' }}
            </a-button>
          </a-space>
        </template>
        <template v-else-if="column.dataIndex === 'stat'">
          <a-tag :color="record.stat === 1 ? 'green' : 'red'">
            {{ record.stat === 1 ? '启用' : '禁用' }}
          </a-tag>
        </template>
      </template>
    </a-table>
    <!-- 表格区 -->

    <!-- 分页区 -->
    <div style="margin-top: 15px; float: right">
      <a-pagination v-model:current="pagination.current" :total="pagination.total"
        :show-total="(total) => `共 ${total} 条数据`" @change="onPaginationChange" />
    </div>
    <!-- 分页区 -->
    <!-- 中间内容区域 -->

    <!-- 创建角色模态框 -->
    <a-modal v-model:visible="createModalVisible" title="创建角色" @ok="handleCreate" @cancel="handleCreateCancel"
      :confirm-loading="createLoading">
      <a-form :model="createForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="角色名称" required>
          <a-input v-model:value="createForm.name" placeholder="请输入角色名称" />
        </a-form-item>
        <a-form-item label="角色描述">
          <a-textarea v-model:value="createForm.description" placeholder="请输入角色描述" :rows="4" />
        </a-form-item>
        <a-form-item label="角色状态">
          <a-select v-model:value="createForm.stat" placeholder="请选择状态">
            <a-select-option :value="1">启用</a-select-option>
            <a-select-option :value="0">禁用</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 编辑角色模态框 -->
    <a-modal v-model:visible="editModalVisible" title="编辑角色" @ok="handleEdit" @cancel="handleEditCancel"
      :confirm-loading="editLoading">
      <a-form :model="editForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="角色名称" required>
          <a-input v-model:value="editForm.name" placeholder="请输入角色名称" />
        </a-form-item>
        <a-form-item label="角色描述">
          <a-textarea v-model:value="editForm.description" placeholder="请输入角色描述" :rows="4" />
        </a-form-item>
        <a-form-item label="角色状态">
          <a-select v-model:value="editForm.stat" placeholder="请选择状态">
            <a-select-option :value="1">启用</a-select-option>
            <a-select-option :value="0">禁用</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { reactive, defineComponent, ref } from 'vue'
import { mapState } from 'pinia'
import { useUserStore } from '@/stores/user'
import { message, Modal } from 'ant-design-vue'

export default defineComponent({
  data() {
    // 默认的表单状态，用于重置
    const defaultFormState = {
      menuName: '',
      name: '',
      description: '',
      stat: undefined
    }

    const formState = reactive({ ...defaultFormState })
    const data = reactive([])
    const roleSource = reactive([])

    // 创建角色相关数据
    const createModalVisible = ref(false)
    const createLoading = ref(false)
    const createForm = reactive({
      name: '',
      description: '',
      stat: 1
    })

    // 编辑角色相关数据
    const editModalVisible = ref(false)
    const editLoading = ref(false)
    const editForm = reactive({
      id: '',
      name: '',
      description: '',
      stat: 1
    })

    return {
      formState,
      data,
      roleSource,
      createModalVisible,
      createLoading,
      createForm,
      editModalVisible,
      editLoading,
      editForm,
      defaultFormState
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
      // 将表单状态重置为默认值
      Object.assign(this.formState, this.defaultFormState)
      // 重置分页到第一页
      this.pagination.current = 1
      // 重新查询数据
      this.query()
    },

    /**
     * 查询数据
     */
    query() {
      this.$http
        .request({
          url: '/personkit/sys/role/page',
          data: {
            num: this.pagination.current,
            query: this.formState,
            size: this.pagination.pageSize
          }
        })
        .then((res) => {
          this.pagination.current = res.num
          this.pagination.total = res.total
          this.data = reactive(res.data)
        })
        .catch((err) => console.log(err))
    },

    /**
     * 删除角色
     */
    handleDelete(record) {
      Modal.confirm({
        title: '确认删除',
        content: `确定要删除角色 "${record.name}" 吗？此操作不可恢复。`,
        okText: '确认',
        cancelText: '取消',
        okType: 'danger',
        onOk: () => {
          this.$http
            .request({
              url: '/personkit/sys/role/deletes',
              method: 'post',
              data: {
                ids: [record.id]
              }
            })
            .then((res) => {
              message.success('删除成功')
              this.query() // 重新加载数据
            })
            .catch((err) => {
              console.error('删除失败:', err)
              message.error('删除失败')
            })
        }
      })
    },

    /**
     * 切换角色状态
     */
    handleToggleStatus(record) {
      const newStatus = record.stat === 1 ? 0 : 1
      const statusText = newStatus === 1 ? '启用' : '禁用'
      const currentStatusText = record.stat === 1 ? '启用' : '禁用'

      Modal.confirm({
        title: '确认操作',
        content: `确定要将角色 "${record.name}" 从${currentStatusText}状态改为${statusText}状态吗？`,
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$http
            .request({
              url: '/personkit/sys/role/update',
              method: 'post',
              data: {
                id: record.id,
                stat: newStatus
              }
            })
            .then((res) => {
              message.success(`角色${statusText}成功`)
              // 更新本地数据
              record.stat = newStatus
              this.query() // 重新加载数据
            })
            .catch((err) => {
              console.error('切换状态失败:', err)
              message.error(`${statusText}失败`)
            })
        }
      })
    },

    /**
     * 显示创建角色模态框
     */
    showCreateModal() {
      this.createForm.name = ''
      this.createForm.description = ''
      this.createForm.stat = 1
      this.createModalVisible = true
    },

    /**
     * 创建角色
     */
    handleCreate() {
      if (!this.createForm.name.trim()) {
        message.warning('请输入角色名称')
        return
      }

      this.createLoading = true
      this.$http
        .request({
          url: '/personkit/sys/role/save',
          method: 'post',
          data: this.createForm
        })
        .then((res) => {
          message.success('创建角色成功')
          this.createModalVisible = false
          this.query() // 重新加载数据
        })
        .catch((err) => {
          console.error('创建角色失败:', err)
          message.error('创建角色失败')
        })
        .finally(() => {
          this.createLoading = false
        })
    },

    /**
     * 取消创建
     */
    handleCreateCancel() {
      this.createModalVisible = false
    },

    /**
     * 显示编辑角色模态框
     */
    showEditModal(record) {
      this.editForm.id = record.id
      this.editForm.name = record.name
      this.editForm.description = record.description || ''
      this.editForm.stat = record.stat
      this.editModalVisible = true
    },

    /**
     * 编辑角色
     */
    handleEdit() {
      if (!this.editForm.name.trim()) {
        message.warning('请输入角色名称')
        return
      }

      this.editLoading = true
      this.$http
        .request({
          url: '/personkit/sys/role/update',
          method: 'post',
          data: this.editForm
        })
        .then((res) => {
          message.success('编辑角色成功')
          this.editModalVisible = false
          this.query() // 重新加载数据
        })
        .catch((err) => {
          console.error('编辑角色失败:', err)
          message.error('编辑角色失败')
        })
        .finally(() => {
          this.editLoading = false
        })
    },

    /**
     * 取消编辑
     */
    handleEditCancel() {
      this.editModalVisible = false
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
     * 修改菜单 - 跳转到角色菜单配置页面
     */
    modifyMenus(record) {
      // 将当前角色信息存储到sessionStorage，供角色菜单配置页面使用
      sessionStorage.setItem(
        'current_role_for_menu',
        JSON.stringify({
          id: record.id,
          name: record.name,
          description: record.description,
          stat: record.stat
        })
      )

      // 跳转到角色菜单配置页面
      this.$router.push({
        name: 'role_menu_management',
        query: { roleId: record.id }
      })
    }
  },
  mounted() {
    this.query()
  },
  setup() {
    const columns = [
      {
        title: '角色名称',
        dataIndex: 'name',
        key: 'name',
        align: 'center'
      },
      {
        title: '角色状态',
        dataIndex: 'stat',
        key: 'stat',
        align: 'center',
        width: 100
      },
      {
        title: '角色描述',
        dataIndex: 'description',
        key: 'description',
        ellipsis: true,
        align: 'center'
      },
      {
        title: '包含菜单',
        dataIndex: 'menuName',
        key: 'menuName',
        ellipsis: true,
        width: 150,
        align: 'center'
      },
      {
        title: '操作',
        dataIndex: 'action',
        key: 'action',
        width: 400,
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
      { label: '启用', value: 1 },
      { label: '禁用', value: 0 }
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
</style>