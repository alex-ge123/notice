USE `virsical_notice`;

-- ----------------------------
-- Table structure for ntc_parameter
-- ----------------------------
DELETE FROM `ntc_parameter` WHERE id=15;
DELETE FROM `ntc_parameter` WHERE id=16;
DELETE FROM `ntc_parameter` WHERE id=17;
DELETE FROM `ntc_parameter` WHERE id=18;
DELETE FROM `ntc_parameter` WHERE id=19;
DELETE FROM `ntc_parameter` WHERE id=20;
DELETE FROM `ntc_parameter` WHERE id=21;

UPDATE `ntc_parameter` SET param_desc='默认logo(Default logo)' WHERE id=1;
UPDATE `ntc_parameter` SET param_desc='短信接口服务地址(SMS interface service address)' WHERE id=2;
UPDATE `ntc_parameter` SET param_desc='短信接口CLIENTID(SMS interface clientId)' WHERE id=3;
UPDATE `ntc_parameter` SET param_desc='短信接口SECRET(SMS interface secret)' WHERE id=4;
UPDATE `ntc_parameter` SET param_desc='系统邮件地址(System email)' WHERE id=5;
UPDATE `ntc_parameter` SET param_desc='系统邮件服务(Email service)' WHERE id=6;
UPDATE `ntc_parameter` SET param_desc='系统邮件密码(Email password)' WHERE id=7;
UPDATE `ntc_parameter` SET param_desc='系统邮件认证(authentication)' WHERE id=8;
UPDATE `ntc_parameter` SET param_desc='系统邮件超时时间(Timeout)' WHERE id=9;
UPDATE `ntc_parameter` SET param_desc='系统邮件编码(Mail encoding)' WHERE id=10;
UPDATE `ntc_parameter` SET param_desc='系统邮件显示名称(Mail name)' WHERE id=11;
UPDATE `ntc_parameter` SET param_desc='后台服务默认时区(Time zone)' WHERE id=12;
UPDATE `ntc_parameter` SET param_desc='默认消息重发次数(Repeat count)' WHERE id=13;
UPDATE `ntc_parameter` SET param_desc='系统默认域名(Domain)' WHERE id=14;
UPDATE `ntc_parameter` SET param_desc='系统邮件端口(Mail port)' WHERE id=22;
UPDATE `ntc_parameter` SET param_desc='静态图片资源路径(Image directory)' WHERE id=24;
UPDATE `ntc_parameter` SET param_desc='系统名称(System name)' WHERE id=25;
UPDATE `ntc_parameter` SET param_desc='电话(Phone)' WHERE id=26;


