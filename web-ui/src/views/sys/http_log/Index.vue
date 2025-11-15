<template>
  <div class="high-data-table">
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

      <a-form-item label="起始时间" name="startTime" class="input">
        <a-date-picker 
          v-model:value="formState.startTime" 
          placeholder="选择起始时间"
          show-time
          format="YYYY-MM-DD HH:mm:ss"
          style="width: 200px"
        />
      </a-form-item>

      <a-form-item label="结束时间" name="endTime" class="input">
        <a-date-picker 
          v-model:value="formState.endTime" 
          placeholder="选择结束时间"
          show-time
          format="YYYY-MM-DD HH:mm:ss"
          style="width: 200px"
        />
      </a-form-item>



    

      <a-space class="input">
        <a-button type="primary" html-type="submit"> 搜索 </a-button>
        <a-button @click="handleReset"> 重置 </a-button>
        <a-button type="dashed" danger @click="handleClear"> 清空 </a-button>
      </a-space>
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
    </div>
    <!-- 中间内容区域 -->
  </div>
</template>

<script>
import { reactive, defineComponent, ref } from 'vue'
import { UserOutlined } from '@ant-design/icons-vue'
import { mapState } from 'pinia'
import { useUserStore } from '@/stores/user'
import { message, Modal, DatePicker } from 'ant-design-vue'
export default defineComponent({
  data() {
    const formState = reactive({
      uri: undefined,
      method: undefined,
      userName: undefined,
      startTime: undefined,
      endTime: undefined
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
      try {
        this.query()
      } catch (error) {
        console.error('表单提交处理错误:', error)
        message.error('搜索操作失败: ' + (error.message || '未知错误'))
      }
    },

    /**
     * 将Date对象转换为北京时间字符串格式
     * @param {Date} date - Date对象
     * @returns {string} - 北京时间字符串 (YYYY-MM-DD HH:mm:ss)
     */
    formatBeijingTime(date) {
      if (!date) return undefined
      try {
        // 确保date是有效的Date对象
        const validDate = date instanceof Date ? date : new Date(date)
        if (isNaN(validDate.getTime())) return undefined
        
        const beijingTime = new Date(validDate.getTime() - validDate.getTimezoneOffset() * 60000)
        return beijingTime.toISOString().slice(0, 19).replace('T', ' ')
      } catch (error) {
        console.error('时间格式转换错误:', error)
        return undefined
      }
    },

    /**
     * 重置搜索条件
     */
    handleReset() {
      this.formState = reactive({
        uri: undefined,
        method: undefined,
        userName: undefined,
        startTime: undefined,
        endTime: undefined
      })
      this.pagination.current = 1
      this.query()
    },

    /**
     * 清空所有数据
     */
    handleClear() {
      Modal.confirm({
        title: '确认清空',
        content: '确定要清空所有HTTP访问记录吗？此操作不可恢复。',
        okText: '确认',
        cancelText: '取消',
        okType: 'danger',
        onOk: () => {
          this.$http
            .request({
              url: '/personkit/record/personal/visitRecord/clear',
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
      try {
        // 处理时间格式转换，将Date对象转换为北京时间字符串格式
        const processedQuery = { 
          ...this.formState,
          startTime: this.formatBeijingTime(this.formState.startTime),
          endTime: this.formatBeijingTime(this.formState.endTime)
        }

        this.$http
          .request({
            url: '/personkit/record/personal/visit/page',
            data: {
              num: this.pagination.current,
              query: processedQuery,
              size: this.pagination.pageSize
            }
          })
          .then((res) => {
            this.pagination.current = res.num
            this.pagination.total = res.total
            this.data = reactive(res.data)
          })
          .catch((err) => {
            console.error('查询数据失败:', err)
            message.error('查询数据失败: ' + (err.message || '未知错误'))
          })
      } catch (error) {
        console.error('查询方法执行错误:', error)
        message.error('查询数据时发生错误')
      }
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
    UserOutlined,
    ADatePicker: DatePicker
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
