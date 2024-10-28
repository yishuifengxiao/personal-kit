import './assets/main.css'

import {
    createApp
} from 'vue'



// 安装ant-design-vue
import Antd from 'ant-design-vue';

import App from './App.vue'
import router from './router'





// 引入stores
import pinia from './stores'


// 安装ant-design-vue
import 'ant-design-vue/dist/reset.css';


// 注册全局配置
import cfg from "./config"

// 全局注册 message
import {
    message, Modal
} from 'ant-design-vue'

// 全局注册http工具
import http from './api/http.js'



const app = createApp(App)


app.use(router)

// 引入stores
app.use(pinia)

// 安装ant-design-vue
app.use(Antd)


// 全局注册 message
app.config.globalProperties.$msg = message
// 注册全局配置
app.config.globalProperties.$cfg = cfg
// 全局注册http工具
app.config.globalProperties.$http = new http(pinia, router, message,Modal)


app.mount('#app')