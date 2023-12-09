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
    name: 'home',
    component: () => import('@/views/HomeView.vue')
}
]
export default routes