DROP DATABASE IF EXISTS `virsical_notice`;
CREATE DATABASE `virsical_notice` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `virsical_notice`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for hibernate_sequence
-- ----------------------------
DROP TABLE IF EXISTS `hibernate_sequence`;
CREATE TABLE `hibernate_sequence`  (
  `next_val` bigint(20) NULL DEFAULT NULL
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ntc_message_to_user
-- ----------------------------
DROP TABLE IF EXISTS `ntc_message_to_user`;
CREATE TABLE `ntc_message_to_user`  (
  `id` bigint(20) NOT NULL,
  `domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `state` int(11) NULL DEFAULT NULL,
  `userId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `message_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKkpus0cseco0x4w1h08si632fn`(`message_id`) USING BTREE,
  CONSTRAINT `FKkpus0cseco0x4w1h08si632fn` FOREIGN KEY (`message_id`) REFERENCES `ntc_messages` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ntc_message_to_user_ibfk_1` FOREIGN KEY (`message_id`) REFERENCES `ntc_messages` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ntc_messages
-- ----------------------------
DROP TABLE IF EXISTS `ntc_messages`;
CREATE TABLE `ntc_messages`  (
  `id` bigint(20) NOT NULL,
  `urls` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `contentType` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `senderId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `senderName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `timeStamp` bigint(20) NULL DEFAULT NULL,
  `type` int(11) NULL DEFAULT NULL,
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ntc_parameter
-- ----------------------------
DROP TABLE IF EXISTS `ntc_parameter`;
CREATE TABLE `ntc_parameter`  (
  `id` bigint(20) NOT NULL,
  `param_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `param_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `param_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ntc_parameter
-- ----------------------------
INSERT INTO `ntc_parameter` VALUES (1, '默认logo', 'LOGO_DEFALUT', 'http://www.virsical.cn/images/logo.png');
INSERT INTO `ntc_parameter` VALUES (2, '短信接口服务地址', 'URL_SMS_SERVER', 'https://work.virsical.cn/sms/ability');
INSERT INTO `ntc_parameter` VALUES (3, '短信接口CLIENTID', 'URL_SMS_CLIENTID', 'meeting');
INSERT INTO `ntc_parameter` VALUES (4, '短信接口SECRET', 'URL_SMS_SECRET', '23cbf2b615184418');
INSERT INTO `ntc_parameter` VALUES (5, '系统邮件发送邮件', 'DEFAULT_MAIL_FROM', 'office_helper1@wafersystems.com');
INSERT INTO `ntc_parameter` VALUES (6, '系统邮件服务', 'DEFAULT_MAIL_HOST', 'smtp.263xmail.com');
INSERT INTO `ntc_parameter` VALUES (7, '系统邮件密码', 'DEFAULT_MAIL_PASSWORD', 'WAffHe@2019');
INSERT INTO `ntc_parameter` VALUES (8, '系统邮件认证', 'DEFAULT_MAIL_AUTH', 'true');
INSERT INTO `ntc_parameter` VALUES (9, '系统邮件超时时间', 'DEFAULT_MAIL_TIMEOUT', '25000');
INSERT INTO `ntc_parameter` VALUES (10, '系统邮件编码', 'DEFAULT_MAIL_CHARSET', 'GBK');
INSERT INTO `ntc_parameter` VALUES (11, '系统邮件显示名称', 'DEFAULT_MAIL_MAILNAME', '威思客预约服务');
INSERT INTO `ntc_parameter` VALUES (12, '后台服务默认时区', 'DEFAULT_TIMEZONE', 'GMT+8');
INSERT INTO `ntc_parameter` VALUES (13, '默认消息重发次数', 'DEFAULT_REPEAT_COUNT', '5');
INSERT INTO `ntc_parameter` VALUES (14, '系统默认域名', 'DEFAULT_DOMAIN', 'wafersystems.com');
INSERT INTO `ntc_parameter` VALUES (15, '个推appId', 'GETUI_APPID', 'iu6CTagBLz8OWTk6Bw76a3');
INSERT INTO `ntc_parameter` VALUES (16, '个推appKey', 'GETUI_APPKEY', 'RUjOTs4BuN69GQebYM81j2');
INSERT INTO `ntc_parameter` VALUES (17, '个推masterSecret', 'GETUI_MASTRE_SECRET', 'RzwqDqWuH99VqG4HYF8koA');
INSERT INTO `ntc_parameter` VALUES (18, '个推服务地址', 'GETUI_URL', 'https://api.getui.com/apiex.htm');
INSERT INTO `ntc_parameter` VALUES (19, '个推消息离线时间(小时)', 'GETUI_OFFLINE_TIME', '24');
INSERT INTO `ntc_parameter` VALUES (20, '阿里API AppCode', 'ALI_APP_CODE', 'b13eaf7cb2ff4cb8a18162aa0a85c4dd');
INSERT INTO `ntc_parameter` VALUES (21, '天气信息间隔更新时间(分钟)', 'WEATHER_UPDATE_INTERVAL', '30');
INSERT INTO `ntc_parameter` VALUES (22, '系统邮件端口', 'DEFAULT_MAIL_PORT', '25');
INSERT INTO `ntc_parameter` VALUES (24, '静态图片资源路径', 'IMAGE_DIRECTORY', 'http://www.virsical.cn/images');

-- ----------------------------
-- Table structure for ntc_weather
-- ----------------------------
DROP TABLE IF EXISTS `ntc_weather`;
CREATE TABLE `ntc_weather`  (
  `id` int(11) NOT NULL,
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
