-- 提醒记录表添加param字段
ALTER TABLE `alert_record` ADD COLUMN `param` varchar(255) DEFAULT NULL COMMENT '内容填充参数';