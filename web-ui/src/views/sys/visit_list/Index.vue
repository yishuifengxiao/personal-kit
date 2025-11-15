<template>
  <div class="high-data-table">
    <!-- 上部搜索条件区域 -->
    <a-form
      layout="inline"
      name="basic"
      autocomplete="off"
      :model="formState"
      @finish="handleFinish"
    >
      <a-form-item label="类型" name="username">
        <a-select
          allowClear
          style="width: 180px"
          placeholder="状态"
          v-model:value="formState.auth"
          :options="statusOptions"
        ></a-select>
      </a-form-item>

      <a-form-item label="账号" name="nickname">
        <a-input allowClear v-model:value="formState.nickname" placeholder="昵称，模糊查询">
        </a-input>
      </a-form-item>


      <a-form-item>
        <a-button type="primary" html-type="submit"> 搜索 </a-button>
      </a-form-item>
      <a-form-item>
        <a-button @click="handleReset"> 重置 </a-button>
      </a-form-item>
      <a-form-item>
        <a-button type="dashed" danger @click="handleClear"> 清空 </a-button>
      </a-form-item>
    </a-form>

    <!-- 中间内容区域 -->
    <div style="margin-top: 16px;">
    <!-- 表格区 -->
    <a-table :columns="columns" :data-source="tableData" :pagination="false" size="small" :scroll="{ x: 1500 }">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <a-space>
            <a-button
              type="link"
              @click="showDetail(record)"
              :disabled="record.stat != 2 || record.actualTotalNum === 0"
              >详情</a-button
            >
            <a>删除</a>
          </a-space>
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
    </div>
    <!-- 中间内容区域 -->
  </div>
</template>

<script>
import { reactive, defineComponent, ref } from 'vue'
import { UserOutlined } from '@ant-design/icons-vue'
import { mapState } from 'pinia'
import { useUserStore } from '@/stores/user'
import { message, Modal } from 'ant-design-vue'
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
     * 重置搜索条件
     */
    handleReset() {
      this.formState = reactive({})
      this.pagination.current = 1
      this.query()
    },

    /**
     * 清空所有数据
     */
    handleClear() {
      Modal.confirm({
        title: '确认清空',
        content: '确定要清空所有登录记录吗？此操作不可恢复。',
        okText: '确认',
        cancelText: '取消',
        okType: 'danger',
        onOk: () => {
          this.$http
            .request({
              url: '/personkit/record/personal/loginRecord/clear',
              method: 'post'
            })
            .then(() => {
              message.success('清空数据成功')
              this.query() // 重新查询数据
            })
            .catch((err) => {
              console.error('清空数据失败:', err)
              message.error('清空数据失败')
            })
        }
      })
    },

    /**
     * 查询数据
     */
    query() {
      this.$http
        .request({
          url: '/personkit/record/personal/login/page',
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
        title: '操作类型',
        dataIndex: 'strategyName',
        key: 'strategyName',
        align: 'center',
        fixed: 'left',
        width: 150
      },
      {
        title: '操作用户',
        dataIndex: 'username',
        key: 'username',
        align: 'center',
        width: 150,
        fixed: 'left'
      },
      {
        title: 'IP地址',
        dataIndex: 'ip',
        key: 'ip',
        ellipsis: true,
        align: 'center',
        width: 100,
        fixed: 'left'
      },
      {
        title: '来源',
        dataIndex: 'referer',
        key: 'referer',
        ellipsis: true,
        width: 100,
        align: 'center'
      },
      {
        title: '浏览器',
        dataIndex: 'userAgent',
        key: 'userAgent',
        ellipsis: true,
        width: 150,
        align: 'center'
      },
      {
        title: '时间',
        dataIndex: 'createTime',
        key: 'createTime',
        ellipsis: true,
        width: 150,
        align: 'center'
      },
      {
        title: '处理信息',
        dataIndex: 'msg',
        key: 'msg',
        width: 150,
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
      { label: '认证成功', value: 0 },
      { label: '认证失败', value: 1 }
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
