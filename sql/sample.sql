CREATE DATABASE IF NOT EXISTS `sip` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `sip`;
SET FOREIGN_KEY_CHECKS=0;
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_log
-- ----------------------------
DROP TABLE IF EXISTS `sample_account`;
CREATE TABLE `sample_account` (
  `account_no` varchar(20) NOT NULL COMMENT '账户',
  `account_name` varchar(50) COMMENT '账户名',
  `sex` char(1)  COMMENT '性别',
  `balance` DECIMAL(12,2) COMMENT '账户余额',
  PRIMARY KEY (`account_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `sample_account` VALUES
('001','张三','0',100.00),
('002','李四','1',200.00),
('003','王五','1',300.00);