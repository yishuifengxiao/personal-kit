<template>
  <div class="profile-manage-container">
    <!-- 搜索条件和操作按钮区域 - 合并到同一行 -->
    <div class="search-operation-area">
      <a-form
        layout="inline"
        :model="searchForm"
        @finish="handleSearch"
        class="search-form"
      >
        <a-form-item label="查询类型" name="queryType">
          <a-select
            v-model:value="searchForm.queryType"
            style="width: 120px"
            placeholder="请选择查询类型"
            allow-clear
          >
            <a-select-option value="all">全部数据</a-select-option>
            <a-select-option value="downloadable">可下载</a-select-option>
            <a-select-option value="resetable">可重置</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="ICCID" name="iccid">
          <a-input
            v-model:value="searchForm.iccid"
            placeholder="请输入20位16进制ICCID"
            style="width: 200px"
            allow-clear
          />
        </a-form-item>

        <a-form-item label="MatchingId" name="matchingId">
          <a-input
            v-model:value="searchForm.matchingId"
            placeholder="如：WTBIJ-EG8C0-C72NY-VNOPX"
            style="width: 200px"
            allow-clear
          />
        </a-form-item>

        <a-form-item label="EID" name="eid">
          <a-input
            v-model:value="searchForm.eid"
            placeholder="请输入32位16进制EID"
            style="width: 200px"
            allow-clear
          />
        </a-form-item>

        <a-form-item label="Profile状态" name="profileStatus">
          <a-select
            v-model:value="searchForm.profileStatus"
            style="width: 140px"
            placeholder="请选择状态"
            allow-clear
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="Available">Available</a-select-option>
            <a-select-option value="Allocated">Allocated</a-select-option>
            <a-select-option value="Linked">Linked</a-select-option>
            <a-select-option value="Confirmed">Confirmed</a-select-option>
            <a-select-option value="Released">Released</a-select-option>
            <a-select-option value="Downloaded">Downloaded</a-select-option>
            <a-select-option value="Installed">Installed</a-select-option>
            <a-select-option value="Error">Error</a-select-option>
            <a-select-option value="Unavailable">Unavailable</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="通知状态" name="notificationStatus">
          <a-select
            v-model:value="searchForm.notificationStatus"
            style="width: 120px"
            placeholder="请选择状态"
            allow-clear
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="Enabled">Enabled</a-select-option>
            <a-select-option value="Disabled">Disabled</a-select-option>
            <a-select-option value="Deleted">Deleted</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="下载方式" name="downloadMethod">
          <a-select
            v-model:value="searchForm.downloadMethod"
            style="width: 140px"
            placeholder="请选择下载方式"
            allow-clear
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="default">默认SM-DP+</a-select-option>
            <a-select-option value="activation">激活码</a-select-option>
            <a-select-option value="alt-smds">ALT-SM-DS</a-select-option>
            <a-select-option value="root-smds">ROOT-SM-DS</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="所属租户" name="tenant">
          <a-input
            v-model:value="searchForm.tenant"
            placeholder="请输入租户"
            style="width: 150px"
            allow-clear
          />
        </a-form-item>

        <a-form-item label="运营商" name="carrier">
          <a-input
            v-model:value="searchForm.carrier"
            placeholder="请输入运营商"
            style="width: 150px"
            allow-clear
          />
        </a-form-item>

        <a-form-item label="Profile类" name="profileClass">
          <a-select
            v-model:value="searchForm.profileClass"
            style="width: 120px"
            placeholder="请选择Profile类"
            allow-clear
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="test">test</a-select-option>
            <a-select-option value="provisioning">provisioning</a-select-option>
            <a-select-option value="operational">operational</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="PPR策略" name="pprPolicy">
          <a-select
            v-model:value="searchForm.pprPolicy"
            style="width: 120px"
            placeholder="请选择PPR策略"
            allow-clear
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="PPR1">PPR1</a-select-option>
            <a-select-option value="PPR2">PPR2</a-select-option>
            <a-select-option value="PPR1,PPR2">PPR1、PPR2</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="重置规则" name="resetRule">
          <a-select
            v-model:value="searchForm.resetRule"
            style="width: 140px"
            placeholder="请选择重置规则"
            allow-clear
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="no_reset">不可重置</a-select-option>
            <a-select-option value="resetable">可重置</a-select-option>
            <a-select-option value="auto_reset">自动重置</a-select-option>
            <a-select-option value="auto_recycle">自动回收</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="DS标记" name="dsFlag">
          <a-input
            v-model:value="searchForm.dsFlag"
            placeholder="请输入DS标记"
            style="width: 150px"
            allow-clear
          />
        </a-form-item>

        <a-form-item label="更新时间" name="updateTime">
          <a-range-picker
            v-model:value="searchForm.updateTime"
            style="width: 240px"
            show-time
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </a-form-item>

        <a-form-item>
          <a-button type="primary" html-type="submit">搜索</a-button>
        </a-form-item>
        <a-form-item>
          <a-button @click="handleReset">重置</a-button>
        </a-form-item>

        <!-- 操作按钮 - 放在表单项中，会随着表单一起换行 -->
        <a-form-item class="operation-buttons">
          <a-space>
            <a-button type="primary" @click="handleAdd">新增</a-button>
            <a-dropdown>
              <template #overlay>
                <a-menu>
                  <a-menu-item key="import" @click="handleImport">导入</a-menu-item>
                  <a-menu-item key="history" @click="showImportHistory">导入记录</a-menu-item>
                </a-menu>
              </template>
              <a-button>
                导入
                <DownOutlined />
              </a-button>
            </a-dropdown>
            <a-dropdown>
              <template #overlay>
                <a-menu>
                  <a-menu-item key="delete" @click="handleBatchDelete">删除</a-menu-item>
                  <a-menu-item key="reset" @click="handleBatchReset">重置</a-menu-item>
                  <a-menu-item key="assignTenant" @click="handleBatchAssignTenant">分配租户</a-menu-item>
                  <a-menu-item key="reuse" @click="handleBatchReuse">重用</a-menu-item>
                  <a-menu-item key="export" @click="handleBatchExport">导出</a-menu-item>
                </a-menu>
              </template>
              <a-button>
                批量操作
                <DownOutlined />
              </a-button>
            </a-dropdown>
          </a-space>
        </a-form-item>
      </a-form>
    </div>

    <!-- 表格区域 -->
    <div class="table-area">
      <a-table
        :columns="columns"
        :data-source="tableData"
        :row-selection="rowSelection"
        :pagination="paginationConfig"
        :loading="loading"
        size="small"
        row-key="id"
        :scroll="{ x: 1200 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'iccid'">
            <span :title="record.iccid">{{ record.iccid }}</span>
          </template>
          
          <template v-else-if="column.dataIndex === 'matchingId'">
            <span :title="record.matchingId">{{ record.matchingId }}</span>
          </template>
          
          <template v-else-if="column.dataIndex === 'eid'">
            <span :title="record.eid">{{ record.eid }}</span>
          </template>
          
          <template v-else-if="column.dataIndex === 'profileStatus'">
            <a-badge
              :status="getProfileStatusColor(record.profileStatus)"
              :text="record.profileStatus"
            />
          </template>
          
          <template v-else-if="column.dataIndex === 'notificationStatus'">
            <a-tag :color="getNotificationStatusColor(record.notificationStatus)">
              {{ record.notificationStatus }}
            </a-tag>
          </template>
          
          <template v-else-if="column.dataIndex === 'updateTime'">
            <span :title="record.updateTime">{{ record.updateTime }}</span>
          </template>
          
          <template v-else-if="column.dataIndex === 'action'">
            <a-space>
              <a-button type="link" @click="showDetail(record)">详情</a-button>
              <a-dropdown>
                <template #overlay>
                  <a-menu>
                    <a-menu-item key="edit" @click="handleEdit(record)">修改</a-menu-item>
                    <a-menu-item key="copy" @click="handleCopy(record)">复制</a-menu-item>
                    <a-menu-item key="delete" @click="handleDelete(record)">删除</a-menu-item>
                    <a-menu-item key="reset" @click="handleResetSingle(record)">重置</a-menu-item>
                    <a-menu-item key="assignTenant" @click="handleAssignTenant(record)">分配租户</a-menu-item>
                    <a-menu-item key="reuse" @click="handleReuse(record)">重用</a-menu-item>
                    <a-menu-item key="downloadDer" @click="handleDownloadDer(record)">下载DER数据</a-menu-item>
                  </a-menu>
                </template>
                <a-button type="link" size="small">
                  操作
                  <DownOutlined />
                </a-button>
              </a-dropdown>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { DownOutlined } from '@ant-design/icons-vue'
import http from '@/api/http'

export default defineComponent({
  name: 'ProfileManage',
  components: {
    DownOutlined
  },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const selectedRowKeys = ref([])
    
    // 创建http实例 (这里简化处理，假设不需要pinia实例)
    const httpInstance = new http(null, router, message, Modal)
    
    const searchForm = reactive({
      queryType: 'all',
      iccid: '',
      matchingId: '',
      profileStatus: '',
      eid: '',
      notificationStatus: '',
      downloadMethod: '',
      tenant: '',
      carrier: '',
      profileClass: '',
      pprPolicy: '',
      resetRule: '',
      dsFlag: '',
      updateTime: null
    })

    const columns = [
      {
        title: 'ICCID',
        dataIndex: 'iccid',
        width: 220,
        ellipsis: true,
        fixed: 'left',
        customCell: () => ({
          title: '集成电路卡识别码，由20位16进制数组成'
        })
      },
      {
        title: 'MatchingId',
        dataIndex: 'matchingId',
        width: 220,
        ellipsis: true,
        fixed: 'left',
        customCell: () => ({
          title: '匹配标识符，用于标识Profile的唯一性'
        })
      },
      {
        title: 'Profile状态',
        dataIndex: 'profileStatus',
        width: 120,
        fixed: 'left',
        customCell: () => ({
          title: 'Profile的当前状态，包括Available、Allocated、Linked等'
        })
      },
      {
        title: 'EID',
        dataIndex: 'eid',
        width: 340,
        ellipsis: true,
        customCell: () => ({
          title: '设备标识符，由32位16进制数组成'
        })
      },
      {
        title: '通知状态',
        dataIndex: 'notificationStatus',
        width: 100,
        customCell: () => ({
          title: '通知功能的启用状态，包括Enabled、Disabled、Deleted'
        })
      },
      {
        title: '下载方式',
        dataIndex: 'downloadMethod',
        width: 120,
        customCell: () => ({
          title: 'Profile的下载方式，如默认SM-DP+、激活码等'
        }),
        customRender: ({ text }) => {
          const downloadMethodMap = {
            'default': '默认SM-DP+',
            'activation': '激活码',
            'alt-smds': 'ALT-SM-DS',
            'root-smds': 'ROOT-SM-DS'
          }
          return downloadMethodMap[text] || text
        }
      },
      {
        title: '所属租户',
        dataIndex: 'tenant',
        width: 120,
        ellipsis: true,
        customCell: () => ({
          title: 'Profile所属的租户信息'
        })
      },
      {
        title: '运营商',
        dataIndex: 'carrier',
        width: 120,
        ellipsis: true,
        customCell: () => ({
          title: '提供服务的运营商名称'
        })
      },
      {
        title: 'Profile类',
        dataIndex: 'profileClass',
        width: 100,
        customCell: () => ({
          title: 'Profile的类别，包括test、provisioning、operational'
        })
      },
      {
        title: 'PPR策略',
        dataIndex: 'pprPolicy',
        width: 100,
        customCell: () => ({
          title: '策略规则，包括PPR1、PPR2、PPR1和PPR2'
        })
      },
      {
        title: '重置规则',
        dataIndex: 'resetRule',
        width: 120,
        customCell: () => ({
          title: 'Profile的重置规则，包括不可重置、可重置、自动重置、自动回收'
        }),
        customRender: ({ text }) => {
          const resetRuleMap = {
            'no_reset': '不可重置',
            'resetable': '可重置',
            'auto_reset': '自动重置',
            'auto_recycle': '自动回收'
          }
          return resetRuleMap[text] || text
        }
      },
      {
        title: 'DS标记',
        dataIndex: 'dsFlag',
        width: 100,
        customCell: () => ({
          title: '数据存储标记'
        })
      },
      {
        title: '更新时间',
        dataIndex: 'updateTime',
        width: 160,
        customCell: () => ({
          title: '数据的最后更新时间'
        })
      },
      {
        title: '操作',
        dataIndex: 'action',
        width: 140,
        fixed: 'right',
        customCell: () => ({
          title: '对数据进行查看、修改、删除等操作'
        })
      }
    ]

    const tableData = ref([])

    const paginationConfig = reactive({
      current: 1,
      pageSize: 10,
      total: 0,
      showTotal: (total) => `共 ${total} 条数据`,
      showSizeChanger: true,
      showQuickJumper: true,
      onChange: (page, pageSize) => {
        paginationConfig.current = page
        paginationConfig.pageSize = pageSize
        handleSearch()
      },
      onShowSizeChange: (current, size) => {
        paginationConfig.current = 1
        paginationConfig.pageSize = size
        handleSearch()
      }
    })

    const rowSelection = reactive({
      selectedRowKeys,
      onChange: (selectedKeys) => {
        selectedRowKeys.value = selectedKeys
      }
    })

    const getProfileStatusColor = (status) => {
      const colorMap = {
        'Available': 'success',
        'Allocated': 'processing',
        'Linked': 'processing',
        'Confirmed': 'success',
        'Released': 'warning',
        'Downloaded': 'success',
        'Installed': 'success',
        'Error': 'error',
        'Unavailable': 'default'
      }
      return colorMap[status] || 'default'
    }

    const getNotificationStatusColor = (status) => {
      const colorMap = {
        'Enabled': 'green',
        'Disabled': 'orange',
        'Deleted': 'red'
      }
      return colorMap[status] || 'default'
    }

    const formatDateTime = (dateTime) => {
      if (!dateTime) return ''
      // 如果已经是格式化好的字符串，直接返回
      if (typeof dateTime === 'string' && dateTime.includes('年')) {
        return dateTime
      }
      try {
        const date = new Date(dateTime)
        if (isNaN(date.getTime())) {
          return dateTime // 如果解析失败，返回原值
        }
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        const seconds = String(date.getSeconds()).padStart(2, '0')
        return `${year}年${month}月${day}日 ${hours}:${minutes}:${seconds}`
      } catch (error) {
        return dateTime // 出错时返回原值
      }
    }

    const handleSearch = async () => {
      loading.value = true
      try {
        // 构建请求参数，符合接口要求的格式
        const requestData = {
          size: paginationConfig.pageSize.toString(),
          pageNum: paginationConfig.current.toString(),
          query: {
            id: searchForm.id || undefined,
            iccid: searchForm.iccid || undefined,
            matchingId: searchForm.matchingId || undefined,
            eid: searchForm.eid || undefined,
            // 根据需要添加其他查询参数
            profileStatus: searchForm.profileStatus || undefined,
            notificationStatus: searchForm.notificationStatus || undefined,
            downloadMethod: searchForm.downloadMethod || undefined,
            tenant: searchForm.tenant || undefined,
            carrier: searchForm.carrier || undefined,
            profileClass: searchForm.profileClass || undefined,
            pprPolicy: searchForm.pprPolicy || undefined,
            resetRule: searchForm.resetRule || undefined,
            dsFlag: searchForm.dsFlag || undefined,
            updateTime: searchForm.updateTime || undefined
          }
        }
        
        // 过滤掉undefined的值
        Object.keys(requestData.query).forEach(key => {
          if (requestData.query[key] === undefined) {
            delete requestData.query[key]
          }
        })
        
        // 调用指定的接口
        const response = await httpInstance.request({
          url: '/personkit/api/esim/profile/page',
          method: 'post',
          data: requestData
        })
        
        // 更新表格数据
        if (response && response.data) {
          tableData.value = response.data
          paginationConfig.total = response.total || 0
          // 如果接口返回了pages信息，也可以使用它
          if (response.pages !== undefined) {
            // 可以根据需要使用pages信息
            console.log('总页数:', response.pages)
          }
        } else {
          tableData.value = []
          paginationConfig.total = 0
        }
        
        message.success('搜索完成')
      } catch (error) {
        console.error('搜索失败:', error)
        message.error('搜索失败，请重试')
        tableData.value = []
        paginationConfig.total = 0
      } finally {
        loading.value = false
      }
    }

    const handleReset = () => {
      Object.assign(searchForm, {
        queryType: 'all',
        iccid: '',
        matchingId: '',
        profileStatus: '',
        eid: '',
        notificationStatus: '',
        downloadMethod: '',
        tenant: '',
        carrier: '',
        profileClass: '',
        pprPolicy: '',
        resetRule: '',
        dsFlag: '',
        updateTime: null
      })
    }

    const handleAdd = () => {
      router.push({
        name: 'profile_form',
        query: { type: 'add' }
      })
    }

    const handleImport = () => {
      message.info('导入功能开发中...')
    }

    const showImportHistory = () => {
      message.info('导入记录功能开发中...')
    }

    const handleBatchDelete = () => {
      if (selectedRowKeys.value.length === 0) {
        message.warning('请选择要删除的数据')
        return
      }
      Modal.confirm({
        title: '确认批量删除',
        content: `确定要删除选中的 ${selectedRowKeys.value.length} 条数据吗？`,
        onOk() {
          message.success('批量删除成功！')
          selectedRowKeys.value = []
        },
        onCancel() {
          console.log('取消批量删除')
        }
      })
    }

    const handleBatchReset = () => {
      if (selectedRowKeys.value.length === 0) {
        message.warning('请选择要重置的数据')
        return
      }
      Modal.confirm({
        title: '确认批量重置',
        content: `确定要重置选中的 ${selectedRowKeys.value.length} 条数据吗？`,
        onOk() {
          message.success('批量重置成功！')
          selectedRowKeys.value = []
        },
        onCancel() {
          console.log('取消批量重置')
        }
      })
    }

    const handleBatchAssignTenant = () => {
      if (selectedRowKeys.value.length === 0) {
        message.warning('请选择要分配租户的数据')
        return
      }
      message.info('批量分配租户功能开发中...')
    }

    const handleBatchReuse = () => {
      if (selectedRowKeys.value.length === 0) {
        message.warning('请选择要重用的数据')
        return
      }
      message.info('批量重用功能开发中...')
    }

    const handleBatchExport = () => {
      if (selectedRowKeys.value.length === 0) {
        message.warning('请选择要导出的数据')
        return
      }
      message.info('批量导出功能开发中...')
    }

    const showDetail = (record) => {
      router.push({
        name: 'profile_form',
        query: { type: 'edit', id: record.id }
      })
    }

    const handleEdit = (record) => {
      router.push({
        name: 'profile_form',
        query: { type: 'edit', id: record.id }
      })
    }

    const handleCopy = (record) => {
      message.info(`复制: ${record.iccid}`)
    }

    const handleDelete = (record) => {
      Modal.confirm({
        title: '确认删除',
        content: `确定要删除ICCID为 ${record.iccid} 的Profile吗？`,
        onOk() {
          message.success('删除成功！')
        },
        onCancel() {
          console.log('取消删除')
        }
      })
    }

    const handleResetSingle = (record) => {
      Modal.confirm({
        title: '确认重置',
        content: `确定要重置ICCID为 ${record.iccid} 的Profile吗？`,
        onOk() {
          message.success('重置成功！')
        },
        onCancel() {
          console.log('取消重置')
        }
      })
    }

    const handleAssignTenant = (record) => {
      message.info(`分配租户: ${record.iccid}`)
    }

    const handleReuse = (record) => {
      Modal.confirm({
        title: '确认重用',
        content: `确定要重用ICCID为 ${record.iccid} 的Profile吗？`,
        onOk() {
          message.success('重用成功！')
        },
        onCancel() {
          console.log('取消重用')
        }
      })
    }

    const handleDownloadDer = (record) => {
      message.info(`下载DER数据: ${record.iccid}`)
    }

    // 页面加载时自动调用搜索接口
    onMounted(() => {
      handleSearch()
    })

    return {
        loading,
        searchForm,
        columns,
        tableData,
        paginationConfig,
        rowSelection,
        selectedRowKeys,
        getProfileStatusColor,
        getNotificationStatusColor,
        formatDateTime,
        handleSearch,
        handleReset,
        handleAdd,
        handleImport,
        showImportHistory,
        handleBatchDelete,
        handleBatchReset,
        handleBatchAssignTenant,
        handleBatchReuse,
        handleBatchExport,
        showDetail,
        handleEdit,
        handleCopy,
        handleDelete,
        handleResetSingle,
        handleAssignTenant,
        handleReuse,
        handleDownloadDer,
        router
      }
  }
})
</script>

<style scoped>
.profile-manage-container {
  padding: 12px;
  background: #fff;
}

.search-operation-area {
  margin-bottom: 12px;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 6px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.search-form :deep(.ant-form-item) {
  margin-bottom: 8px;
  margin-right: 8px;
}

.search-form :deep(.ant-form-item-label) {
  padding-bottom: 0;
  min-height: 32px;
  line-height: 32px;
}

.search-form :deep(.ant-form-item-control-input) {
  min-height: 32px;
}

/* 操作按钮样式 - 放在最后，自动换行到最右侧 */
.operation-buttons {
  margin-left: auto !important;
  order: 999;
}

.operation-buttons :deep(.ant-form-item-control-input-content) {
  display: flex;
  justify-content: flex-end;
}

.table-area {
  background: #fff;
}

:deep(.ant-table-cell) {
  max-width: 200px;
}

:deep(.ant-table-cell-ellipsis) {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.ant-badge-status-dot) {
  width: 6px;
  height: 6px;
}

:deep(.ant-tag) {
  margin: 0;
}

/* 响应式布局优化 */
@media (max-width: 1200px) {
  .search-form {
    gap: 6px;
  }
  
  .search-form :deep(.ant-form-item) {
    margin-bottom: 6px;
    margin-right: 6px;
  }
}

/* 表格区域样式优化 */
.table-area {
  background: #fff;
  overflow-x: auto;
}

/* 表格列宽优化 */
:deep(.ant-table-cell) {
  max-width: 200px;
  padding: 12px 8px !important;
}

:deep(.ant-table-thead > tr > th) {
  padding: 12px 8px !important;
  background: #fafafa;
  font-weight: 600;
}

/* 固定列样式 */
:deep(.ant-table-cell-fix-left),
:deep(.ant-table-cell-fix-right) {
  background: #fff;
  z-index: 2;
}

:deep(.ant-table-cell-fix-left-last),
:deep(.ant-table-cell-fix-right-first) {
  box-shadow: none;
}
</style>