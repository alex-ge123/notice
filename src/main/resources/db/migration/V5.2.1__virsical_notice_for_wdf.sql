-- 添加通讯录同步告警邮件
INSERT INTO `mail_template` (`name`, `content`, `description`, `category`, `modtime`, `createtime`, `state`) VALUES
('syncWarning', '<#macro message code, locale>${loccalMessage.getMessage(code, locale)}</#macro>\n<!DOCTYPE html\n    PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<!-- 删除人数超限告警 -->\n<head>\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n    <title>Virsical</title>\n</head>\n\n<body>\n    <table border=\"0\"\n        style=\"margin-top: 0;margin-bottom: 0;margin-left: auto;margin-right: auto; width:606px; font-size:16px; color:#333333;background: #f9f9f9; font-family: Helvetica, Tahoma, Arial, \'PingFang SC\', STXihei, \'华文细黑\', \'Microsoft YaHei\', \'微软雅黑\', SimSun, \'宋体\', sans-serif;border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0; \">\n        <tr style=\"height:6px;background:linear-gradient(90deg,#33b8e8 0%,#665ba1 100%);\">\n            <td width=\"606px\"></td>\n        </tr>\n        <tr style=\"height: 103px;background: #ffffff; border: 1px solid #eaeaea;\">\n            <td style=\"display: table-cell;vertical-align: middle;width: 130px;height: 39px;\" valign=\"middle\">\n                <!-- <img src=\"./CaptainAmerica\" alt=\"logo\" -->\n                <!-- style=\"margin-left: 17px; display: block;max-width: 100%;height: 100%;\"> -->\n                <img src=\"${logo!\'\'}\" alt=\"logo\"\n                    style=\"margin-left: 17px; display: block; height: 50px;\">\n            </td>\n        </tr>\n        <tr>\n            <td style=\"padding-top:16px;padding-bottom: 16px;background: #f9f9f9;\">\n                <table width=\"573\" align=\"center\"\n                    style=\"border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0;margin-top: 9px;;\">\n                    <tr>\n                        <td>\n                            <table width=\"100%\" bgcolor=\"#FFFFFF\" style=\"padding-left:18px;padding-top: 21px;\">\n                                <tr style=\"font-size: 20px;\">\n                                    <td style=\"color:#333333;\">${value1!\'\'}</td>\n                                    <td style=\"width: 100px;\"></td>\n                                </tr>\n                                <tr style=\"font-size: 16px;line-height: 2;\">\n                                    <td style=\"color:#333333; padding-top:25px\">Hello\n                                    <br>\n                                    This synchronization has been ignored Because the number of users deleted this time is ${value2}, which exceeds the upper limit of alarm by ${value3}. The automatic synchronization switch has been turned off. Please confirm and manually synchronize!\n                                    </td>\n                                </tr>\n                                <tr style=\"font-size: 16px;\">\n                                    <#if locale == \"zh_TW\" || locale == \"TW\">\n                                    <td style=\"color:#333333; padding-top:15px;\">\n                                    <@message code=\"msg.email.content.vst.hello\" locale=\"${locale}\"/>\n                                    本次通訊錄定時同步删除用戶數量${value2}個，已超過告警數量（${value3}）上限，忽略本次同步。已關閉自動同步開關，請確認後手動同步。\n                                    </td>\n                                    <#else>\n                                    <td style=\"color:#333333; padding-top:15px;\">\n                                    <@message code=\"msg.email.content.vst.hello\" locale=\"${locale}\"/>\n                                    本次通讯录定时同步删除用户数量${value2}个，已超过告警数量（${value3}）上限，忽略本次同步。已关闭自动同步开关，请确认后手动同步。\n                                    </td>\n                                    </#if>\n                                </tr>\n                                <tr style=\"font-size: 16px;\">\n                                    <td style=\"width: 100px;\"></td>\n                                </tr>\n                            </table>\n                        </td>\n                    </tr>\n                    <tr style=\"border-top: 1px solid #eaeaea;\">\n                       <td style=\"font-size: 18px;\" bgcolor=\"#ffffff\">\n                            <p style=\"margin-top: 15px;margin-right: 0;margin-bottom: 15px;margin-left: 55px;\"><@message code=\"msg.email.content.vst.happy\" locale=\"${locale}\"/></p>\n			    <p style=\"margin-top: 15px;margin-right: 0;margin-bottom: 15px;margin-left: 55px;\">We wish you a pleasant day!</p>\n                       </td>\n                    </tr>\n                </table>\n            </td>\n        </tr>\n        <tr style=\"background: #f4f4f4;border: 1px solid #eaeaea;\">\n            <td>\n                <table width=\"100%\">\n                    <tr>\n                        <td width=\"260\"\n                            style=\"line-height:150%; color:#333333; font-size:16px;padding-top: 16px;padding-bottom: 16px;padding-left: 10px;\">\n                            &nbsp;&nbsp;&nbsp;${systemName!\'\'}<br>\n                            &nbsp;&nbsp;&nbsp;<@message code=\"msg.email.content.visitorManagement.phone\" locale=\"${locale}\"/>${phone!\'\'}<br>\n                        </td>\n                    </tr>\n                </table>\n            </td>\n        </tr>\n    </table>\n</body>\n\n</html>', '通讯录同步告警邮件', 'common', '2020-12-22 08:41:29', '2020-12-22 08:41:29', 0);