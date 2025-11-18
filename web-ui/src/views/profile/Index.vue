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
import { defineComponent, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { DownOutlined } from '@ant-design/icons-vue'

export default defineComponent({
  name: 'ProfileManage',
  components: {
    DownOutlined
  },
  setup() {
    const loading = ref(false)
    const selectedRowKeys = ref([])
    
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
        width: 200,
        ellipsis: true,
        customCell: () => ({
          title: '集成电路卡识别码，由20位16进制数组成'
        })
      },
      {
        title: 'MatchingId',
        dataIndex: 'matchingId',
        width: 180,
        ellipsis: true,
        customCell: () => ({
          title: '匹配标识符，用于标识Profile的唯一性'
        })
      },
      {
        title: 'Profile状态',
        dataIndex: 'profileStatus',
        width: 120,
        customCell: () => ({
          title: 'Profile的当前状态，包括Available、Allocated、Linked等'
        })
      },
      {
        title: 'EID',
        dataIndex: 'eid',
        width: 280,
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
        })
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
        })
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
          title: 'Profile信息的最后更新时间'
        })
      },
      {
        title: '操作',
        dataIndex: 'action',
        width: 140,
        fixed: 'right'
      }
    ]

    const tableData = ref([
      {
        id: '1',
        iccid: '89000000000000000000',
        matchingId: 'WTBIJ-EG8C0-C72NY-VNOPX',
        profileStatus: 'Available',
        eid: '1234567890ABCDEF1234567890ABCDEF',
        notificationStatus: 'Enabled',
        downloadMethod: '默认SM-DP+',
        tenant: '租户A',
        carrier: '中国移动',
        profileClass: 'operational',
        pprPolicy: 'PPR1',
        resetRule: '可重置',
        dsFlag: '标记1',
        updateTime: '2024-01-15 10:30:00'
      },
      {
        id: '2',
        iccid: '89000000000000000001',
        matchingId: 'ABCDE-FGHIJ-KLMNO-PQRST',
        profileStatus: 'Installed',
        eid: 'ABCDEF1234567890ABCDEF1234567890',
        notificationStatus: 'Disabled',
        downloadMethod: '激活码',
        tenant: '租户B',
        carrier: '中国联通',
        profileClass: 'test',
        pprPolicy: 'PPR2',
        resetRule: '不可重置',
        dsFlag: '标记2',
        updateTime: '2024-01-14 15:45:00'
      }
    ])

    const paginationConfig = reactive({
      current: 1,
      pageSize: 10,
      total: 0,
      showTotal: (total) => `共 ${total} 条数据`,
      showSizeChanger: true,
      showQuickJumper: true
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

    const handleSearch = () => {
      loading.value = true
      // 模拟搜索操作
      setTimeout(() => {
        loading.value = false
        message.success('搜索完成')
      }, 1000)
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
      message.info('新增功能开发中...')
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
      message.info('批量删除功能开发中...')
    }

    const handleBatchReset = () => {
      if (selectedRowKeys.value.length === 0) {
        message.warning('请选择要重置的数据')
        return
      }
      message.info('批量重置功能开发中...')
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
      message.info(`查看详情: ${record.iccid}`)
    }

    const handleEdit = (record) => {
      message.info(`编辑: ${record.iccid}`)
    }

    const handleCopy = (record) => {
      message.info(`复制: ${record.iccid}`)
    }

    const handleDelete = (record) => {
      message.info(`删除: ${record.iccid}`)
    }

    const handleResetSingle = (record) => {
      message.info(`重置: ${record.iccid}`)
    }

    const handleAssignTenant = (record) => {
      message.info(`分配租户: ${record.iccid}`)
    }

    const handleReuse = (record) => {
      message.info(`重用: ${record.iccid}`)
    }

    const handleDownloadDer = (record) => {
      message.info(`下载DER数据: ${record.iccid}`)
    }

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
      handleDownloadDer
    }
  }
})
</script>

<style scoped>
.profile-manage-container {
  padding: 16px;
  background: #fff;
}

.search-operation-area {
  margin-bottom: 16px;
  padding: 16px;
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
</style>