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
      <a-form-item label="请求地址" name="uri" class="input">
        <a-input allowClear v-model:value="formState.uri" placeholder="请求地址，模糊查询">
        </a-input>
      </a-form-item>

      <a-form-item label="请求方法" name="method" class="input">
        <a-input allowClear v-model:value="formState.method" placeholder="请求方法，模糊查询">
        </a-input>
      </a-form-item>

      <a-form-item label="操作用户" name="userName" class="input">
        <a-input allowClear v-model:value="formState.userName" placeholder="操作用户，模糊查询">
        </a-input>
      </a-form-item>

      <a-form-item label="最小耗时" name="email" class="input">
        <a-input-number allowClear v-model:value="formState.email" placeholder="最小耗时，单位毫秒">
        </a-input-number>
      </a-form-item>
      <a-form-item label="最大耗时" name="email" class="input">
        <a-input-number allowClear v-model:value="formState.email" placeholder="最大耗时，单位毫秒">
        </a-input-number>
      </a-form-item>

      <a-form-item label="状态" name="stat" class="input">
        <a-select
          allowClear
          placeholder="状态"
          v-model:value="formState.stat"
          :options="userStatusOptions"
        ></a-select>
      </a-form-item>

      <a-space class="input">
        <a-button type="primary" html-type="submit"> 搜索 </a-button>
      </a-space>
    </a-form>

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
            <a>删除</a></a-space
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
    const formState = reactive({})
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
          url: '/personkit/record/personal/visit/page',
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
        title: '请求地址',
        dataIndex: 'uri',
        key: 'uri',
        align: 'center',
        fixed: 'left',
        ellipsis: true,
        width: 150
      },
      {
        title: '方法',
        dataIndex: 'method',
        key: 'method',
        align: 'center',
        ellipsis: true,
        width: 80,
        fixed: 'left'
      },
      {
        title: '请求参数',
        dataIndex: 'queryParam',
        key: 'queryParam',
        ellipsis: true,
        align: 'center',
        width: 100,
        fixed: 'left'
      },
      {
        title: '请求头',
        dataIndex: 'headerParam',
        key: 'headerParam',
        ellipsis: true,
        width: 100,
        align: 'center'
      },
      {
        title: '请求体',
        dataIndex: 'requestBody',
        key: 'requestBody',
        ellipsis: true,
        width: 150,
        align: 'center'
      },
      {
        title: '响应数据',
        dataIndex: 'responseBody',
        key: 'responseBody',
        ellipsis: true,
        width: 150,
        align: 'center'
      },
      {
        title: '耗时(毫秒)',
        dataIndex: 'useMillis',
        key: 'useMillis',
        width: 100,
        ellipsis: true,
        align: 'center'
      },

      {
        title: '请求时间',
        dataIndex: 'createTime',
        key: 'createTime',
        width: 100,
        ellipsis: true,
        align: 'center'
      },
      {
        title: '操作',
        dataIndex: 'action',
        key: 'action',
        width: 100,
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
