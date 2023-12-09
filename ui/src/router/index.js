import {
  createRouter,
  createWebHistory
} from 'vue-router'
import routes from './routes'
import {
  useUserStore
} from '@/stores/user'
import meun from '@/libs/meun'

const router = createRouter({
  history: createWebHistory(
    import.meta.env.BASE_URL),
  routes: routes,
  mode: 'hash'
})

router.beforeEach(async (to, from) => {
  // 路由守卫 https://router.vuejs.org/zh/guide/advanced/navigation-guards.html
  const store = useUserStore()
  const isLogin = store.isLogin
  if (to.meta.requiresAuth) {
    // 需要登录
    if (
      // 检查用户是否已登录
      !isLogin &&
      // ❗️ 避免无限重定向
      to.name !== 'login'
    ) {
      // 将用户重定向到登录页面
      return {
        name: 'login'
      }
    }
  }
  const matched = to.matched
  let paths = new Array()
  if (null != matched && typeof matched != 'undefined') {
    matched.forEach((route) => {
      const flag = typeof route.meta.label != 'undefined'
      if (flag) {
        paths.push({
          name: route.meta.label,
          val: route.path
        })
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