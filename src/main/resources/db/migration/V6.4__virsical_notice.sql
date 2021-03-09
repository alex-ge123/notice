-- 删除多余配置
delete from `ntc_parameter` where tenant_id = 0 and param_key in ('DEFAULT_TIMEZONE','IMAGE_DIRECTORY','DEFAULT_MAIL_TIMEOUT');

-- 更新描述及重发次数
UPDATE `ntc_parameter` SET `param_desc` = '{\"zh\":\"默认邮件重发次数\",\"en\":\"Mail repeat times\",\"tw\":\"默認邮件重發次數\"}', `param_value` = '3' WHERE `tenant_id` = 0 and `param_key` = 'DEFAULT_REPEAT_COUNT';

-- 插入短信重试参数
INSERT INTO `ntc_parameter`( `param_desc`, `param_key`, `param_value`, `type`, `tenant_id`) VALUES ('{\"zh\":\"默认短信重发次数\",\"en\":\"Sms repeat times\",\"tw\":\"默認短信重發次數\"}', 'SMS_REPEAT_COUNT', '3', NULL, 0);
