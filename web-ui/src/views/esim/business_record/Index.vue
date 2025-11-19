<template>
  <div>
    <!-- 搜索条件区域 -->
    <a-form
      layout="inline"
      name="basic"
      autocomplete="off"
      :model="formState"
      @finish="handleFinish"
    >
      <a-form-item label="ICCID" name="iccid">
        <a-input v-model:value="formState.iccid" placeholder="ICCID，模糊查询" allow-clear />
      </a-form-item>
      <a-form-item label="EID" name="eid">
        <a-input v-model:value="formState.eid" placeholder="EID，模糊查询" allow-clear />
      </a-form-item>
      <a-form-item label="MATCH_ID" name="matchId">
        <a-input v-model:value="formState.matchId" placeholder="MATCH_ID" allow-clear />
      </a-form-item>
      <a-form-item label="功能类型" name="functionType">
        <a-select v-model:value="formState.functionType" placeholder="请选择功能类型" allow-clear style="width: 150px">
          <a-select-option value="initAuth">初始化认证</a-select-option>
          <a-select-option value="authClient">客户端认证</a-select-option>
          <a-select-option value="getBindData">获取绑定数据</a-select-option>
          <a-select-option value="cancelSession">取消会话</a-select-option>
          <a-select-option value="downloadOrder">下载订单</a-select-option>
          <a-select-option value="confirmOrder">确认订单</a-select-option>
          <a-select-option value="publish">发布</a-select-option>
          <a-select-option value="cancelOrder">取消订单</a-select-option>
          <a-select-option value="generateUPP">生成UPP</a-select-option>
          <a-select-option value="getStatus">获取状态</a-select-option>
          <a-select-option value="mifiQueryCode">MIFI查询代码</a-select-option>
          <a-select-option value="mifiQueryStatus">MIFI查询状态</a-select-option>
          <a-select-option value="queryCode">查询代码</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="运营商" name="operator">
        <a-input v-model:value="formState.operator" placeholder="运营商" allow-clear />
      </a-form-item>
      <a-form-item label="所属租户" name="tenant">
        <a-input v-model:value="formState.tenant" placeholder="所属租户" allow-clear />
      </a-form-item>
      <a-form-item label="请求IP" name="requestIp">
        <a-input v-model:value="formState.requestIp" placeholder="请求IP" allow-clear />
      </a-form-item>
      <a-form-item label="请求时间" name="requestTimeRange">
        <a-range-picker v-model:value="formState.requestTimeRange" show-time format="YYYY-MM-DD HH:mm:ss" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
      <a-form-item>
        <a-button @click="handleReset">重置</a-button>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" danger @click="handleClearAll">清除全部</a-button>
      </a-form-item>
    </a-form>

    <a-divider dashed />

    <!-- 表格区域 -->
    <a-table
      :columns="columns"
      :data-source="data"
      :pagination="false"
      :loading="loading"
      size="small"
      row-key="id"
      :scroll="{ x: 1200 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === 1 ? 'green' : 'red'">
            {{ record.status === 1 ? '成功' : '失败' }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'functionName'">
          <a-tag :color="getFunctionColor(record.functionType)">
            {{ record.functionName }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'costTime'">
          <span :style="{ color: record.costTime > 1000 ? '#ff4d4f' : '#52c41a' }">
            {{ record.costTime }}
          </span>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="handleDetail(record)">详情</a-button>
            <a-button type="link" size="small" @click="handleRequestResponse(record)">请求/响应</a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 分页 -->
    <div style="margin-top: 16px; display: flex; justify-content: flex-end">
      <a-pagination
        v-model:current="pagination.current"
        :total="pagination.total"
        :page-size="pagination.pageSize"
        :show-total="(total) => `共 ${total} 条记录`"
        :show-size-changer="true"
        :show-quick-jumper="true"
        @change="onPaginationChange"
      />
    </div>

    <!-- 详情弹窗 -->
    <a-modal
      v-model:open="detailVisible"
      title="业务记录详情"
      @cancel="handleDetailCancel"
      width="700px"
      :footer="null"
    >
      <a-descriptions :column="2" bordered size="small">
        <a-descriptions-item label="ICCID">{{ detailData.iccid }}</a-descriptions-item>
        <a-descriptions-item label="EID">{{ detailData.eid }}</a-descriptions-item>
        <a-descriptions-item label="MATCH_ID">{{ detailData.matchId }}</a-descriptions-item>
        <a-descriptions-item label="功能类型">
          <a-tag :color="getFunctionColor(detailData.functionType)">
            {{ detailData.functionName }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="TransactionId">{{ detailData.transactionId }}</a-descriptions-item>
        <a-descriptions-item label="运营商">{{ detailData.operator }}</a-descriptions-item>
        <a-descriptions-item label="所属租户">{{ detailData.tenant }}</a-descriptions-item>
        <a-descriptions-item label="请求IP">{{ detailData.requestIp }}</a-descriptions-item>
        <a-descriptions-item label="请求时间">{{ detailData.requestTime }}</a-descriptions-item>
        <a-descriptions-item label="耗时(毫秒)">
          <span :style="{ color: detailData.costTime > 1000 ? '#ff4d4f' : '#52c41a' }">
            {{ detailData.costTime }}
          </span>
        </a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="detailData.status === 1 ? 'green' : 'red'">
            {{ detailData.status === 1 ? '成功' : '失败' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="错误信息" :span="2" v-if="detailData.errorMsg">
          <span style="color: #ff4d4f">{{ detailData.errorMsg }}</span>
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>

    <!-- 请求/响应数据弹窗 -->
    <a-modal
      v-model:open="requestResponseVisible"
      title="请求/响应数据"
      @cancel="handleRequestResponseCancel"
      width="800px"
      :footer="null"
    >
      <a-tabs>
        <a-tab-pane key="request" tab="请求参数">
          <a-textarea
            :value="JSON.stringify(requestResponseData.requestParams, null, 2)"
            :rows="15"
            readonly
            style="font-family: 'Courier New', monospace; font-size: 12px"
          />
        </a-tab-pane>
        <a-tab-pane key="response" tab="响应数据">
          <a-textarea
            :value="JSON.stringify(requestResponseData.responseData, null, 2)"
            :rows="15"
            readonly
            style="font-family: 'Courier New', monospace; font-size: 12px"
          />
        </a-tab-pane>
      </a-tabs>
    </a-modal>
  </div>
</template>

<script>
import { reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'

export default {
  name: 'BusinessRecord',
  setup() {
    const formState = reactive({
      iccid: '',
      eid: '',
      matchId: '',
      functionType: undefined,
      operator: '',
      tenant: '',
      requestIp: '',
      requestTimeRange: []
    })

    const data = ref([])
    const loading = ref(false)
    const detailVisible = ref(false)
    const detailData = ref({})
    const requestResponseVisible = ref(false)
    const requestResponseData = ref({
      requestParams: {},
      responseData: {}
    })

    const pagination = reactive({
      current: 1,
      total: 0,
      pageSize: 20
    })

    const columns = [
      {
        title: 'ICCID',
        dataIndex: 'iccid',
        key: 'iccid',
        width: '12%',
        ellipsis: true
      },
      {
        title: 'EID',
        dataIndex: 'eid',
        key: 'eid',
        width: '12%',
        ellipsis: true
      },
      {
        title: 'MATCH_ID',
        dataIndex: 'matchId',
        key: 'matchId',
        width: '10%',
        ellipsis: true
      },
      {
        title: '功能',
        dataIndex: 'functionName',
        key: 'functionName',
        width: '12%'
      },
      {
        title: 'TransactionId',
        dataIndex: 'transactionId',
        key: 'transactionId',
        width: '12%',
        ellipsis: true
      },
      {
        title: '运营商',
        dataIndex: 'operator',
        key: 'operator',
        width: '8%'
      },
      {
        title: '所属租户',
        dataIndex: 'tenant',
        key: 'tenant',
        width: '8%'
      },
      {
        title: '请求IP',
        dataIndex: 'requestIp',
        key: 'requestIp',
        width: '8%'
      },
      {
        title: '请求时间',
        dataIndex: 'requestTime',
        key: 'requestTime',
        width: '12%'
      },
      {
        title: '耗时(毫秒)',
        dataIndex: 'costTime',
        key: 'costTime',
        width: '8%',
        align: 'right'
      },
      {
        title: '状态',
        dataIndex: 'status',
        key: 'status',
        width: '6%',
        align: 'center'
      },
      {
        title: '操作',
        dataIndex: 'action',
        key: 'action',
        width: '10%',
        align: 'center',
        fixed: 'right'
      }
    ]

    const handleFinish = () => {
      pagination.current = 1
      query()
    }

    const handleReset = () => {
      Object.assign(formState, {
        iccid: '',
        eid: '',
        matchId: '',
        functionType: undefined,
        operator: '',
        tenant: '',
        requestIp: '',
        requestTimeRange: []
      })
      pagination.current = 1
      query()
    }

    const handleClearAll = () => {
      Modal.confirm({
        title: '确认清除',
        content: '确定要清除所有业务记录吗？此操作不可恢复。',
        onOk: () => {
          // 这里应该调用清除全部记录的API
          message.success('清除成功')
          query()
        }
      })
    }

    const onPaginationChange = (page, pageSize) => {
      pagination.current = page
      pagination.pageSize = pageSize
      query()
    }

    const query = () => {
      loading.value = true
      const params = {
        pageNum: pagination.current,
        pageSize: pagination.pageSize,
        ...formState
      }
      
      // 处理时间范围
      if (params.requestTimeRange && params.requestTimeRange.length === 2) {
        params.startTime = params.requestTimeRange[0].format('YYYY-MM-DD HH:mm:ss')
        params.endTime = params.requestTimeRange[1].format('YYYY-MM-DD HH:mm:ss')
        delete params.requestTimeRange
      }
      
      // 模拟API调用
      setTimeout(() => {
        loading.value = false
        data.value = [
          {
            id: 1,
            iccid: '89860012345678901234',
            eid: '12345678901234567890123456789012',
            matchId: 'MATCH_001',
            functionType: 'initAuth',
            functionName: '初始化认证',
            transactionId: 'TRANS_001',
            operator: '中国移动',
            tenant: '默认租户',
            requestIp: '192.168.1.100',
            requestTime: '2024-01-15 14:30:25',
            costTime: 1250,
            status: 1,
            errorMsg: ''
          },
          {
            id: 2,
            iccid: '89860012345678901235',
            eid: '12345678901234567890123456789013',
            matchId: 'MATCH_002',
            functionType: 'downloadOrder',
            functionName: '下载订单',
            transactionId: 'TRANS_002',
            operator: '中国联通',
            tenant: '默认租户',
            requestIp: '192.168.1.101',
            requestTime: '2024-01-15 14:35:30',
            costTime: 850,
            status: 0,
            errorMsg: '下载失败：网络超时'
          }
        ]
        pagination.total = 2
      }, 500)
    }

    const handleDetail = (record) => {
      detailData.value = { ...record }
      detailVisible.value = true
    }

    const handleDetailCancel = () => {
      detailVisible.value = false
      detailData.value = {}
    }

    const handleRequestResponse = (record) => {
      // 模拟获取请求响应数据
      loading.value = true
      setTimeout(() => {
        loading.value = false
        requestResponseData.value = {
          requestParams: {
            iccid: record.iccid,
            eid: record.eid,
            functionType: record.functionType,
            timestamp: record.requestTime
          },
          responseData: {
            code: record.status === 1 ? 200 : 500,
            message: record.status === 1 ? '操作成功' : record.errorMsg,
            data: {
              transactionId: record.transactionId,
              matchId: record.matchId
            }
          }
        }
        requestResponseVisible.value = true
      }, 300)
    }

    const handleRequestResponseCancel = () => {
      requestResponseVisible.value = false
      requestResponseData.value = {
        requestParams: {},
        responseData: {}
      }
    }

    const getFunctionColor = (functionType) => {
      const colorMap = {
        'initAuth': 'blue',
        'authClient': 'green',
        'getBindData': 'cyan',
        'cancelSession': 'orange',
        'downloadOrder': 'purple',
        'confirmOrder': 'geekblue',
        'publish': 'gold',
        'cancelOrder': 'red',
        'generateUPP': 'lime',
        'getStatus': 'processing',
        'mifiQueryCode': 'magenta',
        'mifiQueryStatus': 'volcano',
        'queryCode': 'teal'
      }
      return colorMap[functionType] || 'default'
    }

    // 初始化加载数据
    query()

    return {
      formState,
      data,
      loading,
      pagination,
      columns,
      detailVisible,
      detailData,
      requestResponseVisible,
      requestResponseData,
      handleFinish,
      handleReset,
      handleClearAll,
      onPaginationChange,
      query,
      handleDetail,
      handleDetailCancel,
      handleRequestResponse,
      handleRequestResponseCancel,
      getFunctionColor
    }
  }
}
</script>

<style scoped>
</style>