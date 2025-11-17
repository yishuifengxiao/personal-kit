<template>
  <div class="tool-box-page">
    <!-- 页面标题和操作栏 -->
    <div class="page-header">
      <div class="header-title">
        <h2>工具箱</h2>
        <span class="subtitle">集成常用在线开发工具</span>
      </div>
      <div class="header-actions">
        <a-select
          v-model:value="selectedTool"
          style="width: 200px"
          placeholder="选择工具类型"
          @change="handleToolChange"
        >
          <a-select-option value="properties-yaml">Properties ↔ YAML</a-select-option>
          <a-select-option value="json-format">JSON 格式化</a-select-option>
          <a-select-option value="base64">Base64 编码/解码</a-select-option>
          <a-select-option value="url-encode">URL 编码/解码</a-select-option>
          <a-select-option value="timestamp">时间戳转换</a-select-option>
          <a-select-option value="uuid">UUID 生成器</a-select-option>
        </a-select>
      </div>
    </div>

    <!-- 工具内容区域 -->
    <div class="tool-content">
      <!-- Properties ↔ YAML 转换工具 -->
      <div v-if="selectedTool === 'properties-yaml'" class="tool-panel">
        <div class="tool-title">
          <h3>Properties ↔ YAML 互转工具</h3>
          <div class="tool-actions">
            <a-button type="primary" @click="convertPropertiesYaml">
              <template #icon><SwapOutlined /></template>
              转换
            </a-button>
            <a-button @click="clearPropertiesYaml">
              <template #icon><ClearOutlined /></template>
              清空
            </a-button>
          </div>
        </div>
        <div class="converter-container">
          <div class="input-panel">
            <div class="panel-header">
              <span>输入</span>
              <a-radio-group v-model:value="propertiesYamlDirection" size="small">
                <a-radio-button value="properties-to-yaml">Properties → YAML</a-radio-button>
                <a-radio-button value="yaml-to-properties">YAML → Properties</a-radio-button>
              </a-radio-group>
            </div>
            <a-textarea
              v-model:value="propertiesYamlInput"
              placeholder="请输入需要转换的内容..."
              :rows="20"
              class="code-textarea"
            />
          </div>
          <div class="output-panel">
            <div class="panel-header">
              <span>输出</span>
              <a-button type="link" size="small" @click="copyToClipboard(propertiesYamlOutput)">
                <template #icon><CopyOutlined /></template>
                复制
              </a-button>
            </div>
            <a-textarea
              v-model:value="propertiesYamlOutput"
              placeholder="转换结果将显示在这里..."
              :rows="20"
              readonly
              class="code-textarea"
            />
          </div>
        </div>
      </div>

      <!-- JSON 格式化工具 -->
      <div v-else-if="selectedTool === 'json-format'" class="tool-panel">
        <div class="tool-title">
          <h3>JSON 格式化工具</h3>
          <div class="tool-actions">
            <a-button type="primary" @click="formatJson">
              <template #icon><FormatPainterOutlined /></template>
              格式化
            </a-button>
            <a-button @click="minifyJson">
              <template #icon><CompressOutlined /></template>
              压缩
            </a-button>
            <a-button @click="clearJson">
              <template #icon><ClearOutlined /></template>
              清空
            </a-button>
          </div>
        </div>
        <div class="json-container">
          <div class="input-panel">
            <div class="panel-header">
              <span>输入 JSON</span>
            </div>
            <a-textarea
              v-model:value="jsonInput"
              placeholder="请输入 JSON 字符串..."
              :rows="20"
              class="code-textarea"
            />
          </div>
          <div class="output-panel">
            <div class="panel-header">
              <span>格式化结果</span>
              <a-button type="link" size="small" @click="copyToClipboard(jsonOutput)">
                <template #icon><CopyOutlined /></template>
                复制
              </a-button>
            </div>
            <pre class="json-output">{{ jsonOutput }}</pre>
          </div>
        </div>
      </div>

      <!-- Base64 编码/解码工具 -->
      <div v-else-if="selectedTool === 'base64'" class="tool-panel">
        <div class="tool-title">
          <h3>Base64 编码/解码工具</h3>
          <div class="tool-actions">
            <a-button type="primary" @click="encodeBase64">
              <template #icon><LockOutlined /></template>
              编码
            </a-button>
            <a-button type="primary" @click="decodeBase64">
              <template #icon><UnlockOutlined /></template>
              解码
            </a-button>
            <a-button @click="clearBase64">
              <template #icon><ClearOutlined /></template>
              清空
            </a-button>
          </div>
        </div>
        <div class="base64-container">
          <div class="input-panel">
            <div class="panel-header">
              <span>输入</span>
            </div>
            <a-textarea
              v-model:value="base64Input"
              placeholder="请输入需要编码或解码的内容..."
              :rows="15"
              class="code-textarea"
            />
          </div>
          <div class="output-panel">
            <div class="panel-header">
              <span>输出</span>
              <a-button type="link" size="small" @click="copyToClipboard(base64Output)">
                <template #icon><CopyOutlined /></template>
                复制
              </a-button>
            </div>
            <a-textarea
              v-model:value="base64Output"
              placeholder="结果将显示在这里..."
              :rows="15"
              readonly
              class="code-textarea"
            />
          </div>
        </div>
      </div>

      <!-- URL 编码/解码工具 -->
      <div v-else-if="selectedTool === 'url-encode'" class="tool-panel">
        <div class="tool-title">
          <h3>URL 编码/解码工具</h3>
          <div class="tool-actions">
            <a-button type="primary" @click="encodeUrl">
              <template #icon="icon"><span style="font-weight: bold;">{{ icon }}</span></template>
              编码
            </a-button>
            <a-button type="primary" @click="decodeUrl">
              <template #icon="icon"><span style="font-weight: bold;">{{ icon }}</span></template>
              解码
            </a-button>
            <a-button @click="clearUrl">
              <template #icon><ClearOutlined /></template>
              清空
            </a-button>
          </div>
        </div>
        <div class="url-container">
          <div class="input-panel">
            <div class="panel-header">
              <span>输入</span>
            </div>
            <a-textarea
              v-model:value="urlInput"
              placeholder="请输入需要编码或解码的内容..."
              :rows="15"
              class="code-textarea"
            />
          </div>
          <div class="output-panel">
            <div class="panel-header">
              <span>输出</span>
              <a-button type="link" size="small" @click="copyToClipboard(urlOutput)">
                <template #icon><CopyOutlined /></template>
                复制
              </a-button>
            </div>
            <a-textarea
              v-model:value="urlOutput"
              placeholder="结果将显示在这里..."
              :rows="15"
              readonly
              class="code-textarea"
            />
          </div>
        </div>
      </div>

      <!-- 时间戳转换工具 -->
      <div v-else-if="selectedTool === 'timestamp'" class="tool-panel">
        <div class="tool-title">
          <h3>时间戳转换工具</h3>
        </div>
        <div class="timestamp-container">
          <a-row :gutter="24">
            <a-col :span="12">
              <div class="timestamp-panel">
                <div class="panel-header">
                  <span>当前时间戳</span>
                </div>
                <div class="timestamp-display">
                  <div class="timestamp-item">
                    <label>秒级时间戳:</label>
                    <div class="timestamp-value">{{ currentTimestamp.s }}</div>
                    <a-button type="link" size="small" @click="copyToClipboard(currentTimestamp.s)">
                      <template #icon><CopyOutlined /></template>
                    </a-button>
                  </div>
                  <div class="timestamp-item">
                    <label>毫秒级时间戳:</label>
                    <div class="timestamp-value">{{ currentTimestamp.ms }}</div>
                    <a-button type="link" size="small" @click="copyToClipboard(currentTimestamp.ms)">
                      <template #icon><CopyOutlined /></template>
                    </a-button>
                  </div>
                </div>
              </div>
            </a-col>
            <a-col :span="12">
              <div class="timestamp-panel">
                <div class="panel-header">
                  <span>时间戳转日期</span>
                </div>
                <div class="timestamp-converter">
                  <a-input
                    v-model:value="timestampToConvert"
                    placeholder="请输入时间戳"
                    style="margin-bottom: 12px"
                  />
                  <a-button type="primary" @click="convertTimestamp" style="width: 100%">
                    转换
                  </a-button>
                  <div v-if="convertedDateTime" class="converted-result">
                    <label>转换结果:</label>
                    <div class="datetime-value">{{ convertedDateTime }}</div>
                    <a-button type="link" size="small" @click="copyToClipboard(convertedDateTime)">
                      <template #icon><CopyOutlined /></template>
                    </a-button>
                  </div>
                </div>
              </div>
            </a-col>
          </a-row>
        </div>
      </div>

      <!-- UUID 生成器 -->
      <div v-else-if="selectedTool === 'uuid'" class="tool-panel">
        <div class="tool-title">
          <h3>UUID 生成器</h3>
          <div class="tool-actions">
            <a-button type="primary" @click="generateUUID">
              <template #icon><ThunderboltOutlined /></template>
              生成 UUID
            </a-button>
            <a-button @click="clearUUID">
              <template #icon><ClearOutlined /></template>
              清空
            </a-button>
          </div>
        </div>
        <div class="uuid-container">
          <div class="uuid-options">
            <a-radio-group v-model:value="uuidVersion" size="small">
              <a-radio value="v4">UUID v4</a-radio>
              <a-radio value="v1">UUID v1</a-radio>
            </a-radio-group>
            <a-input-number
              v-model:value="uuidCount"
              :min="1"
              :max="10"
              size="small"
              style="width: 80px; margin-left: 16px"
            />
            <span style="margin-left: 8px; color: #666">个</span>
          </div>
          <div class="uuid-results">
            <div v-for="(uuid, index) in generatedUUIDs" :key="index" class="uuid-item">
              <span class="uuid-text">{{ uuid }}</span>
              <a-button type="link" size="small" @click="copyToClipboard(uuid)">
                <template #icon><CopyOutlined /></template>
              </a-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 默认提示 -->
      <div v-else class="tool-placeholder">
        <a-empty description="请选择一个工具开始操作">
          <template #image>
            <ToolOutlined style="font-size: 64px; color: #1890ff" />
          </template>
        </a-empty>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, onMounted, onUnmounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  SwapOutlined,
  ClearOutlined,
  CopyOutlined,
  FormatPainterOutlined,
  CompressOutlined,
  LockOutlined,
  UnlockOutlined,
  ThunderboltOutlined,
  ToolOutlined
} from '@ant-design/icons-vue'

export default defineComponent({
  name: 'ToolBox',
  components: {
    SwapOutlined,
    ClearOutlined,
    CopyOutlined,
    FormatPainterOutlined,
    CompressOutlined,
    LockOutlined,
    UnlockOutlined,
    ThunderboltOutlined,
    ToolOutlined
  },
  setup() {
    // 工具选择
    const selectedTool = ref('')
    
    // Properties ↔ YAML 转换
    const propertiesYamlInput = ref('')
    const propertiesYamlOutput = ref('')
    const propertiesYamlDirection = ref('properties-to-yaml')
    
    // JSON 格式化
    const jsonInput = ref('')
    const jsonOutput = ref('')
    
    // Base64 编码/解码
    const base64Input = ref('')
    const base64Output = ref('')
    
    // URL 编码/解码
    const urlInput = ref('')
    const urlOutput = ref('')
    
    // 时间戳转换
    const currentTimestamp = reactive({
      s: '',
      ms: ''
    })
    const timestampToConvert = ref('')
    const convertedDateTime = ref('')
    let timestampTimer = null
    
    // UUID 生成器
    const uuidVersion = ref('v4')
    const uuidCount = ref(1)
    const generatedUUIDs = ref([])

    // 工具切换
    const handleToolChange = (value) => {
      selectedTool.value = value
      // 可以在这里添加工具切换时的初始化逻辑
    }

    // Properties ↔ YAML 转换
    const convertPropertiesYaml = () => {
      if (!propertiesYamlInput.value.trim()) {
        message.warning('请输入需要转换的内容')
        return
      }

      try {
        if (propertiesYamlDirection.value === 'properties-to-yaml') {
          propertiesYamlOutput.value = convertPropertiesToYaml(propertiesYamlInput.value)
        } else {
          propertiesYamlOutput.value = convertYamlToProperties(propertiesYamlInput.value)
        }
        message.success('转换成功')
      } catch (error) {
        message.error('转换失败: ' + error.message)
      }
    }

    const clearPropertiesYaml = () => {
      propertiesYamlInput.value = ''
      propertiesYamlOutput.value = ''
    }

    // JSON 格式化
    const formatJson = () => {
      if (!jsonInput.value.trim()) {
        message.warning('请输入 JSON 字符串')
        return
      }

      try {
        const parsed = JSON.parse(jsonInput.value)
        jsonOutput.value = JSON.stringify(parsed, null, 2)
        message.success('格式化成功')
      } catch (error) {
        message.error('JSON 格式错误: ' + error.message)
      }
    }

    const minifyJson = () => {
      if (!jsonInput.value.trim()) {
        message.warning('请输入 JSON 字符串')
        return
      }

      try {
        const parsed = JSON.parse(jsonInput.value)
        jsonOutput.value = JSON.stringify(parsed)
        message.success('压缩成功')
      } catch (error) {
        message.error('JSON 格式错误: ' + error.message)
      }
    }

    const clearJson = () => {
      jsonInput.value = ''
      jsonOutput.value = ''
    }

    // Base64 编码/解码
    const encodeBase64 = () => {
      if (!base64Input.value.trim()) {
        message.warning('请输入需要编码的内容')
        return
      }

      try {
        base64Output.value = btoa(unescape(encodeURIComponent(base64Input.value)))
        message.success('编码成功')
      } catch (error) {
        message.error('编码失败: ' + error.message)
      }
    }

    const decodeBase64 = () => {
      if (!base64Input.value.trim()) {
        message.warning('请输入需要解码的内容')
        return
      }

      try {
        base64Output.value = decodeURIComponent(escape(atob(base64Input.value)))
        message.success('解码成功')
      } catch (error) {
        message.error('解码失败: ' + error.message)
      }
    }

    const clearBase64 = () => {
      base64Input.value = ''
      base64Output.value = ''
    }

    // URL 编码/解码
    const encodeUrl = () => {
      if (!urlInput.value.trim()) {
        message.warning('请输入需要编码的内容')
        return
      }

      try {
        urlOutput.value = encodeURIComponent(urlInput.value)
        message.success('编码成功')
      } catch (error) {
        message.error('编码失败: ' + error.message)
      }
    }

    const decodeUrl = () => {
      if (!urlInput.value.trim()) {
        message.warning('请输入需要解码的内容')
        return
      }

      try {
        urlOutput.value = decodeURIComponent(urlInput.value)
        message.success('解码成功')
      } catch (error) {
        message.error('解码失败: ' + error.message)
      }
    }

    const clearUrl = () => {
      urlInput.value = ''
      urlOutput.value = ''
    }

    // 时间戳转换
    const updateCurrentTimestamp = () => {
      const now = Date.now()
      currentTimestamp.s = Math.floor(now / 1000).toString()
      currentTimestamp.ms = now.toString()
    }

    const convertTimestamp = () => {
      if (!timestampToConvert.value.trim()) {
        message.warning('请输入时间戳')
        return
      }

      try {
        const timestamp = parseInt(timestampToConvert.value)
        if (isNaN(timestamp)) {
          throw new Error('无效的时间戳')
        }

        // 判断是秒级还是毫秒级时间戳
        const date = timestamp > 9999999999 ? new Date(timestamp) : new Date(timestamp * 1000)
        convertedDateTime.value = date.toLocaleString('zh-CN')
      } catch (error) {
        message.error('转换失败: ' + error.message)
      }
    }

    // UUID 生成器
    const generateUUID = () => {
      const uuids = []
      for (let i = 0; i < uuidCount.value; i++) {
        if (uuidVersion.value === 'v4') {
          uuids.push(generateUUIDv4())
        } else {
          uuids.push(generateUUIDv1())
        }
      }
      generatedUUIDs.value = uuids
      message.success(`已生成 ${uuids.length} 个 UUID`)
    }

    const generateUUIDv4 = () => {
      return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0
        const v = c === 'x' ? r : (r & 0x3 | 0x8)
        return v.toString(16)
      })
    }

    const generateUUIDv1 = () => {
      // 简化的 UUID v1 实现
      const timestamp = Date.now()
      const clockId = Math.floor(Math.random() * 0x3fff) + 0x8000
      const nodeId = Math.floor(Math.random() * 0xffffff) + 0x100000
      
      return `${(timestamp >>> 32).toString(16).padStart(8, '0')}-${
        (timestamp >>> 16 & 0xffff).toString(16).padStart(4, '0')}-${
        (timestamp & 0x0fff | 0x1000).toString(16).padStart(4, '0')}-${
        clockId.toString(16).padStart(4, '0')}-${
        nodeId.toString(16).padStart(12, '0')}`
    }

    const clearUUID = () => {
      generatedUUIDs.value = []
    }

    // 通用方法
    const copyToClipboard = (text) => {
      if (!text) {
        message.warning('没有可复制的内容')
        return
      }

      navigator.clipboard.writeText(text).then(() => {
        message.success('已复制到剪贴板')
      }).catch(() => {
        // 降级方案
        const textarea = document.createElement('textarea')
        textarea.value = text
        document.body.appendChild(textarea)
        textarea.select()
        document.execCommand('copy')
        document.body.removeChild(textarea)
        message.success('已复制到剪贴板')
      })
    }

    // 辅助方法
    const convertPropertiesToYaml = (properties) => {
      const lines = properties.split('\n').filter(line => line.trim() && !line.startsWith('#'))
      const result = {}
      
      lines.forEach(line => {
        const equalIndex = line.indexOf('=')
        if (equalIndex > -1) {
          const key = line.substring(0, equalIndex).trim()
          const value = line.substring(equalIndex + 1).trim()
          setNestedValue(result, key.split('.'), value)
        }
      })
      
      return convertObjectToYaml(result)
    }

    const convertYamlToProperties = (yaml) => {
      // 简化的 YAML 转 Properties 实现
      const lines = yaml.split('\n')
      const result = []
      let currentPath = []
      
      lines.forEach(line => {
        const trimmed = line.trim()
        if (!trimmed || trimmed.startsWith('#')) return
        
        const indent = line.search(/\S/)
        const content = trimmed
        
        if (content.includes(':')) {
          const [key, value] = content.split(':')
          const cleanKey = key.trim()
          const cleanValue = value ? value.trim() : ''
          
          // 调整当前路径
          currentPath = currentPath.slice(0, Math.floor(indent / 2))
          currentPath.push(cleanKey)
          
          if (cleanValue && !cleanValue.startsWith('{') && !cleanValue.startsWith('[')) {
            result.push(`${currentPath.join('.')}=${cleanValue}`)
          }
        }
      })
      
      return result.join('\n')
    }

    const setNestedValue = (obj, keys, value) => {
      let current = obj
      for (let i = 0; i < keys.length - 1; i++) {
        if (!current[keys[i]]) {
          current[keys[i]] = {}
        }
        current = current[keys[i]]
      }
      current[keys[keys.length - 1]] = value
    }

    const convertObjectToYaml = (obj, indent = 0) => {
      let result = ''
      const spaces = '  '.repeat(indent)
      
      for (const [key, value] of Object.entries(obj)) {
        if (typeof value === 'object' && value !== null) {
          result += `${spaces}${key}:\n`
          result += convertObjectToYaml(value, indent + 1)
        } else {
          result += `${spaces}${key}: ${value}\n`
        }
      }
      
      return result
    }

    // 生命周期
    onMounted(() => {
      updateCurrentTimestamp()
      timestampTimer = setInterval(updateCurrentTimestamp, 1000)
    })

    onUnmounted(() => {
      if (timestampTimer) {
        clearInterval(timestampTimer)
      }
    })

    return {
      // 数据
      selectedTool,
      propertiesYamlInput,
      propertiesYamlOutput,
      propertiesYamlDirection,
      jsonInput,
      jsonOutput,
      base64Input,
      base64Output,
      urlInput,
      urlOutput,
      currentTimestamp,
      timestampToConvert,
      convertedDateTime,
      uuidVersion,
      uuidCount,
      generatedUUIDs,
      
      // 方法
      handleToolChange,
      convertPropertiesYaml,
      clearPropertiesYaml,
      formatJson,
      minifyJson,
      clearJson,
      encodeBase64,
      decodeBase64,
      clearBase64,
      encodeUrl,
      decodeUrl,
      clearUrl,
      convertTimestamp,
      generateUUID,
      clearUUID,
      copyToClipboard
    }
  }
})
</script>

<style scoped>
.tool-box-page {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 120px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header-title h2 {
  margin: 0;
  color: #262626;
  font-size: 20px;
  font-weight: 500;
}

.subtitle {
  color: #8c8c8c;
  font-size: 14px;
  margin-left: 8px;
}

.tool-content {
  background: #fafafa;
  border-radius: 8px;
}

.tool-panel {
  background: #fff;
  border-radius: 6px;
  padding: 20px;
}

.tool-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.tool-title h3 {
  margin: 0;
  color: #262626;
  font-size: 16px;
  font-weight: 500;
}

.tool-actions {
  display: flex;
  gap: 12px;
}

.converter-container,
.json-container,
.base64-container,
.url-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.input-panel,
.output-panel {
  background: #f9f9f9;
  border-radius: 6px;
  padding: 16px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  color: #595959;
  font-weight: 500;
}

.code-textarea {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.5;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

.json-output {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.5;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 8px 12px;
  margin: 0;
  min-height: 400px;
  max-height: 400px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

.timestamp-container {
  padding: 20px;
}

.timestamp-panel {
  background: #f9f9f9;
  border-radius: 6px;
  padding: 20px;
}

.timestamp-display {
  margin-top: 16px;
}

.timestamp-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
}

.timestamp-item label {
  width: 100px;
  color: #595959;
  font-weight: 500;
}

.timestamp-value {
  flex: 1;
  background: #fff;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-family: monospace;
  font-size: 14px;
}

.timestamp-converter {
  margin-top: 16px;
}

.converted-result {
  margin-top: 16px;
  padding: 12px;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

.converted-result label {
  display: block;
  margin-bottom: 8px;
  color: #595959;
  font-weight: 500;
}

.datetime-value {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-family: monospace;
  font-size: 14px;
  color: #262626;
}

.uuid-container {
  padding: 20px;
}

.uuid-options {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.uuid-results {
  background: #f9f9f9;
  border-radius: 6px;
  padding: 16px;
}

.uuid-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  padding: 8px 12px;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

.uuid-text {
  font-family: monospace;
  font-size: 14px;
  color: #262626;
}

.tool-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

:deep(.ant-btn) {
  border-radius: 4px;
}

:deep(.ant-input),
:deep(.ant-input-number),
:deep(.ant-select-selector) {
  border-radius: 4px;
}

:deep(.ant-radio-button-wrapper) {
  border-radius: 4px;
}
</style>