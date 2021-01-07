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
-- Table structure for sip_service_log
-- ----------------------------
DROP TABLE IF EXISTS `sip_service_log`;
CREATE TABLE `sip_service_log`
(
    `trace_id`        char(32)  NOT NULL COMMENT '服务跟踪ID',
    `parent_trace_id` char(32)       DEFAULT NULL COMMENT '父服务跟踪ID',
    `begin_time`      timestamp NULL DEFAULT NULL COMMENT '服务开始时间',
    `end_time`        timestamp NULL DEFAULT NULL COMMENT '服务结束时间',
    `code`            int            DEFAULT NULL COMMENT '返回码',
    `message`         varchar(255)   DEFAULT NULL COMMENT '返回消息',
    `ext_message`     varchar(255)   DEFAULT NULL COMMENT '返回扩展消息',
    `request_data`    json           DEFAULT NULL COMMENT '请求数据',
    `response_data`   json           DEFAULT NULL COMMENT '响应数据',
    `service_status`  char(1)   NOT NULL COMMENT '服务状态',
    PRIMARY KEY (`trace_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;