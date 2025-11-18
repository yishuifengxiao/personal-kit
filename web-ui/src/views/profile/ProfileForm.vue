<template>
  <div class="profile-form-container">
    <!-- 页面头部 - 参考搜索源详情页风格 -->
    <a-page-header
      class="compact-header"
      :title="pageTitle"
      @back="handleCancel"
    >
      <template #extra>
        <div class="header-actions">
          <a-button v-if="!isView" type="primary" size="large" @click="handleSubmit">保存</a-button>
          <a-button v-if="!isView" size="large" @click="handleCancel">取消</a-button>
          <a-button v-if="isView" type="primary" size="large" @click="switchToEditMode">编辑</a-button>
          <a-button v-if="isView" size="large" @click="handleCancel">关闭</a-button>
        </div>
      </template>
    </a-page-header>
    
    <!-- 基本信息区域 -->
    <div class="info-section-header">
      <div class="info-content">
        <div class="info-item">
          <span class="info-label">ICCID：</span>
          <span v-if="isView" class="info-value">{{ formData.iccid }}</span>
          <a-input v-else-if="isAdd" v-model:value="formData.iccid" placeholder="请输入20位16进制ICCID" :maxlength="20" style="width: 200px" />
          <span v-else class="info-value">{{ formData.iccid }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">EID：</span>
          <span v-if="isView" class="info-value">{{ formData.eid }}</span>
          <a-input v-else-if="isAdd" v-model:value="formData.eid" placeholder="请输入32位16进制EID" :maxlength="32" style="width: 200px" />
          <span v-else class="info-value">{{ formData.eid }}</span>
        </div>
        <div class="info-item" v-if="!isAdd">
          <span class="info-label">MatchingId：</span>
          <span class="info-value">{{ formData.matchingId }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">所属租户：</span>
          <span v-if="isView" class="info-value">{{ formData.tenant || '未设置' }}</span>
          <a-select v-else v-model:value="formData.tenant" placeholder="请选择租户" style="width: 150px" allow-clear>
            <a-select-option value="租户A">租户A</a-select-option>
            <a-select-option value="租户B">租户B</a-select-option>
            <a-select-option value="租户C">租户C</a-select-option>
            <a-select-option value="系统租户">系统租户</a-select-option>
          </a-select>
        </div>
      </div>
    </div>
    
    <div class="detail-container">
      <!-- 左侧区域 - 3/5 -->
      <div class="left-section">
        <!-- 基本信息编辑区域 -->
        <div class="basic-info-section">
          <div class="section-header">
            <h3>基本信息</h3>
          </div>
          
          <a-form
            ref="formRef"
            :model="formData"
            :label-col="{ span: 6 }"
            :wrapper-col="{ span: 16 }"
            :disabled="isView"
            @finish="handleSubmit"
          >
        
              
        

            <!-- 新增的基本信息字段 -->
            <a-row :gutter="16">
              <a-col :span="8">
                <a-form-item
                  label="所属租户"
                  name="tenant"
                  :rules="[{ required: true, message: '请选择所属租户' }]"
                >
                  <a-select
                    v-model:value="formData.tenant"
                    placeholder="请选择租户"
                    allow-clear
                  >
                    <a-select-option value="租户A">租户A</a-select-option>
                    <a-select-option value="租户B">租户B</a-select-option>
                    <a-select-option value="租户C">租户C</a-select-option>
                    <a-select-option value="系统租户">系统租户</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              
              <a-col :span="8">
                <a-form-item
                  label="ASN模版"
                  name="asnTemplate"
                  :rules="[{ required: true, message: '请选择ASN模版' }]"
                >
                  <a-tooltip :title="formData.asnTemplate && formData.asnTemplate.length > 0 ? formData.asnTemplate.join(', ') : '暂无选择'" placement="top">
                    <a-select
                      v-model:value="formData.asnTemplate"
                      placeholder="请选择ASN模版"
                      allow-clear
                    >
                      <a-select-option value="template1">标准模版</a-select-option>
                      <a-select-option value="template2">企业模版</a-select-option>
                      <a-select-option value="template3">自定义模版</a-select-option>
                    </a-select>
                  </a-tooltip>
                </a-form-item>
              </a-col>
              
              <a-col :span="8">
                <a-form-item
                  label="确认码"
                  name="confirmationCode"
                >
                  <a-input
                    v-model:value="formData.confirmationCode"
                    placeholder="请输入确认码"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
            </a-row>
            
            <a-row :gutter="16">
              <a-col :span="8">
                <a-form-item
                  label="电话号码"
                  name="phoneNumber"
                >
                  <a-input
                    v-model:value="formData.phoneNumber"
                    placeholder="请输入电话号码"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
              
              <a-col :span="8">
                <a-form-item
                  label="Profile图标"
                  name="profileIcon"
                >
                  <a-input
                    v-model:value="formData.profileIcon"
                    placeholder="请输入Profile图标"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
              
              <a-col :span="8">
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
            </a-row>

            <a-row :gutter="16">
              <a-col :span="8">
                <a-form-item
                  label="服务提供商"
                  name="serviceProvider"
                  :rules="[{ required: true, message: '请选择服务提供商' }]"
                >
                  <a-select
                    v-model:value="formData.serviceProvider"
                    placeholder="请选择服务提供商"
                    allow-clear
                  >
                    <a-select-option value="provider1">提供商1</a-select-option>
                    <a-select-option value="provider2">提供商2</a-select-option>
                    <a-select-option value="provider3">提供商3</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              
              <a-col :span="8">
                <a-form-item
                  label="通知事件"
                  name="notificationEvent"
                >
                  <a-select
                    v-model:value="formData.notificationEvent"
                    placeholder="请选择通知事件"
                    allow-clear
                  >
                    <a-select-option value="event1">事件1</a-select-option>
                    <a-select-option value="event2">事件2</a-select-option>
                    <a-select-option value="event3">事件3</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              
              <a-col :span="8">
                <a-form-item
                  label="通知地址"
                  name="notificationAddress"
                  :rules="[{ required: true, message: '请输入通知地址' }]"
                >
                  <a-input
                    v-model:value="formData.notificationAddress"
                    placeholder="请输入通知地址"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="8">
                <a-form-item
                  label="Profile图标"
                  name="profileIcon"
                >
                  <a-input
                    v-model:value="formData.profileIcon"
                    placeholder="请输入Profile图标"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
              
              <a-col :span="8">
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
              
              <a-col :span="8">
                <a-form-item
                  label="运营商"
                  name="carrier"
                >
                  <a-select
                    v-model:value="formData.carrier"
                    placeholder="请选择运营商"
                    allow-clear
                  >
                    <a-select-option value="中国移动">中国移动</a-select-option>
                    <a-select-option value="中国联通">中国联通</a-select-option>
                    <a-select-option value="中国电信">中国电信</a-select-option>
                    <a-select-option value="中国广电">中国广电</a-select-option>
                  </a-select>
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

          </a-form>
        </div>
      </div>
      
      <!-- 右侧区域 - 2/5 -->
      <div class="right-section">
        <!-- V3功能支持 - 整行布局 -->
        <div class="v3-features-section">
          <a-form-item label="v3功能支持" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
            <a-tooltip :title="formData.v3Features && formData.v3Features.length > 0 ? formData.v3Features.join(', ') : '暂无选择'" placement="top">
              <a-select
                v-model:value="formData.v3Features"
                mode="multiple"
                placeholder="请选择功能"
                :disabled="isView"
                allow-clear
                style="width: 100%"
              >
                <a-select-option value="rpmData">RPM数据</a-select-option>
                <a-select-option value="deviceSwitchData">设备切换数据</a-select-option>
                <a-select-option value="enterpriseProfileData">企业Profile数据</a-select-option>
              </a-select>
            </a-tooltip>
          </a-form-item>
        </div>

        <!-- RPM数据配置 -->
        <div class="rpm-config-section" v-if="formData.v3Features && formData.v3Features.includes('rpmData')">
          <a-divider dashed orientation="center">RPM数据配置</a-divider>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="RPM类型" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-tooltip :title="formData.rpmType && formData.rpmType.length > 0 ? formData.rpmType.join(', ') : '暂无选择'" placement="top">
                  <a-select
                    v-model:value="formData.rpmType"
                    mode="multiple"
                    placeholder="请选择RPM类型"
                    :disabled="isView"
                    allow-clear
                    style="width: 100%"
                    :maxTagCount="2"
                    :maxTagPlaceholder="(omittedValues) => `+${omittedValues.length} 更多`"
                  >
                    <a-select-option value="Enable">Enable</a-select-option>
                    <a-select-option value="Disable">Disable</a-select-option>
                    <a-select-option value="Delete">Delete</a-select-option>
                    <a-select-option value="ListProfileInfo">ListProfileInfo</a-select-option>
                    <a-select-option value="ContactPcmp">ContactPcmp</a-select-option>
                  </a-select>
                </a-tooltip>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="RPM下载方式" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-select
                  v-model:value="formData.rpmDownloadMethod"
                  placeholder="请选择RPM下载方式"
                  :disabled="isView"
                  allow-clear
                  style="width: 100%"
                >
                  <a-select-option value="SM-DP+">SM-DP+</a-select-option>
                  <a-select-option value="SM-DS">SM-DS</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
          
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="允许的CA" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-select
                  v-model:value="formData.allowedCA"
                  placeholder="请选择允许的CA"
                  :disabled="isView"
                  allow-clear
                  style="width: 100%"
                >
                  <a-select-option value="CA1">CA1</a-select-option>
                  <a-select-option value="CA2">CA2</a-select-option>
                  <a-select-option value="CA3">CA3</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12" v-if="formData.rpmDownloadMethod">
              <a-form-item label="RPM轮询地址" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-select
                  v-model:value="formData.rpmPollAddress"
                  placeholder="请选择RPM轮询地址"
                  :disabled="isView"
                  allow-clear
                  style="width: 100%"
                >
                  <a-select-option value="https://api.example.com/rpm1">https://api.example.com/rpm1</a-select-option>
                  <a-select-option value="https://api.example.com/rpm2">https://api.example.com/rpm2</a-select-option>
                  <a-select-option value="https://api.example.com/rpm3">https://api.example.com/rpm3</a-select-option>
                  <a-select-option value="custom">自定义地址</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>

          <!-- 允许的Tags -->
          <a-form-item label="允许的Tags" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
            <a-tooltip :title="formData.allowedTags && formData.allowedTags.length > 0 ? formData.allowedTags.join(', ') : '暂无选择'" placement="top">
              <a-select
                v-model:value="formData.allowedTags"
                mode="multiple"
                placeholder="请选择允许的Tags"
                :disabled="isView"
                allow-clear
                style="width: 100%"
                :maxTagCount="3"
                :maxTagPlaceholder="(omittedValues) => `+${omittedValues.length} 更多`"
              >
                <a-select-option value="Service provider name">Service provider name</a-select-option>
                <a-select-option value="Profile name">Profile name</a-select-option>
                <a-select-option value="Notification Configuration Info">Notification Configuration Info</a-select-option>
                <a-select-option value="Icon type and Icon">Icon type and Icon</a-select-option>
                <a-select-option value="Profile Policy Rules">Profile Policy Rules</a-select-option>
                <a-select-option value="Service Specific Data stored in eUICC">Service Specific Data stored in eUICC</a-select-option>
                <a-select-option value="RPM Configuration">RPM Configuration</a-select-option>
                <a-select-option value="HRI Server address">HRI Server address</a-select-option>
                <a-select-option value="LPR Configuration">LPR Configuration</a-select-option>
                <a-select-option value="Enterprise Configuration">Enterprise Configuration</a-select-option>
                <a-select-option value="Device Change configuration">Device Change configuration</a-select-option>
              </a-select>
            </a-tooltip>
          </a-form-item>
        </div>

        <!-- 设备切换数据 -->
        <div class="device-switch-section" v-if="formData.v3Features && formData.v3Features.includes('deviceSwitchData')">
          <a-divider dashed orientation="center">设备切换数据</a-divider>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="设备切换方式" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-radio-group v-model:value="formData.deviceSwitchMethod" :disabled="isView">
                  <a-radio value="requestPlatform">请求平台</a-radio>
                  <a-radio value="useStoredCode">使用存储的激活码</a-radio>
                </a-radio-group>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="允许的CA" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-select
                  v-model:value="formData.deviceSwitchAllowedCA"
                  placeholder="请选择允许的CA"
                  :disabled="isView"
                  allow-clear
                  style="width: 100%"
                >
                  <a-select-option value="CA1">CA1</a-select-option>
                  <a-select-option value="CA2">CA2</a-select-option>
                  <a-select-option value="CA3">CA3</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
          
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="是否需要新设备EID" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-radio-group v-model:value="formData.needNewEID" :disabled="isView">
                  <a-radio value="yes">是</a-radio>
                  <a-radio value="no">否</a-radio>
                </a-radio-group>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="是否需要新设备TAC" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-radio-group v-model:value="formData.needNewTAC" :disabled="isView">
                  <a-radio value="yes">是</a-radio>
                  <a-radio value="no">否</a-radio>
                </a-radio-group>
              </a-form-item>
            </a-col>
          </a-row>
        </div>

        <!-- 企业Profile数据 -->
        <div class="enterprise-profile-section" v-if="formData.v3Features && formData.v3Features.includes('enterpriseProfileData')">
          <a-divider dashed orientation="center">企业Profile数据</a-divider>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="企业名称" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-select
                  v-model:value="formData.enterpriseName"
                  placeholder="请选择企业名称"
                  :disabled="isView"
                  allow-clear
                  style="width: 100%"
                >
                  <a-select-option value="enterprise1">企业1</a-select-option>
                  <a-select-option value="enterprise2">企业2</a-select-option>
                  <a-select-option value="enterprise3">企业3</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12" v-if="formData.enterpriseRules && formData.enterpriseRules.length > 0">
              <a-form-item label="非企业Profile数量" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-input-number
                  v-model:value="formData.nonEnterpriseProfileCount"
                  :min="0"
                  :max="10"
                  placeholder="请输入数量"
                  :disabled="isView"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
          </a-row>
          
          <!-- 规则 -->
          <a-form-item label="规则" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
            <a-tooltip :title="formData.enterpriseRules && formData.enterpriseRules.length > 0 ? formData.enterpriseRules.join(', ') : '暂无选择'" placement="top">
              <a-select
                v-model:value="formData.enterpriseRules"
                mode="multiple"
                placeholder="请选择规则"
                :disabled="isView"
                allow-clear
                style="width: 100%"
              >
                <a-select-option value="priorityEnterpriseProfile">优先级企业Profile</a-select-option>
                <a-select-option value="onlyInstallEnterpriseProfile">只能安装企业Profile</a-select-option>
              </a-select>
            </a-tooltip>
          </a-form-item>
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
      // 新增基本信息字段
      confirmationCode: '',
      profileName: '',
      phoneNumber: '',
      serviceProvider: '',
      notificationEvent: '',
      notificationAddress: '',
      profileIcon: '',
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
      smsp: '',
      // V3功能支持字段 - 初始化为数组避免类型错误
      v3Features: [],
      // RPM数据配置字段
      rpmType: [],
      rpmDownloadMethod: '',
      rpmPollingAddress: '',
      allowedCA: '',
      allowedTags: [],
      // 设备切换数据字段
      deviceSwitchMethod: '',
      needNewEID: '',
      needNewTAC: '',
      deviceSwitchAllowedCA: '',
      // 企业Profile数据字段
      enterpriseName: '',
      enterpriseRules: [],
      nonEnterpriseProfileCount: 0
    })

    // 计算属性：是否为新增模式
    const isAdd = computed(() => pageType.value === 'add')

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

    // 模拟获取Profile详情的API函数
    const getProfileDetail = async (id) => {
      // 这里应该是真实的API调用
      // 现在返回模拟数据
      return {
        code: 200,
        data: {
          iccid: '12345678901234567890',
          matchingId: 'matching-' + id,
          profileStatus: 'Available',
          eid: '12345678901234567890123456789012',
          notificationStatus: 'Enabled',
          downloadMethod: '1',
          tenant: 'default-tenant',
          carrier: '中国移动',
          profileClass: 'operational',
          pprPolicy: 'PPR1',
          resetRule: '1',
          dsFlag: 'flag1',
          remark: '测试数据',
          confirmationCode: 'code123',
          profileName: '测试Profile',
          phoneNumber: '13800138000',
          serviceProvider: 'provider1',
          notificationEvent: 'event1',
          notificationAddress: 'http://example.com',
          profileIcon: 'icon1',
          imsi: '460001234567890',
          imsi2: '',
          pin1: '1234',
          pin2: '5678',
          puk1: '12345678',
          puk2: '87654321',
          adm1: 'adm1234',
          ki: 'ki123456',
          opc: 'opc123456',
          smsp: 'smsp123',
          v3Features: ['rpmData', 'deviceSwitchData'],
          rpmType: ['Enable', 'Disable'],
          rpmDownloadMethod: 'SM-DP+',
          rpmPollingAddress: 'http://rpm.example.com',
          allowedCA: 'CA1',
          allowedTags: ['Service provider name', 'Profile name'],
          deviceSwitchMethod: 'requestPlatform',
          needNewEID: 'yes',
          needNewTAC: 'no',
          deviceSwitchAllowedCA: 'CA1',
          enterpriseName: '',
          enterpriseRules: [],
          nonEnterpriseProfileCount: 0
        }
      }
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
          if (key === 'v3Features' || key === 'rpmType' || key === 'allowedTags' || key === 'enterpriseRules') {
            formData[key] = []
          } else if (key === 'nonEnterpriseProfileCount') {
            formData[key] = 0
          } else {
            formData[key] = ''
          }
        })
      }
    }
    
    const handleSubmit = async () => {
      try {
        await formRef.value.validate()
        message.success('保存成功')
        router.push('/view/profile_manage')
      } catch (error) {
        console.error('表单验证失败:', error)
      }
    }
    
    const handleCancel = () => {
      router.push('/view/profile_manage')
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
      isAdd,
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
  overflow-x: hidden;
  overflow-y: auto;
}

/* 紧凑的页面头部 */
.compact-header {
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  padding: 8px 16px;
  margin-bottom: 0;
}

.header-actions {
  display: flex;
  gap: 8px;
}

/* 基本信息区域 - 参考搜索源详情页风格 */
.info-section-header {
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  padding: 12px 16px;
}

.info-content {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.info-label {
  color: #666;
  font-size: 13px;
  white-space: nowrap;
}

.info-value {
  color: #333;
  font-size: 13px;
  font-weight: 500;
}

/* 详情容器 - 左右布局 */
.detail-container {
  display: flex;
  gap: 12px;
  width: 100%;
  box-sizing: border-box;
  padding-bottom: 32px;
  min-height: calc(100vh - 100px);
  max-height: calc(100vh - 100px);
  overflow-y: auto;
  overflow-x: hidden; /* 防止水平滚动 */
}

/* 左侧区域 */
.left-section {
  flex: 3;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  min-width: 0;
  overflow: visible;
  max-height: calc(100vh - 124px); /* 添加最大高度限制 */
  overflow-y: auto; /* 允许垂直滚动 */
}

/* 基本信息编辑区域 */
.basic-info-section {
  padding: 8px; /* 减少内边距 */
  overflow: visible;
  padding-bottom: 15px; /* 减少底部内边距 */
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px; /* 减少底部间距 */
  padding-bottom: 4px; /* 减少底部内边距 */
  border-bottom: 1px solid #f0f0f0;
}

.section-header h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

/* 右侧区域 */
.right-section {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 0;
  overflow-y: auto;
  max-height: calc(100vh - 124px);
  min-width: 0;
  padding-bottom: 10px; /* 减少底部内边距 */
  background: #fff; /* 默认白色背景 */
  border: 1px solid #e8e8e8;
  border-radius: 6px;
}

/* V3功能支持 */
.v3-features-section {
  padding: 8px; /* 减少内边距 */
  margin-bottom: 0;
  border-bottom: 1px solid #e8e8e8;
}

.rpm-config-section,
.device-switch-section,
.enterprise-profile-section {
  padding: 8px; /* 减少内边距 */
  margin-bottom: 0;
  overflow: visible;
  min-height: auto; /* 移除最小高度限制 */
  border-bottom: 1px solid #e8e8e8;
}

.enterprise-profile-section {
  margin-bottom: 10px; /* 减少底部间距 */
  border-bottom: none; /* 最后一个section不需要底部边框 */
}

.readonly-value {
  display: inline-block;
  padding: 4px 8px;
  background: #f5f5f5;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  color: #666;
  font-size: 13px;
  min-height: 22px;
  line-height: 1.5;
}

:deep(.ant-form-item) {
  margin-bottom: 4px; /* 减少表单项目间距 */
}

/* 优化多选下拉框的显示 */
:deep(.ant-select-selection-item) {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.ant-select-selection-overflow) {
  flex-wrap: wrap;
  gap: 4px;
  padding: 2px 0;
}

/* 多选下拉框样式优化 - 使用tooltip显示已选项 */

:deep(.ant-form-item-label) {
  padding-bottom: 2px;
  font-size: 13px;
}

:deep(.ant-form-item-label > label) {
  font-size: 13px;
}

:deep(.ant-input),
:deep(.ant-select-selector),
:deep(.ant-input-number-input) {
  border-radius: 4px;
  font-size: 13px;
  min-height: 28px;
}

:deep(.ant-btn) {
  border-radius: 4px;
  font-size: 13px;
  height: 28px;
  padding: 4px 12px;
}

:deep(.ant-btn-sm) {
  font-size: 12px;
  height: 24px;
  padding: 2px 8px;
}

:deep(.ant-checkbox-wrapper) {
  font-size: 13px;
  margin-bottom: 4px;
}

:deep(.ant-radio-wrapper) {
  font-size: 13px;
  margin-bottom: 4px;
}

:deep(.ant-divider) {
  margin: 4px 0; /* 减少分割线间距 */
  font-size: 13px;
  font-weight: 500;
}

/* 确保表单区域有足够的间距 */
:deep(.ant-form-item-control) {
  min-height: 32px; /* 确保控件有足够高度 */
}

:deep(.ant-select-multiple .ant-select-selector) {
  min-height: 32px; /* 多选框最小高度 */
  padding: 2px 4px;
}

:deep(.ant-divider-dashed) {
  border-color: #d9d9d9;
  border-style: dashed;
  border-width: 1px 0 0 0;
}

:deep(.ant-divider-with-text) {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 8px 0;
}

:deep(.ant-row) {
  margin-left: -4px !important;
  margin-right: -4px !important;
}

:deep(.ant-col) {
  padding-left: 4px !important;
  padding-right: 4px !important;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .detail-container {
    flex-direction: column;
  }
  
  .right-section {
    width: 100%;
    max-height: none;
  }
  
  .left-section {
    max-height: none; /* 在小屏幕上移除高度限制 */
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