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
          <a-form-item label="菜单名称" name="name" class="input">
            <a-input v-model:value="formState.name" placeholder="账号，模糊查询"> </a-input>
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="父级菜单" name="父级菜单" class="input">
            <a-input v-model:value="formState.parentName" placeholder="昵称，模糊查询"> </a-input>
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="路由名称" name="routerName" class="input">
            <a-input v-model:value="formState.routerName" placeholder="手机号，模糊查询"> </a-input>
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="需要鉴权" name="auth" class="input">
            <a-input v-model:value="formState.auth" placeholder="邮箱，模糊查询"> </a-input>
          </a-form-item>
        </a-col>

        <a-col :span="1" class="input">
          <a-button type="primary" html-type="submit"> 搜索 </a-button>
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
      name: '',
      parentName: '',
      stat: 0
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
      this.query()
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
    this.query()
  },
  setup() {
    const columns = [
      {
        title: '菜单名称',
        dataIndex: 'name',
        key: 'name',
        align: 'center'
      },
      {
        title: '父级菜单',
        dataIndex: 'parentName',
        key: 'parentName',
        align: 'center'
      },
      {
        title: '角色',
        dataIndex: 'roleName',
        key: 'roleName',
        ellipsis: true,
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
        title: '需要鉴权',
        dataIndex: 'auth',
        key: 'auth',
        ellipsis: true,

        align: 'center'
      },
      {
        title: '菜单位置',
        dataIndex: 'type',
        key: 'type',
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
