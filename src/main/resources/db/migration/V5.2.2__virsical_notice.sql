-- 修改ntc_parameter加入类型、租户id支持多租户配置
alter table `ntc_parameter` add column `type` varchar(50) null COMMENT '类型';
alter table `ntc_parameter` add column `tenant_id` int(11) not null default 0 COMMENT '租户id';
-- 将现有配置作为通用默认配置
update `ntc_parameter` set tenant_id = 0 where tenant_id != 0;

-- 修改ntc_parameter表id自增
alter table `ntc_parameter` AUTO_INCREMENT=100;