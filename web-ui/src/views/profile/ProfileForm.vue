<template>
  <div class="profile-form-container">
    <!-- 页面头部 - 参考搜索源详情页风格 -->
    <a-page-header class="compact-header" :title="pageTitle" @back="handleCancel">
      <template #extra>
        <div class="header-actions">
          <a-button type="primary" size="large" @click="handleSubmit">保存</a-button>
          <a-button size="large" @click="handleCancel">取消</a-button>
        </div>
      </template>
    </a-page-header>

    <!-- 基本信息区域 -->
    <div class="info-section-header">
      <div class="info-content">
        <div class="info-item">
          <span class="info-label">ICCID：</span>
          <a-input v-if="isAdd" v-model:value="formData.iccid" placeholder="请输入20位16进制ICCID" :maxlength="20"
            style="width: 200px" size="middle" />
          <span v-else class="info-value">{{ formData.iccid }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">EID：</span>
          <a-input v-if="isAdd" v-model:value="formData.eid" placeholder="请输入32位16进制EID" :maxlength="32"
            style="width: 200px" size="middle" />
          <span v-else class="info-value">{{ formData.eid }}</span>
        </div>
        <div class="info-item" v-if="!isAdd">
          <span class="info-label">MatchingId：</span>
          <span class="info-value">{{ formData.matchingId }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">ASN模版：</span>
          <a-select v-model:value="formData.asnTemplate" placeholder="请选择ASN模版" allow-clear style="width: 200px"
            :rules="[{ required: true, message: '请选择ASN模版' }]" size="middle">
            <a-select-option v-for="template in asnTemplates" :key="template.value" :value="template.value">
              {{ template.label }}
            </a-select-option>
          </a-select>
        </div>
        <div class="info-item">
          <span class="info-label">所属租户：</span>
          <a-select v-model:value="formData.tenant" placeholder="请选择租户" allow-clear style="width: 200px"
            :rules="[{ required: true, message: '请选择所属租户' }]" size="middle">
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

          <a-form ref="formRef" :model="formData" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }"
            @finish="handleSubmit">
            <!-- 三列布局区域 - 严格按照要求包含13个字段 -->
            <a-row :gutter="16">
              <a-col :span="8">
                <a-form-item label="运营商" name="carrier" :rules="[{ required: true, message: '请选择运营商' }]">
                  <a-select v-model:value="formData.carrier" placeholder="请选择运营商" allow-clear size="middle">
                    <a-select-option v-for="carrier in carriers" :key="carrier.value" :value="carrier.value">
                      {{ carrier.label }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <!-- 第一行：确认码、电话号码、Profile图标 -->
              <a-col :span="8">
                <a-form-item label="确认码" name="confirmationCode" :rules="[
                  { max: 8, message: '确认码最长8个字符' }
                ]">
                  <a-input v-model:value="formData.confirmationCode" placeholder="请输入确认码" allow-clear size="middle" :maxlength="8" />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="确认码次数" name="confirmLimit">
                  <a-input-number v-model:value="formData.confirmLimit" placeholder="请输入确认码次数" :min="0" allow-clear
                    size="middle" style="width: 100%" />
                </a-form-item>
              </a-col>

            </a-row>

            <a-row :gutter="16">
              <!-- 第二行：下载方式、运营商、Profile名称 -->
              <a-col :span="8">
                <a-form-item label="下载方式" name="downloadMethod">
                  <a-select v-model:value="formData.downloadMethod" placeholder="请选择下载方式" allow-clear size="middle">
                    <a-select-option value="defaultMethod">默认SM-DP+</a-select-option>
                    <a-select-option value="activation">激活码</a-select-option>
                    <a-select-option value="rootSmDs">ALT-SM-DS</a-select-option>
                    <a-select-option value="altSmDs">ROOT-SM-DS</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="重置规则" name="resetRule">
                  <a-select v-model:value="formData.resetRule" placeholder="请选择重置规则" allow-clear size="middle">
                    <a-select-option value="noReset">不可重置</a-select-option>
                    <a-select-option value="resettable">可重置</a-select-option>
                    <a-select-option value="autoReset">自动重置</a-select-option>
                    <a-select-option value="autoRecycle">自动回收</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>

              <a-col :span="8">
                <a-form-item label="Profile类" name="profileClass">
                  <a-select v-model:value="formData.profileClass" placeholder="请选择Profile类" allow-clear size="middle">
                    <a-select-option value="test">test</a-select-option>
                    <a-select-option value="provisioning">provisioning</a-select-option>
                    <a-select-option value="operational">operational</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <!-- 第三行：服务提供商、通知事件、通知地址 -->
              <a-col :span="8">
                <a-form-item label="服务提供商" name="serviceProvider" :rules="[
                  { max: 50, message: '服务提供商最长50个字符' }
                ]">
                  <a-input v-model:value="formData.serviceProvider" placeholder="请输入服务提供商" allow-clear size="middle" :maxlength="50" />
                </a-form-item>
              </a-col>

              <a-col :span="8">
                <a-form-item label="Profile名称" name="profileName" :rules="[
                  { max: 50, message: 'Profile名称最长50个字符' }
                ]">
                  <a-input v-model:value="formData.profileName" placeholder="请输入Profile名称" allow-clear size="middle" :maxlength="50" />
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="Profile图标" name="profileIcon">
                  <a-input v-model:value="formData.profileIcon" placeholder="请输入Profile图标" allow-clear size="middle" />
                </a-form-item>
              </a-col>

            </a-row>

            <a-row :gutter="16">
              <!-- 第四行：Profile类、PPR策略、DS标记 -->

              <a-col :span="8">
                <a-form-item label="PPR策略" name="pprPolicy">
                  <a-select v-model:value="formData.pprPolicy" placeholder="请选择PPR策略" allow-clear size="middle">
                    <a-select-option value="PPR1">PPR1</a-select-option>
                    <a-select-option value="PPR2">PPR2</a-select-option>
                    <a-select-option value="PPR1,PPR2">PPR1、PPR2</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="DS标记" name="dsFlag">
                  <a-select v-model:value="formData.dsFlag" placeholder="请选择DS标记" allow-clear size="middle"
                    style="width: 100%">
                    <a-select-option value="1">是</a-select-option>
                    <a-select-option value="0">否</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>

              <a-col :span="8">
                <a-form-item label="电话号码" name="phoneNumber" :rules="[
                  { max: 20, message: '电话号码最长20个字符' }
                ]">
                  <a-input v-model:value="formData.phoneNumber" placeholder="请输入电话号码" allow-clear size="middle" :maxlength="20" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16"> </a-row>

            <!-- 两列布局区域：通知事件和通知地址 -->
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="通知事件" name="notificationEvent">
                  <a-tooltip :title="formData.notificationEvent && formData.notificationEvent.length > 0
                    ? formData.notificationEvent.join(', ')
                    : '暂无选择'
                    " placement="top">
                    <a-select v-model:value="formData.notificationEvent" mode="multiple" placeholder="请选择通知事件"
                      allow-clear :maxTagCount="2" :maxTagPlaceholder="(omittedValues) => `+${omittedValues.length} 更多`"
                      size="middle">
                      <a-select-option value="下载">下载</a-select-option>
                      <a-select-option value="启用">启用</a-select-option>
                      <a-select-option value="禁用">禁用</a-select-option>
                      <a-select-option value="删除">删除</a-select-option>
                      <a-select-option value="RPM启用">RPM启用</a-select-option>
                      <a-select-option value="RPM禁用">RPM禁用</a-select-option>
                      <a-select-option value="RPM删除">RPM删除</a-select-option>
                      <a-select-option value="RPM结果">RPM结果</a-select-option>
                    </a-select>
                  </a-tooltip>
                </a-form-item>
              </a-col>

              <a-col :span="12">
                <a-form-item label="通知地址" name="notificationAddress" :rules="[{ required: true, message: '请选择通知地址' }]">
                  <a-select v-model:value="formData.notificationAddress" placeholder="请选择通知地址" allow-clear
                    size="middle">
                    <a-select-option value="http://localhost:8080/notification">本地通知地址</a-select-option>
                    <a-select-option value="http://api.example.com/notify">示例API地址</a-select-option>
                    <a-select-option value="https://notify.service.com/callback">生产通知地址</a-select-option>
                    <a-select-option value="http://test.notify.com/webhook">测试通知地址</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>

            <!-- 码号信息 - 必填字段 -->
            <a-divider>码号信息</a-divider>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="IMSI" name="imsi" :rules="[
                  { required: true, message: '请输入IMSI' },
                  { max: 15, message: 'IMSI最长15个字符' },
                  { pattern: /^[0-9+]+$/, message: 'IMSI只能包含数字或+' }
                ]">
                  <a-input v-model:value="formData.imsi" placeholder="请输入IMSI" allow-clear size="middle" :maxlength="15" />
                </a-form-item>
              </a-col>

              <a-col :span="12">
                <a-form-item label="IMSI2" name="imsi2" :rules="[
                  { max: 15, message: 'IMSI2最长15个字符' },
                  { pattern: /^[0-9+]+$/, message: 'IMSI2只能包含数字或+' }
                ]">
                  <a-input v-model:value="formData.imsi2" placeholder="请输入IMSI2（可选）" allow-clear size="middle" :maxlength="15" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="PIN1" name="pin1" :rules="[
                  { required: true, message: '请输入PIN1' },
                  { pattern: /^\d{4,8}$/, message: 'PIN1只能由4至8位数字组成' }
                ]">
                  <a-input v-model:value="formData.pin1" placeholder="请输入PIN1" allow-clear size="middle" />
                </a-form-item>
              </a-col>

              <a-col :span="12">
                <a-form-item label="PIN2" name="pin2" :rules="[
                  { required: true, message: '请输入PIN2' },
                  { pattern: /^\d{4,8}$/, message: 'PIN2只能由4至8位数字组成' }
                ]">
                  <a-input v-model:value="formData.pin2" placeholder="请输入PIN2" allow-clear size="middle" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="PUK1" name="puk1" :rules="[
                  { required: true, message: '请输入PUK1' },
                  { pattern: /^\d{4,8}$/, message: 'PUK1只能由4至8位数字组成' }
                ]">
                  <a-input v-model:value="formData.puk1" placeholder="请输入PUK1" allow-clear size="middle" />
                </a-form-item>
              </a-col>

              <a-col :span="12">
                <a-form-item label="PUK2" name="puk2" :rules="[
                  { required: true, message: '请输入PUK2' },
                  { pattern: /^\d{4,8}$/, message: 'PUK2只能由4至8位数字组成' }
                ]">
                  <a-input v-model:value="formData.puk2" placeholder="请输入PUK2" allow-clear size="middle" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="KI" name="ki" :rules="[
                  { required: true, message: '请输入KI' },
                  { pattern: /^[0-9a-fA-F]{32}$/, message: 'KI只能由32位16进制数组成' }
                ]">
                  <a-input v-model:value="formData.ki" placeholder="请输入KI" allow-clear size="middle" />
                </a-form-item>
              </a-col>

              <a-col :span="12">
                <a-form-item label="OPC" name="opc" :rules="[
                  { required: true, message: '请输入OPC' },
                  { pattern: /^[0-9a-fA-F]{32}$/, message: 'OPC只能由32位16进制数组成' }
                ]">
                  <a-input v-model:value="formData.opc" placeholder="请输入OPC（可选）" allow-clear size="middle" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="SMSP" name="smsp" :rules="[
                  { required: true, message: '请输入SMSP' },
                  { max: 32, message: 'SMSP最长32个字符' }
                ]">
                  <a-input v-model:value="formData.smsp" placeholder="请输入SMSP" allow-clear size="middle" :maxlength="32" />
                </a-form-item>
              </a-col>

              <a-col :span="12">
                <a-form-item label="ADM1" name="adm1" :rules="[
                  { required: true, message: '请输入ADM1' },
                  { max: 32, message: 'ADM1最长32个字符' }
                ]">
                  <a-input v-model:value="formData.adm1" placeholder="请输入ADM1" allow-clear size="middle" :maxlength="32" />
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
            <a-tooltip :title="formData.v3Support.features && formData.v3Support.features.length > 0
              ? formData.v3Support.features.join(', ')
              : '暂无选择'
              " placement="top">
              <a-select v-model:value="formData.v3Support.features" mode="multiple" placeholder="请选择功能" allow-clear
                style="width: 100%" size="middle">
                <a-select-option value="rpmData">RPM数据</a-select-option>
                <a-select-option value="deviceSwitchData">设备切换数据</a-select-option>
                <a-select-option value="enterpriseProfileData">企业Profile数据</a-select-option>
              </a-select>
            </a-tooltip>
          </a-form-item>
        </div>

        <!-- RPM数据配置 -->
        <div class="rpm-config-section"
          v-if="formData.v3Support.features && formData.v3Support.features.includes('rpmData')">
          <a-divider dashed orientation="center">RPM数据配置</a-divider>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="RPM类型" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-tooltip :title="formData.rpmType && formData.rpmType.length > 0
                  ? formData.rpmType.join(', ')
                  : '暂无选择'
                  " placement="top">
                  <a-select v-model:value="formData.v3Support.rpmConfig.rpmType" mode="multiple" placeholder="请选择RPM类型"
                    allow-clear style="width: 100%" :maxTagCount="2"
                    :maxTagPlaceholder="(omittedValues) => `+${omittedValues.length} 更多`" size="middle">
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
                <a-select v-model:value="formData.v3Support.rpmConfig.rpmDownloadMethod" placeholder="请选择RPM下载方式"
                  allow-clear style="width: 100%" size="middle">
                  <a-select-option value="SM-DP+">SM-DP+</a-select-option>
                  <a-select-option value="SM-DS">SM-DS</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>

          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="允许的CA" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-select v-model:value="formData.v3Support.rpmConfig.allowedCA" placeholder="请选择允许的CA" allow-clear
                  style="width: 100%" size="middle">
                  <a-select-option value="CA1">CA1</a-select-option>
                  <a-select-option value="CA2">CA2</a-select-option>
                  <a-select-option value="CA3">CA3</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12" v-if="formData.v3Support.rpmConfig.rpmDownloadMethod">
              <a-form-item label="RPM轮询地址" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-select v-model:value="formData.v3Support.rpmConfig.rpmPollingAddress" placeholder="请选择RPM轮询地址"
                  allow-clear style="width: 100%" size="middle">
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
            <a-tooltip :title="formData.allowedTags && formData.allowedTags.length > 0
              ? formData.allowedTags.join(', ')
              : '暂无选择'
              " placement="top">
              <a-select v-model:value="formData.v3Support.rpmConfig.allowedTags" mode="multiple"
                placeholder="请选择允许的Tags" allow-clear style="width: 100%" :maxTagCount="3"
                :maxTagPlaceholder="(omittedValues) => `+${omittedValues.length} 更多`" size="middle">
                <a-select-option value="Service provider name">Service provider name</a-select-option>
                <a-select-option value="Profile name">Profile name</a-select-option>
                <a-select-option value="Notification Configuration Info">Notification Configuration
                  Info</a-select-option>
                <a-select-option value="Icon type and Icon">Icon type and Icon</a-select-option>
                <a-select-option value="Profile Policy Rules">Profile Policy Rules</a-select-option>
                <a-select-option value="Service Specific Data stored in eUICC">Service Specific Data stored in
                  eUICC</a-select-option>
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
        <div class="device-switch-section" v-if="
          formData.v3Support.features && formData.v3Support.features.includes('deviceSwitchData')
        ">
          <a-divider dashed orientation="center">设备切换数据</a-divider>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="设备切换方式" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-radio-group v-model:value="formData.v3Support.deviceSwitch.deviceSwitchMethod">
                  <a-radio value="requestPlatform">请求平台</a-radio>
                  <a-radio value="useStoredCode">使用存储的激活码</a-radio>
                </a-radio-group>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="允许的CA" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-select v-model:value="formData.v3Support.deviceSwitch.allowedCA" placeholder="请选择允许的CA" allow-clear
                  style="width: 100%" size="middle">
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
                <a-radio-group v-model:value="formData.v3Support.deviceSwitch.needNewEID">
                  <a-radio value="yes">是</a-radio>
                  <a-radio value="no">否</a-radio>
                </a-radio-group>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="是否需要新设备TAC" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-radio-group v-model:value="formData.v3Support.deviceSwitch.needNewTAC">
                  <a-radio value="yes">是</a-radio>
                  <a-radio value="no">否</a-radio>
                </a-radio-group>
              </a-form-item>
            </a-col>
          </a-row>
        </div>

        <!-- 企业Profile数据 -->
        <div class="enterprise-profile-section" v-if="
          formData.v3Support.features &&
          formData.v3Support.features.includes('enterpriseProfileData')
        ">
          <a-divider dashed orientation="center">企业Profile数据</a-divider>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="企业名称" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-select v-model:value="formData.v3Support.enterprise.enterpriseName" placeholder="请选择企业名称" allow-clear
                  style="width: 100%">
                  <a-select-option value="enterprise1">企业1</a-select-option>
                  <a-select-option value="enterprise2">企业2</a-select-option>
                  <a-select-option value="enterprise3">企业3</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12" v-if="
              formData.v3Support.enterprise.enterpriseRules &&
              formData.v3Support.enterprise.enterpriseRules.length > 0
            ">
              <a-form-item label="非企业Profile数量" :label-col="{ span: 12 }" :wrapper-col="{ span: 12 }">
                <a-input-number v-model:value="formData.v3Support.enterprise.nonEnterpriseProfileCount" :min="0"
                  :max="10" placeholder="请输入数量" style="width: 100%" />
              </a-form-item>
            </a-col>
          </a-row>

          <!-- 规则 -->
          <a-form-item label="规则" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
            <a-tooltip :title="formData.enterpriseRules && formData.enterpriseRules.length > 0
              ? formData.enterpriseRules.join(', ')
              : '暂无选择'
              " placement="top">
              <a-select v-model:value="formData.v3Support.enterprise.enterpriseRules" mode="multiple"
                placeholder="请选择规则" allow-clear style="width: 100%">
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
import { defineComponent, ref, reactive, computed, onMounted, getCurrentInstance } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'

export default defineComponent({
  name: 'ProfileForm',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const { proxy } = getCurrentInstance()
    const $http = proxy.$http
    const formRef = ref()

    const pageType = ref('add') // add, edit, view
    const recordId = ref('')

    const formData = reactive({
      iccid: '',
      matchingId: '',
      profileState: '',
      eid: '',
      localProfileState: '',
      downloadMethod: '',
      tenant: '',
      carrier: '',
      profileClass: '',
      pprPolicy: '',
      resetRule: '',
      dsFlag: '',
      confirmLimit: '',
      remark: '',
      // 新增基本信息字段
      confirmationCode: '',
      profileName: '',
      phoneNumber: '',
      serviceProvider: '',
      notificationEvent: undefined,
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
      // V3功能支持字段 - 重构为嵌套结构
      v3Support: {
        // 支持的v3功能列表
        features: [],
        // RPM数据配置
        rpmConfig: {
          rpmType: [],
          rpmDownloadMethod: '',
          rpmPollingAddress: '',
          allowedCA: '',
          allowedTags: []
        },
        // 设备切换数据配置
        deviceSwitch: {
          deviceSwitchMethod: '',
          needNewEID: '',
          needNewTAC: '',
          allowedCA: ''
        },
        // 企业Profile数据配置
        enterprise: {
          enterpriseName: '',
          enterpriseRules: [],
          nonEnterpriseProfileCount: 0
        }
      }
    })

    // 下拉选项数据
    const asnTemplates = ref([]) // ASN模板列表
    const carriers = ref([]) // 运营商列表

    // 获取ASN模板数据
    const fetchAsnTemplates = async () => {
      try {
        const response = await $http.request({
          url: '/personkit/api/esim/tempdate/page',
          method: 'post',
          data: {
            pageNum: 1,
            pageSize: 10,
            query: {
              tempName: '',
              profileType: ''
            }
          }
        })
        // 按照接口实际响应格式处理数据，将id转换为字符串类型以匹配详情接口返回的格式
        asnTemplates.value = response.data.map((item) => ({
          value: String(item.id),
          label: item.tempName
        }))
      } catch (error) {
        console.error('获取ASN模板失败:', error)
        message.error('获取ASN模板失败')
      }
    }

    // 获取运营商数据
    const fetchCarriers = async () => {
      try {
        const response = await $http.request({
          url: '/personkit/api/esim/mon/page',
          method: 'post',
          data: {
            num: 1,
            size: 10,
            query: {
              monName: '',
              monShortName: ''
            }
          }
        })
        // 按照接口实际响应格式处理数据，将id转换为字符串类型以匹配详情接口返回的格式
        carriers.value = response.data.map((item) => ({
          value: String(item.id),
          label: item.monName
        }))
      } catch (error) {
        console.error('获取运营商失败:', error)
        message.error('获取运营商失败')
      }
    }

    // 计算属性：是否为新增模式
    const isAdd = computed(() => pageType.value === 'add')

    const pageTitle = computed(() => {
      switch (pageType.value) {
        case 'add':
          return '新增Profile'
        case 'edit':
          return '编辑Profile'
        case 'view':
          return 'Profile详情'
        default:
          return 'Profile管理'
      }
    })



    // 获取Profile状态对应的颜色
    const getProfileStatusColor = (status) => {
      const colorMap = {
        Available: 'green',
        Allocated: 'blue',
        Linked: 'cyan',
        Confirmed: 'purple',
        Released: 'orange',
        Downloaded: 'geekblue',
        Installed: 'success',
        Error: 'red',
        Unavailable: 'default'
      }
      return colorMap[status] || 'default'
    }

    // 获取通知状态对应的badge状态
    const getNotificationStatusStatus = (status) => {
      const statusMap = {
        Enabled: 'success',
        Disabled: 'default',
        Deleted: 'error'
      }
      return statusMap[status] || 'default'
    }

    // 获取下载方式文本
    const getDownloadMethodText = (method) => {
      const methodMap = {
        1: '标准下载',
        2: '快速下载',
        3: '批量下载'
      }
      return methodMap[method] || method
    }

    // 获取重置规则文本
    const getResetRuleText = (rule) => {
      const ruleMap = {
        1: '允许重置',
        2: '禁止重置',
        3: '条件重置'
      }
      return ruleMap[rule] || rule
    }

    // 获取Profile详情的API函数
    const getProfileDetail = async (id) => {
      try {
        // 调用真实的API接口
        const response = await $http.request({
          url: '/personkit/api/esim/profile/detail',
          method: 'post',
          data: { id: id }
        })
        // 返回响应数据，确保类型一致性
        return response
      } catch (error) {
        console.error('获取Profile详情失败:', error)
        // 抛出错误以便loadDetail函数捕获处理
        throw error
      }
    }

    const loadDetail = async () => {
      if (recordId.value) {
        try {
          const response = await getProfileDetail(recordId.value)
          Object.assign(formData, response)
        } catch (error) {
          message.error('获取详情失败')
        }
      } else if (pageType.value === 'add') {
        // 新增时清空表单数据
        // 重置基本字段
        Object.keys(formData).forEach((key) => {
          if (key !== 'v3Support' && key !== 'notificationEvent') {
            formData[key] = ''
          } else if (key === 'notificationEvent') {
            formData[key] = undefined
          }
        })

        // 重置v3Support及其子字段
        formData.v3Support = {
          features: [],
          rpmConfig: {
            rpmType: [],
            rpmDownloadMethod: '',
            rpmPollingAddress: '',
            allowedCA: '',
            allowedTags: []
          },
          deviceSwitch: {
            deviceSwitchMethod: '',
            needNewEID: '',
            needNewTAC: '',
            allowedCA: ''
          },
          enterprise: {
            enterpriseName: '',
            enterpriseRules: [],
            nonEnterpriseProfileCount: 0
          }
        }
      }
    }

    const handleSubmit = async () => {
      try {
        // 表单验证
        await formRef.value.validate().catch((errorInfo) => {
          // 显示验证失败的详细信息
          console.error('表单验证失败:', errorInfo)
          const firstErrorField = errorInfo.errorFields[0]
          if (firstErrorField) {
            message.error(`${firstErrorField.errors[0]}`)
          }
          throw new Error('表单验证失败')
        })

        // 根据是新增还是编辑调用不同的接口
        if (isAdd.value) {
          // 新增 - 调用保存接口
          await $http.request({
            url: '/personkit/api/esim/profile/save',
            method: 'post',
            data: formData
          })
          message.success('保存成功')
          router.push('/view/profile_manage')
        } else {
          // 编辑 - 调用更新接口
          await $http.request({
            url: '/personkit/api/esim/profile/update',
            method: 'post',
            data: {
              ...formData,
              id: recordId.value
            }
          })
          message.success('更新成功')
          router.push('/view/profile_manage')
        }
      } catch (error) {
        console.error('表单提交失败:', error)
        if (error.message !== '表单验证失败') {
          message.error('操作失败')
        }
      }
    }

    const handleCancel = () => {
      router.push('/view/profile_manage')
    }



    onMounted(() => {
      const { type, id } = route.query
      // 移除详情模式，所有情况都使用编辑模式
      pageType.value = (type === 'view') ? 'edit' : (type || 'add')
      recordId.value = id || ''
      // 加载下拉数据
      fetchAsnTemplates()
      fetchCarriers()
      // 加载详情数据
      loadDetail()
    })

    return {
      formRef,
      formData,
      pageTitle,
      isAdd,
      asnTemplates,
      carriers,
      getProfileStatusColor,
      getNotificationStatusStatus,
      getDownloadMethodText,
      getResetRuleText,
      handleSubmit,
      handleCancel
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
  overflow-x: hidden;
  /* 防止水平滚动 */
}

/* 左侧区域 */
.left-section {
  flex: 3;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  min-width: 0;
  overflow: visible;
  max-height: calc(100vh - 124px);
  /* 添加最大高度限制 */
  overflow-y: auto;
  /* 允许垂直滚动 */
}

/* 基本信息编辑区域 */
.basic-info-section {
  padding: 8px;
  /* 减少内边距 */
  overflow: visible;
  padding-bottom: 15px;
  /* 减少底部内边距 */
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  /* 减少底部间距 */
  padding-bottom: 4px;
  /* 减少底部内边距 */
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
  padding-bottom: 10px;
  /* 减少底部内边距 */
  background: #fff;
  /* 默认白色背景 */
  border: 1px solid #e8e8e8;
  border-radius: 6px;
}

/* V3功能支持 */
.v3-features-section {
  padding: 8px;
  /* 减少内边距 */
  margin-bottom: 0;
}

.rpm-config-section,
.device-switch-section,
.enterprise-profile-section {
  padding: 8px;
  /* 减少内边距 */
  margin-bottom: 0;
  overflow: visible;
  min-height: auto;
  /* 移除最小高度限制 */
}

.enterprise-profile-section {
  margin-bottom: 10px;
  /* 减少底部间距 */
  border-bottom: none;
  /* 最后一个section不需要底部边框 */
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
  margin-bottom: 4px;
  /* 减少表单项目间距 */
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
  margin: 4px 0;
  /* 减少分割线间距 */
  font-size: 13px;
  font-weight: 500;
}

/* 确保表单区域有足够的间距 */
:deep(.ant-form-item-control) {
  min-height: 32px;
  /* 确保控件有足够高度 */
}

:deep(.ant-select-multiple .ant-select-selector) {
  min-height: 32px;
  /* 多选框最小高度 */
  padding: 2px 4px;
}

:deep(.ant-divider-dashed) {
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
    max-height: none;
    /* 在小屏幕上移除高度限制 */
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
