const routes = [
  {
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
  },
  {
    path: '/search',
    name: 'search',
    component: () => import('@/views/search/MySearch.vue'),
    meta: {
      requiresAuth: false,
      label: '时光搜索',
      breadcrumbName: ['时光搜索']
    }
  },
  {
    path: '/my_search',
    name: 'root_my_search',
    component: () => import('@/views/search/MySearch.vue'),
    meta: {
      requiresAuth: false,
      label: '时光搜索',
      breadcrumbName: ['时光搜索']
    }
  },
  {
    path: '/result',
    name: 'result',
    component: () => import('@/views/search/SearchResults.vue'),
    meta: {
      requiresAuth: false,
      label: '搜索结果',
      breadcrumbName: ['拾光搜索', '搜索结果']
    }
  },
  {
    path: '/view',
    name: 'home',
    component: () => import('@/views/HomeView.vue'),
    redirect: {
      name: 'data_source_management'
    },
    children: [
      {
        path: '',
        name: 'data_source_management',
        component: () => import('@/views/kg/data_source/DataSource.vue'),
        meta: {
          label: '数据源',
          breadcrumbName: ['知识图谱', '数据中心', '数据源管理']
        }
      },
      {
        path: 'detail',
        name: 'data_source_detail',
        component: () => import('@/views/kg/data_source/DataDetail.vue'),
        props: (route) => ({
          record: route.query.record
        }),

        meta: {
          label: '数据详情',
          breadcrumbName: ['知识图谱', '数据中心', '数据详情']
        }
      },
      {
        path: 'upload_history',
        name: 'upload_history',
        component: () => import('@/views/kg/data_source/UploadHistory.vue'),
        meta: {
          label: '上传历史',
          breadcrumbName: ['知识图谱', '数据中心', '上传历史']
        }
      },
      {
        path: 'dataset_management',
        name: 'dataset_management',
        component: () => import('@/views/kg/data_set/DataSet.vue'),

        meta: {
          label: '数据集管理',
          breadcrumbName: ['知识图谱', '数据中心', '数据集管理']
        }
      },
      {
        path: 'all_spider',
        name: 'all_spider',
        component: () => import('@/views/spider/AllSpider.vue'),
        meta: {
          label: '爬虫总览',
          breadcrumbName: ['知识图谱', '爬虫总览']
        }
      },
      {
        path: 'spider_records',
        name: 'spider_records',
        component: () => import('@/views/spider/SpiderRecords.vue'),
        meta: {
          label: '爬虫记录',
          breadcrumbName: ['知识图谱', '爬虫记录']
        }
      },
      {
        path: 'crawl_records/:spiderId',
        name: 'crawl_records',
        component: () => import('@/views/spider/CrawlRecords.vue'),
        meta: { title: '抓取记录' }
      },
      {
        path: 'crawl_data/:spiderId',
        name: 'crawl_data',
        component: () => import('@/views/spider/CrawlData.vue'),
        props: true,
        meta: { title: '抓取数据' }
      },
      {
        path: 'crawl_data/:spiderId/:recordId',
        name: 'crawl_data_detail',
        component: () => import('@/views/spider/CrawlDataDetail.vue'),
        props: true,
        meta: { title: '抓取数据详情' }
      },
      {
        path: 'spider_task',
        name: 'spider_task',
        component: () => import('@/views/spider_task/SpiderTask.vue'),
        meta: {
          label: '任务总览',
          breadcrumbName: ['知识图谱', '任务总览']
        }
      },
      {
        path: 'spider_data',
        name: 'spider_data',
        component: () => import('@/views/spider_data/SpiderData.vue'),
        meta: {
          label: '数据仓库',
          breadcrumbName: ['知识图谱', '数据仓库']
        }
      },
      {
        path: 'add_spider',
        name: 'add_spider',
        component: () => import('@/views/spider/AddSpider.vue'),
        meta: {
          label: '新增爬虫',
          breadcrumbName: ['知识图谱', '新增爬虫']
        }
      },
      {
        path: 'edit_spider',
        name: 'edit_spider',
        component: () => import('@/views/spider/EditSpider.vue'),
        props: (route) => ({
          id: route.query.id
        }),
        meta: {
          label: '编辑爬虫',
          breadcrumbName: ['知识图谱', '编辑爬虫']
        }
      },
      {
        path: 'ont',
        name: 'ontology_management',
        component: () => import('@/views/kg/ont/Ont.vue'),

        meta: {
          label: '本体管理',
          breadcrumbName: ['知识图谱', '图谱中心', '本体管理']
        }
      },
      {
        path: 'ont_detail',
        name: 'ontology_detail',
        component: () => import('@/views/kg/ont/OntDetail.vue'),
        props: (route) => ({
          id: route.query.id,
          isAdd: route.query.isAdd
        }),
        meta: {
          label: '本体管理',
          breadcrumbName: ['知识图谱', '图谱中心', '本体详情']
        }
      },
      {
        path: 'graph',
        name: 'graph_management',
        component: () => import('@/views/kg/graph/Graph.vue'),

        meta: {
          label: '本体管理',
          breadcrumbName: ['知识图谱', '图谱中心', '图谱管理']
        }
      },
      {
        path: 'graph_config',
        name: 'graph_config',
        component: () => import('@/views/kg/graph/GraphConfigOnt.vue'),

        meta: {
          label: '本体管理',
          breadcrumbName: ['知识图谱', '图谱中心', '图谱配置', '配置本体']
        }
      },
      {
        path: 'graph_config_data',
        name: 'graph_config_data',
        component: () => import('@/views/kg/graph/GraphConfigData.vue'),
        props: (route) => ({
          id: route.query.id
        }),
        meta: {
          label: '本体管理',
          breadcrumbName: ['知识图谱', '图谱中心', '图谱配置', '配置数据源']
        }
      },
      {
        path: 'user_list',
        name: 'user_list',
        component: () => import('@/views/sys/user_list/Index.vue'),
        props: (route) => ({
          id: route.query.id
        }),
        meta: {
          label: '系统管理',
          breadcrumbName: ['系统管理', '用户管理', '用户列表']
        }
      },
      {
        path: 'role_manager',
        name: 'role_manager',
        component: () => import('@/views/sys/role_list/Index.vue'),
        props: (route) => ({
          id: route.query.id
        }),
        meta: {
          label: '系统管理',
          breadcrumbName: ['系统管理', '角色管理', '角色列表']
        }
      },
      {
        path: 'menu_manager',
        name: 'menu_manager',
        component: () => import('@/views/sys/menu_list/Index.vue'),
        props: (route) => ({
          id: route.query.id
        }),
        meta: {
          label: '系统管理',
          breadcrumbName: ['系统管理', '菜单管理', '菜单列表']
        }
      },
      {
        path: 'menu_permission_management',
        name: 'menu_permission_management',
        component: () => import('@/views/sys/menu_list/PermissionManagement.vue'),
        props: (route) => ({
          menuId: route.query.menuId
        }),
        meta: {
          label: '菜单权限管理',
          breadcrumbName: ['系统管理', '菜单管理', '权限管理']
        }
      },
      {
        path: 'perssion_mananer',
        name: 'perssion_mananer',
        component: () => import('@/views/sys/resource_list/Index.vue'),
        props: (route) => ({
          id: route.query.id
        }),
        meta: {
          label: '系统管理',
          breadcrumbName: ['系统管理', '权限管理', '权限列表']
        }
      },
      {
        path: 'login_record',
        name: 'login_record',
        component: () => import('@/views/sys/visit_list/Index.vue'),
        props: (route) => ({
          id: route.query.id
        }),
        meta: {
          label: '系统管理',
          breadcrumbName: ['系统管理', '认证管理', '认证列表']
        }
      },
      {
        path: 'personal_cloud',
        name: 'personal_cloud',
        component: () => import('@/views/personal_cloud/Index.vue'),
        meta: {
          label: '个人云',
          breadcrumbName: ['在线网盘','个人云', '文件管理']
        }
      },
      {
        path: 'transport_list',
        name: 'transport_list',
        component: () => import('@/views/personal_cloud/TransportList.vue'),
        meta: {
          label: '传输列表',
          breadcrumbName: ['在线网盘','个人云', '传输列表']
        }
      },
      {
        path: 'my_share',
        name: 'my_share',
        component: () => import('@/views/personal_cloud/MyShare.vue'),
        meta: {
          label: '我的分享',
          breadcrumbName: ['在线网盘','个人云', '我的分享']
        }
      },
      {
        path: 'http_log',
        name: 'http_log',
        component: () => import('@/views/sys/http_log/Index.vue'),
        props: (route) => ({
          id: route.query.id
        }),
        meta: {
          label: '系统管理',
          breadcrumbName: ['系统管理', '认证管理', '访问记录']
        }
      },
      {
        path: 'role_menu_management',
        name: 'role_menu_management',
        component: () => import('@/views/sys/role_list/MenuManagement.vue'),
        props: (route) => ({
          roleId: route.query.roleId
        }),
        meta: {
          label: '角色菜单管理',
          breadcrumbName: ['系统管理', '角色管理', '菜单配置']
        }
      },
      {
        path: 'search_source',
        name: 'search_source',
        component: () => import('@/views/search_source/Index.vue'),
        meta: {
          label: '搜索数据源管理',
          breadcrumbName: ['系统管理', '时光搜索', '数据源管理']
        }
      },
      {
        path: 'my_search',
        name: 'my_search',
        component: () => import('@/views/search_source/Index.vue'),
        meta: {
          label: '时光搜索',
          breadcrumbName: ['系统管理', '时光搜索']
        }
      },
      {
        path: 'search_analyze',
        name: 'search_analyze',
        component: () => import('@/views/sys/search_analyze/Index.vue'),
        meta: {
          label: '搜索分析',
          breadcrumbName: ['系统管理', '搜索分析']
        }
      }
    ]
  }
]
export default routes