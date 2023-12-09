<template>
  <a-layout class="root_view">
    <a-layout-header class="header" style="vertical-align: middle; display: flex; float: right; font-size: 1.3rem">
      <div class="logo" style="width: 10vw" />
      <div :style="{ lineHeight: '64px', width: '70vw', 'font-size': '1.3rem !important' }">
        <a-menu v-model:selectedKeys="selectedKeys1" theme="dark" mode="horizontal"
          :style="{ lineHeight: '64px', 'font-size': '1.3rem !important' }">
          <a-menu-item key="1">知识图谱</a-menu-item>
          <a-menu-item key="2">在线网盘</a-menu-item>
          <a-menu-item key="3">数据爬虫</a-menu-item>
          <a-menu-item key="4">垂直搜索</a-menu-item>
          <a-menu-item key="5">在线工具</a-menu-item>
          <a-menu-item key="6">系统管理</a-menu-item>
        </a-menu>
      </div>

      <!-- 头部菜单最右侧 -->
      <div :style="{
        lineHeight: '64px',
        color: 'white',
        float: 'right',
        width: '20vw',
        display: 'inline-block',
        'text-align': 'right',
        'vertical-align': 'middle',
        'padding-right': '-1vw'
      }">
        <span>易水</span>
        <a-dropdown>
          <a class="ant-dropdown-link" @click.prevent>

            <a-avatar size="large" style="position: relative;top: -1vh;">
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
    <a-layout>
      <a-layout-sider width="200" style="background: #fff">
        <a-menu v-model:selectedKeys="selectedKeys2" v-model:openKeys="openKeys" mode="inline"
          :style="{ height: '100%', borderRight: 0 }">
          <a-sub-menu key="sub1">
            <template #title>
              <span>
                <user-outlined />
               数据中心
              </span>
            </template>
            <a-menu-item key="1">数据源管理</a-menu-item>
            <a-menu-item key="2">数据集管理</a-menu-item>
          </a-sub-menu>
          <a-sub-menu key="sub2">
            <template #title>
              <span>
                <laptop-outlined />
                图谱中心
              </span>
            </template>
      
            <a-menu-item key="6">本体管理</a-menu-item>
            <a-menu-item key="7">图谱管理</a-menu-item>
            <a-menu-item key="8">图谱应用</a-menu-item>
          </a-sub-menu>
          <a-sub-menu key="sub3">
            <template #title>
              <span>
                <notification-outlined />
                subnav 3
              </span>
            </template>
            <a-menu-item key="9">option9</a-menu-item>
            <a-menu-item key="10">option10</a-menu-item>
            <a-menu-item key="11">option11</a-menu-item>
            <a-menu-item key="12">option12</a-menu-item>
          </a-sub-menu>
        </a-menu>
      </a-layout-sider>
      <a-layout style="padding: 0 24px 24px">
        <a-breadcrumb style="margin: 16px 0">
          <a-breadcrumb-item>Home</a-breadcrumb-item>
          <a-breadcrumb-item>List</a-breadcrumb-item>
          <a-breadcrumb-item>App</a-breadcrumb-item>
        </a-breadcrumb>
        <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
          Content
        </a-layout-content>
      </a-layout>
    </a-layout>
  </a-layout>
</template>
<script>
import { ref, defineComponent } from 'vue'

import { UserOutlined, LaptopOutlined, NotificationOutlined, AntDesignOutlined } from '@ant-design/icons-vue'
export default defineComponent({
  setup() {
    const selectedKeys1 = ref(['2'])
    const selectedKeys2 = ref(['1'])
    const openKeys = ref(['sub1'])

    return { selectedKeys1, selectedKeys2, openKeys }
  },
  components: {
    UserOutlined,
    LaptopOutlined,
    NotificationOutlined,
    AntDesignOutlined
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
