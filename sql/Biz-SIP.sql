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
  `client_adaptor_id` varchar(20) NOT NULL COMMENT '客户端适配器ID',
  `desc` varchar(100) DEFAULT NULL COMMENT '描述',
  `message_type` varchar(10) DEFAULT NULL COMMENT '消息类型',
  `message_params` varchar(255) DEFAULT NULL COMMENT '消息格式参数',
  `message_pack_rules` varchar(1000) DEFAULT NULL COMMENT '消息打包断言规则',
  `message_unpack_rules` varchar(1000) DEFAULT NULL COMMENT '消息解包断言规则',
  `create_user` varchar(20) DEFAULT NULL COMMENT '创建用户',
  `update_user` varchar(20) DEFAULT NULL COMMENT '更新用户',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`client_adaptor_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户端适配器定义表';

-- ----------------------------
-- Table structure for sip_def_message_transform
-- ----------------------------
DROP TABLE IF EXISTS `sip_def_message_transform`;
CREATE TABLE `sip_def_message_transform` (
  `node_id` varchar(255) NOT NULL COMMENT '节点ID',
  `node_type` char(1) DEFAULT NULL COMMENT '节点类型',
  `node_name` varchar(20) DEFAULT NULL COMMENT '节点名称',
  `node_desc` varchar(100) DEFAULT NULL COMMENT '描述',
  `transform_type` char(1) DEFAULT NULL COMMENT '消息转换类型',
  `transform_def` varchar(1000) DEFAULT NULL COMMENT '消息转换定义',
  `create_user` varchar(20) DEFAULT NULL COMMENT '创建用户',
  `update_user` varchar(20) DEFAULT NULL COMMENT '更新用户',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息转换定义表';

-- ----------------------------
-- Table structure for sip_def_server_adaptor
-- ----------------------------
DROP TABLE IF EXISTS `sip_def_server_adaptor`;
CREATE TABLE `sip_def_server_adaptor` (
  `server_adaptor_id` varchar(20) NOT NULL COMMENT '服务端适配器ID',
  `desc` varchar(100) DEFAULT NULL COMMENT '描述',
  `interface_type` varchar(10) DEFAULT NULL COMMENT '接口类型',
  `interface_params` varchar(255) DEFAULT NULL COMMENT '接口参数',
  `message_type` varchar(10) DEFAULT NULL COMMENT '消息类型',
  `message_params` varchar(255) DEFAULT NULL COMMENT '消息格式参数',
  `message_pack_rules` varchar(1000) DEFAULT NULL COMMENT '消息打包断言规则',
  `message_unpack_rules` varchar(1000) DEFAULT NULL COMMENT '消息解包断言规则',
  `protocol_type` varchar(10) DEFAULT NULL COMMENT '通讯协议类型',
  `protocol_params` varchar(255) DEFAULT NULL COMMENT '通讯协议接口参数',
  `create_user` varchar(255) DEFAULT NULL COMMENT '创建用户',
  `update_user` varchar(20) DEFAULT NULL COMMENT '更新用户',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`server_adaptor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务端适配器定义表';

-- ----------------------------
-- Table structure for sip_def_service
-- ----------------------------
DROP TABLE IF EXISTS `sip_def_service`;
CREATE TABLE `sip_def_service` (
  `node_id` varchar(255) NOT NULL COMMENT '节点ID',
  `node_type` char(1) DEFAULT NULL COMMENT '节点类型',
  `node_name` varchar(20) DEFAULT NULL COMMENT '节点名',
  `node_desc` varchar(100) DEFAULT NULL COMMENT '描述',
  `service_rule` varchar(1000) DEFAULT NULL COMMENT '服务规则',
  `field_check_rules` varchar(1000) DEFAULT NULL COMMENT '域校验规则',
  `service_check_rules` varchar(1000) DEFAULT NULL COMMENT '服务校验规则',
  `create_user` varchar(20) DEFAULT NULL COMMENT '创建用户',
  `update_user` varchar(20) DEFAULT NULL COMMENT '更新用户',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务定义表';

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
  PRIMARY KEY (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易流水表';

