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
        <a-form-item label="运营商名称" name="monName">
          <a-input v-model:value="formState.monName" placeholder="运营商名称，模糊查询"> </a-input>
        </a-form-item>
        <a-form-item label="简称" name="monShortName">
          <a-input v-model:value="formState.monShortName" placeholder="简称，模糊查询"> </a-input>
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-select v-model:value="formState.status" placeholder="请选择状态" allow-clear style="width: 120px">
            <a-select-option value="1">启用</a-select-option>
            <a-select-option value="0">禁用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit"> 搜索 </a-button>
        </a-form-item>
        <a-form-item>
          <a-button @click="handleReset"> 重置 </a-button>
        </a-form-item>
      </a-form>

      <a-space>
        <a-button type="primary" @click="handleAdd">新增运营商</a-button>
      </a-space>
    </div>

    <!-- 上部搜索条件区域 -->
    <a-divider dashed />
    <!-- 中间内容区域 -->
    <!-- 表格区 -->
    <a-table :columns="columns" :data-source="tableData" size="small" :pagination="false">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'status'">
          <a-tag :color="record.status === 1 ? 'green' : 'red'">
            {{ record.status === 1 ? '启用' : '禁用' }}
          </a-tag>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <a-space>
            <a-button type="link" @click="handleEdit(record)">编辑</a-button>
            <a-button 
              type="link" 
              danger 
              @click="handleDelete(record)"
            >
              删除
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

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
      width="600px"
    >
      <a-form
        ref="modalFormRef"
        :model="modalFormData"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
        :rules="formRules"
      >
        <a-form-item label="运营商OID" name="monOid">
          <a-input
            v-model:value="modalFormData.monOid"
            placeholder="请输入运营商OID，格式如：v1.v2.v3...vn"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="运营商名称" name="monName">
          <a-input
            v-model:value="modalFormData.monName"
            placeholder="请输入运营商名称"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="简称" name="monShortName">
          <a-input
            v-model:value="modalFormData.monShortName"
            placeholder="请输入简称"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="modalFormData.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { reactive, defineComponent, ref } from 'vue'
import { message } from 'ant-design-vue'

export default defineComponent({
  name: 'MonManage',
  data() {
    return {
      formState: reactive({
        monName: '',
        monShortName: '',
        status: undefined
      }),
      data: reactive([]),
      modalVisible: false,
      modalTitle: '',
      modalFormData: reactive({
        id: undefined,
        monOid: '',
        monName: '',
        monShortName: '',
        status: 1
      }),
      modalFormRef: ref(),
      pagination: {
        current: 1,
        total: 0,
        pageSize: 10
      }
    }
  },
  computed: {
    tableData: function () {
      return this.data
    },
    columns() {
      return [
        {
          title: '运营商OID',
          dataIndex: 'monOid',
          key: 'monOid',
          width: '25%'
        },
        {
          title: '运营商名称',
          dataIndex: 'monName',
          key: 'monName',
          width: '25%'
        },
        {
          title: '简称',
          dataIndex: 'monShortName',
          key: 'monShortName',
          width: '20%'
        },
        {
          title: '状态',
          dataIndex: 'status',
          key: 'status',
          width: '10%'
        },
        {
          title: '操作',
          dataIndex: 'action',
          key: 'action',
          width: '20%'
        }
      ]
    },
    formRules() {
      return {
        monOid: [
          { required: true, message: '请输入运营商OID' },
          { 
            validator: this.validateMonOid,
            message: 'OID格式不正确，v1取值范围为0~2，当v1为0或1时v2取值范围为0-39，v2~vn取值必须小于0x80000000'
          }
        ],
        monName: [
          { required: true, message: '请输入运营商名称' }
        ],
        monShortName: [
          { required: true, message: '请输入简称' }
        ],
        status: [
          { required: true, message: '请选择状态' }
        ]
      }
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
          url: '/personkit/api/esim/mon/page',
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
        .catch((err) => {
          console.error('查询数据失败:', err)
          message.error('查询数据失败: ' + (err.message || '未知错误'))
        })
    },

    /**
     * 重置搜索条件
     */
    handleReset() {
      this.formState = reactive({
        monName: '',
        monShortName: '',
        status: undefined
      })
      this.pagination.current = 1
      this.query()
    },

    /**
     * 新增运营商
     */
    handleAdd() {
      this.modalTitle = '新增运营商'
      this.modalFormData = reactive({
        id: undefined,
        monOid: '',
        monName: '',
        monShortName: '',
        status: 1
      })
      this.modalVisible = true
    },

    /**
     * 编辑运营商
     */
    handleEdit(record) {
      this.modalTitle = '编辑运营商'
      this.modalFormData = reactive({
        id: record.id,
        monOid: record.monOid,
        monName: record.monName,
        monShortName: record.monShortName,
        status: record.status
      })
      this.modalVisible = true
    },

    /**
     * 删除运营商
     */
    handleDelete(record) {
      this.$confirm({
        title: '确认删除',
        content: `确定要删除运营商 "${record.monName}" 吗？`,
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$http
            .request({
              url: '/personkit/api/esim/mon/delete',
              method: 'post',
              data: {
                id: record.id
              }
            })
            .then((res) => {
              message.success('删除成功')
              this.query()
            })
            .catch((err) => {
              console.error('删除失败:', err)
              // message.error('删除失败: ' + (err.message || '未知错误'))
            })
        }
      })
    },

    /**
     * 切换状态
     */
    handleToggleStatus(record) {
      const newStatus = record.status === 1 ? 0 : 1
      const statusText = newStatus === 1 ? '启用' : '禁用'
      
      this.$confirm({
        title: `确认${statusText}`,
        content: `确定要${statusText}运营商 "${record.monName}" 吗？`,
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          this.$http
            .request({
              url: '/personkit/mon/updateStatus',
              method: 'post',
              data: {
                id: record.id,
                status: newStatus
              }
            })
            .then((res) => {
              message.success(`${statusText}成功`)
              this.query()
            })
            .catch((err) => {
              console.error('状态更新失败:', err)
              message.error('状态更新失败: ' + (err.message || '未知错误'))
            })
        }
      })
    },

    /**
     * 模态框确认
     */
    handleModalOk() {
      this.$refs.modalFormRef.validate().then(() => {
        const url = this.modalFormData.id ? '/personkit/api/esim/mon/update' : '/personkit/api/esim/mon/save'
        
        this.$http
          .request({
            url: url,
            method: 'post',
            data: this.modalFormData
          })
          .then((res) => {
            message.success(this.modalFormData.id ? '编辑成功' : '新增成功')
            this.modalVisible = false
            this.query()
          })
          .catch((err) => {
            console.error('操作失败:', err)
            // message.error('操作失败: ' + (err.message || '未知错误'))
          })
      }).catch(() => {
        // 表单验证失败
      })
    },

    /**
     * 模态框取消
     */
    handleModalCancel() {
      this.modalVisible = false
      this.$refs.modalFormRef.resetFields()
    },

    /**
     * 验证运营商OID格式
     */
    validateMonOid(rule, value) {
      if (!value) {
        return Promise.resolve()
      }
      
      // 验证OID格式：v1.v2.v3...vn
      const oidPattern = /^(\d+)\.(\d+)\.(\d+)(\.(\d+))*$/
      if (!oidPattern.test(value)) {
        return Promise.reject('OID格式不正确')
      }
      
      const parts = value.split('.').map(Number)
      const v1 = parts[0]
      const v2 = parts[1]
      
      // v1取值范围为0~2
      if (v1 < 0 || v1 > 2) {
        return Promise.reject('v1取值范围为0~2')
      }
      
      // 当v1取值为0或1时v2取值范围为0-39
      if ((v1 === 0 || v1 === 1) && (v2 < 0 || v2 > 39)) {
        return Promise.reject('当v1为0或1时，v2取值范围为0-39')
      }
      
      // v2~vn取值都必须小于0x80000000
      for (let i = 1; i < parts.length; i++) {
        if (parts[i] >= 0x80000000) {
          return Promise.reject('v2~vn取值都必须小于0x80000000')
        }
      }
      
      return Promise.resolve()
    },

    //导航栏发生变化
    onPaginationChange(page, pageSize) {
      this.pagination.current = page
      this.pagination.pageSize = pageSize
      this.query()
    }
  },
  created() {
    this.query()
  }
})
</script>

<style scoped>
/* 可以添加自定义样式 */
</style>