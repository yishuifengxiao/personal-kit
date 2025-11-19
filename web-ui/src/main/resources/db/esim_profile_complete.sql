-- eSIM Profile 完整表结构
-- 基于 ProfileForm.vue 中的完整数据结构创建

CREATE TABLE `esim_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  
  -- 核心标识信息
  `iccid` varchar(20) NOT NULL COMMENT 'ICCID，20位16进制字符串',
  `matching_id` varchar(50) DEFAULT NULL COMMENT 'Matching ID，最大长度50',
  `eid` varchar(32) DEFAULT NULL COMMENT 'EID，32位16进制字符串',
  
  -- 状态信息
  `profile_status` varchar(20) DEFAULT 'Available' COMMENT 'Profile状态：Available, Allocated, Linked, Confirmed, Released, Downloaded, Installed, Error, Unavailable',
  `notification_status` varchar(20) DEFAULT 'Enabled' COMMENT '通知状态：Enabled, Disabled, Deleted',
  
  -- 业务配置
  `download_method` varchar(20) DEFAULT '1' COMMENT '下载方式：1-标准下载, 2-快速下载, 3-批量下载, default-默认SM-DP+, activation-激活码, alt-smds-ALT-SM-DS, root-smds-ROOT-SM-DS',
  `tenant` varchar(50) NOT NULL COMMENT '所属租户',
  `carrier` varchar(20) DEFAULT NULL COMMENT '运营商：中国移动, 中国联通, 中国电信, 中国广电',
  `profile_class` varchar(20) DEFAULT 'operational' COMMENT 'Profile类别：operational, test, provisioning',
  `ppr_policy` varchar(10) DEFAULT 'PPR1' COMMENT 'PPR策略：PPR1, PPR2, PPR3',
  `reset_rule` varchar(20) DEFAULT '1' COMMENT '重置规则：1-允许重置, 2-禁止重置, 3-条件重置, no_reset-不可重置, resetable-可重置, auto_reset-自动重置, auto_recycle-自动回收',
  `ds_flag` varchar(20) DEFAULT 'flag1' COMMENT 'DS标识：flag1, flag2, flag3',
  
  -- 基本信息
  `confirmation_code` varchar(50) DEFAULT NULL COMMENT '确认码',
  `profile_name` varchar(100) DEFAULT NULL COMMENT 'Profile名称',
  `phone_number` varchar(20) DEFAULT NULL COMMENT '电话号码',
  `service_provider` varchar(50) DEFAULT NULL COMMENT '服务提供商',
  `notification_event` varchar(50) DEFAULT NULL COMMENT '通知事件',
  `notification_address` varchar(200) DEFAULT NULL COMMENT '通知地址URL',
  `profile_icon` varchar(50) DEFAULT NULL COMMENT 'Profile图标',
  
  -- 码号信息
  `imsi` varchar(15) DEFAULT NULL COMMENT 'IMSI，15位数字',
  `imsi2` varchar(15) DEFAULT NULL COMMENT 'IMSI2，15位数字',
  `pin1` varchar(10) DEFAULT NULL COMMENT 'PIN1',
  `pin2` varchar(10) DEFAULT NULL COMMENT 'PIN2',
  `puk1` varchar(10) DEFAULT NULL COMMENT 'PUK1',
  `puk2` varchar(10) DEFAULT NULL COMMENT 'PUK2',
  `adm1` varchar(10) NOT NULL COMMENT 'ADM1',
  `ki` varchar(20) DEFAULT NULL COMMENT 'KI密钥',
  `opc` varchar(20) DEFAULT NULL COMMENT 'OPC密钥',
  `smsp` varchar(20) DEFAULT NULL COMMENT 'SMSP',
  
  -- V3功能支持 (JSON数组存储)
  `v3_features` json DEFAULT NULL COMMENT 'V3功能支持：rpmData-RPM数据, deviceSwitchData-设备切换数据, enterpriseProfileData-企业Profile数据',
  
  -- RPM数据配置
  `rpm_type` json DEFAULT NULL COMMENT 'RPM类型：Enable, Disable, Delete, ListProfileInfo, ContactPcmp',
  `rpm_download_method` varchar(20) DEFAULT NULL COMMENT 'RPM下载方式：SM-DP+, SM-DS',
  `rpm_polling_address` varchar(200) DEFAULT NULL COMMENT 'RPM轮询地址URL',
  `allowed_ca` varchar(10) DEFAULT NULL COMMENT '允许的CA：CA1, CA2, CA3',
  `allowed_tags` json DEFAULT NULL COMMENT '允许的Tags (JSON数组)',
  
  -- 设备切换数据
  `device_switch_method` varchar(20) DEFAULT NULL COMMENT '设备切换方式：requestPlatform-请求平台, useStoredCode-使用存储的激活码',
  `need_new_eid` varchar(10) DEFAULT NULL COMMENT '是否需要新设备EID：yes, no',
  `need_new_tac` varchar(10) DEFAULT NULL COMMENT '是否需要新设备TAC：yes, no',
  `device_switch_allowed_ca` varchar(10) DEFAULT NULL COMMENT '设备切换允许的CA：CA1, CA2, CA3',
  
  -- 企业Profile数据
  `enterprise_name` varchar(50) DEFAULT NULL COMMENT '企业名称',
  `enterprise_rules` json DEFAULT NULL COMMENT '企业规则 (JSON数组)：priorityEnterpriseProfile-优先级企业Profile, onlyInstallEnterpriseProfile-只能安装企业Profile',
  `non_enterprise_profile_count` int DEFAULT '0' COMMENT '非企业Profile数量',
  
  -- ASN模板
  `asn_template` varchar(50) DEFAULT NULL COMMENT 'ASN模板：template1-标准模版, template2-企业模版, template3-自定义模版',
  
  -- 通用字段
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_iccid` (`iccid`),
  UNIQUE KEY `uk_matching_id` (`matching_id`),
  KEY `idx_profile_status` (`profile_status`),
  KEY `idx_notification_status` (`notification_status`),
  KEY `idx_tenant` (`tenant`),
  KEY `idx_carrier` (`carrier`),
  KEY `idx_reset_rule` (`reset_rule`),
  KEY `idx_download_method` (`download_method`),
  KEY `idx_profile_class` (`profile_class`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_update_time` (`update_time`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='eSIM Profile完整信息表';

-- 添加表注释
ALTER TABLE `esim_profile` COMMENT = 'eSIM Profile完整信息表 - 基于ProfileForm.vue完整数据结构';

-- 插入测试数据（基于提供的示例数据）
INSERT INTO `esim_profile` (
  `iccid`, `matching_id`, `profile_status`, `eid`, `notification_status`, 
  `download_method`, `tenant`, `carrier`, `profile_class`, `ppr_policy`, 
  `reset_rule`, `ds_flag`, `confirmation_code`, `profile_name`, `phone_number`, 
  `service_provider`, `notification_address`, `profile_icon`, `imsi`, `imsi2`, 
  `pin1`, `pin2`, `puk1`, `puk2`, `adm1`, `ki`, `opc`, `smsp`, 
  `v3_features`, `rpm_type`, `rpm_download_method`, `rpm_polling_address`, `allowed_ca`, `allowed_tags`, 
  `device_switch_method`, `need_new_eid`, `need_new_tac`, `device_switch_allowed_ca`, 
  `enterprise_name`, `enterprise_rules`, `non_enterprise_profile_count`, `asn_template`, `remark`
) VALUES (
  '12345678901234567890', 'matching-1', 'Available', '12345678901234567890123456789012', 'Enabled',
  '1', 'default-tenant', '中国移动', 'operational', 'PPR1',
  '1', 'flag1', 'code123', '测试Profile', '13800138000',
  'provider1', 'http://example.com', 'icon1', '460001234567890', '',
  '1234', '5678', '12345678', '87654321', 'adm1234', 'ki123456', 'opc123456', 'smsp123',
  '["rpmData", "deviceSwitchData"]', '["Enable", "Disable"]', 'SM-DP+', 'http://rpm.example.com', 'CA1', '["Service provider name", "Profile name"]',
  'requestPlatform', 'yes', 'no', 'CA1',
  '', '[]', 0, 'template1', '测试数据'
);