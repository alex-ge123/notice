-- 删除邮件模板
DELETE FROM `mail_template` WHERE `name` = 'lease_end';
-- 插入租約邮件模板
INSERT INTO `mail_template` ( `name`, `content`, `description`, `category`, `createtime`) VALUES ( 'lease_end', '<#macro message code, locale> ${loccalMessage.getMessage(code, locale)} </#macro>\r\n<!DOCTYPE html\r\n        PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n\r\n<head>\r\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\r\n    <title>Virsical</title>\r\n</head>\r\n\r\n<body>\r\n<table border=\"0\"\r\n       style=\"margin-top: 0;margin-bottom: 0;margin-left: auto;margin-right: auto; width:606px; font-size:16px; color:#333333;background: #f9f9f9; font-family: Helvetica, Tahoma, Arial, \'PingFang SC\', STXihei, \'华文细黑\', \'Microsoft YaHei\', \'微软雅黑\', SimSun, \'宋体\', sans-serif;border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0; \">\r\n    <tr style=\"height:6px;background:linear-gradient(90deg,#33b8e8 0%,#665ba1 100%);\">\r\n        <td width=\"606px\"></td>\r\n    </tr>\r\n    <tr style=\"height: 103px;background: #ffffff; border: 1px solid #eaeaea;\">\r\n        <td style=\"display: table-cell;vertical-align: middle;width: 130px;height: 39px;\">\r\n            <img src=\"${logo!\'\'}\" alt=\"logo\"\r\n                 style=\"margin-left: 17px; display: block;max-width: 100%;height: 100%;\">\r\n        </td>\r\n    </tr>\r\n    <tr>\r\n\r\n        <td style=\"padding-top:16px;padding-bottom: 16px;\">\r\n            <table width=\"573\" align=\"center\"\r\n                   style=\"border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0;\">\r\n                <tr>\r\n                    <td>\r\n                        <table width=\"100%\" bgcolor=\"#FFFFFF\" style=\"padding-left:18px;padding-top: 21px;\">\r\n                            <tr style=\"font-size: 20px;\">\r\n                                <td style=\"color:#333333;\">\r\n                                    <@message code=\"mgs.mail.lease.subject\" locale=\"${locale}\"/>\r\n                                </td>\r\n                            </tr>\r\n                            <tr style=\"font-size: 16px;line-height: 2;\">\r\n                                <td style=\"color:#333333; padding-top:25px\">\r\n                                    ${value1!\'\'} ,  <@message code=\"mgs.mail.lease.hello\" locale=\"${locale}\"/>:<br>\r\n                                        &nbsp;&nbsp;&nbsp;&nbsp;<@message code=\"mgs.mail.lesse.contentstart\" locale=\"${locale}\"/>\r\n                                        ${value2!\'\'}\r\n                                        <@message code=\"mgs.mail.lease.contentend\" locale=\"${locale}\"/>\r\n                                </td>\r\n                             </tr>\r\n                        </table>\r\n                    </td>\r\n                </tr>\r\n                <tr style=\"border-top: 1px solid #eaeaea;\">\r\n                    <td>\r\n                        <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n                            <tr>\r\n                                <td width=\"150\" height=\"50\"\r\n                                    style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%;padding-top:25px; \">\r\n                                    <@message code=\"mgs.mail.lseae.name\" locale=\"${locale}\"/> :\r\n                                <td style=\"font-size: 16px; color:#333333; text-align:left;padding-top: 25px;\">\r\n                                    &nbsp;&nbsp;&nbsp;&nbsp;${value3!\'\'}\r\n                                </td>\r\n                            </tr>\r\n                            <tr>\r\n                                <td width=\"150\" height=\"50\"\r\n                                    style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n                                    <@message code=\"mgs.mail.lseae.address\" locale=\"${locale}\"/>:\r\n                                <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n                                    &nbsp;&nbsp;&nbsp;&nbsp;${value4!\'\'}\r\n                                </td>\r\n                            </tr>\r\n                            <tr>\r\n                                <td width=\"150\" height=\"50\"\r\n                                    style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n                                    <@message code=\"mgs.mail.lseae.statrtime\" locale=\"${locale}\"/>:\r\n                                <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n                                    &nbsp;&nbsp;&nbsp;&nbsp;${value5!\'\'}\r\n                                </td>\r\n                            </tr>\r\n                            <tr>\r\n                                <td width=\"150\" height=\"50\"\r\n                                    style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n                                    <@message code=\"mgs.mail.lseae.endtime\" locale=\"${locale}\"/>:\r\n                                <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n                                    &nbsp;&nbsp;&nbsp;&nbsp;${value6!\'\'}\r\n                                </td>\r\n                            </tr>\r\n                        </table>\r\n                    </td>\r\n                </tr>\r\n\r\n                <tr style=\"border-top: 1px solid #eaeaea;\">\r\n                    <td style=\"font-size: 18px;\" bgcolor=\"#ffffff\">\r\n                        <p style=\"margin-top: 15px;margin-right: 0;margin-bottom: 15px;margin-left: 55px;\">\r\n                            <@message code=\"mgs.mail.sws.remarks\" locale=\"${locale}\"/>\r\n                        </p>\r\n                    </td>\r\n                </tr>\r\n            </table>\r\n        </td>\r\n    </tr>\r\n    <tr style=\"background: #f4f4f4;border: 1px solid #eaeaea;\">\r\n        <td>\r\n            <table width=\"100%\">\r\n                <tr>\r\n                    <td width=\"260\"\r\n                        style=\"line-height:150%; color:#333333; font-size:16px;padding-top: 16px;padding-bottom: 16px;padding-left: 10px;\">\r\n                        &nbsp;&nbsp;&nbsp;<@message code=\"mgs.mail.sws.systemname\" locale=\"${locale}\"/>\r\n                        <br>\r\n                        &nbsp;&nbsp;&nbsp;<@message code=\"mgs.mail.sws.phone\" locale=\"${locale}\"/> ：<@message code=\"mgs.mail.sws.phone.no\" locale=\"${locale}\"/><br>\r\n                    </td>\r\n                </tr>\r\n            </table>\r\n        </td>\r\n    </tr>\r\n</table>\r\n</body>\r\n\r\n</html>', '租约管理系统中租约联系人收到的租约到期通知', 'sls', '2020-04-13 15:05:52');
-- 删除邮件模板
DELETE FROM `mail_template` WHERE `category` = 'sws';
-- 插入工位邮件模板
INSERT INTO `mail_template`(`name`, `content`, `description`, `category`, `modtime`, `createtime`) VALUES ('sws_station_apply', '<#macro message code, locale> ${loccalMessage.getMessage(code, locale)} </#macro>\r\n<!DOCTYPE html\r\n        PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n\r\n<head>\r\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\r\n    <title>Virsical</title>\r\n</head>\r\n\r\n<body>\r\n<table border=\"0\"\r\n       style=\"margin-top: 0;margin-bottom: 0;margin-left: auto;margin-right: auto; width:606px; font-size:16px; color:#333333;background: #f9f9f9; font-family: Helvetica, Tahoma, Arial, \'PingFang SC\', STXihei, \'华文细黑\', \'Microsoft YaHei\', \'微软雅黑\', SimSun, \'宋体\', sans-serif;border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0; \">\r\n    <tr style=\"height:6px;background:linear-gradient(90deg,#33b8e8 0%,#665ba1 100%);\">\r\n        <td width=\"606px\"></td>\r\n    </tr>\r\n    <tr style=\"height: 103px;background: #ffffff; border: 1px solid #eaeaea;\">\r\n        <td style=\"display: table-cell;vertical-align: middle;width: 130px;height: 39px;\">\r\n            <img src=\"${logo!\'\'}\" alt=\"logo\"\r\n                 style=\"margin-left: 17px; display: block;max-width: 100%;height: 100%;\">\r\n        </td>\r\n    </tr>\r\n    <tr>\r\n\r\n        <td style=\"padding-top:16px;padding-bottom: 16px;\">\r\n            <table width=\"573\" align=\"center\"\r\n                   style=\"border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0;\">\r\n                <tr>\r\n                    <td>\r\n                        <table width=\"100%\" bgcolor=\"#FFFFFF\" style=\"padding-left:18px;padding-top: 21px;\">\r\n                            <tr style=\"font-size: 20px;\">\r\n                            <#if value1 = \"DEPT_MANAGE_APPLY\">\r\n                                <td style=\"color:#333333;\"><@message code=\"mgs.mail.sws.station.app\" locale=\"${locale}\"/></td>\r\n                            </#if>\r\n                            <#if value1 = \"ADMIN_MANAGE_APPROVAL\">\r\n                                <td style=\"color:#333333;\"><@message code=\"mgs.mail.sws.station.approval\" locale=\"${locale}\"/></td>\r\n                            </#if>\r\n                            <#if value1=\"ADMIN_MANAGE_REFUSE\">\r\n                                <td style=\"color:#333333;\"><@message code=\"mgs.mail.sws.station.rej\" locale=\"${locale}\"/></td>\r\n                            </#if>\r\n                            </tr>\r\n                            <tr style=\"font-size: 16px;line-height: 2;\">\r\n                                <td style=\"color:#333333; padding-top:25px\">\r\n                                    <@message code=\"mgs.mail.sws.hello\" locale=\"${locale}\"/> ，${value2!\'\'}:<br>\r\n                                    &nbsp;&nbsp;&nbsp;&nbsp;<@message code=\"mgs.mail.sws.station.content1\" locale=\"${locale}\"/>\r\n                                </td>\r\n                            </tr>\r\n                        </table>\r\n                    </td>\r\n                </tr>\r\n                <tr style=\"border-top: 1px solid #eaeaea;\">\r\n                    <td>\r\n                        <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n                            <tr>\r\n                                <td width=\"150\" height=\"50\"\r\n                                    style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%;padding-top:25px; \">\r\n                                    <@message code=\"mgs.mail.sws.station\" locale=\"${locale}\"/> :\r\n                                <td style=\"font-size: 16px; color:#333333; text-align:left;padding-top: 25px;\">\r\n                                    &nbsp;&nbsp;&nbsp;&nbsp;${value3!\'\'}\r\n                                </td>\r\n                            </tr>\r\n                            <tr>\r\n                                <td width=\"150\" height=\"50\"\r\n                                    style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n                                    <@message code=\"mgs.mail.sws.dept\" locale=\"${locale}\"/>:\r\n                                <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n                                    &nbsp;&nbsp;&nbsp;&nbsp;${value6!\'\'}\r\n                                </td>\r\n                            </tr>\r\n                            <tr>\r\n                                <td width=\"150\" height=\"50\"\r\n                                    style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n                                    <@message code=\"mgs.mail.sws.apply.time\" locale=\"${locale}\"/>:\r\n                                <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n                                    &nbsp;&nbsp;&nbsp;&nbsp;${value4!\'\'}\r\n                                </td>\r\n                            </tr>\r\n                            <tr>\r\n                                <td width=\"150\" height=\"50\"\r\n                                    style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n                                    <@message code=\"mgs.mail.sws.approval.time\" locale=\"${locale}\"/>:\r\n                                <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n                                    &nbsp;&nbsp;&nbsp;&nbsp;${value5!\'\'}\r\n                                </td>\r\n                            </tr>\r\n                            <tr>\r\n                                <td width=\"150\" height=\"50\"\r\n                                    style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%;padding-bottom: 25px;\">\r\n                                    <@message code=\"mgs.mail.sws.describe\" locale=\"${locale}\"/>:\r\n                                <td style=\"font-size: 16px; color:#333333; text-align:left;padding-bottom: 25px;\">\r\n                                    &nbsp;&nbsp;&nbsp;&nbsp;${value8!\'\'}\r\n                                </td>\r\n\r\n                            </tr>\r\n                        </table>\r\n                    </td>\r\n                </tr>\r\n\r\n                <tr style=\"border-top: 1px solid #eaeaea;\">\r\n                    <td style=\"font-size: 18px;\" bgcolor=\"#ffffff\">\r\n                        <p style=\"margin-top: 15px;margin-right: 0;margin-bottom: 15px;margin-left: 55px;\">\r\n                            <@message code=\"mgs.mail.sws.remarks\" locale=\"${locale}\"/>\r\n                        </p>\r\n                    </td>\r\n                </tr>\r\n            </table>\r\n        </td>\r\n    </tr>\r\n    <tr style=\"background: #f4f4f4;border: 1px solid #eaeaea;\">\r\n        <td>\r\n            <table width=\"100%\">\r\n                <tr>\r\n                    <td width=\"260\"\r\n                        style=\"line-height:150%; color:#333333; font-size:16px;padding-top: 16px;padding-bottom: 16px;padding-left: 10px;\">\r\n                        &nbsp;&nbsp;&nbsp;<@message code=\"mgs.mail.sws.systemname\" locale=\"${locale}\"/>\r\n                        <br>\r\n                        &nbsp;&nbsp;&nbsp;<@message code=\"mgs.mail.sws.phone\" locale=\"${locale}\"/> ：<@message code=\"mgs.mail.sws.phone.no\" locale=\"${locale}\"/><br>\r\n                    </td>\r\n                </tr>\r\n            </table>\r\n        </td>\r\n    </tr>\r\n</table>\r\n</body>\r\n\r\n</html>', '工位系统中行政管理员收到部门管理员发起的工位申请邮件通知，进行审批业务', 'sws', '2019-11-29 14:53:19', '2019-11-29 14:53:19');
INSERT INTO `mail_template`(`name`, `content`, `description`, `category`, `modtime`, `createtime`) VALUES ('sws_station_repair', '<#macro message code, locale> ${loccalMessage.getMessage(code, locale)} </#macro>\n<!DOCTYPE html\n        PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n\n<head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n    <title>Virsical</title>\n</head>\n\n<body>\n<table border=\"0\"\n       style=\"margin-top: 0;margin-bottom: 0;margin-left: auto;margin-right: auto; width:606px; font-size:16px; color:#333333;background: #f9f9f9; font-family: Helvetica, Tahoma, Arial, \'PingFang SC\', STXihei, \'华文细黑\', \'Microsoft YaHei\', \'微软雅黑\', SimSun, \'宋体\', sans-serif;border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0; \">\n    <tr style=\"height:6px;background:linear-gradient(90deg,#33b8e8 0%,#665ba1 100%);\">\n        <td width=\"606px\"></td>\n    </tr>\n    <tr style=\"height: 103px;background: #ffffff; border: 1px solid #eaeaea;\">\n        <td style=\"display: table-cell;vertical-align: middle;width: 130px;height: 39px;\">\n            <img src=\"${logo!\'\'}\" alt=\"logo\"\n                 style=\"margin-left: 17px; display: block;max-width: 100%;height: 100%;\">\n        </td>\n    </tr>\n    <tr>\n\n        <td style=\"padding-top:16px;padding-bottom: 16px;\">\n            <table width=\"573\" align=\"center\"\n                   style=\"border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0;\">\n                <tr>\n                    <td>\n                        <table width=\"100%\" bgcolor=\"#FFFFFF\" style=\"padding-left:18px;padding-top: 21px;\">\n                            <tr style=\"font-size: 20px;\">\n                                <#if value1 = \"STATION_REPAIR\">\n                                <td style=\"color:#333333;\">\n                                    <@message code=\"mgs.mail.sws.station.repair\" locale=\"${locale}\"/>\n                                </td>\n                                </#if>\n\n                            <#if value1 = \"STATION_REPAIR_BACK\">\n                            <td style=\"color:#333333;\">\n                                <@message code=\"mgs.mail.sws.station.req\" locale=\"${locale}\"/>\n                            </td>\n                            </#if>\n                            </tr>\n                            <tr style=\"font-size: 16px;line-height: 2;\">\n                                <td style=\"color:#333333; padding-top:25px\">\n                                    <@message code=\"mgs.mail.sws.hello\" locale=\"${locale}\"/> ，${value2!\'\'}:<br>\n                                    &nbsp;&nbsp;&nbsp;&nbsp;\n                                    <#if value1 = \"STATION_REPAIR\">\n                                       <@message code=\"mgs.mail.sws.station.content2\" locale=\"${locale}\"/>\n                                    </#if>\n\n                                    <#if value1 = \"STATION_REPAIR_BACK\">\n                                       <@message code=\"mgs.mail.sws.station.content3\" locale=\"${locale}\"/>\n                                    </#if>\n\n                                </td>\n                            </tr>\n                        </table>\n                    </td>\n                </tr>\n                <tr style=\"border-top: 1px solid #eaeaea;\">\n                    <td>\n                        <table width=\"100%\" bgcolor=\"#FFFFFF\">\n                            <tr>\n                                <td width=\"150\" height=\"50\"\n                                    style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%;padding-top:25px; \">\n                                    <@message code=\"mgs.mail.sws.station\" locale=\"${locale}\"/> :\n                                <td style=\"font-size: 16px; color:#333333; text-align:left;padding-top: 25px;\">\n                                    &nbsp;&nbsp;&nbsp;&nbsp;${value3!\'\'}\n                                </td>\n                            </tr>\n                            <tr>\n                                <td width=\"150\" height=\"50\"\n                                    style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\n                                    <@message code=\"mgs.email.station.address\" locale=\"${locale}\"/> :\n                                <td style=\"font-size: 16px; color:#333333; text-align:left;\">\n                                    &nbsp;&nbsp;&nbsp;&nbsp;${value4!\'\'}\n                                </td>\n                            </tr>\n                            <tr>\n                                <td width=\"150\" height=\"50\"\n                                    style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\n                                    <@message code=\"mgs.mail.sws.describe\" locale=\"${locale}\"/>:\n                                <td style=\"font-size: 16px; color:#333333; text-align:left;\">\n                                    &nbsp;&nbsp;&nbsp;&nbsp;${value5!\'\'}\n                                </td>\n                            </tr>\n                        </table>\n                    </td>\n                </tr>\n\n                <tr style=\"border-top: 1px solid #eaeaea;\">\n                    <td style=\"font-size: 18px;\" bgcolor=\"#ffffff\">\n                        <p style=\"margin-top: 15px;margin-right: 0;margin-bottom: 15px;margin-left: 55px;\">\n                            <@message code=\"mgs.mail.sws.remarks\" locale=\"${locale}\"/>\n                        </p>\n                    </td>\n                </tr>\n            </table>\n        </td>\n    </tr>\n    <tr style=\"background: #f4f4f4;border: 1px solid #eaeaea;\">\n        <td>\n            <table width=\"100%\">\n                <tr>\n                    <td width=\"260\"\n                        style=\"line-height:150%; color:#333333; font-size:16px;padding-top: 16px;padding-bottom: 16px;padding-left: 10px;\">\n                        &nbsp;&nbsp;&nbsp;<@message code=\"mgs.mail.sws.systemname\" locale=\"${locale}\"/>\n                        <br>\n                        &nbsp;&nbsp;&nbsp;<@message code=\"mgs.mail.sws.phone\" locale=\"${locale}\"/> ：<@message code=\"mgs.mail.sws.phone.no\" locale=\"${locale}\"/><br>\n                    </td>\n                </tr>\n            </table>\n        </td>\n    </tr>\n</table>\n</body>\n\n</html>', '工位系统中报修管理员收到用户发起的工位报修邮件通知', 'sws', '2019-11-29 14:53:37', '2019-11-29 14:53:37');
DELETE FROM `mail_template` WHERE `name`='sws_station_reservation';
INSERT INTO `mail_template` (`id`, `name`, `content`, `description`, `category`, `createtime`) VALUES (NULL, 'sws_station_reservation', '<#macro message code, locale> ${loccalMessage.getMessage(code, locale)} </#macro>\r\n<!DOCTYPE html\r\n PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n\r\n<head>\r\n <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\r\n <title>Virsical</title>\r\n</head>\r\n\r\n<body>\r\n<table border=\"0\"\r\n style=\"margin-top: 0;margin-bottom: 0;margin-left: auto;margin-right: auto; width:606px; font-size:16px; color:#333333;background: #f9f9f9; font-family: Helvetica, Tahoma, Arial, \'PingFang SC\', STXihei, \'华文细黑\', \'Microsoft YaHei\', \'微软雅黑\', SimSun, \'宋体\', sans-serif;border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0; \">\r\n <tr style=\"height:6px;background:linear-gradient(90deg,#33b8e8 0%,#665ba1 100%);\">\r\n <td width=\"606px\"></td>\r\n </tr>\r\n <tr style=\"height: 103px;background: #ffffff; border: 1px solid #eaeaea;\">\r\n <td style=\"display: table-cell;vertical-align: middle;width: 130px;height: 39px;\">\r\n <img src=\"${logo!\'\'}\" alt=\"logo\"\r\n style=\"margin-left: 17px; display: block;max-width: 100%;height: 100%;\">\r\n </td>\r\n </tr>\r\n <tr>\r\n\r\n <td style=\"padding-top:16px;padding-bottom: 16px;\">\r\n <table width=\"573\" align=\"center\"\r\n style=\"border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0;\">\r\n <tr>\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\" style=\"padding-left:18px;padding-top: 21px;\">\r\n <tr style=\"font-size: 20px;\">\r\n <#if value1 = \"RESERVATION_NEW\">\r\n <td style=\"color:#333333;\">\r\n <@message code=\"mgs.mail.sws.station.res\" locale=\"${locale}\"/>\r\n </td>\r\n </#if>\r\n\r\n <#if value1 = \"RESERVATION_CALL\">\r\n <td style=\"color:#333333;\">\r\n <@message code=\"mgs.mail.sws.station.rem\" locale=\"${locale}\"/>\r\n </td>\r\n </#if>\r\n\r\n <#if value1=\"STATION_DISTORY\">\r\n <td style=\"color:#333333;\">\r\n <@message code=\"mgs.mail.sws.station.rel\" locale=\"${locale}\"/>\r\n </td>\r\n </#if>\r\n\r\n <#if value1=\"STATION_TIME_DELAY\">\r\n <td style=\"color:#333333;\">\r\n <@message code=\"mgs.mail.sws.station.del\" locale=\"${locale}\"/>\r\n </td>\r\n </#if>\r\n\r\n <#if value1=\"RESERVATION_NOSIGN\">\r\n <td style=\"color:#333333;\">\r\n <@message code=\"mgs.mail.sws.station.nosgin\" locale=\"${locale}\"/>\r\n </td>\r\n </#if>\r\n\r\n <#if value1=\"RESERVATION_ADMIN\">\r\n <td style=\"color:#333333;\">\r\n <#if value12=\"manage\">\r\n <#if value10=\"3\">\r\n <@message code=\"mgs.mail.sws.station.admin\" locale=\"${locale}\"/>\r\n </#if>\r\n\r\n <#if value10=\"4\">\r\n <@message code=\"mgs.mail.sws.station.admin.end\" locale=\"${locale}\"/>\r\n </#if>\r\n\r\n </#if>\r\n\r\n <#if value12=\"client\">\r\n <@message code=\"mgs.mail.sws.station.admin\" locale=\"${locale}\"/>\r\n </#if>\r\n\r\n </td>\r\n </#if>\r\n \r\n </tr>\r\n <tr style=\"font-size: 16px;line-height: 2;\">\r\n <td style=\"color:#333333; padding-top:25px\">\r\n <@message code=\"mgs.mail.sws.hello\" locale=\"${locale}\"/> ，${value2!\'\'}:<br>\r\n &nbsp;&nbsp;&nbsp;&nbsp;\r\n <#if value1 = \"RESERVATION_NEW\">\r\n <@message code=\"mgs.mail.sws.station\" locale=\"${locale}\"/>\r\n ${value14!\'\'}，\r\n <#if value11=\"2\">\r\n <@message code=\"mgs.mail.sws.station.content.ordinary\" locale=\"${locale}\"/>\r\n </#if>\r\n <#if value11=\"1\">\r\n <@message code=\"mgs.mail.sws.station.content.special\" locale=\"${locale}\"/>\r\n </#if>\r\n </#if>\r\n\r\n <#if value1 = \"RESERVATION_CALL\">\r\n <@message code=\"mgs.mail.sws.station.call\" locale=\"${locale}\"/>\r\n ${value14!\'\'}，\r\n <@message code=\"mgs.mail.sws.station.check\" locale=\"${locale}\"/>\r\n </#if>\r\n\r\n <#if value1 = \"STATION_DISTORY\">\r\n <@message code=\"mgs.mail.sws.station.call\" locale=\"${locale}\"/>\r\n ${value14!\'\'}，\r\n <@message code=\"mgs.mail.sws.station.release\" locale=\"${locale}\"/>\r\n </#if>\r\n\r\n <#if value1 = \"STATION_TIME_DELAY\">\r\n <@message code=\"mgs.mail.sws.station.tit\" locale=\"${locale}\"/>\r\n ${value14!\'\'}，\r\n <@message code=\"mgs.mail.sws.station.delay\" locale=\"${locale}\"/>\r\n <a href= \"${value7!\'\'}\"> <@message code=\"mgs.mail.sws.station.confirm\" locale=\"${locale}\"/> </a>.\r\n </#if>\r\n\r\n <#if value1 = \"RESERVATION_NOSIGN\">\r\n <@message code=\"mgs.mail.sws.station.call\" locale=\"${locale}\"/>\r\n ${value14!\'\'}\r\n <@message code=\"mgs.mail.sws.station.content4\" locale=\"${locale}\"/>\r\n </#if>\r\n\r\n <#if value1=\"RESERVATION_ADMIN\">\r\n\r\n <#if value12=\"manage\">\r\n <#if value10=\"3\">\r\n <#if value13=\"1\">\r\n <@message code=\"mgs.mail.sws.station.tit\" locale=\"${locale}\"/>\r\n ${value14!\'\'}\r\n </#if>\r\n <#if value13=\"2\">\r\n ${value14!\'\'}\r\n </#if>\r\n <@message code=\"mgs.mail.sws.station.content5\" locale=\"${locale}\"/>\r\n ${value9!\'\'}\r\n <@message code=\"mgs.mail.sws.station.admin.cancel\" locale=\"${locale}\"/>\r\n </#if>\r\n\r\n <#if value10=\"4\">\r\n <#if value13=\"1\">\r\n <@message code=\"mgs.mail.sws.station.tit\" locale=\"${locale}\"/>\r\n ${value14!\'\'}\r\n </#if>\r\n\r\n <#if value13=\"2\">\r\n ${value14!\'\'}\r\n </#if>\r\n <@message code=\"mgs.mail.sws.station.content6\" locale=\"${locale}\"/>\r\n ${value9!\'\'}\r\n <@message code=\"mgs.mail.sws.station.end.cancel\" locale=\"${locale}\"/>\r\n </#if>\r\n </#if>\r\n\r\n <#if value12=\"client\">\r\n <@message code=\"mgs.mail.sws.station.tit\" locale=\"${locale}\"/>\r\n ${value14!\'\'}\r\n <@message code=\"mgs.mail.sws.station.cancel\" locale=\"${locale}\"/>\r\n </#if>\r\n </#if>\r\n </td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n <#if value13=\"2\">\r\n ${value3!\'\'}\r\n </#if>\r\n <#if value13=\"1\">\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%;padding-top:25px; \">\r\n <@message code=\"mgs.email.station.num\" locale=\"${locale}\"/> :\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-top: 25px;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${value3!\'\'}\r\n </td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n <@message code=\"mgs.email.station.address\" locale=\"${locale}\"/> :\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${value4!\'\'}\r\n </td>\r\n </tr>\r\n\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n <@message code=\"mgs.email.station.starttime\" locale=\"${locale}\"/>:\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${value5!\'\'}\r\n </td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n <@message code=\"mgs.mail.sws.station.entime\" locale=\"${locale}\"/>:\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${value6!\'\'}\r\n </td>\r\n </tr>\r\n </#if>\r\n </table>\r\n </td>\r\n </tr>\r\n\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td style=\"font-size: 18px;\" bgcolor=\"#ffffff\">\r\n <p style=\"margin-top: 15px;margin-right: 0;margin-bottom: 15px;margin-left: 55px;\">\r\n <@message code=\"mgs.mail.sws.remarks\" locale=\"${locale}\"/>\r\n </p>\r\n </td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n <tr style=\"background: #f4f4f4;border: 1px solid #eaeaea;\">\r\n <td>\r\n <table width=\"100%\">\r\n <tr>\r\n <td width=\"260\"\r\n style=\"line-height:150%; color:#333333; font-size:16px;padding-top: 16px;padding-bottom: 16px;padding-left: 10px;\">\r\n &nbsp;&nbsp;&nbsp;<@message code=\"mgs.mail.sws.systemname\" locale=\"${locale}\"/>\r\n <br>\r\n &nbsp;&nbsp;&nbsp;<@message code=\"mgs.mail.sws.phone\" locale=\"${locale}\"/> ：<@message code=\"mgs.mail.sws.phone.no\" locale=\"${locale}\"/><br>\r\n </td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n</table>\r\n</body>\r\n\r\n</html>', '工位系统工位预约邮件通知', 'sws', '2019-11-29 14:53:51');
