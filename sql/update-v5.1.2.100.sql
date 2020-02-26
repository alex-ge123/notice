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

UPDATE `ntc_parameter` SET param_desc='{"zh":"默认logo","en":"Default logo","tw":"默認logo"}' WHERE id=1;
UPDATE `ntc_parameter` SET param_desc='{"zh":"短信接口服务地址","en":"SMS interface service address","tw":"短信接口服務地址"}' WHERE id=2;
UPDATE `ntc_parameter` SET param_desc='{"zh":"短信接口CLIENTID","en":"SMS interface clientId","tw":"短信接口CLIENTID"}' WHERE id=3;
UPDATE `ntc_parameter` SET param_desc='{"zh":"短信接口SECRET","en":"SMS interface secret","tw":"短信接口SEC​​RET"}' WHERE id=4;
UPDATE `ntc_parameter` SET param_desc='{"zh":"系统邮件地址","en":"System email","tw":"系統郵件地址"}' WHERE id=5;
UPDATE `ntc_parameter` SET param_desc='{"zh":"系统邮件服务","en":"Email service","tw":"系統郵件服務"}' WHERE id=6;
UPDATE `ntc_parameter` SET param_desc='{"zh":"系统邮件密码","en":"Email password","tw":"系統郵件密碼"}' WHERE id=7;
UPDATE `ntc_parameter` SET param_desc='{"zh":"系统邮件认证","en":"authentication","tw":"系統郵件認證"}' WHERE id=8;
UPDATE `ntc_parameter` SET param_desc='{"zh":"系统邮件超时时间","en":"Timeout","tw":"系統郵件超時時間"}' WHERE id=9;
UPDATE `ntc_parameter` SET param_desc='{"zh":"系统邮件编码","en":"Mail encoding","tw":"系統郵件編碼"}' WHERE id=10;
UPDATE `ntc_parameter` SET param_desc='{"zh":"系统邮件显示名称","en":"Mail name","tw":"系統郵件顯示名稱"}' WHERE id=11;
UPDATE `ntc_parameter` SET param_desc='{"zh":"后台服务默认时区","en":"Time zone","tw":"後台服務默認時區"}' WHERE id=12;
UPDATE `ntc_parameter` SET param_desc='{"zh":"默认消息重发次数","en":"Repeat count","tw":"默認消息重發次數"}' WHERE id=13;
UPDATE `ntc_parameter` SET param_desc='{"zh":"系统默认域名","en":"Domain","tw":"系統默認域名"}' WHERE id=14;
UPDATE `ntc_parameter` SET param_desc='{"zh":"系统邮件端口","en":"Mail Service port","tw":"系統郵件端口"}' WHERE id=22;
UPDATE `ntc_parameter` SET param_desc='{"zh":"静态图片资源路径","en":"Image directory","tw":"靜態圖片資源路徑"}' WHERE id=24;
UPDATE `ntc_parameter` SET param_desc='{"zh":"系统名称","en":"System name","tw":"系統名稱"}' WHERE id=25;
UPDATE `ntc_parameter` SET param_desc='{"zh":"电话","en":"Phone","tw":"電話"}' WHERE id=26;


