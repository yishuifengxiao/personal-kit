-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` (`id`, `auth`, `description`, `idx`, `name`, `parent_id`, `type`, `is_show`, `router_name`, `router_path`) VALUES ('1', '0', NULL, '1', '知识图谱', '0', '0', '1', 'knowledge_graph', NULL);
INSERT INTO `sys_menu` (`id`, `auth`, `description`, `idx`, `name`, `parent_id`, `type`, `is_show`, `router_name`, `router_path`) VALUES ('1001', '0', NULL, '1', '数据中心', '1', '1', '1', 'data_center', NULL);
INSERT INTO `sys_menu` (`id`, `auth`, `description`, `idx`, `name`, `parent_id`, `type`, `is_show`, `router_name`, `router_path`) VALUES ('100101', '0', NULL, '1', '数据源管理', '1001', '1', '1', 'data_source_management', NULL);
INSERT INTO `sys_menu` (`id`, `auth`, `description`, `idx`, `name`, `parent_id`, `type`, `is_show`, `router_name`, `router_path`) VALUES ('100102', '0', NULL, '2', '数据集管理', '1001', '1', '1', 'dataset_management', NULL);
INSERT INTO `sys_menu` (`id`, `auth`, `description`, `idx`, `name`, `parent_id`, `type`, `is_show`, `router_name`, `router_path`) VALUES ('1002', '0', NULL, '2', '图谱中心', '1', '1', '1', 'graph_center', NULL);
INSERT INTO `sys_menu` (`id`, `auth`, `description`, `idx`, `name`, `parent_id`, `type`, `is_show`, `router_name`, `router_path`) VALUES ('100201', '0', NULL, '1', '本体管理', '1002', '1', '1', 'ontology_management', NULL);
INSERT INTO `sys_menu` (`id`, `auth`, `description`, `idx`, `name`, `parent_id`, `type`, `is_show`, `router_name`, `router_path`) VALUES ('100202', '0', NULL, '2', '图谱管理', '1002', '1', '1', 'graph_management', NULL);
INSERT INTO `sys_menu` (`id`, `auth`, `description`, `idx`, `name`, `parent_id`, `type`, `is_show`, `router_name`, `router_path`) VALUES ('1003', '0', NULL, '3', '图谱应用', '1', '1', '1', 'graph_application', NULL);
INSERT INTO `sys_menu` (`id`, `auth`, `description`, `idx`, `name`, `parent_id`, `type`, `is_show`, `router_name`, `router_path`) VALUES ('2', '0', NULL, '2', '在线网盘', '0', '0', '1', 'online_cloud_storage', NULL);
INSERT INTO `sys_menu` (`id`, `auth`, `description`, `idx`, `name`, `parent_id`, `type`, `is_show`, `router_name`, `router_path`) VALUES ('3', '0', NULL, '3', '数据爬虫', '0', '0', '1', 'data_crawler', NULL);
INSERT INTO `sys_menu` (`id`, `auth`, `description`, `idx`, `name`, `parent_id`, `type`, `is_show`, `router_name`, `router_path`) VALUES ('4', '0', NULL, '4', '垂直搜索', '0', '0', '1', 'vertical_search', NULL);
INSERT INTO `sys_menu` (`id`, `auth`, `description`, `idx`, `name`, `parent_id`, `type`, `is_show`, `router_name`, `router_path`) VALUES ('5', '0', NULL, '5', '在线工具', '0', '0', '1', 'online_tools', NULL);
INSERT INTO `sys_menu` (`id`, `auth`, `description`, `idx`, `name`, `parent_id`, `type`, `is_show`, `router_name`, `router_path`) VALUES ('6', '0', NULL, '6', '系统管理', '0', '0', '1', 'system_management', NULL);
INSERT INTO `sys_menu` (`id`, `auth`, `description`, `idx`, `name`, `parent_id`, `type`, `is_show`, `router_name`, `router_path`) VALUES ('7', '0', NULL, '7', '默认菜单', '0', '0', '0', 'default_menu', NULL);

