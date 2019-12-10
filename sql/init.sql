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

INSERT INTO `hibernate_sequence` VALUES (100);
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
INSERT INTO `ntc_parameter` VALUES (1, '默认logo', 'LOGO_DEFALUT', 'http://www.virsical.cn/images/ico_wafer.png');
INSERT INTO `ntc_parameter` VALUES (2, '短信接口服务地址', 'URL_SMS_SERVER', 'https://work.virsical.cn/sms/ability');
INSERT INTO `ntc_parameter` VALUES (3, '短信接口CLIENTID', 'URL_SMS_CLIENTID', 'meeting');
INSERT INTO `ntc_parameter` VALUES (4, '短信接口SECRET', 'URL_SMS_SECRET', '23cbf2b615184418');
INSERT INTO `ntc_parameter` VALUES (5, '系统邮件发送邮件', 'DEFAULT_MAIL_FROM', 'office_helper1@wafersystems.com');
INSERT INTO `ntc_parameter` VALUES (6, '系统邮件服务', 'DEFAULT_MAIL_HOST', 'smtp.263.net');
INSERT INTO `ntc_parameter` VALUES (7, '系统邮件密码', 'DEFAULT_MAIL_PASSWORD', 'WAffHe@2019');
INSERT INTO `ntc_parameter` VALUES (8, '系统邮件认证', 'DEFAULT_MAIL_AUTH', 'true');
INSERT INTO `ntc_parameter` VALUES (9, '系统邮件超时时间', 'DEFAULT_MAIL_TIMEOUT', '25000');
INSERT INTO `ntc_parameter` VALUES (10, '系统邮件编码', 'DEFAULT_MAIL_CHARSET', 'GBK');
INSERT INTO `ntc_parameter` VALUES (11, '系统邮件显示名称', 'DEFAULT_MAIL_MAILNAME', '威思客');
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
INSERT INTO `ntc_parameter` VALUES (24, '静态图片资源路径', 'IMAGE_DIRECTORY', 'https://work.virsical.cn/res/release/mail');
INSERT INTO `ntc_parameter` VALUES (25, '系统名称', 'SYSTEM_NAME', '威发系统有限公司');
INSERT INTO `ntc_parameter` VALUES (26, '电话', 'PHONE', '400-685-3160');

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

-- ----------------------------
-- Table structure for mail_template
-- ----------------------------
DROP TABLE IF EXISTS `mail_template`;
CREATE TABLE `mail_template`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板内容',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板描述',
  `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类别',
  `modtime` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `createtime` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of mail_template
-- ----------------------------
INSERT INTO `mail_template` VALUES (1, 'commonForgetPwd', '<#macro message code, locale>${loccalMessage.getMessage(code, locale)}</#macro>\r\n<!DOCTYPE html\r\n    PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n\r\n<head>\r\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r\n    <title>Virsical</title>\r\n</head>\r\n\r\n<body>\r\n    <table border=\"0\"\r\n        style=\"margin-top: 0;margin-bottom: 0;margin-left: auto;margin-right: auto; width:606px; font-size:16px; color:#333333;background: #f9f9f9; font-family: Helvetica, Tahoma, Arial, \'PingFang SC\', STXihei, \'华文细黑\', \'Microsoft YaHei\', \'微软雅黑\', SimSun, \'宋体\', sans-serif;border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0; \">\r\n        <tr style=\"height:6px;background:linear-gradient(90deg,#33b8e8 0%,#665ba1 100%);\">\r\n            <td width=\"606px\"></td>\r\n        </tr>\r\n        <tr style=\"height: 103px;background: #ffffff; border: 1px solid #eaeaea;\">\r\n            <td style=\"display: table-cell;vertical-align: middle;width: 130px;height: 39px;\" valign=\"middle\">\r\n                <!-- <img src=\"./CaptainAmerica\" alt=\"logo\" -->\r\n                <!-- style=\"margin-left: 17px; display: block;max-width: 100%;height: 100%;\"> -->\r\n                <img src=\"${logo!\'\'}\" alt=\"logo\"\r\n                    style=\"margin-left: 17px; display: block; height: 50px;\">\r\n            </td>\r\n        </tr>\r\n        <tr>\r\n            <td style=\"padding-top:16px;padding-bottom: 16px;background: #f9f9f9;\">\r\n                <table width=\"573\" align=\"center\"\r\n                    style=\"border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0;margin-top: 9px;;\">\r\n                    <tr>\r\n                        <td>\r\n                            <table width=\"100%\" bgcolor=\"#FFFFFF\" style=\"padding-left:18px;padding-top: 21px;\">\r\n                                <tr style=\"font-size: 20px;\">\r\n                                    <td style=\"color:#333333;\">${value3!\'\'}</td>\r\n                                    <td style=\"width: 100px;\"></td>\r\n                                </tr>\r\n           						 <tr style=\"font-size: 16px;\">\r\n                                    <td style=\"color:#333333; padding-top:15px;\">\r\n                                        ${value1!\'\'}，您好：\r\n                                    </td>\r\n                                    <td style=\"width: 100px;\"></td>\r\n                                </tr>\r\n                                <tr style=\"font-size: 16px;\">\r\n									<td style=\"color:#333333; padding-top:15px; padding-bottom:15px;\">\r\n                                        &nbsp;&nbsp;&nbsp;&nbsp;为确保您账户的安全请在10分钟内重置密码，重置密码链接为：\r\n										<a href=\"${value2!\'\'}\"\r\n                                            style=\"font-size: 14px;color: #0d75cd;text-decoration:none;\">\r\n                                            点击这里\r\n                                        </a>\r\n                                    </td>\r\n                                </tr>\r\n                            </table>\r\n                        </td>\r\n                    </tr>\r\n                    <tr style=\"border-top: 1px solid #eaeaea;\">\r\n                        <td style=\"font-size: 18px;\" bgcolor=\"#ffffff\">\r\n                            <p style=\"margin-top: 15px;margin-right: 0;margin-bottom: 15px;margin-left: 55px;\">祝您工作愉快\r\n                            </p>\r\n                        </td>\r\n                    </tr>\r\n                </table>\r\n            </td>\r\n        </tr>\r\n        <tr style=\"background: #f4f4f4;border: 1px solid #eaeaea;\">\r\n            <td>\r\n                <table width=\"100%\">\r\n                    <tr>\r\n                        <td width=\"260\"\r\n                            style=\"line-height:150%; color:#333333; font-size:16px;padding-top: 16px;padding-bottom: 16px;padding-left: 10px;\">\r\n                            &nbsp;&nbsp;&nbsp;${systemName!\'\'}<br>\r\n                            &nbsp;&nbsp;&nbsp;电话：${phone!\'\'}<br>\r\n                        </td>\r\n                    </tr>\r\n                </table>\r\n            </td>\r\n        </tr>\r\n    </table>\r\n</body>\r\n\r\n</html>', 'commonForgetPwd', 'common', '2019-10-16 08:38:11', '2019-10-16 08:38:11');

SET FOREIGN_KEY_CHECKS = 1;
