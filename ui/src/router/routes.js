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