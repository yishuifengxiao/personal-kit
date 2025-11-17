<template>
  <div class="detail-page">
    <a-page-header
      class="compact-header"
      :title="pageTitle"
      @back="goBack"
    />
    
    <!-- 中部：基本信息 - 移动到头部和数据标签之间 -->
    <div class="info-section-header">
      <div class="info-content">
        <div class="info-item">
          <span class="info-label">标题：</span>
          <span v-if="!isEditMode" class="info-value">{{ record.title }}</span>
          <a-input v-else v-model:value="record.title" placeholder="请输入标题" style="width: 300px" />
        </div>
        <div class="info-item">
          <span class="info-label">数据来源：</span>
          <a-tag v-if="!isEditMode" :color="getDataSourceColor(record.dataSource)" size="small">
            {{ getDataSourceLabel(record.dataSource) }}
          </a-tag>
          <a-select v-else v-model:value="record.dataSource" style="width: 120px">
            <a-select-option value="api-import">API导入</a-select-option>
            <a-select-option value="manual">手动创建</a-select-option>
            <a-select-option value="third-party">第三方链接</a-select-option>
            <a-select-option value="crawler">数据爬虫</a-select-option>
          </a-select>
        </div>
        <div class="info-item">
          <span class="info-label">数据状态：</span>
          <a-tag v-if="!isEditMode" :color="getDataStatusColor(record.dataStatus)" size="small">
            {{ getDataStatusText(record.dataStatus) }}
          </a-tag>
          <a-select v-else v-model:value="record.dataStatus" style="width: 120px">
            <a-select-option value="published">已发布</a-select-option>
            <a-select-option value="unpublished">未发布</a-select-option>
            <a-select-option value="deleted">已删除</a-select-option>
            <a-select-option value="abnormal">异常</a-select-option>
          </a-select>
        </div>
        <div class="info-item">
          <span class="info-label">同步状态：</span>
          <a-badge v-if="!isEditMode" :status="getSyncStatusStatus(record.syncStatus)" :text="getSyncStatusLabel(record.syncStatus)" />
          <a-select v-else v-model:value="record.syncStatus" style="width: 120px">
            <a-select-option value="normal">正常</a-select-option>
            <a-select-option value="abnormal">异常</a-select-option>
            <a-select-option value="not-synced">未同步</a-select-option>
            <a-select-option value="syncing">同步中</a-select-option>
          </a-select>
        </div>

      </div>
    </div>
    
    <div class="detail-container">
      <!-- 左侧区域 -->
      <div class="left-section">
        <!-- 上部：数据标签 -->
        <div class="tags-section">
          <div class="tags-header">
            <h3>数据标签</h3>
            <div class="action-buttons">
              <a-button v-if="mode === 'detail'" type="primary" size="small" @click="switchToEditMode">编辑</a-button>
              <a-button v-if="isEditMode" type="primary" size="small" @click="handleSave">保存</a-button>
              <a-button v-if="isEditMode" size="small" @click="handleCancel">取消</a-button>
              <a-button v-if="mode === 'detail'" size="small" @click="handleSync">同步</a-button>
              <a-button v-if="mode === 'detail'" size="small" @click="showModifyTagsModal">标签</a-button>
              <a-button v-if="mode === 'detail'" size="small" @click="showModifyStatusModal">状态</a-button>
              <a-button v-if="mode === 'detail'" size="small" danger @click="handleDelete">删除</a-button>
              <a-button 
                v-if="mode === 'detail' && record.dataStatus === 'published'" 
                size="small" 
                @click="handleUnpublish"
              >
                下架
              </a-button>
              <a-button 
                v-if="mode === 'detail' && record.dataStatus !== 'published'" 
                type="primary" 
                size="small" 
                @click="handlePublish"
              >
                发布
              </a-button>
            </div>
          </div>
          <div class="tags-container">
            <a-tag
              v-if="!isEditMode"
              v-for="(tag, index) in record.tags"
              :key="index"
              :color="getTagColor(tag)"
              class="tag-item"
            >
              {{ tag }}
            </a-tag>
            <div v-else class="edit-tags-container">
              <a-tag
                v-for="(tag, index) in record.tags"
                :key="index"
                :color="getTagColor(tag)"
                closable
                @close="removeTagFromRecord(index)"
                class="tag-item"
              >
                {{ tag }}
              </a-tag>
              <a-input
                v-if="inputVisible"
                ref="inputRef"
                v-model:value="inputValue"
                type="text"
                size="small"
                style="width: 78px"
                @blur="handleInputConfirm"
                @keyup.enter="handleInputConfirm"
              />
              <a-tag v-else style="background: #fff; borderStyle: dashed;" @click="showInput">
                <plus-outlined />
                新标签
              </a-tag>
            </div>
          </div>
        </div>
        
        <!-- 中部：网页信息摘要区域（替换原基本信息位置） -->
        <div class="web-summary-section">
          <h3>网页信息摘要</h3>
          <div class="summary-content">
            <div class="summary-item">
              <span class="summary-label">网页标题：</span>
              <span v-if="!isEditMode" class="summary-value">{{ record.title }}</span>
              <a-input v-else v-model:value="record.title" placeholder="请输入网页标题" />
            </div>
            <div class="summary-item">
              <span class="summary-label">网页URL：</span>
              <a v-if="!isEditMode" :href="record.url" target="_blank" class="summary-link">{{ record.url }}</a>
              <a-input v-else v-model:value="record.url" placeholder="请输入网页URL" />
            </div>
            <div class="summary-item">
              <span class="summary-label">描述：</span>
              <span v-if="!isEditMode" class="summary-value">{{ record.description || '暂无描述信息' }}</span>
              <a-textarea v-else v-model:value="record.description" placeholder="请输入描述信息" :rows="3" />
            </div>
            <div class="summary-item">
              <span class="summary-label">关键词：</span>
              <span v-if="!isEditMode" class="summary-value">{{ record.keywords || '暂无关键词' }}</span>
              <a-input v-else v-model:value="record.keywords" placeholder="请输入关键词，用逗号分隔" />
            </div>
            <div class="summary-item">
              <span class="summary-label">摘要：</span>
              <span v-if="!isEditMode" class="summary-value">{{ record.summary || '暂无摘要信息' }}</span>
              <a-textarea v-else v-model:value="record.summary" placeholder="请输入摘要信息" :rows="4" />
            </div>
          </div>
        </div>
        
        <!-- 下部：数据源网页内容 -->
        <div class="web-content-section">
          <h3>数据源内容</h3>
          <div class="web-content-container">
            <div v-if="isEditMode" style="margin-bottom: 12px;">
              <a-textarea 
                v-model:value="record.content" 
                placeholder="请输入数据源内容" 
                :rows="20"
                style="width: 100%; border: 1px solid #d9d9d9; border-radius: 4px;"
              />
            </div>
            <div v-else>
              <iframe 
                v-if="record.url" 
                :src="record.url" 
                frameborder="0"
                style="width: 100%; height: 600px; border: 1px solid #d9d9d9; border-radius: 4px;"
                sandbox="allow-same-origin allow-scripts allow-popups allow-forms"
              ></iframe>
              <a-empty v-else description="暂无网页内容" />
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧区域 -->
      <div class="right-section">
        <!-- 上部：数据图谱 -->
        <div class="graph-section">
          <h3>数据图谱</h3>
          <div class="graph-placeholder">
            <a-empty v-if="!isEditMode" description="数据图谱区域" />
            <div v-else class="edit-graph-section">
              <a-textarea 
                v-model:value="record.graphData" 
                placeholder="请输入数据图谱信息（JSON格式）"
                :rows="10"
                style="width: 100%; margin-bottom: 8px;"
              />
              <a-button size="small" @click="generateGraphData">生成图谱</a-button>
            </div>
          </div>
        </div>
        
        <!-- 下部：脉络区域 -->
        <div class="timeline-section">
          <h3>数据脉络</h3>
          <div class="timeline-placeholder">
            <a-timeline v-if="!isEditMode">
              <a-timeline-item v-for="(event, index) in timelineEvents" :key="index" :color="event.color">
                <div class="timeline-content">
                  <div class="timeline-title">
                    <p style="font-weight: 500; margin: 0;">{{ event.title }}</p>
                    <p style="margin: 4px 0; color: #666;">{{ event.time ? event.time.format('YYYY-MM-DD HH:mm:ss') : event.defaultTime }}</p>
                  </div>
                  <div v-if="event.url || event.description" class="timeline-details" style="margin-top: 8px;">
                    <div v-if="event.url" class="timeline-url">
                      <a :href="event.url" target="_blank" rel="noopener noreferrer" style="font-size: 12px;">
                        {{ event.url }}
                      </a>
                    </div>
                    <div v-if="event.description" class="timeline-description" style="font-size: 12px; color: #666; margin-top: 4px;">
                      {{ event.description }}
                    </div>
                  </div>
                </div>
              </a-timeline-item>
            </a-timeline>
            <div v-else class="edit-timeline-section">
              <div v-for="(event, index) in timelineEvents" :key="index" class="timeline-edit-card">
                <div v-if="event.isCustom" class="custom-event-editor">
                  <div class="event-form-item">
                    <span class="required-label">标题：</span>
                    <a-input 
                      v-model:value="record.timelineEvents[event.index].title" 
                      placeholder="请输入时间点标题"
                      style="flex: 1;"
                    />
                  </div>
                  <div class="event-form-item">
                    <span class="timeline-label">时间：</span>
                    <a-date-picker 
                      v-model:value="record.timelineEvents[event.index].time" 
                      show-time 
                      placeholder="选择时间"
                      style="flex: 1;"
                    />
                  </div>
                  <div class="event-form-item">
                    <span class="timeline-label">URL：</span>
                    <a-input 
                      v-model:value="record.timelineEvents[event.index].url" 
                      placeholder="请输入URL（可选）"
                      style="flex: 1;"
                    />
                  </div>
                  <div class="event-form-item">
                    <span class="timeline-label">描述：</span>
                    <a-textarea 
                      v-model:value="record.timelineEvents[event.index].description" 
                      placeholder="请输入描述（可选）"
                      :rows="2"
                      style="flex: 1;"
                    />
                  </div>
                  <div class="event-actions">
                    <a-button size="small" danger @click="removeTimelineEvent(event.index)">
                      删除时间点
                    </a-button>
                  </div>
                </div>
                <div v-else class="builtin-event-editor">
                  <div class="event-form-item">
                    <span class="timeline-label">{{ event.title }}：</span>
                    <a-date-picker 
                      v-model:value="record[event.field]" 
                      show-time 
                      :placeholder="'选择' + event.title"
                      style="flex: 1;" 
                    />
                  </div>
                </div>
              </div>
              <div class="add-timeline-item">
                <a-button size="small" @click="addTimelineEvent">
                  <PlusOutlined />
                  添加时间点
                </a-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 修改标签弹窗 - 复制Index.vue风格 -->
    <a-modal
      v-model:open="modifyTagsModalVisible"
      title="修改标签"
      @ok="handleModifyTagsOk"
      @cancel="handleModifyTagsCancel"
      width="500px"
    >
      <div class="modify-tags-content">
        <p>当前标签：</p>
        <div class="current-tags">
          <a-tag 
            v-for="(tag, index) in currentRecord?.tags" 
            :key="index" 
            :color="getTagColor(tag)"
            closable 
            @close="removeTag(index)"
          >
            {{ tag }}
          </a-tag>
        </div>
        <div style="margin-top: 16px;">
          <p>添加新标签：</p>
          <a-input
            v-model:value="newTag"
            placeholder="请输入标签名称，按回车添加"
            @pressEnter="addTag"
          />
        </div>
      </div>
    </a-modal>
    
    <!-- 修改状态弹窗 -->
    <a-modal
      v-model:open="modifyStatusModalVisible"
      title="修改数据状态"
      @ok="handleModifyStatusOk"
      @cancel="handleModifyStatusCancel"
    >
      <a-form>
        <a-form-item label="数据状态">
          <a-radio-group v-model:value="modifyStatusValue">
            <a-radio value="published">已发布</a-radio>
            <a-radio value="unpublished">未发布</a-radio>
            <a-radio value="deleted">已删除</a-radio>
            <a-radio value="abnormal">异常</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { message, Modal } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

export default {
  name: 'SearchSourceDetail',
  components: {
    PlusOutlined
  },
  data() {
    return {
      mode: 'detail', // detail, add, edit
      originalRecord: null, // 用于取消时恢复数据
      record: {
        id: null,
        title: '',
        url: '',
        description: '',
        keywords: '',
        summary: '',
        content: '',
        graphData: '',
        dataSource: 'manual',
        tags: [],
        dataStatus: 'unpublished',
        syncStatus: 'not-synced',
        syncTime: null,
        createTime: null,
        updateTime: null,
        publishTime: null,
        timelineEvents: [] // 动态时间轴事件
      },
      currentRecord: null, // 用于修改标签的临时记录
      modifyTagsModalVisible: false,
      newTag: '', // 新增标签输入
      modifyStatusModalVisible: false,
      modifyStatusValue: 'published',
      // 标签编辑相关
      inputVisible: false,
      inputValue: ''
    }
  },
  computed: {
    pageTitle() {
      switch (this.mode) {
        case 'add': return '新增数据'
        case 'edit': return '编辑数据'
        case 'detail': return '数据详情'
        default: return '数据详情'
      }
    },
    isEditMode() {
      return this.mode === 'add' || this.mode === 'edit'
    },
    timelineEvents() {
      const events = []
      
      // 只显示自定义时间轴事件，移除固定的四个时间点
      if (this.record.timelineEvents && this.record.timelineEvents.length > 0) {
        this.record.timelineEvents.forEach((event, index) => {
          events.push({
            title: event.title,
            field: `timelineEvents[${index}].time`,
            time: event.time,
            defaultTime: '2024-01-01 00:00:00',
            color: event.color || 'gray',
            isCustom: true,
            index: index
          })
        })
      }
      
      return events
    }
  },
  mounted() {
    this.initPage()
  },
  methods: {
    initPage() {
      const id = this.$route.params.id
      const routeName = this.$route.name
      
      if (routeName === 'search_source_add') {
        // 新增模式：初始化空数据并直接进入编辑状态
        this.mode = 'edit'
        this.initEmptyRecord()
      } else if (routeName === 'search_source_edit' && id) {
        // 编辑模式：加载数据并进入编辑状态
        this.loadRecord(id)
        this.mode = 'edit'
        // 编辑模式：保存原始数据用于取消恢复
        this.originalRecord = { ...this.record }
      } else if (id) {
        // 详情模式：加载数据
        this.loadRecord(id)
        this.mode = 'detail'
      } else {
        // 默认详情模式
        this.mode = 'detail'
      }
    },
    initEmptyRecord() {
      this.record = {
        id: null,
        title: '',
        url: '',
        description: '',
        keywords: '',
        summary: '',
        content: '',
        graphData: '',
        dataSource: 'manual',
        tags: [],
        dataStatus: 'unpublished',
        syncStatus: 'not-synced',
        timelineEvents: []
      }
    },
    loadRecord(id) {
      // 模拟加载数据 - 实际项目中这里应该调用API获取数据
      console.log('Loading record with ID:', id)
      
      // 模拟数据加载
      const mockData = {
        id: parseInt(id),
        title: '示例数据标题',
        url: 'https://example.com',
        description: '这是一个示例数据的描述信息',
        keywords: '示例, 数据, 关键词',
        summary: '这是网页内容的摘要信息，包含了页面的主要内容和关键信息点，帮助用户快速了解网页的核心内容。',
        content: '',
        graphData: '',
        dataSource: 'api-import',
        tags: ['标签1', '标签2', '标签3'],
        dataStatus: 'published',
        syncStatus: 'normal',
        // 移除了固定的四个时间点字段
      }
      
      this.record = { ...mockData }
      this.originalRecord = { ...mockData }
    },
    goBack() {
      this.$router.push('/view/my_search')
    },
    switchToEditMode() {
      this.mode = 'edit'
      this.originalRecord = { ...this.record }
    },
    handleSave() {
      if (!this.validateForm()) {
        return
      }
      
      // 验证自定义时间轴事件标题
      if (this.record.timelineEvents && this.record.timelineEvents.length > 0) {
        const emptyTitleEvents = this.record.timelineEvents.filter(event => !event.title || event.title.trim() === '')
        if (emptyTitleEvents.length > 0) {
          message.error('存在未填写标题的时间点，请填写完整')
          return
        }
      }
      
      // 移除了固定的更新时间逻辑
      
      // 保存逻辑
      if (this.mode === 'add' || !this.record.id) {
        // 新增保存
        this.$msg.success('新增成功')
        this.mode = 'detail'
        // 这里应该调用API保存数据
      } else {
        // 编辑保存
        this.$msg.success('保存成功')
        this.mode = 'detail'
        // 这里应该调用API更新数据
      }
      
      this.originalRecord = { ...this.record }
    },
    handleCancel() {
      if (this.mode === 'add') {
        // 新增模式下取消，返回列表页
        this.goBack()
      } else {
        // 编辑模式下取消，恢复数据并返回详情模式
        this.record = { ...this.originalRecord }
        this.mode = 'detail'
      }
    },
    validateForm() {
      if (!this.record.title.trim()) {
        this.$msg.error('请输入标题')
        return false
      }
      if (!this.record.url.trim()) {
        this.$msg.error('请输入网页URL')
        return false
      }
      return true
    },
    handleSync() {
      message.success('同步操作已触发')
    },
    handlePublish() {
      Modal.confirm({
        title: '确认发布',
        content: '确定要发布这条数据吗？发布后将在前台展示。',
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          this.publishArticle()
        },
        onCancel() {
          console.log('取消发布')
        }
      })
    },
    handleUnpublish() {
      Modal.confirm({
        title: '确认下架',
        content: '确定要下架这条数据吗？下架后将在前台隐藏。',
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          this.unpublishArticle()
        },
        onCancel() {
          console.log('取消下架')
        }
      })
    },
    publishArticle() {
      // 模拟发布API调用
      console.log('调用发布API，文章ID:', this.record.id)
      
      // 模拟异步操作
      setTimeout(() => {
        this.record.dataStatus = 'published'
        message.success('发布成功')
      }, 1000)
      
      // 实际项目中应该调用真实的API
      // this.$api.publishArticle(this.record.id).then(() => {
      //   this.record.dataStatus = 'published'
      //   this.$msg.success('发布成功')
      // }).catch(() => {
      //   this.$msg.error('发布失败')
      // })
    },
    unpublishArticle() {
      // 模拟下架API调用
      console.log('调用下架API，文章ID:', this.record.id)
      
      // 模拟异步操作
      setTimeout(() => {
        this.record.dataStatus = 'unpublished'
        message.success('下架成功')
      }, 1000)
      
      // 实际项目中应该调用真实的API
      // this.$api.unpublishArticle(this.record.id).then(() => {
      //   this.record.dataStatus = 'unpublished'
      //   this.$msg.success('下架成功')
      // }).catch(() => {
      //   this.$msg.error('下架失败')
      // })
    },
    handleDelete() {
      Modal.confirm({
        title: '确认删除',
        content: '确定要删除这条数据吗？删除后无法恢复！',
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          message.success('删除成功')
          this.goBack()
          // 这里应该调用API删除数据
        },
        onCancel() {
          console.log('取消删除')
        }
      })
    },
    showModifyTagsModal() {
      this.currentRecord = { ...this.record }
      this.newTag = ''
      this.modifyTagsModalVisible = true
    },
    handleModifyTagsOk() {
      this.record.tags = [...this.currentRecord.tags]
      this.modifyTagsModalVisible = false
      this.currentRecord = null
      message.success('标签修改成功')
    },
    handleModifyTagsCancel() {
      this.modifyTagsModalVisible = false
      this.currentRecord = null
      this.newTag = ''
    },
    addTag() {
      if (this.newTag.trim() && this.currentRecord) {
        if (!this.currentRecord.tags) {
          this.currentRecord.tags = []
        }
        if (!this.currentRecord.tags.includes(this.newTag.trim())) {
          this.currentRecord.tags.push(this.newTag.trim())
        }
        this.newTag = ''
      }
    },
    removeTag(index) {
      if (this.currentRecord && this.currentRecord.tags) {
        this.currentRecord.tags.splice(index, 1)
      }
    },
    removeTagFromRecord(index) {
      if (this.record.tags) {
        this.record.tags.splice(index, 1)
      }
    },
    showInput() {
      this.inputVisible = true
      this.$nextTick(() => {
        this.$refs.inputRef.focus()
      })
    },
    handleInputConfirm() {
      const inputValue = this.inputValue
      if (inputValue && !this.record.tags.includes(inputValue)) {
        this.record.tags.push(inputValue)
      }
      this.inputVisible = false
      this.inputValue = ''
    },
    generateGraphData() {
      // 生成图谱数据的逻辑
      message.success('图谱数据生成成功')
    },
    addTimelineEvent() {
      if (!this.record.timelineEvents) {
        this.record.timelineEvents = []
      }
      
      const newEvent = {
        title: '', // 必填项，空字符串待用户填写
        time: dayjs(),
        url: '', // 选填项
        description: '', // 选填项
        color: 'gray'
      }
      
      this.record.timelineEvents.push(newEvent)
      message.success('已添加新的时间点，请填写标题信息')
    },
    removeTimelineEvent(index) {
      if (this.record.timelineEvents && this.record.timelineEvents.length > index) {
        this.record.timelineEvents.splice(index, 1)
        message.success('已删除时间点')
      }
    },
    showModifyStatusModal() {
      this.modifyStatusValue = this.record.dataStatus
      this.modifyStatusModalVisible = true
    },
    handleModifyStatusOk() {
      this.record.dataStatus = this.modifyStatusValue
      this.modifyStatusModalVisible = false
      message.success('状态修改成功')
    },
    handleModifyStatusCancel() {
      this.modifyStatusModalVisible = false
    },
    // 映射方法
    getTagColor(tag) {
      const colors = ['blue', 'green', 'orange', 'purple', 'red', 'cyan']
      return colors[tag.length % colors.length]
    },
    getDataSourceColor(dataSource) {
      const colors = {
        'api-import': 'blue',
        'manual': 'green',
        'third-party': 'orange',
        'crawler': 'purple'
      }
      return colors[dataSource] || 'default'
    },
    getDataSourceLabel(dataSource) {
      const labels = {
        'api-import': 'API导入',
        'manual': '手动创建',
        'third-party': '第三方链接',
        'crawler': '数据爬虫'
      }
      return labels[dataSource] || dataSource
    },
    getDataStatusColor(dataStatus) {
      const colors = {
        'published': 'success',
        'unpublished': 'warning',
        'deleted': 'default',
        'abnormal': 'error'
      }
      return colors[dataStatus] || 'default'
    },
    getDataStatusText(dataStatus) {
      const texts = {
        'published': '已发布',
        'unpublished': '未发布',
        'deleted': '已删除',
        'abnormal': '异常'
      }
      return texts[dataStatus] || dataStatus
    },
    getSyncStatusStatus(syncStatus) {
      const statuses = {
        'normal': 'success',
        'abnormal': 'error',
        'not-synced': 'default',
        'syncing': 'processing'
      }
      return statuses[syncStatus] || 'default'
    },
    getSyncStatusLabel(syncStatus) {
      const labels = {
        'normal': '正常',
        'abnormal': '异常',
        'not-synced': '未同步',
        'syncing': '同步中'
      }
      return labels[syncStatus] || syncStatus
    }
  }
}
</script>

<style scoped>

.detail-page {
  height: 100%;
  background-color: #f5f5f5;
}

/* 缩小页面头部内边距 */
.compact-header {
  padding: 8px 24px !important;
  margin-bottom: 0 !important;
}

/* 基本信息区域样式 - 位于头部下方 */
.info-section-header {
  background: white;
  padding: 12px 24px;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

.info-section-header .info-content {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
}

.info-section-header .info-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  margin-bottom: 0;
}

.info-section-header .info-label {
  font-weight: 500;
  color: #666;
  margin-right: 8px;
}

.info-section-header .info-value {
  color: #333;
}

.detail-container {
  display: flex;
  gap: 16px;
  padding: 16px;
  height: calc(100% - 120px); /* 调整高度计算 */
}

.left-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.right-section {
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.tags-section, .web-content-section, .web-summary-section, .graph-section, .timeline-section {
  background: white;
  padding: 16px;
  border-radius: 6px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

/* 标签区域头部样式 */
.tags-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.tags-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.tags-header .action-buttons {
  display: flex;
  gap: 8px;
  padding: 0;
  background: transparent;
  box-shadow: none;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  margin: 0;
}

/* 网页信息摘要区域样式 */
.web-summary-section {
  margin-top: 0;
}

.summary-content {
  margin-top: 12px;
}

.summary-item {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
  line-height: 1.5;
}

.summary-label {
  font-weight: 500;
  color: #666;
  width: 90px;
  flex-shrink: 0;
}

.summary-value {
  flex: 1;
  color: #333;
}

.summary-link {
  flex: 1;
  color: #1890ff;
  text-decoration: none;
}

.summary-link:hover {
  text-decoration: underline;
}

/* 编辑模式下的输入框样式 */
.edit-tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.edit-graph-section {
  display: flex;
  flex-direction: column;
}

.timeline-edit-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
}

.timeline-label {
  font-weight: 500;
  color: #666;
  margin-bottom: 4px;
  font-size: 14px;
}

.web-content-container {
  margin-top: 12px;
}

/* 修改标签弹窗样式 - 与Index.vue保持一致 */
.modify-tags-content {
  padding: 16px 0;
}

.modify-tags-content .current-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
  min-height: 32px;
  padding: 8px;
  background-color: #fafafa;
  border-radius: 4px;
}

.graph-placeholder, .timeline-placeholder {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 12px;
}

/* 时间轴样式 */
.timeline-placeholder {
  padding: 16px;
}

h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 500;
}

/* 时间轴编辑卡片样式 */
.timeline-edit-card {
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 12px;
  background: #fafafa;
}

.custom-event-editor {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.builtin-event-editor {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.event-form-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.event-form-item .required-label {
  font-weight: 500;
  color: #ff4d4f;
  min-width: 60px;
}

.event-form-item .required-label::before {
  content: '*';
  margin-right: 4px;
}

.event-form-item .timeline-label {
  font-weight: 500;
  min-width: 60px;
}

.event-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}

.timeline-content {
  padding: 4px 0;
}

.timeline-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.timeline-details {
  margin-left: 0;
  padding-left: 0;
}

.timeline-url a {
  color: #1890ff;
  text-decoration: none;
}

.timeline-url a:hover {
  text-decoration: underline;
}

.timeline-description {
  line-height: 1.4;
}
</style>