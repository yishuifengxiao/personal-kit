# 一  安装pinia 

文档 参见  [Pinia | Pinia (vuejs.org)](https://pinia.vuejs.org/zh/getting-started.html)  

```bash
yarn add pinia
# 或者使用 npm
npm install pinia
```

## 1.1 安装持久化插件 pinia-plugin-persistedstate 

文档 参见  [快速开始 | pinia-plugin-persistedstate (prazdevs.github.io)](https://prazdevs.github.io/pinia-plugin-persistedstate/zh/guide/) 

```
yarn add pinia-plugin-persistedstate

或 
npm i pinia-plugin-persistedstate
```

### 1.1.1 将插件添加到 pinia 实例上

```js
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
```

该插件的默认配置如下:

- 使用 [`localStorage`](https://developer.mozilla.org/en-US/docs/Web/API/Window/localStorage) 进行存储
- [`store.$id`](https://pinia.vuejs.org/api/interfaces/pinia.StoreProperties.html) 作为 storage 默认的 key
- 使用 [`JSON.stringify`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/JSON/stringify)/[`JSON.parse`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/JSON/parse) 进行序列化/反序列化
- 整个 state 默认将被持久化

### 1.1.2 全局持久化配置

文档参见  [进阶用法 | pinia-plugin-persistedstate (prazdevs.github.io)](https://prazdevs.github.io/pinia-plugin-persistedstate/zh/guide/advanced.html) 

 在安装插件之后，你可以使用 `createPersistedState` 来初始化插件。这些配置将会成为项目内所有 Store 的默认选项。 

```js
import { createPinia } from 'pinia'
import { createPersistedState } from 'pinia-plugin-persistedstate'

const pinia = createPinia()

pinia.use(
  createPersistedState({
    storage: sessionStorage,
  })
)
```

------

### 1.1.3 启用所有 Store 默认持久化

 该配置将会使所有 Store 持久化存储，且必须配置 `persist: false` 显式禁用持久化。 

```
import { createPinia } from 'pinia'
import { createPersistedState } from 'pinia-plugin-persistedstate'

const pinia = createPinia()

pinia.use(
  createPersistedState({
    auto: true,
  })
)
```



## 1.2 使用pinia

```js
// 创建一个 pinia 实例 (根 store) 并将其传递给应用 

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'

const pinia = createPinia()
const app = createApp(App)

app.use(pinia)
app.mount('#app')
```

### 1.2.1 增加业务文件

路径为 src\stores\user.js

```js
import {
  defineStore
} from 'pinia'

export const useUserStore = defineStore('useUserStore', {
  state: () => {
    return {
      token: '',
      currentPath: ''
    }
  },
  getters: {
    tokenVal: (state) => state.token,
    isLogin: (state) => typeof state.token != 'undefined' && state.token.trim().length > 0
  },
  actions: {
    setToken(payload) {
      this.token = payload
    },
    setCurrentFolder(val) {
      this.currentPath = val
    }
  },
  persist: true
})
```



# 二 安装Ant Design of Vue

文档地址  [Ant Design of Vue - Ant Design Vue (antdv.com)](https://next.antdv.com/docs/vue/introduce-cn#ant-design-of-vue) 

## 2.1 安装

```bash
$ npm install ant-design-vue@4.x --save

或

$ yarn add ant-design-vue@4.x
```

## 2.2  **全局完整注册** 

文档地址  [快速上手 - Ant Design Vue (antdv.com)](https://next.antdv.com/docs/vue/getting-started-cn) 



```js
import { createApp } from 'vue';
import Antd from 'ant-design-vue';
import App from './App';
import 'ant-design-vue/dist/reset.css';

const app = createApp(App);

app.use(Antd).mount('#app');
```

 `ant-design-vue` 默认支持基于 ES modules 的 tree shaking，直接引入 `import { Button } from 'ant-design-vue';` 就会有按需加载的效果。 



2.3 全局注册部分组件

```js
import { message } from 'ant-design-vue'
```

挂在到全局

```js
app.config.globalProperties.$msg = message
```

# 三 http请求工具

## 3.1 封装后的工具

```js
/* eslint-disable operator-linebreak */
import axios from 'axios'
import config from '@/config'

// 外部文件使用 pinia
import { useUserStore } from '@/stores/user'

axios.defaults.withCredentials = true
const regex = new RegExp(
  '^http[s]?://[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+.?(:[0-9]+)?'
)

class http {
  /**
   * 获取存储的token信息
   * @returns
   */
  getTokenVal() {
    const token = this.userStore.tokenVal
    if (typeof token === 'undefined') {
      return ''
    }
    return token
  }

  /**
   * 获取请求的请求头信息
   */
  headers(options) {
    const token = this.getTokenVal()
    let headers = typeof options.headers === 'undefined' ? {} : options.headers
    if (token.length === 0) {
      return headers
    }
    // debugger
    return Object.assign(headers, {
      Authorization: `xtoken ${token}`
    })
  }

  protocolAndDomainAndPort(url) {
    try {
      if (!url.match('^http[s]?://')) {
        return ''
      }
      const protocolAndDomainAndPort = url.match(/^http[s]?:\/\/[^/]+/i)[0]
      return protocolAndDomainAndPort
    } catch (e) {
      console.log(e)
    }
    return ''
  }

  /**
   * 从url中获取协议+域名+端口信息
   * @param {*} options
   */
  baseUrl(url) {
    let baseUrl = this.protocolAndDomainAndPort(url)
    if (baseUrl.length != 0) {
      // 请求的url是一个完整的url,包含了协议和域名
      return baseUrl
    }
    const dev = process.env.NODE_ENV === 'development'
    if (url.match(/^\/datawh-sql/gi)) {
      if (dev) {
        // url 为 datawh-sql 开头
        return 'http://10.8.19.7:8260'
      }
    } else if (url.match(/^\/idea/gi)) {
      // url 为 datawh-sql 开头
      return 'http://10.8.19.7:8080'
    }
    baseUrl = dev ? config.baseUrl.dev : config.baseUrl.prod
    return baseUrl
  }

  /**
   * 获取真正的url
   * 并在请求的URL后面携带上token信息
   * @param {*} options
   */
  url(url) {
    let baseUrl = this.protocolAndDomainAndPort(url)
    const uri = url.replace(baseUrl, '').trim()

    return uri
    // const token = this.getTokenVal()
    // if (token.length === 0) {
    //   return url
    // }
    // if (url.indexOf('?') != -1) {
    //   return url + `&xtoken=${token}`
    // }
    // return url + `?xtoken=${token}`
  }

  /**
   * 开始真正的请求
   * @param {*} options
   */
  request(options) {
    //定义一个全局的请求头
    const url = options.url

    const uri = this.url(url)
    const baseUrl = this.baseUrl(url)

    // console.log('======> url = ' + url + '  baseUrl = ' + baseUrl + ' uri= ' + uri)

    options.url = uri
    options.method =
      typeof options.method === 'undefined' || options.method.trim().length === 0
        ? 'get'
        : options.method

    const config = {
      baseURL: baseUrl,
      headers: this.headers(options)
    }
    const instance = axios.create()
    options = Object.assign(config, options)
    this.interceptors(instance, options.url)
    return instance(options)
  }

  destroy(url) {
    delete this.queue[url]
    if (!Object.keys(this.queue).length) {
      // Spin.hide()
    }
  }

  // 拦截器
  interceptors(instance, url) {
    const that = this
    // 请求拦截
    instance.interceptors.request.use(
      (config) => {
        // 添加全局的loading...
        if (!Object.keys(this.queue).length) {
          // Spin.show() // 不建议开启，因为界面不友好
        }
        this.queue[url] = true
        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )
    // 响应拦截
    instance.interceptors.response.use(
      (res) => {
        this.destroy(url)
        const { data, status } = res
        // return { data, status };
        // if (data.code !== 200) {
        //   this.$Message.error(data.msg);
        //   return false;
        // }
        if (status !== 200) {
          data.code = status
        }
        if (data.code === 401) {
          that.message.warn(data.msg, 5, () => {
            that.router.push({ name: 'login' })
          })
          return Promise.reject(Promise.reject(data.msg))
        }

        return data
        // return status === 200 ? data : { code: status };
      },
      (error) => {
        this.destroy(url)
        return Promise.reject(error)
      }
    )
  }

  constructor(pinia, router, message) {
    this.userStore = useUserStore(pinia)
    this.router = router
    this.queue = {}
    this.message = message
  }
}

export default http

```

## 3.2 全局注册http工具

```js
// 全局注册http工具
app.config.globalProperties.$http = new http(pinia, router, message)
```

# 四 配置vue-router

## 4.1 配置路由守卫

参考   路由守卫 https://router.vuejs.org/zh/guide/advanced/navigation-guards.html 

src\router\index.js

内容如下：

```
import { createRouter, createWebHashHistory } from 'vue-router'
import routes from './routes'
import { useUserStore } from '@/stores/user'
import meun from '@/libs/meun'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes,
  mode: 'hash'
})

router.beforeEach(async (to, from) => {
  // 路由守卫 https://router.vuejs.org/zh/guide/advanced/navigation-guards.html
  const store = useUserStore()
  const isLogin = store.isLogin
  console.log('======================> 登录状态为 ' + isLogin + '  登录token 为 ' + store.tokenVal)
  if (to.meta.requiresAuth) {
    // 需要登录
    if (
      // 检查用户是否已登录
      !isLogin &&
      // ❗️ 避免无限重定向
      to.name !== 'login'
    ) {
      // 将用户重定向到登录页面
      return { name: 'login' }
    }
  }
  const matched = to.matched
  let paths = new Array()
  if (null != matched && typeof matched != 'undefined') {
    matched.forEach((route) => {
      const flag = typeof route.meta.label != 'undefined'
      if (flag) {
        paths.push({ name: route.meta.label, val: route.path })
      }
    })
  }
  to.meta.paths = paths
})
//全局后置钩子
router.afterEach((to, from) => {
  // 获取左侧菜单
  const lctxn = meun(to, router.getRoutes())
  to.meta.lctxn = lctxn
  // localStorage.setItem('currentPosition', JSON.stringify(to))
})
export default router

```

## 4.2 增加路由配置

文件地址为 src\router\index.js



```js
const routes = [{
        path: '/',
        name: 'index',
        redirect: {
            name: 'login'
        }
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('@/views/Login.vue'),
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/view',
        component: () => import('@/views/Home.vue'),
        children: [{
                // 当 /user/:id/profile 匹配成功
                // UserProfile 将被渲染到 User 的 <router-view> 内部
                path: 'user',
                component: () => import('@/views/user/home.vue'),
                meta: {
                    label: '用户管理'
                },
                children: [{
                        path: 'userIndex',
                        name: 'userIndexName',
                        component: () => import('@/views/user/index.vue'),
                        meta: {
                            requiresAuth: true,
                            topLevel: 'user',
                            label: '用户列表'
                        }
                    },
                    {
                        path: 'userRecord',
                        name: 'userRecordName',
                        component: () => import('@/views/user/record.vue'),
                        meta: {
                            requiresAuth: true,
                            topLevel: 'user',
                            label: '登录记录'
                        }
                    }
                ]
            },
            {
                // 当 /user/:id/profile 匹配成功
                // UserProfile 将被渲染到 User 的 <router-view> 内部
                path: 'file',
                component: () => import('@/views/files/home.vue'),
                meta: {
                    label: '文件管理'
                },
                children: [{
                        path: 'fxindex',
                        name: 'fxindexName',
                        component: () => import('@/views/files/index.vue'),
                        meta: {
                            requiresAuth: true,
                            topLevel: 'disk',
                            label: '个人网盘'
                        }
                    },
                    {
                        path: 'fxUpload',
                        name: 'fxUploadName',
                        component: () => import('@/views/files/upload.vue'),
                        meta: {
                            requiresAuth: true,
                            topLevel: 'disk',
                            label: '断点上传'
                        }
                    }
                ]
            },
            {
                // 当 /user/:id/profile 匹配成功
                // UserProfile 将被渲染到 User 的 <router-view> 内部
                path: 'spider',
                component: () => import('@/views/spider/home.vue'),
                meta: {
                    label: '爬虫'
                },
                children: [{
                        path: 'spindex',
                        name: 'spindexIndexName',
                        component: () => import('@/views/spider/index.vue'),
                        meta: {
                            requiresAuth: true,
                            topLevel: 'spider',
                            label: '爬虫管理'
                        }
                    },
                    {
                        path: 'spData',
                        name: 'spindexDataName',
                        component: () => import('@/views/spider/data.vue'),
                        meta: {
                            requiresAuth: true,
                            topLevel: 'spider',
                            label: '数据中心'
                        }
                    },
                    {
                        path: 'spOut',
                        name: 'spindexOutName',
                        component: () => import('@/views/spider/out.vue'),
                        meta: {
                            requiresAuth: true,
                            topLevel: 'spider',
                            label: '导出记录'
                        }
                    }
                ]
            },
            {
                // 当 /user/:id/profile 匹配成功
                // UserProfile 将被渲染到 User 的 <router-view> 内部
                path: 'sql',
                component: () => import('@/views/sql/home.vue'),
                meta: {
                    label: '数据管理'
                },
                children: [{
                        path: 'sqlIndex',
                        name: 'sqlIndexName',
                        component: () => import('@/views/sql/sql.vue'),
                        meta: {
                            requiresAuth: false,
                            topLevel: 'sql',
                            label: 'SQL编排'
                        }
                    },
                    {
                        path: 'sqlData',
                        name: 'sqlDataName',
                        component: () => import('@/views/sql/data.vue'),
                        meta: {
                            requiresAuth: false,
                            topLevel: 'sql',
                            label: '数据中心'
                        }
                    },
                    {
                        path: 'sqlPreview',
                        name: 'sqlPreviewName',
                        component: () => import('@/views/sql/preview.vue'),
                        meta: {
                            requiresAuth: false,
                            topLevel: 'sql',
                            label: '数据导入'
                        }
                    }
                ]
            }
        ]
    },
    {
        path: '/sql111111111',
        name: 'sql111111111',
        component: () => import('@/views/sql/sql.vue')
    }
]
export default routes
```

## 4.3 修改main.js

```js
import router from './router'

app.use(router)
```

