USE `virsical_notice`;

UPDATE `ntc_parameter` SET param_value='10000' WHERE id=9;
INSERT INTO `ntc_parameter` (id, param_desc, param_key, param_value) VALUES (27, '{"zh":"系统邮件连接超时时间","en":"Connect Timeout","tw":"系統郵件連接超時時間"}', 'DEFAULT_MAIL_CONNECTIONTIMEOUT', '5000');
INSERT INTO `ntc_parameter` (id, param_desc, param_key, param_value) VALUES (28, '{"zh":"系统邮件写超时时间","en":"Write Timeout","tw":"系統郵件寫超時時間"}', 'DEFAULT_MAIL_WRITETIMEOUT', '10000');
