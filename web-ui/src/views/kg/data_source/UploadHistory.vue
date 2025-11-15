<template>
  <div>
    <!-- 上部搜索条件区域 -->
    <div style="display: flex; justify-content: space-between; align-items: center">
      <a-form
        layout="inline"
        name="basic"
        autocomplete="off"
        :model="formState"
        @finish="handleFinish"
      >
        <a-form-item label="文件名" name="fileName">
          <a-input v-model:value="formState.fileName" placeholder="文件名，模糊查询"> </a-input>
        </a-form-item>
        <a-form-item label="状态" name="stat">
          <a-select v-model:value="formState.stat" placeholder="请选择状态" style="width: 120px">
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="0">上传中</a-select-option>
            <a-select-option value="1">上传成功</a-select-option>
            <a-select-option value="2">上传失败</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit"> 搜索 </a-button>
        </a-form-item>
        <a-form-item>
          <a-button @click="handleReset"> 重置 </a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 上部搜索条件区域 -->
    <a-divider dashed />
    <!-- 中间内容区域 -->
    <!-- 表格区 -->
    <a-table :columns="columns" :data-source="tableData" size="small" :pagination="false">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'stat'">
          <a-tag :color="getStatusColor(record.stat)">
            {{ getStatusText(record.stat) }}
          </a-tag>
        </template>
        <template v-if="column.dataIndex === 'uploadMode'">
          {{ getUploadModeText(record.uploadMode) }}
        </template>
        <template v-if="column.dataIndex === 'action'">
          <a-space>
            <a-button type="link" @click="showDetail(record)" :disabled="record.stat !== 1">
              详情
            </a-button>
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
    <!-- 中间内容区域 -->
  </div>
</template>

<script>
import { reactive, defineComponent, ref } from 'vue'
import { message } from 'ant-design-vue'

export default defineComponent({
  data() {
    const formState = reactive({
      fileName: '',
      stat: ''
    })
    const data = reactive([])
    return { formState, data }
  },
  computed: {
    tableData: function () {
      return this.data
    }
  },
  methods: {
    handleFinish() {
      this.query()
    },

    /**
     * 查询上传历史数据
     */
    query() {
      const params = {
        num: String(this.pagination.current),
        size: String(this.pagination.pageSize),
        query: {}
      }

      // 添加搜索条件
      if (this.formState.fileName) {
        params.query.fileName = this.formState.fileName
      }
      if (this.formState.stat !== '') {
        params.query.stat = this.formState.stat
      }

      this.$http
        .request({
          url: '/personkit/upload/record',
          data: params
        })
        .then((res) => {
          this.pagination.current = res.num || 1
          this.pagination.total = res.total || 0
          this.data = reactive(res.data || [])
        })
        .catch((err) => {
          console.error('获取上传历史失败:', err)
          message.error('获取上传历史失败: ' + (err.message || '未知错误'))
        })
    },

    /**
     * 重置搜索条件
     */
    handleReset() {
      this.formState = reactive({
        fileName: '',
        stat: ''
      })
      this.pagination.current = 1
      this.query()
    },

    /**
     * 获取状态颜色
     */
    getStatusColor(stat) {
      const colorMap = {
        0: 'processing', // 上传中 - 蓝色
        1: 'success', // 上传成功 - 绿色
        2: 'error' // 上传失败 - 红色
      }
      return colorMap[stat] || 'default'
    },

    /**
     * 获取状态文本
     */
    getStatusText(stat) {
      const textMap = {
        0: '上传中',
        1: '上传成功',
        2: '上传失败'
      }
      return textMap[stat] || '未知'
    },

    /**
     * 获取上传模式文本
     */
    getUploadModeText(mode) {
      const textMap = {
        0: '仅上传',
        1: '上传且解析'
      }
      return textMap[mode] || '未知'
    },

    /**
     * 显示详情
     */
    showDetail(record) {
      // 这里可以根据需要跳转到详情页面或显示详情弹窗
      console.log('查看详情:', record)
      message.info('详情功能开发中...')
    },

    /**
     * 分页变化
     */
    onPaginationChange(page, pageSize) {
      this.pagination.current = page
      this.pagination.pageSize = pageSize
      this.query()
    }
  },

  mounted() {
    this.query()
  },

  setup() {
    const columns = [
      {
        title: '文件名称',
        dataIndex: 'fileName',
        key: 'fileName',
        align: 'center',
        ellipsis: true
      },
      {
        title: '文件夹',
        dataIndex: 'folderName',
        key: 'folderName',
        align: 'center',
        ellipsis: true
      },
      {
        title: '用户ID',
        dataIndex: 'userId',
        key: 'userId',
        align: 'center',
        width: 100
      },
      {
        title: '状态',
        dataIndex: 'stat',
        key: 'stat',
        align: 'center',
        width: 100
      },
      {
        title: '上传模式',
        dataIndex: 'uploadMode',
        key: 'uploadMode',
        align: 'center',
        width: 120
      },
      {
        title: '消息',
        dataIndex: 'msg',
        key: 'msg',
        align: 'center',
        ellipsis: true
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
        key: 'createTime',
        align: 'center',
        width: 180
      },
      {
        title: '完成时间',
        dataIndex: 'finishTime',
        key: 'finishTime',
        align: 'center',
        width: 180
      },
      {
        title: '操作',
        dataIndex: 'action',
        key: 'action',
        align: 'center',
        width: 100
      }
    ]

    const pagination = {
      current: 1,
      total: 0,
      pageSize: 10
    }

    return { columns, pagination }
  }
})
</script>

<style lang="less" scoped></style>
