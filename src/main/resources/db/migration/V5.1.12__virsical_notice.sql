-- 邮件模板添加状态字段
alter table `mail_template` add `state` tinyint(2) NOT NULL DEFAULT 0  COMMENT '状态：0-正常，1-停用';

-- ----------------------------
-- Table structure for sms_template
-- ----------------------------
CREATE TABLE `sms_template`  (
  `id` varchar(255) NOT NULL COMMENT '短信编号',
  `name` varchar(255) NOT NULL COMMENT '模板名称',
  `content` text NOT NULL COMMENT '模板内容',
  `description` varchar(255) NOT NULL COMMENT '模板描述',
  `category` varchar(255) NOT NULL COMMENT '类别',
  `state` tinyint(2) NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-停用',
  `modtime` timestamp(0) DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `createtime` timestamp(0) DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) COMMENT = '短信模版表';

-- 邮件模板描述调整
update `mail_template` set description = '账户状态通知模板-简体中文' where name = 'commonAccountStatus';
update `mail_template` set description = '账户状态通知模板-繁体中文' where name = 'commonAccountStatus_zh_TW';
update `mail_template` set description = '账户状态通知模板-英文' where name = 'commonAccountStatus_en';

update `mail_template` set description = '忘记密码模板-简体中文' where name = 'commonForgetPwd';
update `mail_template` set description = '忘记密码模板-繁体中文' where name = 'commonForgetPwd_zh_TW';
update `mail_template` set description = '忘记密码模板-英文' where name = 'commonForgetPwd_en';

update `mail_template` set description = '惩罚通知模板-简体中文' where name = 'pointPunishmentNotice';
update `mail_template` set description = '惩罚通知模板-繁体中文' where name = 'pointPunishmentNotice_zh_TW';
update `mail_template` set description = '惩罚通知模板-英文' where name = 'pointPunishmentNotice_en';