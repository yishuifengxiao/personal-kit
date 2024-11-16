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
      <a-row :gutter="24">
        <a-col :span="6">
          <a-form-item label="账号" name="username" class="input">
            <a-input v-model:value="formState.username" placeholder="账号，模糊查询"> </a-input>
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="昵称" name="nickname" class="input">
            <a-input v-model:value="formState.nickname" placeholder="昵称，模糊查询"> </a-input>
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="手机号" name="phone" class="input">
            <a-input v-model:value="formState.phone" placeholder="手机号，模糊查询"> </a-input>
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="邮箱" name="email" class="input">
            <a-input v-model:value="formState.email" placeholder="邮箱，模糊查询"> </a-input>
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="证件号码" name="certNo" class="input">
            <a-input v-model:value="formState.certNo" placeholder="证件号码，模糊查询"> </a-input>
          </a-form-item>
        </a-col>
        <a-col :span="5">
          <a-form-item label="角色" name="roleId" class="input">
            <a-select
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
        </a-col>
        <a-col :span="5">
          <a-form-item label="状态" name="stat" class="input">
            <a-select
              placeholder="状态"
              v-model:value="formState.stat"
              :options="userStatusOptions"
            ></a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="创建时间" name="fileName" class="input">
            <a-range-picker
              v-model:value="formState['rangetimepicker']"
              :placeholder="['开始时间', '结束时间']"
              show-time
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </a-form-item>
        </a-col>
        <a-col :span="1" class="input">
          <a-button type="primary" html-type="submit"> 搜索 </a-button>
        </a-col>
        <a-col :span="20" class="input" style="float: right">
          <a-space>
            <a-button type="primary" html-type="submit"> 创建账号 </a-button>
            <a-button type="primary" html-type="submit"> 修改角色 </a-button>
            <a-button type="primary" danger>批量删除</a-button>
            <a-button type="primary" danger>批量禁用</a-button>
          </a-space>
        </a-col>
      </a-row>
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
            <a>删除</a> <a>修改角色</a> <a>修改状态</a></a-space
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
</template>

<script>
import { reactive, defineComponent, ref } from 'vue'
import { UserOutlined } from '@ant-design/icons-vue'
import { mapState } from 'pinia'
import { useUserStore } from '@/stores/user'
export default defineComponent({
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
      endLockTime: ''
    })
    const data = reactive([])
    const roleSource = reactive([])
    return { formState, data, roleSource }
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
      const tmp = this.formState
      this.formState.startCreateTimetmp = this.formState.rangetimepicker[0]
      this.formState.endCreateTime = this.formState.rangetimepicker[0]
      debugger
      this.query()
    },
    //搜索角色
    handleRoleSearch(val) {
      debugger
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
    }
  },
  components: {
    UserOutlined
  },
  mounted() {
    console.log('------------------------- data sourc emounte')
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
