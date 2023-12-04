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

