# CHANGELOG
> 本文档记录公共模块基础服务版本变化说明
>
> 变动类型说明：
>
> - **Added** 新添加的功能
> - **Changed** 对现有功能的变更
> - **Deprecated** 已经不建议使用，准备很快移除的功能
> - **Removed** 已经移除的功能
> - **Fixed** 对bug的修复
> - **Security** 对安全的改进

## [v5.1.3.100] 
### Removed
  - 删除冗余表ntc_message_to_user,ntc_messages,ntc_weather 
  - 删除冗余表hibernate_sequence 
### Security
  - 统一处理接口参数无效异常，解决安全扫描问题：JSON 中反映的未清理用户输入
### Added
  - 添加邮件发送结果通知
  
## [v5.1.2.100] - 2020-2-27
### Added
  - 短信邮件重复发送拦截功能
  
## [v1.0.0] 
- 接入Spring Cloud
## [v1.1.0] 
- 升级公共包virsical-common-security版本为3.0.0
## [v1.2.0]
- 改造接口统一返回R对象
- 实现手机验证码短信发送与校验接口
## [v1.3.0]
## [v1.4.0]
## [v1.5.0]
- 加入MQ，实现监听MQ消息进行发送邮件、短信功能
## [v5.0.0.100]
- 加入忘记密码邮件模板
## [v5.0.12.100]
- 邮件模板填充电话从数据库中取