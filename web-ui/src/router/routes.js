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
        path: 'dataSet',
        name: 'dataset_management',
        component: () => import('@/views/kg/data_set/DataSet.vue'),

        meta: {
          label: '数据集管理',
          breadcrumbName: ['知识图谱', '数据中心', '数据集管理']
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
      }
    ]
  }
]
export default routes