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
        <a-form-item label="证书名称" name="certName">
          <a-input v-model:value="formState.certName" placeholder="证书名称，模糊查询"> </a-input>
        </a-form-item>
        <a-form-item label="CI证书" name="ciCert">
          <a-input v-model:value="formState.ciCert" placeholder="CI证书，模糊查询"> </a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit"> 搜索 </a-button>
        </a-form-item>
        <a-form-item>
          <a-button @click="handleReset"> 重置 </a-button>
        </a-form-item>
      </a-form>

      <a-space>
        <a-button type="primary" @click="handleAdd">新增证书</a-button>
      </a-space>
    </div>

    <!-- 上部搜索条件区域 -->
    <a-divider dashed />
    <!-- 中间内容区域 -->
    <!-- 卡片网格 -->
    <div class="content-area-compact">
      <a-row :gutter="[16, 16]" class="cert-grid-compact">
        <a-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-for="cert in certData" :key="cert.id">
          <a-card hoverable class="cert-card-compact">
            <template #cover>
              <div class="card-cover-compact">
                <div class="cert-icon-container">
                  <a-avatar
                    :size="48"
                    class="cert-avatar"
                    :style="{ backgroundColor: getCertTypeColor(cert.certType) }"
                  >
                    <SafetyOutlined />
                  </a-avatar>
                </div>
                <!-- 状态标签 -->
                <div
                  class="status-badge"
                  :style="{ backgroundColor: getCertStatusColor(cert.isLabel) }"
                >
                  {{ cert.isLabel === 1 ? '标签' : '私钥' }}
                </div>
                <!-- 操作按钮覆盖层 -->
                <div class="card-actions-overlay">
                  <a-dropdown :trigger="['click']" placement="bottomRight">
                    <template #overlay>
                      <a-menu @click="({ key }) => handleCertAction(key, cert)">
                        <a-menu-item key="view">
                          <EyeOutlined />
                          查看详情
                        </a-menu-item>
                        <a-menu-item key="edit">
                          <EditOutlined />
                          编辑
                        </a-menu-item>
                        <a-menu-divider />
                        <a-menu-item key="delete">
                          <DeleteOutlined />
                          删除
                        </a-menu-item>
                      </a-menu>
                    </template>
                    <a-button type="primary" size="small" class="action-menu-btn">
                      <SettingOutlined />
                      操作
                    </a-button>
                  </a-dropdown>
                </div>
              </div>
            </template>

            <a-card-meta>
              <template #title>
                <div class="card-title-compact" :title="cert.certName">{{ cert.certName }}</div>
              </template>
              <template #description>
                <div class="card-description-compact">
                  <div class="cert-info">
                    <div class="info-item">
                      <span class="label">CI证书:</span>
                      <span class="value" :title="cert.ciCert">{{
                        formatCertContent(cert.ciCert)
                      }}</span>
                    </div>
                    <div class="info-item" v-if="cert.ciSubCaCert">
                      <span class="label">CI SubCA:</span>
                      <span class="value" :title="cert.ciSubCaCert">{{
                        formatCertContent(cert.ciSubCaCert)
                      }}</span>
                    </div>
                    <div class="info-item" v-if="cert.dpSubCaCert">
                      <span class="label">DP SubCA:</span>
                      <span class="value" :title="cert.dpSubCaCert">{{
                        formatCertContent(cert.dpSubCaCert)
                      }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">认证证书:</span>
                      <span class="value" :title="cert.authCert">{{
                        formatCertContent(cert.authCert)
                      }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">数据绑定证书:</span>
                      <span class="value" :title="cert.dbCert">{{
                        formatCertContent(cert.dbCert)
                      }}</span>
                    </div>
                  </div>
                </div>
              </template>
            </a-card-meta>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 分页 -->
    <div class="pagination-container-compact">
      <a-pagination
        v-model:current="pagination.current"
        :total="pagination.total"
        :show-total="(total) => `共 ${total} 个证书`"
        @change="onPaginationChange"
        size="small"
        class="pagination-compact"
      />
    </div>

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
        <a-form-item label="证书名称" name="certName">
          <a-input
            v-model:value="modalFormData.certName"
            placeholder="请输入证书名称"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="CI证书" name="ciCert">
          <a-textarea
            v-model:value="modalFormData.ciCert"
            placeholder="请输入CI证书内容"
            :rows="2"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="CI SubCA证书" name="ciSubCaCert">
          <a-textarea
            v-model:value="modalFormData.ciSubCaCert"
            placeholder="请输入CI SubCA证书内容（选填）"
            :rows="2"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="DP SubCA证书" name="dpSubCaCert">
          <a-textarea
            v-model:value="modalFormData.dpSubCaCert"
            placeholder="请输入DP SubCA证书内容（选填）"
            :rows="2"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="认证证书" name="authCert">
          <a-textarea
            v-model:value="modalFormData.authCert"
            placeholder="请输入认证证书内容"
            :rows="2"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="认证私钥/标签" name="authKey">
          <a-textarea
            v-model:value="modalFormData.authKey"
            placeholder="请输入认证私钥或标签"
            :rows="2"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="数据绑定证书" name="dbCert">
          <a-textarea
            v-model:value="modalFormData.dbCert"
            placeholder="请输入数据绑定证书内容"
            :rows="2"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="数据绑定私钥/标签" name="dbKey">
          <a-textarea
            v-model:value="modalFormData.dbKey"
            placeholder="请输入数据绑定私钥或标签"
            :rows="2"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="是否标签" name="isLabel">
          <a-radio-group v-model:value="modalFormData.isLabel">
            <a-radio :value="1">标签</a-radio>
            <a-radio :value="0">私钥</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 详情弹窗 -->
    <a-modal
      v-model:open="detailVisible"
      title="证书详情"
      @cancel="handleDetailCancel"
      width="600px"
      :footer="null"
    >
      <a-descriptions :column="1" bordered size="small">
        <a-descriptions-item label="证书名称">{{ detailData.certName }}</a-descriptions-item>
        <a-descriptions-item label="CI证书">
          <div class="cert-content-small">{{ detailData.ciCert }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="CI SubCA证书" v-if="detailData.ciSubCaCert">
          <div class="cert-content-small">{{ detailData.ciSubCaCert }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="DP SubCA证书" v-if="detailData.dpSubCaCert">
          <div class="cert-content-small">{{ detailData.dpSubCaCert }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="认证证书">
          <div class="cert-content-small">{{ detailData.authCert }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="认证私钥/标签">
          <div class="cert-content-small">{{ detailData.authKey }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="数据绑定证书">
          <div class="cert-content-small">{{ detailData.dbCert }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="数据绑定私钥/标签">
          <div class="cert-content-small">{{ detailData.dbKey }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="是否标签">
          <a-tag :color="detailData.isLabel === 1 ? 'blue' : 'green'" size="small">
            {{ detailData.isLabel === 1 ? '标签' : '私钥' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ detailData.createTime }}</a-descriptions-item>
        <a-descriptions-item label="更新时间">{{ detailData.updateTime }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script>
import { reactive, defineComponent, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  SafetyOutlined,
  SettingOutlined,
  EditOutlined,
  DeleteOutlined,
  EyeOutlined
} from '@ant-design/icons-vue'

export default defineComponent({
  name: 'CertManage',
  components: {
    SafetyOutlined,
    SettingOutlined,
    EditOutlined,
    DeleteOutlined,
    EyeOutlined
  },
  data() {
    return {
      formState: reactive({
        certName: '',
        ciCert: ''
      }),
      certData: reactive([]),
      modalVisible: false,
      modalTitle: '',
      modalFormData: reactive({
        id: undefined,
        certName: '',
        ciCert: '',
        ciSubCaCert: '',
        dpSubCaCert: '',
        authCert: '',
        authKey: '',
        dbCert: '',
        dbKey: '',
        isLabel: 1
      }),
      modalFormRef: ref(),
      detailVisible: false,
      detailData: {
        certName: '',
        ciCert: '',
        ciSubCaCert: '',
        dpSubCaCert: '',
        authCert: '',
        authKey: '',
        dbCert: '',
        dbKey: '',
        isLabel: 1,
        createTime: '',
        updateTime: ''
      },
      pagination: {
        current: 1,
        total: 0,
        pageSize: 12
      }
    }
  },
  computed: {
    formRules() {
      return {
        certName: [
          { required: true, message: '请输入证书名称', trigger: 'blur' },
          { min: 2, max: 100, message: '证书名称长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        ciCert: [{ required: true, message: '请输入CI证书', trigger: 'blur' }],
        authCert: [{ required: true, message: '请输入认证证书', trigger: 'blur' }],
        authKey: [{ required: true, message: '请输入认证私钥或标签', trigger: 'blur' }],
        dbCert: [{ required: true, message: '请输入数据绑定证书', trigger: 'blur' }],
        dbKey: [{ required: true, message: '请输入数据绑定私钥或标签', trigger: 'blur' }],
        isLabel: [{ required: true, message: '请选择是否标签', trigger: 'change' }]
      }
    }
  },
  methods: {
    handleFinish() {
      this.pagination.current = 1
      this.query()
    },
    handleReset() {
      this.formState = {
        certName: '',
        ciCert: ''
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
          url: '/personkit/api/esim/cert/page',
          method: 'post',
          data: params
        })
        .then((res) => {
          this.certData = res.data || []
          this.pagination.total = res.total || 0
        })
    },
    getCertTypeColor(isLabel) {
      return isLabel === 1 ? '#1890ff' : '#52c41a'
    },
    getCertStatusColor(isLabel) {
      return isLabel === 1 ? '#1890ff' : '#52c41a'
    },
    formatCertContent(content) {
      if (!content) return '-'
      return content.length > 30 ? content.substring(0, 30) + '...' : content
    },
    handleAdd() {
      this.modalTitle = '新增证书'
      this.modalFormData = {
        id: undefined,
        certName: '',
        ciCert: '',
        ciSubCaCert: '',
        dpSubCaCert: '',
        authCert: '',
        authKey: '',
        dbCert: '',
        dbKey: '',
        isLabel: 1
      }
      this.modalVisible = true
    },
    handleEdit(record) {
      this.modalTitle = '编辑证书'
      this.modalFormData = {
        id: record.id,
        certName: record.certName,
        ciCert: record.ciCert,
        ciSubCaCert: record.ciSubCaCert,
        dpSubCaCert: record.dpSubCaCert,
        authCert: record.authCert,
        authKey: record.authKey,
        dbCert: record.dbCert,
        dbKey: record.dbKey,
        isLabel: record.isLabel
      }
      this.modalVisible = true
    },
    handleView(record) {
      this.detailData = {
        certName: record.certName,
        ciCert: record.ciCert,
        ciSubCaCert: record.ciSubCaCert,
        dpSubCaCert: record.dpSubCaCert,
        authCert: record.authCert,
        authKey: record.authKey,
        dbCert: record.dbCert,
        dbKey: record.dbKey,
        isLabel: record.isLabel,
        createTime: record.createTime,
        updateTime: record.updateTime
      }
      this.detailVisible = true
    },
    handleDelete(record) {
      this.$modal.confirm({
        title: '确认删除',
        content: `确定要删除证书"${record.certName}"吗？`,
        onOk: () => {
          this.$http
            .request({
              url: '/personkit/api/esim/cert/delete',
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
    handleCertAction(action, cert) {
      switch (action) {
        case 'view':
          this.handleView(cert)
          break
        case 'edit':
          this.handleEdit(cert)
          break
        case 'delete':
          this.handleDelete(cert)
          break
      }
    },
    handleModalOk() {
      this.$refs.modalFormRef
        .validate()
        .then(() => {
          const url = this.modalFormData.id
            ? '/personkit/api/esim/cert/update'
            : '/personkit/api/esim/cert/save'
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
    }
  },
  created() {
    this.query()
  }
})
</script>

<style scoped>
.content-area-compact {
  padding: 0;
  margin-bottom: 16px;
}

.cert-grid-compact {
  margin: 0 !important;
}

.cert-card-compact {
  height: 280px;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.cert-card-compact:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-cover-compact {
  position: relative;
  height: 120px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.cert-icon-container {
  display: flex;
  align-items: center;
  justify-content: center;
}

.cert-avatar {
  font-size: 24px;
}

.status-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 2px 8px;
  border-radius: 12px;
  color: white;
  font-size: 12px;
  font-weight: 500;
}

.card-actions-overlay {
  position: absolute;
  bottom: 8px;
  right: 8px;
}

.action-menu-btn {
  border-radius: 16px;
  padding: 0 12px;
  height: 28px;
  font-size: 12px;
}

.card-title-compact {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-description-compact {
  height: 120px;
  overflow: hidden;
}

.cert-info {
  font-size: 13px;
  color: #595959;
}

.info-item {
  display: flex;
  margin-bottom: 6px;
  align-items: flex-start;
}

.label {
  flex-shrink: 0;
  width: 80px;
  font-weight: 500;
  color: #434343;
}

.value {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #8c8c8c;
}

.pagination-container-compact {
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
}

.pagination-compact {
  font-size: 13px;
}

.cert-content {
  max-height: 200px;
  overflow-y: auto;
  word-break: break-all;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.5;
  background: #fafafa;
  padding: 8px;
  border-radius: 4px;
}

.cert-content-small {
  max-height: 120px;
  overflow-y: auto;
  word-break: break-all;
  font-family: 'Courier New', monospace;
  font-size: 11px;
  line-height: 1.4;
  background: #fafafa;
  padding: 6px;
  border-radius: 4px;
}
</style>
