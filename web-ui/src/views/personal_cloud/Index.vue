<template>
  <div class="personal-cloud">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <!-- 面包屑导航 -->
        <a-breadcrumb class="breadcrumb">
          <a-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index">
            <a @click="navigateToPath(index)">{{ item }}</a>
          </a-breadcrumb-item>
        </a-breadcrumb>
        
        <!-- 操作按钮 -->
        <div class="toolbar-actions">
          <a-button type="primary" @click="showUploadModal = true">
            <template #icon><UploadOutlined /></template>
            上传文件
          </a-button>
          <a-button @click="showNewFolderModal = true">
            <template #icon><FolderAddOutlined /></template>
            新建文件夹
          </a-button>
          <a-button @click="refreshFiles">
            <template #icon><ReloadOutlined /></template>
            刷新
          </a-button>
        </div>
      </div>
      
      <!-- 搜索框 -->
      <div class="toolbar-right">
        <a-input-search
          v-model:value="searchValue"
          placeholder="搜索文件..."
          style="width: 250px"
          @search="handleSearch"
          @change="handleSearch"
          allow-clear
        />
      </div>
    </div>

    <!-- 文件网格 -->
    <div class="file-grid-container">
      <div class="file-grid">
        <div
          v-for="file in filteredFileList"
          :key="file.id"
          class="file-grid-item"
          :class="{ selected: selectedItems.includes(file.id) }"
          @click="handleItemClick(file, $event)"
          @dblclick="handleItemDoubleClick(file)"
          @contextmenu.prevent="handleRowContextMenu(file, $event)"
        >
          <!-- 文件缩略图 -->
          <div class="file-thumbnail">
            <FolderTwoTone v-if="file.type === 'folder'" class="file-icon folder-icon" two-tone-color="#1890ff" />
            <FileImageTwoTone v-else-if="isImage(file.name)" class="file-icon image-icon" two-tone-color="#52c41a" />
            <FileTextTwoTone v-else-if="isText(file.name)" class="file-icon text-icon" two-tone-color="#722ed1" />
            <FilePdfTwoTone v-else-if="isPDF(file.name)" class="file-icon pdf-icon" two-tone-color="#ff4d4f" />
            <FileTwoTone v-else class="file-icon file-icon" two-tone-color="#fa8c16" />
          </div>
          
          <!-- 文件信息 -->
          <div class="file-info">
            <div class="file-name" :title="file.name">{{ file.name }}</div>
          </div>
          
          <!-- 悬停时显示的详细信息 -->
          <div class="file-hover-info">
            <div class="hover-file-name">{{ file.name }}</div>
            <div v-if="file.type !== 'folder'" class="hover-file-size">{{ formatFileSize(file.size) }}</div>
            <div class="hover-file-date">{{ formatDate(file.updatedAt) }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右键菜单 -->
    <div
      v-if="showContextMenu"
      class="context-menu"
      :style="{
        left: contextMenuPosition.x + 'px',
        top: contextMenuPosition.y + 'px'
      }"
      @mouseleave="showContextMenu = false"
    >
      <div class="context-menu-item" @click="handleContextMenuAction('open')">
        <FolderOpenOutlined />
        <span>打开</span>
      </div>
      <div class="context-menu-item" @click="handleContextMenuAction('download')">
        <DownloadOutlined />
        <span>下载</span>
      </div>
      <a-divider style="margin: 4px 0" />
      <div class="context-menu-item" @click="handleContextMenuAction('copy')">
        <CopyOutlined />
        <span>复制</span>
      </div>
      <div class="context-menu-item" @click="handleContextMenuAction('cut')">
        <ScissorOutlined />
        <span>剪切</span>
      </div>
      <div
        class="context-menu-item"
        :class="{ disabled: !clipboardItem }"
        @click="handleContextMenuAction('paste')"
      >
        <SnippetsOutlined />
        <span>粘贴</span>
      </div>
      <a-divider style="margin: 4px 0" />
      <div class="context-menu-item" @click="handleContextMenuAction('rename')">
        <EditOutlined />
        <span>重命名</span>
      </div>
      <div class="context-menu-item" @click="handleContextMenuAction('delete')">
        <DeleteOutlined />
        <span>删除</span>
      </div>
      <a-divider style="margin: 4px 0" />
      <div class="context-menu-item" @click="handleContextMenuAction('properties')">
        <InfoCircleOutlined />
        <span>属性</span>
      </div>
    </div>

    <!-- 上传文件模态框 -->
    <a-modal
      v-model:open="showUploadModal"
      title="上传文件"
      @ok="handleUpload"
      @cancel="showUploadModal = false"
    >
      <a-upload
        :file-list="fileList"
        :before-upload="beforeUpload"
        multiple
      >
        <a-button>
          <UploadOutlined />
          选择文件
        </a-button>
      </a-upload>
    </a-modal>

    <!-- 新建文件夹模态框 -->
    <a-modal
      v-model:open="showNewFolderModal"
      title="新建文件夹"
      @ok="handleNewFolder"
      @cancel="showNewFolderModal = false"
    >
      <a-form :model="newFolderForm" layout="vertical">
        <a-form-item label="文件夹名称" required>
          <a-input v-model:value="newFolderForm.name" placeholder="请输入文件夹名称" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 重命名模态框 -->
    <a-modal
      v-model:open="showRenameModal"
      title="重命名"
      @ok="handleRename"
      @cancel="showRenameModal = false"
    >
      <a-form :model="renameForm" layout="vertical">
        <a-form-item label="新名称" required>
          <a-input v-model:value="renameForm.name" placeholder="请输入新名称" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { ref, reactive, computed, nextTick, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  UploadOutlined,
  FolderAddOutlined,
  ReloadOutlined,
  FolderTwoTone,
  FileTwoTone,
  FileImageTwoTone,
  FileTextTwoTone,
  FilePdfTwoTone,
  FolderOpenOutlined,
  DownloadOutlined,
  CopyOutlined,
  ScissorOutlined,
  SnippetsOutlined,
  EditOutlined,
  DeleteOutlined,
  InfoCircleOutlined
} from '@ant-design/icons-vue'

export default {
  name: 'PersonalCloud',
  components: {
    UploadOutlined,
    FolderAddOutlined,
    ReloadOutlined,
    FolderTwoTone,
    FileTwoTone,
    FileImageTwoTone,
    FileTextTwoTone,
    FilePdfTwoTone,
    FolderOpenOutlined,
    DownloadOutlined,
    CopyOutlined,
    ScissorOutlined,
    SnippetsOutlined,
    EditOutlined,
    DeleteOutlined,
    InfoCircleOutlined
  },
  setup() {
    // 响应式数据
    const currentPath = ref('/')
    const breadcrumbs = computed(() => {
      const parts = currentPath.value.split('/').filter(Boolean)
      return ['根目录', ...parts]
    })
    
    const fileList = ref([
      {
        id: 1,
        name: '文档',
        type: 'folder',
        size: 0,
        updatedAt: '2024-01-15 10:30:00',
        path: '/文档'
      },
      {
        id: 2,
        name: '图片',
        type: 'folder',
        size: 0,
        updatedAt: '2024-01-14 15:20:00',
        path: '/图片'
      },
      {
        id: 3,
        name: '项目报告.pdf',
        type: 'file',
        size: 2048576,
        updatedAt: '2024-01-13 09:15:00',
        path: '/项目报告.pdf'
      },
      {
        id: 4,
        name: '会议记录AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA.txt',
        type: 'file',
        size: 10240,
        updatedAt: '2024-01-12 14:30:00',
        path: '/会议记录.txt'
      },
      {
        id: 5,
        name: '产品截图.png',
        type: 'file',
        size: 524288,
        updatedAt: '2024-01-11 16:45:00',
        path: '/产品截图.png'
      }
    ])
    
    const loading = ref(false)
    const searchValue = ref('')
    const selectedItems = ref([])
    
    // 右键菜单状态
    const showContextMenu = ref(false)
    const contextMenuPosition = reactive({ x: 0, y: 0 })
    const contextMenuTarget = ref(null)
    
    // 剪贴板
    const clipboardItem = ref(null)
    const clipboardAction = ref('')
    
    // 模态框状态
    const showUploadModal = ref(false)
    const showNewFolderModal = ref(false)
    const showRenameModal = ref(false)
    const showMoveCopyModal = ref(false)
    const showDeleteModal = ref(false)
    
    // 表单数据
    const renameForm = reactive({ name: '' })
    const newFolderForm = reactive({ name: '' })
    
    // 选中的文件
    const selectedFile = ref(null)

    // 计算属性
    const filteredFileList = computed(() => {
      if (!searchValue.value) return fileList.value
      const keyword = searchValue.value.toLowerCase()
      return fileList.value.filter(file => 
        file.name.toLowerCase().includes(keyword)
      )
    })

    // 方法
    const formatFileSize = (bytes) => {
      if (bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    }

    const formatDate = (dateString) => {
      return dateString
    }

    const navigateToPath = (index) => {
      if (index === 0) {
        currentPath.value = '/'
      } else {
        const parts = currentPath.value.split('/').filter(Boolean)
        const newPath = '/' + parts.slice(0, index).join('/')
        currentPath.value = newPath
      }
      refreshFiles()
    }

    const refreshFiles = () => {
      loading.value = true
      // 模拟加载数据
      setTimeout(() => {
        loading.value = false
        message.success('文件列表已刷新')
      }, 500)
    }

    const handleUpload = () => {
      message.success('文件上传成功')
      showUploadModal.value = false
      refreshFiles()
    }

    const handleNewFolder = () => {
      if (!newFolderForm.name.trim()) {
        message.warning('请输入文件夹名称')
        return
      }
      
      const newFolder = {
        id: Date.now(),
        name: newFolderForm.name,
        type: 'folder',
        size: 0,
        updatedAt: new Date().toLocaleString('zh-CN'),
        path: currentPath.value + '/' + newFolderForm.name
      }
      
      fileList.value.unshift(newFolder)
      message.success('文件夹创建成功')
      showNewFolderModal.value = false
      newFolderForm.name = ''
    }

    const handleRename = () => {
      if (!renameForm.name.trim()) {
        message.warning('请输入新名称')
        return
      }
      
      if (selectedFile.value) {
        selectedFile.value.name = renameForm.name
        message.success('重命名成功')
        showRenameModal.value = false
        renameForm.name = ''
      }
    }

    const handleDelete = (file) => {
      Modal.confirm({
        title: '确认删除',
        content: `确定要删除 "${file.name}" 吗？`,
        onOk() {
          const index = fileList.value.findIndex(item => item.id === file.id)
          if (index > -1) {
            fileList.value.splice(index, 1)
            message.success('删除成功')
          }
        }
      })
    }

    const downloadFile = (file) => {
      message.success(`开始下载 ${file.name}`)
      // 模拟下载
      const link = document.createElement('a')
      link.href = '#'
      link.download = file.name
      link.click()
    }

    const isImage = (filename) => {
      const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.svg']
      return imageExtensions.some(ext => filename.toLowerCase().endsWith(ext))
    }

    const isText = (filename) => {
      const textExtensions = ['.txt', '.md', '.json', '.xml', '.csv', '.log']
      return textExtensions.some(ext => filename.toLowerCase().endsWith(ext))
    }

    const isPDF = (filename) => {
      return filename.toLowerCase().endsWith('.pdf')
    }

    const handleRowContextMenu = (file, event) => {
      event.preventDefault()
      contextMenuTarget.value = file
      showContextMenu.value = true
      
      nextTick(() => {
        const menuWidth = 180
        const menuHeight = 250
        let x = event.clientX
        let y = event.clientY
        
        // 防止菜单超出屏幕
        if (x + menuWidth > window.innerWidth) {
          x = window.innerWidth - menuWidth - 10
        }
        if (y + menuHeight > window.innerHeight) {
          y = window.innerHeight - menuHeight - 10
        }
        
        contextMenuPosition.x = x
        contextMenuPosition.y = y
      })
    }

    const handleContextMenu = (action) => {
      handleContextMenuAction(action)
    }

    const handleContextMenuAction = (action) => {
      const file = contextMenuTarget.value
      if (!file) return
      
      switch (action) {
        case 'open':
          if (file.type === 'folder') {
            currentPath.value = file.path
            refreshFiles()
          } else {
            downloadFile(file)
          }
          break
        case 'download':
          downloadFile(file)
          break
        case 'copy':
          clipboardItem.value = file
          clipboardAction.value = 'copy'
          message.success('已复制到剪贴板')
          break
        case 'cut':
          clipboardItem.value = file
          clipboardAction.value = 'cut'
          message.success('已剪切到剪贴板')
          break
        case 'paste':
          if (clipboardItem.value && file.type === 'folder') {
            message.success('粘贴成功')
            clipboardItem.value = null
            clipboardAction.value = ''
          }
          break
        case 'rename':
          selectedFile.value = file
          renameForm.name = file.name
          showRenameModal.value = true
          break
        case 'delete':
          handleDelete(file)
          break
        case 'properties':
          showPropertiesModal(file)
          break
      }
      
      showContextMenu.value = false
    }

    const handleItemClick = (file, event) => {
      if (event.ctrlKey || event.metaKey) {
        // 多选
        const index = selectedItems.value.indexOf(file.id)
        if (index > -1) {
          selectedItems.value.splice(index, 1)
        } else {
          selectedItems.value.push(file.id)
        }
      } else {
        // 单选
        selectedItems.value = [file.id]
      }
    }

    const handleItemDoubleClick = (file) => {
      if (file.type === 'folder') {
        currentPath.value = file.path
        refreshFiles()
      } else {
        downloadFile(file)
      }
    }

    const handleSearch = () => {
      // 搜索功能通过filteredFileList计算属性实现
    }

    const showPropertiesModal = (file) => {
      const content = document.createElement('div')
      content.style.marginTop = '16px'
      content.innerHTML = `
        <p><strong>名称：</strong>${file.name}</p>
        <p><strong>类型：</strong>${file.type === 'folder' ? '文件夹' : '文件'}</p>
        ${file.type !== 'folder' ? `<p><strong>大小：</strong>${formatFileSize(file.size)}</p>` : ''}
        <p><strong>修改时间：</strong>${file.updatedAt}</p>
        <p><strong>路径：</strong>${file.path}</p>
      `
      
      Modal.info({
        title: '文件属性',
        content: content,
        width: 400
      })
    }

    const beforeUpload = (file) => {
      // 处理文件上传逻辑
      return false // 阻止自动上传
    }

    // 生命周期
    onMounted(() => {
      refreshFiles()
    })

    // 点击其他地方关闭右键菜单
    const handleClickOutside = (event) => {
      if (showContextMenu.value) {
        const menuElement = document.querySelector('.context-menu')
        if (menuElement && !menuElement.contains(event.target)) {
          showContextMenu.value = false
        }
      }
    }

    // 添加事件监听
    if (typeof document !== 'undefined') {
      document.addEventListener('click', handleClickOutside)
    }

    return {
      // 数据
      currentPath,
      breadcrumbs,
      fileList,
      loading,
      searchValue,
      selectedItems,
      showContextMenu,
      contextMenuPosition,
      contextMenuTarget,
      clipboardItem,
      clipboardAction,
      
      // 模态框状态
      showUploadModal,
      showNewFolderModal,
      showRenameModal,
      showMoveCopyModal,
      showDeleteModal,
      renameForm,
      newFolderForm,
      
      // 计算属性
      filteredFileList,
      
      // 方法
      formatFileSize,
      formatDate,
      navigateToPath,
      refreshFiles,
      handleUpload,
      handleNewFolder,
      handleRename,
      handleDelete,
      downloadFile,
      isImage,
      isText,
      isPDF,
      handleRowContextMenu,
      handleContextMenu,
      handleContextMenuAction,
      handleItemClick,
      handleItemDoubleClick,
      handleSearch,
      showRenameModal,
      showDeleteModal,
      showPropertiesModal,
      beforeUpload
    }
  }
}
</script>

<style scoped>
/* ===== 样式部分 ===== */
.personal-cloud {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

/* 工具栏样式 */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 24px;
  flex: 1;
}

.toolbar-right {
  display: flex;
  align-items: center;
}

.breadcrumb {
  margin: 0;
}

.toolbar-actions {
  display: flex;
  gap: 12px;
}

/* 文件网格容器 */
.file-grid-container {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background-color: #fafafa;
}

/* 文件网格 */
.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
  padding: 0;
}

/* 文件项样式 */
.file-grid-item {
  background: #fff;
  border: none;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  min-height: 120px;
  position: relative;
}

.file-grid-item:hover {
  background-color: #f5f5f5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.file-grid-item.selected {
  background-color: #e6f7ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.2);
}

/* 文件缩略图 */
.file-thumbnail {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 48px;
  width: 48px;
  border-radius: 6px;
  background-color: #f5f5f5;
}

.file-icon {
  font-size: 24px;
}

.folder-icon {
  color: #1890ff;
}

.image-icon {
  color: #52c41a;
}

.text-icon {
  color: #722ed1;
}

.pdf-icon {
  color: #ff4d4f;
}

.file-icon {
  color: #fa8c16;
}

/* 文件信息 */
.file-info {
  width: 100%;
}

.file-name {
  font-size: 13px;
  color: #262626;
  margin-bottom: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
  max-width: 100%;
}

/* 悬停时显示的详细信息 */
.file-hover-info {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  padding: 8px;
  border-radius: 6px;
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.3s ease;
  z-index: 10;
  pointer-events: none;
  max-width: 100%;
}

.file-grid-item:hover .file-hover-info {
  opacity: 1;
  visibility: visible;
}

.hover-file-name {
  font-size: 12px;
  font-weight: 500;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.hover-file-size,
.hover-file-date {
  font-size: 11px;
  opacity: 0.9;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-meta {
  font-size: 12px;
  color: #8c8c8c;
  line-height: 1.4;
}

/* 右键菜单样式 */
.context-menu {
  position: fixed;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  min-width: 180px;
  padding: 4px 0;
}

.context-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
  font-size: 14px;
  color: #262626;
}

.context-menu-item:hover {
  background-color: #f5f5f5;
}

.context-menu-item.disabled {
  color: #d9d9d9;
  cursor: not-allowed;
}

.context-menu-item.disabled:hover {
  background-color: transparent;
}

.context-menu-item span {
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .toolbar-left {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .toolbar-actions {
    justify-content: flex-start;
  }
  
  .file-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 12px;
  }
  
  .file-grid-item {
    padding: 10px;
    min-height: 100px;
  }
  
  .file-thumbnail {
    height: 40px;
    width: 40px;
  }
  
  .file-icon {
    font-size: 20px;
  }
  
  .file-name {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .file-grid {
    grid-template-columns: repeat(auto-fill, minmax(90px, 1fr));
    gap: 8px;
  }
  
  .file-grid-item {
    padding: 8px;
    min-height: 90px;
  }
  
  .file-thumbnail {
    height: 36px;
    width: 36px;
  }
  
  .file-icon {
    font-size: 18px;
  }
}
</style>