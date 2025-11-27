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
        <a-form-item label="模板名称" name="tempName">
          <a-input v-model:value="formState.tempName" placeholder="模板名称，模糊查询"> </a-input>
        </a-form-item>
        <a-form-item label="Profile Type" name="profileType">
          <a-input v-model:value="formState.profileType" placeholder="Profile Type，模糊查询">
          </a-input>
        </a-form-item>
        <a-form-item label="所属运营商" name="monId">
          <a-select
            v-model:value="formState.monId"
            placeholder="请选择运营商"
            allow-clear
            style="width: 150px"
          >
            <a-select-option v-for="mon in monList" :key="mon.id" :value="mon.id">
              {{ mon.monName }}
            </a-select-option>
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
        <a-button type="primary" @click="handleAdd">新增模板</a-button>
      </a-space>
    </div>

    <!-- 上部搜索条件区域 -->
    <a-divider dashed />
    <!-- 中间内容区域 -->
    <!-- 表格区 -->
    <a-table :columns="columns" :data-source="tableData" size="small" :pagination="false">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'monName'">
          {{ record.monName }}
        </template>
        <template v-if="column.dataIndex === 'action'">
          <a-space>
            <a-button type="link" @click="handleView(record)">详情</a-button>
            <a-button type="link" @click="handleEdit(record)">编辑</a-button>
            <a-button type="link" danger @click="handleDelete(record)"> 删除 </a-button>
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
      width="800px"
    >
      <a-form
        ref="modalFormRef"
        :model="modalFormData"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 18 }"
        :rules="formRules"
      >
        <a-form-item label="模板名称" name="tempName">
          <a-input
            v-model:value="modalFormData.tempName"
            placeholder="请输入模板名称"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="Profile Type" name="profileType">
          <a-input
            v-model:value="modalFormData.profileType"
            placeholder="请输入Profile Type"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="所属运营商" name="monId">
          <a-select
            v-model:value="modalFormData.monId"
            placeholder="请选择运营商"
            style="width: 100%"
          >
            <a-select-option v-for="mon in monList" :key="mon.id" :value="mon.id">
              {{ mon.monName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="模板内容" name="tempContent">
          <a-textarea
            v-model:value="modalFormData.tempContent"
            placeholder="请输入模板内容"
            :rows="15"
            allow-clear
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 详情弹窗 -->
    <a-modal
      v-model:open="detailVisible"
      title="模板详情"
      @cancel="handleDetailCancel"
      width="900px"
      :height="600"
      :body-style="{ overflow: 'hidden', maxHeight: '500px' }"
      destroy-on-close
    >
      <a-descriptions :column="1" bordered size="small">
        <a-descriptions-item label="模板名称">{{ detailData.tempName }}</a-descriptions-item>
        <a-descriptions-item label="Profile Type">{{ detailData.profileType }}</a-descriptions-item>
        <a-descriptions-item label="所属运营商">{{ detailData.monName }}</a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ detailData.createTime }}</a-descriptions-item>
        <a-descriptions-item label="更新时间">{{ detailData.updateTime }}</a-descriptions-item>
        <a-descriptions-item label="模板内容" :span="1">
          <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
            <span>内容展示</span>
            <a-button size="small" @click="handleCopyContent">复制内容</a-button>
          </div>
          <pre
            style="
              white-space: pre-wrap;
              word-break: break-all;
              max-height: 350px;
              overflow-y: auto;
              background-color: #f6f8fa;
              padding: 12px;
              border-radius: 4px;
              font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
              font-size: 12px;
              line-height: 1.5;
            "
          >
            {{ detailData.tempContent }}
          </pre>
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script>
import { message, Modal } from 'ant-design-vue'

export default {
  name: 'TempdateManage',
  data() {
    return {
      formState: {
        tempName: '',
        profileType: '',
        monId: undefined
      },
      data: [],
      monList: [],
      modalVisible: false,
      modalTitle: '',
      modalFormData: {
        id: undefined,
        tempName: '',
        profileType: '',
        monId: undefined,
        tempContent: ''
      },
      detailVisible: false,
      detailData: {
        tempName: '',
        monName: '',
        profileType: '',
        monId: undefined,
        tempContent: '',
        createTime: '',
        updateTime: ''
      },
      pagination: {
        current: 1,
        total: 0,
        pageSize: 10
      }
    }
  },
  computed: {
    tableData() {
      return this.data
    },
    columns() {
      return [
        {
          title: '模板名称',
          dataIndex: 'tempName',
          key: 'tempName',
          width: '30%'
        },
        {
          title: 'Profile Type',
          dataIndex: 'profileType',
          key: 'profileType',
          width: '25%'
        },
        {
          title: '所属运营商',
          dataIndex: 'monName',
          key: 'monName',
          width: '25%'
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
        tempName: [
          { required: true, message: '请输入模板名称', trigger: 'blur' },
          { min: 2, max: 100, message: '模板名称长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        profileType: [
          { required: true, message: '请输入Profile Type', trigger: 'blur' },
          { max: 50, message: 'Profile Type长度不能超过50个字符', trigger: 'blur' }
        ],
        monId: [{ required: true, message: '请选择所属运营商', trigger: 'change' }],
        tempContent: [{ required: true, message: '请输入模板内容', trigger: 'blur' }]
      }
    }
  },
  methods: {
    handleFinish(values) {
      this.pagination.current = 1
      this.query()
    },
    handleReset() {
      this.formState = {
        tempName: '',
        profileType: '',
        monId: undefined
      }
      this.pagination.current = 1
      this.query()
    },
    onPaginationChange(page, pageSize) {
      this.pagination.current = page
      this.pagination.pageSize = pageSize
      this.query()
    },
    query() {
      const params = {
        pageNum: this.pagination.current,
        pageSize: this.pagination.pageSize,
        query: this.formState
      }
      this.$http
        .request({
          url: '/personkit/api/esim/tempdate/page',
          method: 'post',
          data: params
        })
        .then((res) => {
          console.log('模板列表接口返回:', res)
          this.data = res.data || []
          console.log('模板数据:', this.data)
          this.pagination.total = res.total || 0
        })
    },
    loadMonList() {
      return this.$http
        .request({
          url: '/personkit/api/esim/mon/list',
          method: 'post',
          data: { pageNum: 1, pageSize: 1000 }
        })
        .then((res) => {
          console.log('运营商接口返回:', res)
          this.monList = res || []
          console.log('设置monList:', this.monList)
          return this.monList
        })
    },
    getMonName(monId) {
      console.log('getMonName 调用:', monId, '类型:', typeof monId)
      console.log('monList:', this.monList)
      const mon = this.monList.find((item) => item.id === monId)
      console.log('匹配结果:', mon)
      return mon ? mon.monName : ''
    },
    handleAdd() {
      this.modalTitle = '新增模板'
      this.modalFormData = {
        id: undefined,
        tempName: '',
        profileType: '',
        monId: undefined,
        tempContent: ''
      }
      this.modalVisible = true
    },
    handleEdit(record) {
      this.modalTitle = '编辑模板'
      console.log('编辑记录:', record)
      console.log('运营商ID:', record.monId, '类型:', typeof record.monId)
      console.log('当前运营商列表:', this.monList)

      // 检查运营商列表中的ID类型
      if (this.monList.length > 0) {
        console.log('运营商列表ID示例:', this.monList[0].id, '类型:', typeof this.monList[0].id)
      }

      // 确保运营商列表已加载
      if (this.monList.length === 0) {
        this.loadMonList().then(() => {
          console.log('运营商列表加载完成:', this.monList)
          console.log('设置monId:', record.monId)
          // 确保类型匹配，转换为数字类型
          const monIdValue = Number(record.monId)
          this.modalFormData = {
            id: record.id,
            tempName: record.tempName,
            profileType: record.profileType,
            monId: monIdValue,
            tempContent: record.tempContent
          }
          console.log('modalFormData:', this.modalFormData)
          this.modalVisible = true
        })
      } else {
        // 确保类型匹配，转换为数字类型
        const monIdValue = Number(record.monId)
        this.modalFormData = {
          id: record.id,
          tempName: record.tempName,
          profileType: record.profileType,
          monId: monIdValue,
          tempContent: record.tempContent
        }
        this.modalVisible = true
      }
    },
    handleView(record) {
      this.detailData = {
        tempName: record.tempName,
        profileType: record.profileType,
        monId: record.monId,
        monName: record.monName,
        tempContent: record.tempContent,
        createTime: record.createTime,
        updateTime: record.updateTime
      }
      this.detailVisible = true
    },
    handleDelete(record) {
      Modal.confirm({
        title: '确认删除',
        content: `确定要删除模板"${record.tempName}"吗？`,
        onOk: () => {
          this.$http
            .request({
              url: '/personkit/api/esim/tempdate/delete',
              method: 'post',
              data: { id: record.id }
            })
            .then((res) => {
              message.success('删除成功')
              this.query()
            })
        }
      })
    },
    handleModalOk() {
      this.$refs.modalFormRef
        .validate()
        .then(() => {
          const url = this.modalFormData.id
            ? '/personkit/api/esim/tempdate/update'
            : '/personkit/api/esim/tempdate/save'
          this.$http
            .request({
              url: url,
              method: 'post',
              data: this.modalFormData
            })
            .then((res) => {
              message.success(this.modalFormData.id ? '更新成功' : '新增成功')
              this.modalVisible = false
              this.query()
            })
        })
        .catch((error) => {
          console.log('表单验证失败:', error)
        })
    },
    handleModalCancel() {
      this.modalVisible = false
      this.$refs.modalFormRef.resetFields()
    },
    handleDetailCancel() {
      this.detailVisible = false
    },
    handleCopyContent() {
      if (this.detailData.tempContent) {
        // 创建一个临时的textarea元素
        const textarea = document.createElement('textarea')
        textarea.value = this.detailData.tempContent
        // 设置样式使其不可见
        textarea.style.position = 'fixed'
        textarea.style.left = '-999999px'
        textarea.style.top = '-999999px'
        document.body.appendChild(textarea)
        // 选中并复制
        textarea.focus()
        textarea.select()
        
        try {
          const successful = document.execCommand('copy')
          if (successful) {
            message.success('内容已复制到剪贴板')
          } else {
            message.error('复制失败，请手动复制')
          }
        } catch (err) {
          message.error('复制失败，请手动复制')
          console.error('复制内容出错:', err)
        }
        
        // 清理
        document.body.removeChild(textarea)
      } else {
        message.warning('没有可复制的内容')
      }
    }
  },
  watch: {
    'modalFormData.monId'(newVal, oldVal) {
      console.log('monId 变化:', oldVal, '->', newVal)
    },
    monList(newVal) {
      console.log('monList 变化:', newVal)
    }
  },
  created() {
    this.loadMonList()
    this.query()
  }
}
</script>
