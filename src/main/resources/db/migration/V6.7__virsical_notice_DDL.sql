DROP TABLE IF EXISTS `alert_conf`;
CREATE TABLE `alert_conf`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '提醒配置ID',
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `type` int(1) NOT NULL DEFAULT 1 COMMENT '告警类型1-站内，2-短信，3-邮件',
  `state` int(1) NOT NULL DEFAULT 1 COMMENT '状态1-开启，2-关闭',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`)
)COMMENT = '提醒配置表';

DROP TABLE IF EXISTS `alert_recipient`;
CREATE TABLE `alert_recipient`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '提醒接收人ID',
  `alert_conf_id` int(20) NOT NULL COMMENT '提醒配置ID',
  `recipient` varchar(255) NOT NULL COMMENT '接收人信息用户ID/手机号/邮箱',
  PRIMARY KEY (`id`)
)COMMENT = '提醒接收人';

DROP TABLE IF EXISTS `alert_record`;
CREATE TABLE `alert_record`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '提醒记录ID',
  `alert_id` varchar(255) NOT NULL COMMENT '告警ID，业务系统定义',
  `product` varchar(255) NOT NULL COMMENT '产品标识',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `content` varchar(255) NOT NULL COMMENT '内容',
  `alert_type` int(1) NOT NULL DEFAULT 1 COMMENT '告警类型1-站内，2-短信，3-邮件',
  `recipient` varchar(255) NOT NULL COMMENT '接收人信息用户ID/手机号/邮箱',
  `status` int(1) NOT NULL DEFAULT 1 COMMENT '状态1-未读，2-已读',
  `delivery_status` int(1) NOT NULL DEFAULT 1 COMMENT '投递状态 1-已投递，2-未投递，3-投递异常',
  `tenant_id` int(11) NOT NULL COMMENT '租户id',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`)
)COMMENT = '提醒记录表';