<template>
  <div>
    <!-- 上部搜索条件区域 -->
    <a-form
      layout="inline"
      name="basic"
      autocomplete="off"
      :model="formState"
      @finish="handleFinish"
      :label-col="labelCol"
    >
      <a-form-item label="账号" name="username" class="input">
         <a-input allowClear v-model:value="formState.username" placeholder="账号，模糊查询"> </a-input>
      </a-form-item>

      <a-form-item label="昵称" name="nickname" class="input">
         <a-input allowClear v-model:value="formState.nickname" placeholder="昵称，模糊查询"> </a-input>
      </a-form-item>

      <a-form-item label="手机号" name="phone" class="input">
         <a-input allowClear v-model:value="formState.phone" placeholder="手机号，模糊查询"> </a-input>
      </a-form-item>

      <a-form-item label="邮箱" name="email" class="input">
         <a-input allowClear v-model:value="formState.email" placeholder="邮箱，模糊查询"> </a-input>
      </a-form-item>

      <a-form-item label="证件号码" name="certNo" class="input">
         <a-input allowClear v-model:value="formState.certNo" placeholder="证件号码，模糊查询"> </a-input>
      </a-form-item>

      <a-form-item label="角色" name="roleId" class="input">
        <a-select allowClear
          style="width: 180px"
          v-model:value="formState.roleId"
          show-search
          placeholder="input search text"
          :default-active-first-option="false"
          :show-arrow="false"
          :filter-option="false"
          :not-found-content="null"
          :options="roleSource"
          @search="handleRoleSearch"
        ></a-select>
      </a-form-item>

      <a-form-item label="状态" name="stat" class="input">
        <a-select allowClear
          style="width: 180px"
          placeholder="状态"
          v-model:value="formState.stat"
          :options="userStatusOptions"
        ></a-select>
      </a-form-item>

      <a-form-item label="创建时间" name="fileName" class="input">
        <a-range-picker allowClear
          v-model:value="formState['rangetimepicker']"
          :placeholder="['开始时间', '结束时间']"
          show-time
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </a-form-item>

      <a-space>
        <a-button type="primary" html-type="submit"> 搜索 </a-button>
        <a-button type="primary" @click="showCreateModal"> 创建账号 </a-button>
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
              <a-button
                type="link"
                @click="showDetail(record)"
                :disabled="record.stat != 2 || record.actualTotalNum === 0"
                >详情</a-button
              >
              <a-button type="link" @click="showEditModal(record)">编辑</a-button>
              <a>删除</a>  <a>修改状态</a></a-space
            >
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
    <!-- 分页区 -->
    <!-- 中间内容区域 -->
  </div>

  <!-- 创建/编辑账号弹窗 -->
  <a-modal
    v-model:open="modalVisible"
    :title="modalTitle"
    @ok="handleModalOk"
    @cancel="handleModalCancel"
    width="600px"
  >
    <a-form
      ref="userFormRef"
      :model="userForm"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 16 }"
      :rules="userFormRules"
    >
      <a-form-item label="账号" name="username">
        <a-input v-model:value="userForm.username" placeholder="请输入账号" :disabled="isEdit" />
      </a-form-item>
      <a-form-item label="昵称" name="nickname">
        <a-input v-model:value="userForm.nickname" placeholder="请输入昵称" />
      </a-form-item>
      <a-form-item label="手机号" name="phone">
        <a-input v-model:value="userForm.phone" placeholder="请输入手机号（选填）" />
      </a-form-item>
      <a-form-item label="邮箱" name="email">
        <a-input v-model:value="userForm.email" placeholder="请输入邮箱" />
      </a-form-item>
      <a-form-item label="证件号码" name="certNo">
        <a-input v-model:value="userForm.certNo" placeholder="请输入证件号码（选填）" />
      </a-form-item>
      <a-form-item label="角色" name="roleIds">
        <a-select
          v-model:value="userForm.roleIds"
          mode="multiple"
          placeholder="请选择角色"
          :options="roleOptions"
        ></a-select>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { reactive, defineComponent, ref } from 'vue'
import { UserOutlined } from '@ant-design/icons-vue'
import { mapState } from 'pinia'
import { useUserStore } from '@/stores/user'
export default defineComponent({
  props: {
    id: {
      type: [String, Number],
      default: undefined
    }
  },
  data() {
    const formState = reactive({
      id: '',
      username: '',
      nickname: '',
      phone: '',
      email: '',
      certNo: '',
      stat: 0,
      createTime: '',
      lockTime: '',
      lastUpdateTime: '',
      roleName: '',
      roleId: '',
      startCreateTime: '',
      endCreateTime: '',
      startLockTime: '',
      endLockTime: '',
      rangetimepicker: []
    })
    const data = reactive([])
    const roleSource = reactive([])
    
    // 弹窗相关数据
    const modalVisible = ref(false)
    const modalTitle = ref('')
    const isEdit = ref(false)
    const userForm = reactive({
      username: '',
      nickname: '',
      phone: '',
      email: '',
      certNo: '',
      roleIds: []
    })
    const roleOptions = reactive([])
    
    // 表单验证规则
    const userFormRules = {
      username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
      nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
      phone: [
        { required: false, message: '请输入手机号', trigger: 'blur' },
        { 
          pattern: /^1[3-9]\d{9}$/, 
          message: '请输入正确的手机号格式', 
          trigger: 'blur',
          validator: (rule, value) => {
            if (!value || value === '') {
              return Promise.resolve()
            }
            if (!/^1[3-9]\d{9}$/.test(value)) {
              return Promise.reject('请输入正确的手机号格式')
            }
            return Promise.resolve()
          }
        }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
      ],
      certNo: [{ required: false, message: '请输入证件号码', trigger: 'blur' }],
      roleIds: [{ required: true, message: '请选择角色', trigger: 'change' }]
    }
    
    return { 
      formState, 
      data, 
      roleSource,
      modalVisible,
      modalTitle,
      isEdit,
      userForm,
      roleOptions,
      userFormRules
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
      // 处理时间范围选择器的数据
      if (this.formState.rangetimepicker && this.formState.rangetimepicker.length === 2) {
        this.formState.startCreateTime = this.formState.rangetimepicker[0]
        this.formState.endCreateTime = this.formState.rangetimepicker[1]
      } else {
        this.formState.startCreateTime = ''
        this.formState.endCreateTime = ''
      }
      this.query()
    },
    //搜索角色
    handleRoleSearch(val) {
      this.$http
        .request({
          url: '/personkit/sys/role/page',
          data: {
            num: 1,
            query: { name: val },
            size: 1000
          }
        })
        .then((res) => {
          this.roleSource = reactive(res.data)
        })
        .catch((err) => {
          console.error(err)
          this.$msg.error('操作失败')
        })
    },
    /**
     * 查询数据
     */
    query() {
      this.$http
        .request({
          url: '/personkit/sys/user/findPage',
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
     * 显示创建账号弹窗
     */
    showCreateModal() {
      this.isEdit = false
      this.modalTitle = '创建账号'
      this.resetUserForm()
      this.loadRoleOptions()
      this.modalVisible = true
    },

    /**
     * 显示编辑账号弹窗
     */
    showEditModal(record) {
      this.isEdit = true
      this.modalTitle = '编辑账号'
      this.resetUserForm()
      this.loadRoleOptions()
      // 填充编辑数据
      this.userForm.username = record.username
      this.userForm.nickname = record.nickname
      this.userForm.phone = record.phone
      this.userForm.email = record.email
      this.userForm.certNo = record.certNo
      this.userForm.roleIds = record.roleIds || []
      this.modalVisible = true
    },

    /**
     * 重置用户表单
     */
    resetUserForm() {
      this.userForm.username = ''
      this.userForm.nickname = ''
      this.userForm.phone = ''
      this.userForm.email = ''
      this.userForm.certNo = ''
      this.userForm.roleIds = []
    },

    /**
     * 加载角色选项
     */
    loadRoleOptions() {
      this.$http
        .request({
          url: '/personkit/sys/role/page',
          data: {
            num: 1,
            query: {},
            size: 1000
          }
        })
        .then((res) => {
          this.roleOptions = res.data.map(role => ({
            label: role.name,
            value: role.id
          }))
        })
        .catch((err) => console.log(err))
    },

    /**
     * 处理弹窗确定
     */
    handleModalOk() {
      this.$refs.userFormRef.validate().then(() => {
        const apiUrl = this.isEdit ? '/personkit/sys/user/update' : '/personkit/sys/user/create'
        
        // 构建请求数据，只包含非空字段
        const requestData = {
          username: this.userForm.username,
          nickname: this.userForm.nickname,
          email: this.userForm.email,
          roleIds: this.userForm.roleIds
        }
        
        // 手机号和证件号码为选填项，只在有值时添加
        if (this.userForm.phone && this.userForm.phone.trim() !== '') {
          requestData.phone = this.userForm.phone.trim()
        }
        
        if (this.userForm.certNo && this.userForm.certNo.trim() !== '') {
          requestData.certNo = this.userForm.certNo.trim()
        }

        this.$http
          .request({
            url: apiUrl,
            data: requestData
          })
          .then((res) => {
            this.$msg.success(this.isEdit ? '编辑成功' : '创建成功')
            this.modalVisible = false
            this.query() // 重新查询数据
          })
          .catch((err) => {
            this.$msg.error(this.isEdit ? '编辑失败' : '创建失败')
            console.log(err)
          })
      }).catch(() => {
        // 表单验证失败
      })
    },

    /**
     * 处理弹窗取消
     */
    handleModalCancel() {
      this.modalVisible = false
      this.resetUserForm()
    }
  },
  components: {
    UserOutlined
  },
  mounted() {
   
    this.query()
  },
  setup() {
    const columns = [
      {
        title: '账号',
        dataIndex: 'username',
        key: 'username',
        align: 'center',
        fixed: 'left',
        width: 150
      },
      {
        title: '昵称',
        dataIndex: 'nickname',
        key: 'nickname',
        align: 'center',
        width: 150,
        fixed: 'left'
      },
      {
        title: '角色',
        dataIndex: 'roleName',
        key: 'roleName',
        ellipsis: true,
        align: 'center',
        width: 100,
        fixed: 'left'
      },
      {
        title: '状态',
        dataIndex: 'statName',
        key: 'statName',
        ellipsis: true,
        width: 100,
        align: 'center'
      },
      {
        title: '邮箱',
        dataIndex: 'email',
        key: 'email',
        ellipsis: true,
        width: 150,
        align: 'center'
      },
      {
        title: '证件号码',
        dataIndex: 'certNo',
        key: 'certNo',
        ellipsis: true,
        width: 150,
        align: 'center'
      },
      {
        title: '手机号',
        dataIndex: 'phone',
        key: 'phone',
        width: 150,
        ellipsis: true,
        align: 'center'
      },

      {
        title: '创建时间',
        dataIndex: 'createTime',
        key: 'createTime',
        width: 150,
        ellipsis: true,
        align: 'center'
      },
      {
        title: '最后登录时间',
        dataIndex: 'userName',
        key: 'userName',
        width: 100,
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
    const userStatusOptions = reactive([
      { label: '账号正常', value: 0 },
      { label: '账号禁用', value: 1 },
      { label: '账号过期', value: 2 },
      { label: '密码过期', value: 3 },
      { label: '账号锁定', value: 4 }
    ])
    return { columns, pagination, fileList, labelCol, wrapperCol, userStatusOptions }
  }
})
</script>

<style lang="less" scoped>
.input {
  margin: 5px 5px 10px 5px;
  padding-right: 10px;
}
</style>
