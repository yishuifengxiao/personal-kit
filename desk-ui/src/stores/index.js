import { createPinia } from 'pinia'
import { createPersistedState } from 'pinia-plugin-persistedstate'

const pinia = createPinia()

// 将插件提供给 pinia
pinia.use(
  createPersistedState({
    storage: sessionStorage,
    auto: true
  })
)

export default pinia
