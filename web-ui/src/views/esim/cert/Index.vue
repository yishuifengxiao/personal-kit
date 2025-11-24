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
        <a-form-item label="CI公钥ID" name="ciPkid">
          <a-input v-model:value="formState.ciPkid" placeholder="CI公钥ID，模糊查询"> </a-input>  
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
        <a-upload
          name="file"
          :show-upload-list="false"
          :before-upload="beforeUpload"
          :custom-request="customRequest"
        >
          <a-button type="primary">上传证书</a-button>
        </a-upload>
      </a-space>
    </div>

    <!-- 上部搜索条件区域 -->
    <a-divider dashed />
    <!-- 中间内容区域 -->
    <!-- 卡片网格 -->
    <div class="content-area-compact">
      <a-row :gutter="[16, 16]" class="cert-grid-compact">
        <a-col :xs="24" :sm="12" :md="9" :lg="8" :xl="6" v-for="cert in certData" :key="cert.id">
          <a-card hoverable class="cert-card-compact">
            <template #cover>
              <div class="card-cover-compact" @mouseleave="cert.showDetail = false">
                <div 
                  class="cert-icon-container" 
                  @mouseenter="cert.showDetail = true"
                >
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
                      <span class="label">CI公钥ID:</span>
                      <span class="value truncated-text" :title="cert.cipkid">{{
                        truncateText(cert.cipkid, 20)
                      }}</span>
                    </div>
                  </div>
                </div>
              </template>
            </a-card-meta>

            <!-- 悬停详细信息面板 -->
            <div class="hover-detail-panel" v-show="cert.showDetail">
              <div class="detail-header">
                <span class="detail-title">证书详情</span>
                <a-tag :color="cert.isLabel === 1 ? 'blue' : 'green'" size="small">
                  {{ cert.isLabel === 1 ? '标签' : '私钥' }}
                </a-tag>
              </div>
              <div class="detail-content">
                <div class="detail-item">
                  <span class="detail-label">证书名称:</span>
                  <span class="detail-value">{{ cert.certName }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">CI公钥ID:</span>
                  <span class="detail-value">{{ cert.cipkid || '-' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">创建时间:</span>
                  <span class="detail-value">{{ formatDate(cert.createTime) }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">更新时间:</span>
                  <span class="detail-value">{{ formatDate(cert.updateTime) }}</span>
                </div>
              </div>
              <div class="detail-actions">
                <a-button type="link" size="small" @click="handleView(cert)">
                  <EyeOutlined />
                  查看完整信息
                </a-button>
              </div>
            </div>
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
      width="800px"
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
      width="800px"
      :footer="null"
      scrollable
      :bodyStyle="{ maxHeight: '70vh', overflowY: 'auto', padding: '16px' }"
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
import { message, Modal, Upload } from 'ant-design-vue'
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
    EyeOutlined,
    AUpload: Upload
  },
  data() {
    return {
      formState: reactive({
        certName: '',
        ciPkid: ''
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
        ciPkid: ''
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
          this.certData = (res.data || []).map(cert => ({
            ...cert,
            showDetail: false
          }))
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
    truncateText(text, maxLength) {
      if (!text) return ''
      return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
    },
    formatDate(dateString) {
      if (!dateString) return ''
      return dateString.replace('T', ' ').substring(0, 16)
    },
    getCardTooltip(cert) {
      return `证书名称: ${cert.certName || ''}\nCI PKID: ${cert.cipkid || ''}\n创建时间: ${this.formatDate(cert.createTime) || ''}`
    },
    handleAdd() {
      this.modalTitle = '新增证书'
      // 不要替换整个reactive对象，而是逐个更新属性以保持响应性
      this.modalFormData.id = undefined
      this.modalFormData.certName = ''
      this.modalFormData.ciCert = ''
      this.modalFormData.ciSubCaCert = ''
      this.modalFormData.dpSubCaCert = ''
      this.modalFormData.authCert = ''
      this.modalFormData.authKey = ''
      this.modalFormData.dbCert = ''
      this.modalFormData.dbKey = ''
      this.modalFormData.isLabel = 1
      this.modalVisible = true
    },
    handleEdit(record) {
      this.modalTitle = '编辑证书'
      // 不要替换整个reactive对象，而是逐个更新属性以保持响应性
      this.modalFormData.id = record.id
      this.modalFormData.certName = record.certName
      this.modalFormData.ciCert = record.ciCert
      this.modalFormData.ciSubCaCert = record.ciSubCaCert
      this.modalFormData.dpSubCaCert = record.dpSubCaCert
      this.modalFormData.authCert = record.authCert
      this.modalFormData.authKey = record.authKey
      this.modalFormData.dbCert = record.dbCert
      this.modalFormData.dbKey = record.dbKey
      // 处理布尔类型的isLabel值，转换为对应的数字类型
      this.modalFormData.isLabel = typeof record.isLabel === 'boolean' ? (record.isLabel ? 1 : 0) : record.isLabel
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
        // 处理布尔类型的isLabel值，转换为对应的数字类型
        isLabel: typeof record.isLabel === 'boolean' ? (record.isLabel ? 1 : 0) : record.isLabel,
        createTime: record.createTime,
        updateTime: record.updateTime
      }
      this.detailVisible = true
    },
    handleDelete(record) {
      // 使用全局注册的 Modal 而不是 this.$modal
      Modal.confirm({
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
    },
    beforeUpload(file) {
      const isExcel = file.type === 'application/vnd.ms-excel' || 
                     file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                     file.name.endsWith('.xls') ||
                     file.name.endsWith('.xlsx');
      if (!isExcel) {
        message.error('只能上传Excel文件!');
      }
      return isExcel;
    },
    customRequest(options) {
      const { file, onSuccess, onError } = options;
      const formData = new FormData();
      formData.append('file', file);

      this.$http
        .request({
          url: '/personkit/api/esim/cert/upload',
          method: 'post',
          data: formData,
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        .then((res) => {
          message.success('上传成功');
          onSuccess(res);
          // 上传成功后刷新列表
          this.query();
        })
        .catch((error) => {
          message.error('上传失败');
          onError(error);
        });
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
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #f0f0f0;
  position: relative;
}

.cert-card-compact:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #1890ff;
}

.cert-icon-container:hover {
  cursor: pointer;
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
  position: relative;
  z-index: 5;
}

.cert-avatar {
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
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
  height: auto;
  min-height: 40px;
  max-height: 60px;
  overflow: hidden;
}

.cert-info {
  font-size: 13px;
  color: #595959;
  margin-bottom: 0;
  padding-bottom: 0;
}

.info-item {
  display: flex;
  margin-bottom: 4px;
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

.truncated-text {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid #e8e8e8;
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
  max-height: none;
  word-break: break-all;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  background: #fafafa;
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #e8e8e8;
}

/* 调整卡片元信息区域 */
.ant-card-meta {
  padding: 12px;
  position: relative;
  height: auto;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

/* 调整卡片元信息详情区域，使其更紧凑 */
.ant-card-meta-detail {
  padding: 0;
  margin: 0;
  line-height: 1.3;
  height: auto;
  min-height: 0;
  flex-shrink: 1;
  overflow: hidden;
}

/* 调整标题间距 */
.ant-card-meta-title {
  margin-bottom: 6px !important;
  font-size: 14px !important;
  white-space: normal;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

/* 调整描述间距 */
.ant-card-meta-description {
  margin-bottom: 0 !important;
  padding-bottom: 0 !important;
  font-size: 12px !important;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 完全移除卡片内边距，特别是底部内边距 */
.ant-card-body {
  padding: 0 !important;
  padding-bottom: 0 !important;
  margin: 0 !important;
  margin-bottom: 0 !important;
}

/* 悬停详细信息面板 */
.hover-detail-panel {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(8px);
  border-radius: 12px;
  padding: 16px;
  z-index: 10;
  display: flex;
  flex-direction: column;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(24, 144, 255, 0.2);
  /* 去掉动画效果防止闪烁 */
  animation: none !important;
  transition: none !important;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-title {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
}

.detail-content {
  flex: 1;
  overflow-y: auto;
}

.detail-item {
  display: flex;
  margin-bottom: 8px;
  align-items: flex-start;
}

.detail-label {
  flex-shrink: 0;
  width: 70px;
  font-size: 12px;
  font-weight: 500;
  color: #434343;
}

.detail-value {
  flex: 1;
  font-size: 12px;
  color: #595959;
  word-break: break-all;
  line-height: 1.4;
}

.detail-actions {
  margin-top: 12px;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
  text-align: center;
}

/* 响应式调整 */
@media (max-width: 576px) {
  .cert-card-compact {
    height: 260px;
  }
  
  .card-cover-compact {
    height: 100px;
  }
  
  .cert-avatar {
    :size="40"
  }
  
  .hover-detail-panel {
    padding: 12px;
  }
  
  .detail-label {
    width: 60px;
    font-size: 11px;
  }
  
  .detail-value {
    font-size: 11px;
  }
}
</style>