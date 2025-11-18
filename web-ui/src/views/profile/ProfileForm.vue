<template>
  <div class="profile-form-container">
    <!-- 页面头部 - 参考搜索源详情页风格 -->
    <a-page-header
      class="compact-header"
      :title="pageTitle"
      @back="handleCancel"
    />
    
    <!-- 基本信息区域 -->
    <div class="info-section-header">
      <div class="info-content">
        <div class="info-item">
          <span class="info-label">ICCID：</span>
          <span v-if="isView" class="info-value">{{ formData.iccid }}</span>
          <a-input v-else v-model:value="formData.iccid" placeholder="请输入20位16进制ICCID" :maxlength="20" style="width: 200px" />
        </div>
   
   
        <div class="info-item">
          <span class="info-label">所属租户：</span>
          <span v-if="isView" class="info-value">{{ formData.tenant || '未设置' }}</span>
          <a-input v-else v-model:value="formData.tenant" placeholder="请输入租户" style="width: 150px" />
        </div>
      </div>
    </div>
    
    <div class="detail-container">
      <!-- 左侧区域 -->
      <div class="left-section">
        <!-- 基本信息编辑区域 -->
        <div class="basic-info-section">
          <div class="section-header">
            <h3>基本信息</h3>
            <div class="action-buttons" v-if="!isView">
              <a-button type="primary" size="small" @click="handleSubmit">保存</a-button>
              <a-button size="small" @click="handleCancel">取消</a-button>
            </div>
            <div class="action-buttons" v-else>
              <a-button type="primary" size="small" @click="switchToEditMode">编辑</a-button>
              <a-button size="small" @click="handleCancel">关闭</a-button>
            </div>
          </div>
          
          <a-form
            ref="formRef"
            :model="formData"
            :label-col="{ span: 6 }"
            :wrapper-col="{ span: 16 }"
            :disabled="isView"
            @finish="handleSubmit"
          >
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item
                  label="MatchingId"
                  name="matchingId"
                  :rules="[{ required: true, message: '请输入MatchingId' }]"
                >
                  <a-input
                    v-model:value="formData.matchingId"
                    placeholder="如：WTBIJ-EG8C0-C72NY-VNOPX"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
              
              <a-col :span="12">
                <a-form-item
                  label="EID"
                  name="eid"
                  :rules="[{ required: true, message: '请输入EID' }]"
                >
                  <a-input
                    v-model:value="formData.eid"
                    placeholder="请输入32位16进制EID"
                    :maxlength="32"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item
                  label="下载方式"
                  name="downloadMethod"
                >
                  <a-select
                    v-model:value="formData.downloadMethod"
                    placeholder="请选择下载方式"
                    allow-clear
                  >
                    <a-select-option value="default">默认SM-DP+</a-select-option>
                    <a-select-option value="activation">激活码</a-select-option>
                    <a-select-option value="alt-smds">ALT-SM-DS</a-select-option>
                    <a-select-option value="root-smds">ROOT-SM-DS</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              
              <a-col :span="12">
                <a-form-item
                  label="运营商"
                  name="carrier"
                >
                  <a-input
                    v-model:value="formData.carrier"
                    placeholder="请输入运营商"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item
                  label="Profile类"
                  name="profileClass"
                >
                  <a-select
                    v-model:value="formData.profileClass"
                    placeholder="请选择Profile类"
                    allow-clear
                  >
                    <a-select-option value="test">test</a-select-option>
                    <a-select-option value="provisioning">provisioning</a-select-option>
                    <a-select-option value="operational">operational</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              
              <a-col :span="12">
                <a-form-item
                  label="PPR策略"
                  name="pprPolicy"
                >
                  <a-select
                    v-model:value="formData.pprPolicy"
                    placeholder="请选择PPR策略"
                    allow-clear
                  >
                    <a-select-option value="PPR1">PPR1</a-select-option>
                    <a-select-option value="PPR2">PPR2</a-select-option>
                    <a-select-option value="PPR1,PPR2">PPR1、PPR2</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item
                  label="重置规则"
                  name="resetRule"
                >
                  <a-select
                    v-model:value="formData.resetRule"
                    placeholder="请选择重置规则"
                    allow-clear
                  >
                    <a-select-option value="no_reset">不可重置</a-select-option>
                    <a-select-option value="resetable">可重置</a-select-option>
                    <a-select-option value="auto_reset">自动重置</a-select-option>
                    <a-select-option value="auto_recycle">自动回收</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              
              <a-col :span="12">
                <a-form-item
                  label="DS标记"
                  name="dsFlag"
                >
                  <a-input
                    v-model:value="formData.dsFlag"
                    placeholder="请输入DS标记"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <!-- 码号信息 - 必填字段 -->
            <a-divider>码号信息</a-divider>
            
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item
                  label="IMSI"
                  name="imsi"
                  :rules="[{ required: true, message: '请输入IMSI' }]"
                >
                  <a-input
                    v-model:value="formData.imsi"
                    placeholder="请输入IMSI"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
              
              <a-col :span="12">
                <a-form-item
                  label="IMSI2"
                  name="imsi2"
                >
                  <a-input
                    v-model:value="formData.imsi2"
                    placeholder="请输入IMSI2（可选）"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item
                  label="PIN1"
                  name="pin1"
                  :rules="[{ required: true, message: '请输入PIN1' }]"
                >
                  <a-input
                    v-model:value="formData.pin1"
                    placeholder="请输入PIN1"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
              
              <a-col :span="12">
                <a-form-item
                  label="PIN2"
                  name="pin2"
                  :rules="[{ required: true, message: '请输入PIN2' }]"
                >
                  <a-input
                    v-model:value="formData.pin2"
                    placeholder="请输入PIN2"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item
                  label="PUK1"
                  name="puk1"
                  :rules="[{ required: true, message: '请输入PUK1' }]"
                >
                  <a-input
                    v-model:value="formData.puk1"
                    placeholder="请输入PUK1"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
              
              <a-col :span="12">
                <a-form-item
                  label="PUK2"
                  name="puk2"
                  :rules="[{ required: true, message: '请输入PUK2' }]"
                >
                  <a-input
                    v-model:value="formData.puk2"
                    placeholder="请输入PUK2"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item
                  label="ADM1"
                  name="adm1"
                  :rules="[{ required: true, message: '请输入ADM1' }]"
                >
                  <a-input
                    v-model:value="formData.adm1"
                    placeholder="请输入ADM1"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
              
              <a-col :span="12">
                <a-form-item
                  label="KI"
                  name="ki"
                  :rules="[{ required: true, message: '请输入KI' }]"
                >
                  <a-input
                    v-model:value="formData.ki"
                    placeholder="请输入KI"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item
                  label="OPC"
                  name="opc"
                  :rules="[{ required: true, message: '请输入OPC' }]"
                >
                  <a-input
                    v-model:value="formData.opc"
                    placeholder="请输入OPC"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
              
              <a-col :span="12">
                <a-form-item
                  label="SMSP"
                  name="smsp"
                  :rules="[{ required: true, message: '请输入SMSP' }]"
                >
                  <a-input
                    v-model:value="formData.smsp"
                    placeholder="请输入SMSP"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-item
                  label="备注"
                  name="remark"
                  :label-col="{ span: 3 }"
                  :wrapper-col="{ span: 20 }"
                >
                  <a-textarea
                    v-model:value="formData.remark"
                    placeholder="请输入备注信息"
                    :rows="3"
                    :maxlength="500"
                    show-count
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </div>
      </div>
      
      <!-- 右侧区域 -->
      <div class="right-section">
        <!-- 码号信息区 -->
        <div class="number-info-section">
          <h3>码号信息</h3>
          <div class="number-info-content">
            <div class="info-item">
              <span class="info-label">IMSI：</span>
              <div class="info-value">
                <span v-if="isView">{{ formData.imsi || '未设置' }}</span>
                <a-input
                  v-else
                  v-model:value="formData.imsi"
                  placeholder="请输入IMSI"
                  allow-clear
                  size="small"
                />
              </div>
            </div>
            <div class="info-item">
              <span class="info-label">IMSI2：</span>
              <div class="info-value">
                <span v-if="isView">{{ formData.imsi2 || '未设置' }}</span>
                <a-input
                  v-else
                  v-model:value="formData.imsi2"
                  placeholder="请输入IMSI2（可选）"
                  allow-clear
                  size="small"
                />
              </div>
            </div>
            <div class="info-item">
              <span class="info-label">PIN1：</span>
              <div class="info-value">
                <span v-if="isView">{{ formData.pin1 || '未设置' }}</span>
                <a-input
                  v-else
                  v-model:value="formData.pin1"
                  placeholder="请输入PIN1"
                  allow-clear
                  size="small"
                />
              </div>
            </div>
            <div class="info-item">
              <span class="info-label">PIN2：</span>
              <div class="info-value">
                <span v-if="isView">{{ formData.pin2 || '未设置' }}</span>
                <a-input
                  v-else
                  v-model:value="formData.pin2"
                  placeholder="请输入PIN2"
                  allow-clear
                  size="small"
                />
              </div>
            </div>
            <div class="info-item">
              <span class="info-label">PUK1：</span>
              <div class="info-value">
                <span v-if="isView">{{ formData.puk1 || '未设置' }}</span>
                <a-input
                  v-else
                  v-model:value="formData.puk1"
                  placeholder="请输入PUK1"
                  allow-clear
                  size="small"
                />
              </div>
            </div>
            <div class="info-item">
              <span class="info-label">PUK2：</span>
              <div class="info-value">
                <span v-if="isView">{{ formData.puk2 || '未设置' }}</span>
                <a-input
                  v-else
                  v-model:value="formData.puk2"
                  placeholder="请输入PUK2"
                  allow-clear
                  size="small"
                />
              </div>
            </div>
            <div class="info-item">
              <span class="info-label">ADM1：</span>
              <div class="info-value">
                <span v-if="isView">{{ formData.adm1 || '未设置' }}</span>
                <a-input
                  v-else
                  v-model:value="formData.adm1"
                  placeholder="请输入ADM1"
                  allow-clear
                  size="small"
                />
              </div>
            </div>
            <div class="info-item">
              <span class="info-label">KI：</span>
              <div class="info-value">
                <span v-if="isView">{{ formData.ki || '未设置' }}</span>
                <a-input
                  v-else
                  v-model:value="formData.ki"
                  placeholder="请输入KI"
                  allow-clear
                  size="small"
                />
              </div>
            </div>
            <div class="info-item">
              <span class="info-label">OPC：</span>
              <div class="info-value">
                <span v-if="isView">{{ formData.opc || '未设置' }}</span>
                <a-input
                  v-else
                  v-model:value="formData.opc"
                  placeholder="请输入OPC"
                  allow-clear
                  size="small"
                />
              </div>
            </div>
            <div class="info-item">
              <span class="info-label">SMSP：</span>
              <div class="info-value">
                <span v-if="isView">{{ formData.smsp || '未设置' }}</span>
                <a-input
                  v-else
                  v-model:value="formData.smsp"
                  placeholder="请输入SMSP"
                  allow-clear
                  size="small"
                />
              </div>
            </div>
          </div>
        </div>
        
        <!-- 操作记录 -->
        <div class="timeline-section">
          <h3>操作记录</h3>
          <div class="timeline-placeholder">
            <a-empty description="暂无操作记录" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'

export default defineComponent({
  name: 'ProfileForm',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const formRef = ref()
    
    const pageType = ref('add') // add, edit, view
    const recordId = ref('')
    
    const formData = reactive({
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
      remark: '',
      // 码号信息字段
      imsi: '',
      imsi2: '',
      pin1: '',
      pin2: '',
      puk1: '',
      puk2: '',
      adm1: '',
      ki: '',
      opc: '',
      smsp: ''
    })

    const pageTitle = computed(() => {
      switch (pageType.value) {
        case 'add': return '新增Profile'
        case 'edit': return '编辑Profile'
        case 'view': return 'Profile详情'
        default: return 'Profile管理'
      }
    })

    const isView = computed(() => pageType.value === 'view')

    // 获取Profile状态对应的颜色
    const getProfileStatusColor = (status) => {
      const colorMap = {
        'Available': 'green',
        'Allocated': 'blue',
        'Linked': 'cyan',
        'Confirmed': 'purple',
        'Released': 'orange',
        'Downloaded': 'geekblue',
        'Installed': 'success',
        'Error': 'red',
        'Unavailable': 'default'
      }
      return colorMap[status] || 'default'
    }

    // 获取通知状态对应的badge状态
    const getNotificationStatusStatus = (status) => {
      const statusMap = {
        'Enabled': 'success',
        'Disabled': 'default',
        'Deleted': 'error'
      }
      return statusMap[status] || 'default'
    }

    // 获取下载方式文本
    const getDownloadMethodText = (method) => {
      const methodMap = {
        '1': '标准下载',
        '2': '快速下载',
        '3': '批量下载'
      }
      return methodMap[method] || method
    }

    // 获取重置规则文本
    const getResetRuleText = (rule) => {
      const ruleMap = {
        '1': '允许重置',
        '2': '禁止重置',
        '3': '条件重置'
      }
      return ruleMap[rule] || rule
    }
    
    const loadDetail = async () => {
      if (recordId.value) {
        try {
          const response = await getProfileDetail(recordId.value)
          if (response.code === 200) {
            Object.assign(formData, response.data)
          } else {
            message.error(response.message || '获取详情失败')
          }
        } catch (error) {
          message.error('获取详情失败')
        }
      } else if (pageType.value === 'add') {
        // 新增时清空表单数据
        Object.keys(formData).forEach(key => {
          formData[key] = ''
        })
      }
    }
    
    const handleSubmit = async () => {
      try {
        await formRef.value.validate()
        message.success('保存成功')
        router.push('/profile_manage')
      } catch (error) {
        console.error('表单验证失败:', error)
      }
    }
    
    const handleCancel = () => {
      router.push('/profile_manage')
    }
    
    // 切换到编辑模式
    const switchToEditMode = () => {
      pageType.value = 'edit'
      router.replace({
        path: '/profile_form',
        query: { type: 'edit', id: recordId.value }
      })
    }
    
    onMounted(() => {
      const { type, id } = route.query
      pageType.value = type || 'add'
      recordId.value = id || ''
      loadDetail()
    })
    
    return {
      formRef,
      formData,
      pageTitle,
      isView,
      getProfileStatusColor,
      getNotificationStatusStatus,
      getDownloadMethodText,
      getResetRuleText,
      handleSubmit,
      handleCancel,
      switchToEditMode
    }
  }
})
</script>

<style scoped>
.profile-form-container {
  padding: 0;
  background-color: #f5f5f5;
  min-height: 100vh;
}

/* 紧凑的页面头部 */
.compact-header {
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  padding: 12px 24px;
  margin-bottom: 0;
}

/* 基本信息区域 - 参考搜索源详情页风格 */
.info-section-header {
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 24px;
}

.info-content {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  align-items: center;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-label {
  color: #666;
  font-size: 14px;
  white-space: nowrap;
}

.info-value {
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

/* 详情容器 - 左右布局 */
.detail-container {
  display: flex;
  gap: 16px;
  width: 100%;
  box-sizing: border-box;
}

/* 左侧区域 */
.left-section {
  flex: 1;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 基本信息编辑区域 */
.basic-info-section {
  padding: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

/* 右侧区域 */
.right-section {
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 码号信息区 */
.number-info-section {
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  padding: 16px;
}

.number-info-section h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.number-info-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-height: 32px;
}

.info-label {
  color: #666;
  font-size: 14px;
  white-space: nowrap;
  margin-right: 8px;
  width: 60px;
}

.info-value {
  color: #333;
  font-size: 14px;
  text-align: right;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 右侧输入框样式 */
.info-value .ant-input {
  text-align: right;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 14px;
}

.info-value .ant-input:focus {
  border-color: #40a9ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

/* 时间轴 */
.timeline-section {
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  padding: 16px;
}

.timeline-section h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.timeline-placeholder {
  padding: 20px 0;
  text-align: center;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .detail-container {
    flex-direction: column;
  }
  
  .right-section {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .info-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .detail-container {
    padding: 12px;
  }
  
  .compact-header {
    padding: 8px 16px;
  }
  
  .info-section-header {
    padding: 12px 16px;
  }
}
</style>