CREATE DATABASE IF NOT EXISTS `sip` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `sip`;
SET FOREIGN_KEY_CHECKS = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_log
-- ----------------------------
DROP TABLE IF EXISTS `sample_account`;
CREATE TABLE `sample_account`
(
    `account_no`   varchar(20) NOT NULL COMMENT '账户',
    `account_name` varchar(50) COMMENT '账户名',
    `sex`          char(1) COMMENT '性别',
    `balance`      DECIMAL(12, 2) COMMENT '账户余额',
    PRIMARY KEY (`account_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `sample_account`
VALUES ('001', '张三', '0', 100.00),
       ('002', '李四', '1', 200.00),
       ('003', '王五', '1', 300.00);


-- ----------------------------
-- Table structure for sip_def_client_adaptor
-- ----------------------------
DROP TABLE IF EXISTS `sip_def_client_adaptor`;
CREATE TABLE `sip_def_client_adaptor` (
                                          `id` varchar(36) NOT NULL COMMENT 'id',
                                          `client_adaptor_id` varchar(20) NOT NULL COMMENT '客户端适配器ID',
                                          `adaptor_desc` varchar(100) DEFAULT NULL COMMENT '描述',
                                          `message_type` varchar(20) DEFAULT NULL COMMENT '消息类型',
                                          `message_params` varchar(255) DEFAULT NULL COMMENT '消息格式参数',
                                          `message_pack_rules` varchar(1000) DEFAULT NULL COMMENT '消息打包断言规则',
                                          `message_unpack_rules` varchar(1000) DEFAULT NULL COMMENT '消息解包断言规则',
                                          `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
                                          `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                          `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
                                          `update_time` datetime DEFAULT NULL COMMENT '更新日期',
                                          `message_separators` varchar(100) DEFAULT NULL COMMENT '分隔符',
                                          `message_pre_unpack` varchar(255) DEFAULT NULL COMMENT '预解包规则(定长)',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          UNIQUE KEY `client_adaptor_id` (`client_adaptor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户端适配器定义表';

-- ----------------------------
-- Table structure for sip_def_client_pack_rules
-- ----------------------------
DROP TABLE IF EXISTS `sip_def_client_pack_rules`;
CREATE TABLE `sip_def_client_pack_rules` (
                                             `id` varchar(36) NOT NULL COMMENT 'id',
                                             `adaptor_ref_id` varchar(20) NOT NULL COMMENT '适配器ID',
                                             `predicate` varchar(255) DEFAULT NULL COMMENT '断言',
                                             `rule` varchar(255) DEFAULT NULL COMMENT '规则',
                                             `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
                                             `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                             `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
                                             `update_time` datetime DEFAULT NULL COMMENT '更新日期',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户端适配器打包断言规则表';

-- ----------------------------
-- Table structure for sip_def_client_unpack_rules
-- ----------------------------
DROP TABLE IF EXISTS `sip_def_client_unpack_rules`;
CREATE TABLE `sip_def_client_unpack_rules` (
                                               `id` varchar(36) NOT NULL COMMENT 'id',
                                               `adaptor_ref_id` varchar(20) NOT NULL COMMENT '适配器ID',
                                               `predicate` varchar(255) DEFAULT NULL COMMENT '断言',
                                               `rule` varchar(255) DEFAULT NULL COMMENT '规则',
                                               `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
                                               `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                               `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
                                               `update_time` datetime DEFAULT NULL COMMENT '更新日期',
                                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户端适配器解包断言规则表';

-- ----------------------------
-- Table structure for sip_def_field_check_rules
-- ----------------------------
DROP TABLE IF EXISTS `sip_def_field_check_rules`;
CREATE TABLE `sip_def_field_check_rules` (
                                             `id` varchar(36) NOT NULL COMMENT 'id',
                                             `service_ref_id` varchar(255) NOT NULL COMMENT '服务ID',
                                             `check_field` varchar(20) NOT NULL COMMENT '域表达式',
                                             `check_rule` varchar(100) NOT NULL COMMENT '校验规则',
                                             `check_args` varchar(100) DEFAULT NULL COMMENT '校验规则参数',
                                             `check_message` varchar(255) DEFAULT NULL COMMENT '校验出错误信息',
                                             `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
                                             `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                             `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
                                             `update_time` datetime DEFAULT NULL COMMENT '更新日期',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务域校验规则表';

-- ----------------------------
-- Table structure for sip_def_message_transform
-- ----------------------------
DROP TABLE IF EXISTS `sip_def_message_transform`;
CREATE TABLE `sip_def_message_transform` (
                                             `id` varchar(36) NOT NULL COMMENT 'id',
                                             `node_id` varchar(255) DEFAULT NULL COMMENT '节点ID',
                                             `node_type` varchar(1) DEFAULT NULL COMMENT '节点类型',
                                             `node_name` varchar(20) DEFAULT NULL COMMENT '节点名称',
                                             `node_desc` varchar(100) DEFAULT NULL COMMENT '描述',
                                             `transform_type` varchar(20) DEFAULT NULL COMMENT '消息转换类型',
                                             `transform_def` varchar(1000) DEFAULT NULL COMMENT '消息转换定义',
                                             `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
                                             `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                             `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
                                             `update_time` datetime DEFAULT NULL COMMENT '更新日期',
                                             `pid` varchar(36) NOT NULL COMMENT '父ID',
                                             `has_child` varchar(3) DEFAULT NULL COMMENT '是否有子节点',
                                             `message_pack_type` varchar(10) DEFAULT NULL COMMENT '消息打解包类型',
                                             PRIMARY KEY (`id`) USING BTREE,
                                             UNIQUE KEY `node_id` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息转换定义表';

-- ----------------------------
-- Table structure for sip_def_server_adaptor
-- ----------------------------
DROP TABLE IF EXISTS `sip_def_server_adaptor`;
CREATE TABLE `sip_def_server_adaptor` (
                                          `id` varchar(36) NOT NULL COMMENT 'id',
                                          `server_adaptor_id` varchar(20) NOT NULL COMMENT '服务端适配器ID',
                                          `adaptor_desc` varchar(100) DEFAULT NULL COMMENT '描述',
                                          `interface_type` varchar(10) DEFAULT NULL COMMENT '接口类型',
                                          `interface_params` varchar(255) DEFAULT NULL COMMENT '接口参数',
                                          `message_type` varchar(20) DEFAULT NULL COMMENT '消息类型',
                                          `message_params` varchar(255) DEFAULT NULL COMMENT '消息格式参数',
                                          `message_pack_rules` varchar(1000) DEFAULT NULL COMMENT '消息打包断言规则',
                                          `message_unpack_rules` varchar(1000) DEFAULT NULL COMMENT '消息解包断言规则',
                                          `protocol_type` varchar(10) DEFAULT NULL COMMENT '通讯协议类型',
                                          `protocol_params` varchar(255) DEFAULT NULL COMMENT '通讯协议接口参数',
                                          `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
                                          `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                          `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
                                          `update_time` datetime DEFAULT NULL COMMENT '更新日期',
                                          `protocol_class_name` varchar(100) DEFAULT NULL COMMENT '类名(Java)',
                                          `message_separators` varchar(100) DEFAULT NULL COMMENT '分隔符',
                                          `protocol_netty_host` varchar(100) DEFAULT NULL COMMENT '主机地址(Netty)',
                                          `protocol_netty_port` int(10) DEFAULT NULL COMMENT '端口地址(Netty)',
                                          `message_pre_unpack` varchar(255) DEFAULT NULL COMMENT '预解包规则(定长)',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          UNIQUE KEY `server_adaptor_id` (`server_adaptor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务端适配器定义表';

-- ----------------------------
-- Table structure for sip_def_server_pack_rules
-- ----------------------------
DROP TABLE IF EXISTS `sip_def_server_pack_rules`;
CREATE TABLE `sip_def_server_pack_rules` (
                                             `id` varchar(36) NOT NULL COMMENT 'id',
                                             `adaptor_ref_id` varchar(20) NOT NULL COMMENT '适配器ID',
                                             `predicate` varchar(255) DEFAULT NULL COMMENT '断言',
                                             `rule` varchar(255) DEFAULT NULL COMMENT '规则',
                                             `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
                                             `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                             `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
                                             `update_time` datetime DEFAULT NULL COMMENT '更新日期',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务端适配器打包断言规则表';

-- ----------------------------
-- Table structure for sip_def_server_unpack_rules
-- ----------------------------
DROP TABLE IF EXISTS `sip_def_server_unpack_rules`;
CREATE TABLE `sip_def_server_unpack_rules` (
                                               `id` varchar(36) NOT NULL COMMENT 'id',
                                               `adaptor_ref_id` varchar(20) NOT NULL COMMENT '适配器ID',
                                               `predicate` varchar(255) DEFAULT NULL COMMENT '断言',
                                               `rule` varchar(255) DEFAULT NULL COMMENT '规则',
                                               `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
                                               `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                               `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
                                               `update_time` datetime DEFAULT NULL COMMENT '更新日期',
                                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务端适配器解包断言规则表';

-- ----------------------------
-- Table structure for sip_def_service
-- ----------------------------
DROP TABLE IF EXISTS `sip_def_service`;
CREATE TABLE `sip_def_service` (
                                   `id` varchar(36) NOT NULL COMMENT 'id',
                                   `service_id` varchar(255) DEFAULT NULL COMMENT '服务ID',
                                   `service_type` varchar(1) DEFAULT NULL COMMENT '服务类型',
                                   `service_name` varchar(20) DEFAULT NULL COMMENT '服务名称',
                                   `service_desc` varchar(100) DEFAULT NULL COMMENT '服务描述',
                                   `service_rule` varchar(1000) DEFAULT NULL COMMENT '服务规则',
                                   `field_check_mode` varchar(3) DEFAULT NULL COMMENT '域校验模式',
                                   `field_check_rules` varchar(1000) DEFAULT NULL COMMENT '域校验规则',
                                   `service_check_mode` varchar(3) DEFAULT NULL COMMENT '服务校验模式',
                                   `service_check_rules` varchar(1000) DEFAULT NULL COMMENT '服务校验规则',
                                   `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                   `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
                                   `update_time` datetime DEFAULT NULL COMMENT '更新日期',
                                   `pid` varchar(36) NOT NULL COMMENT '父ID',
                                   `has_child` varchar(3) DEFAULT NULL COMMENT '是否有子节点',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务定义表';

-- ----------------------------
-- Table structure for sip_def_service_check_rules
-- ----------------------------
DROP TABLE IF EXISTS `sip_def_service_check_rules`;
CREATE TABLE `sip_def_service_check_rules` (
                                               `id` varchar(36) NOT NULL COMMENT 'id',
                                               `service_ref_id` varchar(255) NOT NULL COMMENT '服务ID',
                                               `check_script` varchar(20) NOT NULL COMMENT '校验脚本',
                                               `check_message` varchar(255) DEFAULT NULL COMMENT '校验出错误信息',
                                               `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
                                               `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                               `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
                                               `update_time` datetime DEFAULT NULL COMMENT '更新日期',
                                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务校验规则表';

-- ----------------------------
-- Table structure for sip_service_log
-- ----------------------------
DROP TABLE IF EXISTS `sip_service_log`;
CREATE TABLE `sip_service_log` (
                                   `trace_id` char(32) NOT NULL COMMENT '服务跟踪ID',
                                   `parent_trace_id` char(32) DEFAULT NULL COMMENT '父服务跟踪ID',
                                   `begin_time` timestamp NULL DEFAULT NULL COMMENT '服务开始时间',
                                   `end_time` timestamp NULL DEFAULT NULL COMMENT '服务结束时间',
                                   `code` int(11) DEFAULT NULL COMMENT '返回码',
                                   `message` varchar(255) DEFAULT NULL COMMENT '返回消息',
                                   `ext_message` varchar(255) DEFAULT NULL COMMENT '返回扩展消息',
                                   `request_data` json DEFAULT NULL COMMENT '请求数据',
                                   `response_data` json DEFAULT NULL COMMENT '响应数据',
                                   `service_status` char(1) NOT NULL COMMENT '服务状态',
                                   PRIMARY KEY (`trace_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易流水表';

DROP TABLE IF EXISTS `sip_test_service_log`;
CREATE TABLE `sip_test_service_log` (
                                    `id` varchar(36) NOT NULL COMMENT 'id',
                                    `service_id` varchar(255) DEFAULT NULL COMMENT '服务ID',
                                    `trace_id` char(32) NOT NULL COMMENT '服务跟踪ID',
                                   `begin_time` timestamp NULL DEFAULT NULL COMMENT '服务开始时间',
                                   `end_time` timestamp NULL DEFAULT NULL COMMENT '服务结束时间',
                                   `code` int(11) DEFAULT NULL COMMENT '返回码',
                                   `message` varchar(255) DEFAULT NULL COMMENT '返回消息',
                                   `ext_message` varchar(255) DEFAULT NULL COMMENT '返回扩展消息',
                                   `request_data` json DEFAULT NULL COMMENT '请求数据',
                                   `response_data` json DEFAULT NULL COMMENT '响应数据',
                                    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                    `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
                                    `update_time` datetime DEFAULT NULL COMMENT '更新日期',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聚合服务测试日志表';

