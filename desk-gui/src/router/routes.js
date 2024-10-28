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
			requiresAuth: false
		}
	}
]
export default routes