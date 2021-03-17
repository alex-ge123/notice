delete from `mail_template` where name = 'smtSyncFail_zh_CN';
insert into `mail_template` (`name`, `content`, `description`, `category`, `modtime`, `createtime`, `state`) values('smtSyncFail_zh_CN','<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"600\" bgcolor=\"#FFFFFF\">\r\n    <tr valign=\"top\">\r\n        <td colspan=\"2\">\r\n            <table align=\"center\" valign=\"top\" cellspacing=\"0\" cellpadding=\"20\" width=\"600\"border=\"0\">\r\n                <tr>\r\n                    <td colspan=\"2\">\r\n                        <table cellpadding=\"0\" cellspacing=\"0\"style=\"width: 100%; border-bottom:solid 1px; border-color:#c3c3c3;\">\r\n                            <tr>\r\n                                <td align=\"left\" style=\"font-size:14pt; line-height:50px;\">\r\n                                    <span>会议同步失败通知</span>\r\n                                </td>\r\n                            </tr>\r\n                        </table>\r\n                        <br/>\r\n                        <span>您有一个会议同步失败通知:</span>\r\n                        </br>\r\n                        <span class=\"k2\">\r\n                            <span>${meetingName!\'\'}</span>\r\n                        </span>\r\n                        <br/>\r\n                    </td>\r\n                </tr>\r\n                <tr valign=\"top\" align=\"center\">\r\n                    <td colspan=\"2\">\r\n                        <table cellpadding=\"10\" cellspacing=\"0\"style=\"width: 100%; border-bottom:solid 1px #f2f2f2; border-top:1px solid #f2f2f2; border-collapse:collapse;\">\r\n						    <tr>\r\n                                <td align=\"right\" style=\"color:#b4b4b4;\" width=\"25%\">\r\n                                    <span>租户名称&nbsp;:</span>\r\n                                </td>\r\n                                <td width=\"75%\">${tenantName!\'\'}</td>\r\n                            </tr>\r\n                            <tr>\r\n                                <td align=\"right\" style=\"color:#b4b4b4;\" width=\"25%\">\r\n                                    <span>会议室名称&nbsp;:</span>\r\n                                </td>\r\n                                <td width=\"75%\">${roomName!\'\'}</td>\r\n                            </tr>\r\n                            <tr>\r\n                                <td align=\"right\" style=\"color:#b4b4b4;\" width=\"25%\">\r\n                                    <span>会议时间&nbsp;:</span>\r\n                                </td>\r\n                                <td width=\"75%\">${meetingTime!\'\'}</td>\r\n                            </tr>\r\n                            <tr>\r\n                                <td align=\"right\" style=\"color:#b4b4b4;\" width=\"25%\">\r\n                                    <span>其他信息&nbsp;:</span>\r\n                                </td>\r\n                                <td width=\"75%\">${jsonStr!\'\'}</td>\r\n                            </tr>\r\n                        </table>\r\n                    </td>\r\n                </tr>\r\n            </table>\r\n        </td>\r\n    </tr>\r\n</table>\r\n','会议同步失败告警','smt','2021-03-16 17:01:55','2021-03-16 17:01:55','0');
