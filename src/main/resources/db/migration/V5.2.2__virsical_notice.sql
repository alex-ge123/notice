-- 修改ntc_parameter加入租户id支持多租户配置
alter table `ntc_parameter` add column `tenant_id` int(11) not null default 0 COMMENT '租户id';
-- 将现有配置作为通用默认配置
update `ntc_parameter` set tenant_id = 0 where tenant_id != 0;
