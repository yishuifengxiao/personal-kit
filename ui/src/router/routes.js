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
    component: () => import('@/views/HomeView.vue'),
    children: [
        {

            path: '',
            name: "data_source_management",
            component: () => import('@/views/kg/DataSource.vue'),
            meta:{
                label:"数据源"
            }
        },
        {

            path: 'dataSet',
            name: "dataset_management",
            component: () => import('@/views/kg/DataSet.vue'),
            meta:{
                label:"数据集"
            }
        },
    ]
}
]
export default routes