USE `virsical_notice`;

UPDATE `ntc_parameter` SET param_value='10000' WHERE id=9;
INSERT INTO `ntc_parameter` (id, param_desc, param_key, param_value) VALUES (27, '{"zh":"系统邮件连接超时时间","en":"Connect Timeout","tw":"系統郵件連接超時時間"}', 'DEFAULT_MAIL_CONNECTIONTIMEOUT', '5000');
INSERT INTO `ntc_parameter` (id, param_desc, param_key, param_value) VALUES (28, '{"zh":"系统邮件写超时时间","en":"Write Timeout","tw":"系統郵件寫超時時間"}', 'DEFAULT_MAIL_WRITETIMEOUT', '10000');

-- ----------------------------
-- Table structure for mail_sendlog
-- ----------------------------
DROP TABLE IF EXISTS `mail_sendlog`;
CREATE TABLE `mail_sendlog`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `mailKey` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮件标识',
  `status` int(2) NOT NULL COMMENT '发送状态(-1 重复邮件  0 成功  >1 失败次数)',
  `sendTime` timestamp(0) DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '发送时间',
  `mailStr` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮件内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;
