<template>
  <a-layout class="root_view">
    <!-- 顶部导航栏 -->
    <a-layout-header class="header">
      <!-- Logo区域 -->
      <div class="logo-container">
        <div class="logo">PersonKit</div>
      </div>
      
      <!-- 顶部菜单区域 -->
      <div class="top-menu-container">
        <a-menu 
          v-model:selectedKeys="selectedTopKeysSource" 
          theme="dark" 
          mode="horizontal" 
          @select="onTopMenuSelect"
          class="top-menu"
        >
          <a-menu-item v-for="item in menu.topMenus" :key="item.routerName" :id="item.id">
            {{ item.name }}
          </a-menu-item>
        </a-menu>
      </div>

      <!-- 用户信息区域 -->
      <div class="user-info-container">
        <span class="user-nickname">{{ user.nickname }}</span>
        <a-dropdown @openChange="handleDropdownVisible">
          <template #overlay>
            <div class="user-dropdown-menu">
              <div class="user-info-header">
                <a-avatar size="large" class="user-avatar">
                  <template #icon><UserOutlined /></template>
                </a-avatar>
                <div class="user-info-text">
                  <div class="user-name">{{ user.nickname }}</div>
                  <div class="user-role">{{ user.roleName || '普通用户' }}</div>
                </div>
              </div>
              <a-divider style="margin: 8px 0" />
              <a-menu>
                <a-menu-item key="profile" @click="showUserProfile">
                  <template #icon><UserOutlined /></template>
                  基本信息
                </a-menu-item>
                <a-menu-item key="password" @click="showPasswordModal">
                  <template #icon><VerticalRightOutlined /></template>
                  修改密码
                </a-menu-item>
                <a-menu-item key="logout" @click="handleLogout">
                  <template #icon><LogoutOutlined /></template>
                  退出登录
                </a-menu-item>
              </a-menu>
            </div>
          </template>
          <a-button type="text" class="user-dropdown-trigger">
            <a-avatar size="small" class="trigger-avatar">
              <template #icon><UserOutlined /></template>
            </a-avatar>
            <DownOutlined class="dropdown-icon" />
          </a-button>
        </a-dropdown>
      </div>
    </a-layout-header>
    <!-- 主体内容区域 -->
    <div class="main-container">
      <a-layout-sider width="200" style="background: #fff" class="sider">
        <!-- 左侧菜单 -->
        <a-menu v-model:selectedKeys="selectedLeftKeysSource" v-model:openKeys="openKeysSource" mode="inline"
          :items="leftMenuSource" @select="onLeftMenuSelect" :style="{ height: '100%', borderRight: 0 }">
        </a-menu>
        <!-- 左侧菜单 -->
      </a-layout-sider>
      <div class="content-wrapper">
        <!-- 面包屑导航 -->
        <a-breadcrumb style="margin: 16px 0">
          <a-breadcrumb-item v-for="item in breadcrumbName" :key="item">{{
            item
          }}</a-breadcrumb-item>
        </a-breadcrumb>
        <!-- 面包屑导航 -->
        <div class="content-area">
          <!-- 内容区 -->
          <RouterView class="router-view-content" />
          <!-- 内容区 -->
        </div>
      </div>
    </div>
  </a-layout>

  <!-- 用户详情弹窗 -->
  <a-modal 
    v-model:open="userDetailVisible" 
    title="用户详情" 
    @ok="userDetailVisible = false"
    @cancel="userDetailVisible = false" 
    width="600px"
    :footer="null"
  >
    <div class="user-detail-content">
      <div class="user-detail-header">
        <a-avatar size="large" class="detail-avatar">
          <template #icon><UserOutlined /></template>
        </a-avatar>
        <div class="user-detail-info">
          <div class="detail-nickname">{{ userDetail.nickname || user.nickname }}</div>
          <div class="detail-username">{{ userDetail.username || user.username }}</div>
        </div>
      </div>
      
      <a-descriptions :column="1" bordered class="user-detail-descriptions">
        <a-descriptions-item label="用户名">
          {{ userDetail.username || user.username || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="昵称">
          {{ userDetail.nickname || user.nickname || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="邮箱">
          {{ userDetail.email || user.email || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="手机号">
          {{ userDetail.phone || user.phone || '-' }}
        </a-descriptions-item>
        <a-descriptions-item label="角色">
          {{ userDetail.roleName || user.roleName || '普通用户' }}
        </a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-badge :status="getStatusBadge(userDetail.status)" :text="getStatusName(userDetail.status)" />
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">
          {{ formatDate(userDetail.createTime) }}
        </a-descriptions-item>
        <a-descriptions-item label="更新时间">
          {{ formatDate(userDetail.updateTime) }}
        </a-descriptions-item>
      </a-descriptions>
    </div>
  </a-modal>

  <!-- 修改密码弹窗 -->
  <a-modal 
    v-model:open="passwordModalVisible" 
    title="修改密码" 
    @ok="handlePasswordSubmit"
    @cancel="closePasswordModal" 
    width="600px"
    :confirmLoading="passwordLoading"
  >
    <a-form 
      ref="passwordFormRef"
      :model="passwordForm" 
      :rules="passwordRules" 
      layout="horizontal"
      class="password-form"
      :labelCol="{ span: 6 }"
      :wrapperCol="{ span: 18 }"
    >
      <a-form-item label="当前密码" name="currentPassword">
        <a-input-password 
          v-model:value="passwordForm.currentPassword" 
          placeholder="请输入当前密码"
          size="large"
        />
      </a-form-item>
      
      <a-form-item label="新密码" name="newPassword">
        <a-input-password 
          v-model:value="passwordForm.newPassword" 
          placeholder="请输入新密码"
          size="large"
        />
      </a-form-item>
      
      <a-form-item label="确认新密码" name="confirmPassword">
        <a-input-password 
          v-model:value="passwordForm.confirmPassword" 
          placeholder="请再次输入新密码"
          size="large"
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script>
import { ref, reactive, defineComponent } from 'vue'
import { mapState, mapActions } from 'pinia'
import { useUserStore } from '@/stores/user'
import { UserOutlined, DownOutlined, VerticalRightOutlined, LogoutOutlined } from '@ant-design/icons-vue'
export default defineComponent({
  data() {
    const user = reactive({})
    const menu = reactive({
      leftMenus: [],
      topMenus: []
    })
    const selectedTopKeys = 'knowledge_graph'
    const selectedLeftKeys = ''
    const openKeys = ''
    return {
      user,
      menu,
      selectedTopKeys,
      selectedLeftKeys,
      openKeys,
      userDetailVisible: false,
      userDetail: {},
      passwordModalVisible: false,
      passwordLoading: false,
      passwordForm: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        currentPassword: [
          { required: true, message: '请输入当前密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '新密码长度至少6位', trigger: 'blur' },
          { 
            validator: (rule, value, callback) => {
              if (value === this.passwordForm.currentPassword) {
                callback(new Error('新密码不能与当前密码相同'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value !== this.passwordForm.newPassword) {
                callback(new Error('两次输入的密码不一致'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  computed: {
    ...mapState(useUserStore, [
      'currentUserId',
      'currentRoleId',
      'currentTopMenuId',
      'currentLeftMenuId'
    ]),

    breadcrumbName: function () {
      // 强制依赖当前路由，确保路由变化时重新计算
      const route = this.$route
      return route.meta?.breadcrumbName || []
    },
    // 上部选中的菜单
    selectedTopKeysSource: {
      get() {
        if (this.currentTopMenuId.length > 0) {
          return [this.currentTopMenuId]
        }
        if (this.selectedTopKeys.length > 0) {
          return [this.selectedTopKeys]
        }

        const routerName = this.menu.topMenus[0].routerName

        return [routerName]
      },
      // setter
      set(newValue) {
        this.selectedTopKeys = newValue
      }
    },

    /**
     * 左侧的菜单数据
     */
    leftMenuSource: function () {
      return this.menu.leftMenus.map((v) => {
        const item = {
          key: v.routerName,
          label: v.name,
          title: v.name
        }
        if (typeof v.childrens !== 'undefined' && v.childrens.length > 0) {
          // 存在子菜单
          item.children = v.childrens.map((m) => {
            return {
              key: m.routerName,
              label: m.name,
              title: m.name
            }
          })
        }
        return item
      })
    },
    //左侧选中的菜单
    selectedLeftKeysSource: {
      // getter
      get() {
        if (this.selectedLeftKeys.length > 0) {
          return [this.selectedLeftKeys]
        }
        if (this.currentLeftMenuId.length > 0) {
          return [this.currentLeftMenuId]
        }

        const leftOne = this.menu.leftMenus[0]
        if (typeof leftOne === 'undefined') {
          return []
        }
        if (typeof leftOne.childrens !== 'undefined' && leftOne.childrens.length > 0) {
          const routerName = leftOne.childrens[0].routerName

          return [routerName]
        }
        return [leftOne.routerName]
      },
      // setter
      set(newValue) {
        this.selectedLeftKeys = newValue
      }
    },
    // 左侧展开的菜单
    openKeysSource: {
      get() {
        if (this.openKeys.length > 0) {
          return [this.openKeys]
        }

        return this.menu.leftMenus
          .filter((v) => {
            return typeof v.childrens !== 'undefined' && v.childrens.length > 0
          })
          .map((v) => v.routerName)
      },
      // setter
      set(newValue) {
        this.openKeys = newValue
      }
    }
  },
  methods: {
    ...mapActions(useUserStore, ['setRole', 'setUser', 'setTopMenuId', 'setLeftMenuId', 'clearUser']),
    /**
     * 加载用户基本信息
     */
    async loadUserInfo() {
      this.$http
        .request({
          url: '/personkit/current',
          method: 'get'
        })
        .then((res) => {
          this.user = reactive(res)

          this.setUser(res)
          if (res.roles.length > 1) {
            //多于一个角色
          } else {
            //一个角色
            const role = res.roles[0]

            this.setRole(role)
          }
          //查询菜单
          this.findRoleMenu()
        })
        .catch((err) => console.log(err))
    },

    /**
     * 加载用户基本菜单
     */
    findRoleMenu() {
      return new Promise((resolve, reject) => {
        this.$http
          .request({
            url: '/personkit/sys/menu/findRoleMenu',
            method: 'post',
            data: {
              roleId: this.currentRoleId,
              topMenuId: this.currentTopMenuId
            }
          })
          .then((res) => {
            this.menu = reactive(res)
            resolve(res)
          })
          .catch((err) => {
            console.log(err)
            reject(err)
          })
      })
    },
    /**
     * 选择顶部菜单
     * @param {*} param0
     */
    async onTopMenuSelect({ item, key }) {
      this.setTopMenuId(item.id)
      this.selectedTopKeys = ref([key])

      // 查询菜单并等待完成
      await this.findRoleMenu()

      // 自动选择左侧菜单的第一个选项
      await this.autoSelectFirstLeftMenu()
    },
    /**
      * 自动选择左侧菜单的第一个选项
      */
    async autoSelectFirstLeftMenu() {
      if (this.menu.leftMenus && this.menu.leftMenus.length > 0) {
        const firstMenu = this.menu.leftMenus[0]
        let targetRouteName = ''

        if (firstMenu.childrens && firstMenu.childrens.length > 0) {
          // 如果有子菜单，选择第一个子菜单
          targetRouteName = firstMenu.childrens[0].routerName
        } else {
          // 如果没有子菜单，选择当前菜单
          targetRouteName = firstMenu.routerName
        }

        if (targetRouteName) {
          this.selectedLeftKeys = targetRouteName
          this.setLeftMenuId(targetRouteName)

          // 检查路由是否存在，如果不存在则跳转到默认路由
          try {
            // 尝试解析路由，如果路由不存在会抛出错误
            const route = this.$router.resolve({ name: targetRouteName })
            if (route.matched.length > 0) {
              // 路由存在，进行跳转
              await this.$router.replace({
                name: targetRouteName
              })
            } else {
              // 路由不存在，跳转到默认路由
              await this.$router.replace({
                name: 'data_source_management'
              })
            }
          } catch (error) {
            // 路由解析失败，跳转到默认路由
            console.warn(`路由 ${targetRouteName} 不存在，跳转到默认路由`)
            await this.$router.replace({
              name: 'data_source_management'
            })
          }

          // 强制更新视图，确保面包屑导航栏重新计算
          this.$forceUpdate()
        }
      }
    },
    /**
     * 选择左侧菜单
     * @param {*} param0
     */
    onLeftMenuSelect({ key }) {
      this.selectedLeftKeys = key
      this.setLeftMenuId(key)
      //路由跳转
      this.$router.push({
        name: key
      })
    },
    /**
     * 退出登陆
     */
    exit() {
      this.$router.push({
        name: 'login'
      })
    },
    formatDate(dateString) {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleString('zh-CN')
    },
    getStatusName(status) {
      const statusNames = {
        1: '正常',
        2: '禁用',
        3: '锁定'
      }
      return statusNames[status] || '未知状态'
    },
    getStatusBadge(status) {
      const statusBadges = {
        1: 'success',
        2: 'error',
        3: 'warning'
      }
      return statusBadges[status] || 'default'
    },
    handleDropdownVisible(open) {
      // 下拉菜单显示/隐藏时的处理
      console.log('Dropdown open:', open)
    },
    showUserProfile() {
      this.userDetailVisible = true
      // 如果userDetail为空，尝试从当前用户信息填充
      if (!this.userDetail.id && this.user.id) {
        this.userDetail = { ...this.user }
      }
    },
    showPasswordModal() {
      this.passwordModalVisible = true
      this.resetPasswordForm()
    },
    closePasswordModal() {
      this.passwordModalVisible = false
      this.resetPasswordForm()
    },
    resetPasswordForm() {
      this.passwordForm = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      if (this.$refs.passwordFormRef) {
        this.$refs.passwordFormRef.clearValidate()
      }
    },
    handlePasswordSubmit() {
      this.$refs.passwordFormRef.validate().then(() => {
        this.passwordLoading = true
        const params = {
          currentPassword: this.passwordForm.currentPassword,
          newPassword: this.passwordForm.newPassword
        }
        
        this.$http.request({
          url: '/personkit/sys/user/updatePassword',
          method: 'post',
          data: params
        })
          .then(response => {
            this.$message.success('密码修改成功，请重新登录')
            this.closePasswordModal()
            // 延迟退出登录，让用户看到成功消息
            setTimeout(() => {
              this.handleLogout()
            }, 1500)
          })
          .catch(error => {
            console.error('修改密码失败:', error)
            this.$message.error(error.message || '密码修改失败')
          })
          .finally(() => {
            this.passwordLoading = false
          })
      }).catch(error => {
        console.error('表单验证失败:', error)
      })
    },
    handleLogout() {
      this.$confirm({
        title: '确认退出',
        content: '确定要退出登录吗？',
        onOk: async () => {
          try {
            // 先清除用户存储
            this.clearUser()
            // 清除所有缓存
            localStorage.clear()
            sessionStorage.clear()
            
            this.$message.success('已退出登录')
            
            // 使用延迟确保消息显示和存储清除后再跳转
            setTimeout(() => {
              // 强制刷新页面，确保所有状态都被清除
              window.location.href = '/'
            }, 500)
          } catch (error) {
            console.error('退出登录失败:', error)
            // 即使出错也要确保跳转到登录页
            window.location.href = '/'
          }
        },
        onCancel() {
          console.log('取消退出')
        }
      })
    }
  },
  updated() {
    this.loadUserInfo()
  },
  created() {
    this.loadUserInfo()
  },

  components: {
    UserOutlined,
    DownOutlined,
    VerticalRightOutlined,
    LogoutOutlined
  }
})
</script>
<style scoped>
#components-layout-demo-top-side-2 .logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  background: rgba(255, 255, 255, 0.3);
}

.ant-row-rtl #components-layout-demo-top-side-2 .logo {
  float: right;
  margin: 16px 0 16px 24px;
}

.site-layout-background {
  background: #fff;
}

.root_view {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.header {
  position: fixed;
  z-index: 1000;
  width: 100%;
  height: 64px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  background: #001529;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.logo-container {
  flex-shrink: 0;
  margin-right: 24px;
}

.logo {
  color: white;
  font-size: 20px;
  font-weight: bold;
  letter-spacing: 1px;
  user-select: none;
}

.top-menu-container {
  flex: 1;
  min-width: 0;
}

.top-menu {
  line-height: 64px;
  font-size: 16px;
  border-bottom: none;
}

.user-info-container {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  margin-left: 24px;
}

.user-nickname {
  color: white;
  font-size: 14px;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-dropdown-trigger {
  padding: 4px 8px;
  border: none;
  background: transparent;
  color: white;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s ease;
}

.user-dropdown-trigger:hover {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

.trigger-avatar {
  background: #1890ff;
}

.dropdown-icon {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.user-dropdown-trigger:hover .dropdown-icon {
  transform: rotate(180deg);
}

.user-dropdown-menu {
  background: white;
  border-radius: 8px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  padding: 8px 0;
  min-width: 240px;
}

.user-info-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.user-avatar {
  background: #1890ff;
  flex-shrink: 0;
}

.user-info-text {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 16px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.user-role {
  font-size: 12px;
  color: #8c8c8c;
}

/* 主体内容区域 */
.main-container {
  display: flex;
  min-height: calc(100vh - 64px);
  margin-top: 64px;
  position: relative;
  width: 100%;
}

.sider {
  overflow: auto;
  height: calc(100vh - 64px);
  position: fixed;
  left: 0;
  top: 64px;
  bottom: 0;
  background: #fff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
  flex-shrink: 0;
}

.content-wrapper {
  flex: 1;
  margin-left: 200px;
  padding: 0 24px 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 64px);
  overflow: auto;
  display: flex;
  flex-direction: column;
}

.content-area {
  background: #fff;
  padding: 24px;
  margin: 0;
  min-height: calc(100vh - 64px - 48px - 48px); /* 视口高度 - header - padding - breadcrumb */
  max-height: calc(100vh - 64px - 48px - 48px); /* 添加最大高度限制 */
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 防止内容溢出 */
}

/* 移除旧的content样式，使用新的content-area */
.content {
  /* 保留为空，避免影响现有代码 */
}

/* 页面内容布局优化 */
.page-container {
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.page-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.page-table {
  flex: 1;
  overflow: auto;
}

.page-pagination {
  margin-top: auto;
  padding: 16px 24px;
  display: flex;
  justify-content: flex-end;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  min-height: 60px;
  align-items: center;
}

/* 表格宽度自适应 */
.ant-table {
  width: 100% !important;
}

.ant-table-content {
  overflow-x: auto !important;
}

/* 表格列宽优化 */
.ant-table-thead > tr > th {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ant-table-tbody > tr > td {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 300px;
}

/* 表格列宽自动分配 */
.ant-table-thead > tr > th:last-child,
.ant-table-tbody > tr > td:last-child {
  min-width: 120px;
}

/* 当表格列较少时，让表格填满可用宽度 */
.ant-table-wrapper {
  width: 100%;
}

.ant-table table {
  width: 100% !important;
}

/* 当表格列很少时，让表格填满整个容器 */
.ant-table-thead > tr > th,
.ant-table-tbody > tr > td {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 表格列很少时的特殊处理 */
.ant-table-thead > tr > th:first-child,
.ant-table-tbody > tr > td:first-child {
  min-width: 80px;
}

.ant-table-thead > tr > th:nth-child(2),
.ant-table-tbody > tr > td:nth-child(2) {
  min-width: 100px;
}

/* 表格内容区域最大高度限制 */
.ant-table-wrapper {
  max-height: calc(100vh - 400px);
  overflow: auto;
}

/* 表格滚动优化 */
.ant-table-body {
  max-height: calc(100vh - 500px) !important;
  overflow-y: auto !important;
}

/* 表格最小高度确保 */
.ant-table {
  min-height: 200px;
}

/* 表格内容自适应 */
.ant-table-tbody {
  max-height: calc(100vh - 550px);
  overflow-y: auto;
}

/* 表格内容过多时的处理 */
.ant-table-content {
  max-height: calc(100vh - 450px);
  overflow: auto;
}

/* 直接表格高度控制 - 针对数据量大的页面 */
.ant-table {
  max-height: calc(100vh - 500px) !important;
  overflow: hidden;
}

/* 表格body高度控制 */
.ant-table-body {
  max-height: calc(100vh - 550px) !important;
  overflow-y: auto !important;
}

/* 当表格数据很少时的最小高度 */
.ant-table-placeholder {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 表格页面内容优化 */
.table-page-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-height: 100%; /* 确保不会超出父容器 */
  overflow: hidden; /* 防止内容溢出 */
}

.table-container {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 300px;
  max-height: calc(100vh - 350px); /* 调整最大高度 */
}

/* 表格wrapper */
.table-wrapper {
  flex: 1;
  overflow: auto;
  padding: 0 24px;
  min-height: 300px;
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 400px); /* 限制最大高度 */
}

/* 表格自适应布局 */
.ant-table-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  max-height: calc(100vh - 350px); /* 默认最大高度 */
}

/* 针对内容区域的表格高度控制 */
.content-area .ant-table-wrapper {
  max-height: calc(100vh - 450px); /* 在content-area中的最大高度 */
}

/* 针对搜索区域后的表格高度调整 */
.search-area + .content-min-height .ant-table-wrapper {
  max-height: calc(100vh - 500px); /* 减去搜索区域的高度 */
}

/* 针对分页区域的表格高度调整 */
.ant-table-wrapper:has(+ .pagination-wrapper) {
  max-height: calc(100vh - 480px); /* 减去分页区域的高度 */
}

.ant-table {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.ant-table-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.ant-table-body {
  flex: 1;
  overflow: auto !important;
  max-height: none !important;
}

/* 针对数据量大的页面的特殊处理 */
.high-data-table .ant-table-body {
  max-height: calc(100vh - 600px) !important;
}

/* 针对数据量中等的页面的特殊处理 */
.medium-data-table .ant-table-body {
  max-height: calc(100vh - 500px) !important;
}

/* 分页区域样式 */
.pagination-wrapper {
  padding: 12px 24px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  min-height: 50px;
  flex-shrink: 0;
  margin-top: auto; /* 确保分页始终在底部 */
}

/* RouterView 内容样式 */
.router-view-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-height: 100%; /* 确保不会超出父容器 */
  overflow: auto;
}

/* 页面内容容器 */
.router-view-content > div {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-height: 100%;
}

/* 默认页面内容样式 */
.page-content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 搜索区域样式 */
.search-area {
  background: #fff;
  padding: 24px;
  border-radius: 8px 8px 0 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 0;
}

/* 搜索区域下方的内容区域样式 */
.search-area + .content-min-height {
  margin-top: 0;
}

.search-area + .content-min-height > .table-container,
.search-area + .content-min-height > div:first-child {
  border-radius: 0 0 8px 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 内容区域最小高度确保 */
.content-min-height {
  flex: 1;
  min-height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 用户详情弹窗样式 */
.user-detail-content {
  padding: 8px 0;
}

.user-detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.detail-avatar {
  background: #1890ff;
  flex-shrink: 0;
}

.user-detail-info {
  flex: 1;
  min-width: 0;
}

.detail-nickname {
  font-size: 18px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.detail-username {
  font-size: 14px;
  color: #8c8c8c;
}

.user-detail-descriptions {
  margin-top: 16px;
}

/* 修改密码弹窗样式 */
.password-form {
  padding: 16px 0;
}

.password-form .ant-form-item {
  margin-bottom: 24px;
}

.password-form .ant-form-item-label {
  font-weight: 500;
  color: #262626;
}

.password-form .ant-input-password {
  border-radius: 6px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .header {
    padding: 0 16px;
  }
  
  .user-nickname {
    max-width: 80px;
  }
}

@media (max-width: 768px) {
  .header {
    padding: 0 12px;
    height: 56px;
  }
  
  .logo {
    font-size: 18px;
  }
  
  .user-nickname {
    display: none;
  }
  
  .user-info-container {
    margin-left: 12px;
  }
  
  .main-container {
    margin-top: 56px;
    min-height: calc(100vh - 56px);
  }
  
  .sider {
    top: 56px;
    height: calc(100vh - 56px);
  }
  
  .content-wrapper {
    padding: 0 16px 16px;
  }
  
  .content-area {
    padding: 16px;
    min-height: calc(100vh - 56px - 32px - 32px); /* 移动端调整 */
  }
  
  .user-detail-header {
    flex-direction: column;
    text-align: center;
  }
  
  /* 移动端表格布局优化 */
  .table-wrapper {
    padding: 0 16px;
  }
  
  .pagination-wrapper {
    padding: 12px 16px;
  }
  
  /* 移动端密码表单适配 */
  .password-form .ant-form-item-label {
    text-align: left;
    padding-bottom: 8px;
  }
}

@media (max-width: 480px) {
  .header {
    padding: 0 8px;
  }
  
  .logo-container {
    margin-right: 12px;
  }
  
  .content-wrapper {
    margin-left: 0;
    padding: 0 12px 12px;
  }
  
  .content-area {
    padding: 12px;
  }
  
  .user-dropdown-menu {
    min-width: 200px;
  }
  
  /* 小屏幕下密码表单垂直布局 */
  .password-form .ant-form-item-label {
    text-align: left;
    padding-bottom: 8px;
  }
}

/* 暗色模式支持 */
@media (prefers-color-scheme: dark) {
  .content {
    background: #1f1f1f;
    color: #fff;
  }
  
  .user-detail-header {
    background: #141414;
  }
}
</style>
