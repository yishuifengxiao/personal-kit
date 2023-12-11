<template>
  <a-layout class="root_view">
    <!-- 顶部导航栏 -->
    <a-layout-header
      class="header"
      style="vertical-align: middle; display: flex; float: right; font-size: 1rem"
    >
      <div class="logo" style="width: 10vw" />
      <div :style="{ lineHeight: '64px', width: '70vw', 'font-size': '1rem !important' }">
        <!-- 顶部菜单 -->
        <a-menu
          v-model:selectedKeys="selectedTopKeys"
          theme="dark"
          mode="horizontal"
          @select="onTopMenuSelect"
          :style="{ lineHeight: '64px', 'font-size': '1.3rem !important' }"
        >
          <a-menu-item v-for="item in menu.topMenus" :key="item.routerName">{{
            item.name
          }}</a-menu-item>
        </a-menu>
        <!-- 顶部菜单 -->
      </div>

      <!-- 头部菜单最右侧 -->
      <div
        :style="{
          lineHeight: '64px',
          color: 'white',
          float: 'right',
          width: '20vw',
          display: 'inline-block',
          'text-align': 'right',
          'vertical-align': 'middle',
          'padding-right': '-1vw'
        }"
      >
        <span>{{ user.nickname }}</span>
        <a-dropdown>
          <a class="ant-dropdown-link" @click.prevent>
            <a-avatar size="large" style="position: relative; top: -1vh">
              <template #icon>
                <UserOutlined />
              </template>
            </a-avatar>
            <DownOutlined />
          </a>
          <template #overlay>
            <a-menu>
              <a-menu-item>
                <a href="javascript:;">基本信息</a>
              </a-menu-item>
              <a-menu-item>
                <a href="javascript:;">修改密码</a>
              </a-menu-item>
              <a-menu-item>
                <a href="javascript:;">退出</a>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
      <!-- 头部菜单最右侧 -->
    </a-layout-header>
    <!-- 顶部导航栏 -->
    <a-layout>
      <a-layout-sider width="200" style="background: #fff">
        <!-- 左侧菜单 -->
        <a-menu
          v-model:selectedKeys="selectedLeftKeys"
          v-model:openKeys="openKeys"
          mode="inline"
          :items="leftMenuSource"
          :style="{ height: '100%', borderRight: 0 }"
        >
        </a-menu>
        <!-- 左侧菜单 -->
      </a-layout-sider>
      <a-layout style="padding: 0 24px 24px">
        <a-breadcrumb style="margin: 16px 0">
          <a-breadcrumb-item>Home</a-breadcrumb-item>
          <a-breadcrumb-item>List</a-breadcrumb-item>
          <a-breadcrumb-item>App</a-breadcrumb-item>
        </a-breadcrumb>
        <a-layout-content
          :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >
          Content {{ menu.topMenus }}
          <br />     <br />
          == currentTopMenuId========= {{ currentTopMenuId }}

          <br />

          === selectedTopKeys ==== {{ selectedTopKeys }}

          <br />

          <br />

          ===== leftMenuSource = {{ leftMenuSource }}
        </a-layout-content>
      </a-layout>
    </a-layout>
  </a-layout>
</template>
<script>
import { ref, reactive, defineComponent } from 'vue'
import { mapState, mapActions } from 'pinia'
import { useUserStore } from '@/stores/user'
import {
  UserOutlined,
  LaptopOutlined,
  NotificationOutlined,
  DownOutlined
} from '@ant-design/icons-vue'
export default defineComponent({
  data() {
    const user = reactive({})
    const menu = reactive({
      leftMenus: [],
      topMenus: []
    })
    const selectedTopKeys = ref(['knowledge_graph'])
    const selectedLeftKeys = ref(['1'])
    const openKeys = ref(['sub1'])
    return { user, menu, selectedTopKeys, selectedLeftKeys, openKeys }
  },
  computed: {
    ...mapState(useUserStore, ['currentUserId', 'currentRoleId', 'currentTopMenuId']),

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
    }
  },
  methods: {
    ...mapActions(useUserStore, ['setRole', 'setUser', 'setTopMenuId']),
    /**
     * 加载用户基本信息
     */
    async loadUserInfo() {
      this.$http
        .request({
          url: '/personkit/user/info/' + this.currentUserId
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
      this.$http
        .request({
          url: '/personkit/menu/findRoleMenu',
          method: 'post',
          data: {
            roleId: this.currentRoleId,
            topMenuId: this.currentTopMenuId
          }
        })
        .then((res) => {
          this.menu = reactive(res)

          // 上部选择的菜单
          if (null !== this.currentTopMenuId && typeof this.currentTopMenuId !== 'undefined') {
            const topMenuId = res.topMenus[0].routerName
            this.setTopMenuId(topMenuId)
            this.selectedTopKeys = ref([topMenuId])
          }

          // 左侧选择的菜单
        })
        .catch((err) => console.log(err))
    },
    /**
     * 选择顶部菜单
     * @param {*} param0
     */
    onTopMenuSelect({ key }) {
      this.setTopMenuId(key)
      this.selectedTopKeys = ref([key])
      //查询菜单
      // this.findRoleMenu()
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
    LaptopOutlined,
    NotificationOutlined,
    DownOutlined
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
}
</style>
