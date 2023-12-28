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
    redirect: { name: "data_source_management" },
    children: [{

        path: '',
        name: "data_source_management",
        component: () => import('@/views/kg/data_source/DataSource.vue'),
        meta: {
            label: "数据源",
            breadcrumbName: ["知识图谱", "数据中心", "数据源管理"]
        }
    },
    {

        path: 'detail',
        name: "data_source_detail",
        component: () => import('@/views/kg/data_source/DataDetail.vue'),
        props: route => ({
            record: route.query.record
        }),

        meta: {
            label: "数据详情",
            breadcrumbName: ["知识图谱", "数据中心", "数据详情"]
        }
    },
    {

        path: 'dataSet',
        name: "dataset_management",
        component: () => import('@/views/kg/data_set/DataSet.vue'),

        meta: {
            label: "数据集",
            breadcrumbName: ["知识图谱", "数据中心", "数据集管理"]
        }
    },
    ]
}
]
export default routes