-- 创建邀请通知-访客 模板修改
update `mail_template` set `content` = '<table width=\"573\" align=\"center\"\r\n style=\"border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0;margin-top: 9px;;\">\r\n <tr>\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\" style=\"padding-left:18px;padding-top: 21px;\">\r\n <tr>\r\n <td style=\"color:#333333;font-size: 20px;\">访问邀请确认函</td>\r\n <td style=\"width: 100px;\"></td>\r\n </tr>\r\n <tr>\r\n <td style=\"color:#333333; padding-top:15px;font-size: 16px;\">${visitorName!\'\'}，您好！</td>\r\n <td style=\"width: 100px;text-align: center;height: 32px;\">\r\n </td>\r\n </tr>\r\n <tr>\r\n <td style=\"color:#333333; padding-top:10px; padding-bottom:15px;line-height: 2;font-size: 16px;\">\r\n ${employeeName!\'\'}诚邀您访问${locationName!\'\'}，具体访问细节如下: </td>\r\n <td style=\"height: 32px;width: 88px;text-align: center;padding-top: 10px;\"\r\n valign=\"top\">\r\n </td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 邀请人: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${employeeName!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 访问时间: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${visitTime!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 联系电话: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${employeePhone!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 公司地址: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 20px;\">\r\n ${companyAddress!\'\'}</td>\r\n </tr>\r\n <!-- 人脸识别开关开启才显示 -->\r\n <#if faceSwitch == \'1\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 上传照片:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 20px;\">\r\n <a href=\"${faceUploadLink!\'\'}\" style=\"color:#0D75CD;font-size: 16px\">点击上传人脸识别照片</a></td>\r\n </tr>\r\n </#if>\r\n <tr style=\"padding-top: -30px;\">\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; padding-bottom: 36px; text-align: right; line-height:120%\">\r\n 签到二维码</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding: 0 0 15px 20px\">\r\n 请保存二维码或打印此函，到达后可扫描二维码或输入邀请码（${qrCode!\'\'}）完成签到。<br>\r\n </td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 44px;padding-bottom: 10px;\">\r\n <img src=\"${qrCodeLink!\'\'}\" alt=\"InvitationCode\"\r\n style=\"margin-top: 1px; margin-left: -24px;width: 150px;height: 150px;\"/>\r\n </td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n <!-- 有会议信息才显示 -->\r\n <#if isShowMeetingInfo == \'1\'>\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 会议主题: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${topic!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 会议室: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${roomName!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 开始时间: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${smtStartTime!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 结束时间: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${smtEndTime!\'\'}</td>\r\n </tr>\r\n <#if meetingNo?? && meetingNo != \'\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 会议ID: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${meetingNo!\'\'}</td>\r\n </tr>\r\n </#if>\r\n <#if url?? && url != \'\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 邀请链接: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${url!\'\'}</td>\r\n </tr>\r\n </#if>\r\n </table>\r\n </td>\r\n </tr>\r\n </#if>\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td style=\"font-size: 18px;\" bgcolor=\"#ffffff\">\r\n <p style=\"margin-top: 15px;margin-right: 0;margin-bottom: 15px;margin-left: 55px;\">祝您度过愉快的一天!</p>\r\n </td>\r\n </tr>\r\n</table>' where `name` = 'vstConfirmedVisitor_zh_CN';
update `mail_template` set `content` = '<table width=\"573\" align=\"center\"\r\n style=\"border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0;margin-top: 9px;;\">\r\n <tr>\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\" style=\"padding-left:18px;padding-top: 21px;\">\r\n <tr>\r\n <td style=\"color:#333333;font-size: 20px;\">訪問邀請確認函</td>\r\n <td style=\"width: 100px;\"></td>\r\n </tr>\r\n <tr>\r\n <td style=\"color:#333333; padding-top:15px;font-size: 16px;\">${visitorName!\'\'}，您好！</td>\r\n <td style=\"width: 100px;text-align: center;height: 32px;\">\r\n </td>\r\n </tr>\r\n <tr>\r\n <td style=\"color:#333333; padding-top:10px; padding-bottom:15px;line-height: 2;font-size: 16px;\">\r\n ${employeeName!\'\'}誠邀您訪問${locationName!\'\'}，具體訪問細節如下：</td>\r\n <td style=\"height: 32px;width: 88px;text-align: center;padding-top: 10px;\"\r\n valign=\"top\">\r\n </td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 邀請人: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${employeeName!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 預約時間: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${visitTime!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 聯系電話: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${employeePhone!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 公司地址: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 20px;\">\r\n ${companyAddress!\'\'}</td>\r\n </tr>\r\n <!-- 人脸识别开关开启才显示 -->\r\n <#if faceSwitch == \'1\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 上傳照片:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 20px;\">\r\n <a href=\"${faceUploadLink!\'\'}\" style=\"color:#0D75CD;font-size: 16px\">點擊上傳人臉識別照片</a></td>\r\n </tr>\r\n </#if>\r\n <tr style=\"padding-top: -30px;\">\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; padding-bottom: 36px; text-align: right; line-height:120%\">\r\n 簽到二維碼</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding: 0 0 15px 20px\">\r\n 請保存二維碼或打印此函，到達後可掃描二維碼或輸入邀請碼（${qrCode!\'\'}）完成簽到。<br>\r\n </td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 44px;padding-bottom: 10px;\">\r\n <img src=\"${qrCodeLink!\'\'}\" alt=\"InvitationCode\"\r\n style=\"margin-top: 1px; margin-left: -24px;width: 150px;height: 150px;\"/>\r\n </td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n <!-- 有会议信息才显示 -->\r\n <#if isShowMeetingInfo == \'1\'>\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 會議主題 :</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${topic!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 會議室 :</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${roomName!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 開始時間 :</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${smtStartTime!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 結束時間 :</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${smtEndTime!\'\'}</td>\r\n </tr>\r\n <#if meetingNo?? && meetingNo != \'\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 會議ID&nbsp;:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${meetingNo!\'\'}</td>\r\n </tr>\r\n </#if>\r\n <#if url?? && url != \'\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 邀請鏈接&nbsp;:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${url!\'\'}</td>\r\n </tr>\r\n </#if>\r\n </table>\r\n </td>\r\n </tr>\r\n </#if>\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td style=\"font-size: 18px;\" bgcolor=\"#ffffff\">\r\n <p style=\"margin-top: 15px;margin-right: 0;margin-bottom: 15px;margin-left: 55px;\">祝您度過愉快的一天!</p>\r\n </td>\r\n </tr>\r\n</table>' where `name` = 'vstConfirmedVisitor_zh_TW';
update `mail_template` set `content` = '<table width=\"573\" align=\"center\"\r\n style=\"border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0;margin-top: 9px;;\">\r\n <tr>\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\" style=\"padding-left:18px;padding-top: 21px;\">\r\n <tr>\r\n <td style=\"color:#333333;font-size: 20px;\">Visit Confirmation</td>\r\n <td style=\"width: 100px;\"></td>\r\n </tr>\r\n <tr style=\"line-height: 2;\">\r\n <td style=\"color:#333333; padding-top:25px;font-size: 16px;\">Dear ${visitorName!\'\'}:<br>We are very pleased to inform you of the visit details as below.</td>\r\n <td style=\"width: 100px;\"></td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Host Employee:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${employeeName!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Visit Time:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${visitTime!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Mobile:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${employeePhone!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Address:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 20px;\">\r\n ${companyAddress!\'\'}</td>\r\n </tr>\r\n <!-- 人脸识别开关开启才显示 -->\r\n <#if faceSwitch == \'1\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Upload Photo:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 20px;\">\r\n <a href=\"${faceUploadLink!\'\'}\" style=\"color:#0D75CD;font-size: 16px\">Click to upload face recognition photos</a></td>\r\n </tr>\r\n </#if>\r\n <tr style=\"padding-top: -30px;\">\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; padding-bottom: 36px; text-align: right; line-height:120%\">\r\n QR Code:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding: 0 0 15px 20px\">\r\n Please save this QR code on your phone or print it out after your arrival to sign in.（${qrCode!\'\'}）<br>\r\n </td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 44px;padding-bottom: 10px;\">\r\n <img src=\"${qrCodeLink!\'\'}\" alt=\"InvitationCode\"\r\n style=\"margin-top: 1px; margin-left: -24px;width: 150px;height: 150px;\"/>\r\n </td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n <!-- 有会议信息才显示 -->\r\n <#if isShowMeetingInfo == \'1\'>\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Title:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${topic!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Room:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${roomName!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Start:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${smtStartTime!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n End:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${smtEndTime!\'\'}</td>\r\n </tr>\r\n <#if meetingNo?? && meetingNo != \'\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Meeting ID:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${meetingNo!\'\'}</td>\r\n </tr>\r\n </#if>\r\n <#if url?? && url != \'\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Invitation URL:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${url!\'\'}</td>\r\n </tr>\r\n </#if>\r\n </table>\r\n </td>\r\n </tr>\r\n </#if>\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td style=\"font-size: 18px;\" bgcolor=\"#ffffff\">\r\n <p style=\"margin-top: 15px;margin-right: 0;margin-bottom: 15px;margin-left: 55px;\">We wish you a pleasant day!</p>\r\n </td>\r\n </tr>\r\n</table>' where `name` = 'vstConfirmedVisitor_en';
-- 变更邀请-访客 模板修改
update `mail_template` set `content` = '<table width=\"573\" align=\"center\"\r\n style=\"border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0;margin-top: 9px;;\">\r\n <tr>\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\" style=\"padding-left:18px;padding-top: 21px;\">\r\n <tr>\r\n <td style=\"color:#333333;font-size: 20px;\">访问变更通知</td>\r\n <td style=\"width: 100px;\"></td>\r\n </tr>\r\n <tr>\r\n <td style=\"color:#333333; padding-top:15px;font-size: 16px;\">${visitorName!\'\'}，您好！</td>\r\n <td style=\"width: 100px;text-align: center;height: 32px;\">\r\n </td>\r\n </tr>\r\n <tr>\r\n <td style=\"color:#333333; padding-top:10px; padding-bottom:15px;line-height: 2;font-size: 16px;\">\r\n 您【${oldVisitTime!\'\'}】的邀约已被变更，新的访问信息如下：</td>\r\n <td style=\"height: 32px;width: 88px;text-align: center;padding-top: 10px;font-size: 16px;\"\r\n valign=\"top\">\r\n </td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 邀请人: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${employeeName!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 预约时间: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${visitTime!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 联系电话: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${employeePhone!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 公司地址: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 20px;\">\r\n ${companyAddress!\'\'}</td>\r\n </tr>\r\n <!-- 人脸识别开关开启才显示 -->\r\n <#if faceSwitch == \'1\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 上传照片:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 20px;\">\r\n <a href=\"${faceUploadLink!\'\'}\" style=\"color:#0D75CD;font-size: 16px\">点击上传人脸识别照片</a></td>\r\n </tr>\r\n </#if>\r\n <tr style=\"padding-top: -30px;\">\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; padding-bottom: 36px; text-align: right; line-height:120%\">\r\n 签到二维码</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding: 0 0 15px 20px\">\r\n 请保存二维码或打印此函，到达后可扫描二维码或输入邀请码（${qrCode!\'\'}）完成签到。<br>\r\n </td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 44px;padding-bottom: 10px;\">\r\n <img src=\"${qrCodeLink!\'\'}\" alt=\"InvitationCode\"\r\n style=\"margin-top: 1px; margin-left: -24px;width: 150px;height: 150px;\"/>\r\n </td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n <!-- 有会议信息才显示 -->\r\n <#if isShowMeetingInfo == \'1\'>\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 会议主题: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${topic!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 会议室: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${roomName!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 开始时间: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${smtStartTime!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 结束时间: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${smtEndTime!\'\'}</td>\r\n </tr>\r\n <#if meetingNo?? && meetingNo != \'\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 会议ID: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${meetingNo!\'\'}</td>\r\n </tr>\r\n </#if>\r\n <#if url?? && url != \'\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 邀请链接: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${url!\'\'}</td>\r\n </tr>\r\n </#if>\r\n </table>\r\n </td>\r\n </tr>\r\n </#if>\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td style=\"font-size: 18px;\" bgcolor=\"#ffffff\">\r\n <p style=\"margin-top: 15px;margin-right: 0;margin-bottom: 15px;margin-left: 55px;\">祝您度过愉快的一天!</p>\r\n </td>\r\n </tr>\r\n</table>' where `name` = 'vstConfirmedEditVisitor_zh_CN';
update `mail_template` set `content` = '<table width=\"573\" align=\"center\"\r\n style=\"border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0;margin-top: 9px;;\">\r\n <tr>\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\" style=\"padding-left:18px;padding-top: 21px;\">\r\n <tr>\r\n <td style=\"color:#333333;font-size: 20px;\">訪問變更通知</td>\r\n <td style=\"width: 100px;\"></td>\r\n </tr>\r\n <tr>\r\n <td style=\"color:#333333; padding-top:15px;font-size: 16px;\">${visitorName!\'\'}，您好！</td>\r\n <td style=\"width: 100px;text-align: center;height: 32px;\">\r\n </td>\r\n </tr>\r\n <tr>\r\n <td style=\"color:#333333; padding-top:10px; padding-bottom:15px;line-height: 2;font-size: 16px;\">\r\n 您【${oldVisitTime!\'\'}】的邀約已被變更，新的訪問資訊如下：</td>\r\n <td style=\"height: 32px;width: 88px;text-align: center;padding-top: 10px;font-size: 16px;\"\r\n valign=\"top\">\r\n </td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 邀請人: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${employeeName!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 預約時間: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${visitTime!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 聯系電話: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${employeePhone!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 公司地址: </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 20px;\">\r\n ${companyAddress!\'\'}</td>\r\n </tr>\r\n <!-- 人脸识别开关开启才显示 -->\r\n <#if faceSwitch == \'1\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 上傳照片:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 20px;\">\r\n <a href=\"${faceUploadLink!\'\'}\" style=\"color:#0D75CD;font-size: 16px\">點擊上傳人臉識別照片</a></td>\r\n </tr>\r\n </#if>\r\n <tr style=\"padding-top: -30px;\">\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; padding-bottom: 36px; text-align: right; line-height:120%\">\r\n 簽到二維碼</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding: 0 0 15px 20px\">\r\n 請保存二維碼或打印此函，到達後可掃描二維碼或輸入邀請碼（${qrCode!\'\'}）完成簽到。<br>\r\n </td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 44px;padding-bottom: 10px;\">\r\n <img src=\"${qrCodeLink!\'\'}\" alt=\"InvitationCode\"\r\n style=\"margin-top: 1px; margin-left: -24px;width: 150px;height: 150px;\"/>\r\n </td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n <!-- 有会议信息才显示 -->\r\n <#if isShowMeetingInfo == \'1\'>\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 會議主題 :</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${topic!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 會議室 :</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${roomName!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 開始時間 :</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${smtStartTime!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 結束時間 :</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${smtEndTime!\'\'}</td>\r\n </tr>\r\n <#if meetingNo?? && meetingNo != \'\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 會議ID&nbsp;:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${meetingNo!\'\'}</td>\r\n </tr>\r\n </#if>\r\n <#if url?? && url != \'\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n 邀請鏈接&nbsp;:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${url!\'\'}</td>\r\n </tr>\r\n </#if>\r\n </table>\r\n </td>\r\n </tr>\r\n </#if>\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td style=\"font-size: 18px;\" bgcolor=\"#ffffff\">\r\n <p style=\"margin-top: 15px;margin-right: 0;margin-bottom: 15px;margin-left: 55px;\">祝您度過愉快的一天!</p>\r\n </td>\r\n </tr>\r\n</table>' where `name` = 'vstConfirmedEditVisitor_zh_TW';
update `mail_template` set `content` = '<table width=\"573\" align=\"center\"\r\n style=\"border: 1px solid #eaeaea;border-collapse: collapse; border-spacing: 0;margin-top: 9px;;\">\r\n <tr>\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\" style=\"padding-left:18px;padding-top: 21px;\">\r\n <tr>\r\n <td style=\"color:#333333;font-size: 20px;\">Visit Modification</td>\r\n <td style=\"width: 100px;\"></td>\r\n </tr>\r\n <tr style=\"line-height: 2;\">\r\n <td style=\"color:#333333; padding-top:25px;font-size: 16px;\">Dear ${visitorName!\'\'}:<br>Your invitation on ${oldVisitTime!\'\'} has been modified as below.</td>\r\n <td style=\"width: 100px;font-size: 16px;\"></td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Host employee:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${employeeName!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Visit time:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${visitTime!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Mobile:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${employeePhone!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Address:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 20px;\">\r\n ${companyAddress!\'\'}</td>\r\n </tr>\r\n <!-- 人脸识别开关开启才显示 -->\r\n <#if faceSwitch == \'1\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Upload Photo:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 20px;\">\r\n <a href=\"${faceUploadLink!\'\'}\" style=\"color:#0D75CD;font-size: 16px\">Click to upload face recognition photos</a></td>\r\n </tr>\r\n </#if>\r\n <tr style=\"padding-top: -30px;\">\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; padding-bottom: 36px; text-align: right; line-height:120%\">\r\n QR Code:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding: 0 0 15px 20px\">\r\n Please save this QR code on your phone or print it out after your arrival to sign in.（${qrCode!\'\'}）<br>\r\n </td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n </td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;padding-left: 44px;padding-bottom: 10px;\">\r\n <img src=\"${qrCodeLink!\'\'}\" alt=\"InvitationCode\"\r\n style=\"margin-top: 1px; margin-left: -24px;width: 150px;height: 150px;\"/>\r\n </td>\r\n </tr>\r\n </table>\r\n </td>\r\n </tr>\r\n <!-- 有会议信息才显示 -->\r\n <#if isShowMeetingInfo == \'1\'>\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td>\r\n <table width=\"100%\" bgcolor=\"#FFFFFF\">\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Title:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${topic!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Room:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${roomName!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Start:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${smtStartTime!\'\'}</td>\r\n </tr>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n End:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${smtEndTime!\'\'}</td>\r\n </tr>\r\n <#if meetingNo?? && meetingNo != \'\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Meeting ID:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${meetingNo!\'\'}</td>\r\n </tr>\r\n </#if>\r\n <#if url?? && url != \'\'>\r\n <tr>\r\n <td width=\"150\" height=\"50\"\r\n style=\"font-size: 16px; color:#999999; text-align: right; line-height:120%\">\r\n Invitation URL:</td>\r\n <td style=\"font-size: 16px; color:#333333; text-align:left;\">\r\n &nbsp;&nbsp;&nbsp;&nbsp;${url!\'\'}</td>\r\n </tr>\r\n </#if>\r\n </table>\r\n </td>\r\n </tr>\r\n </#if>\r\n <tr style=\"border-top: 1px solid #eaeaea;\">\r\n <td style=\"font-size: 18px;\" bgcolor=\"#ffffff\">\r\n <p style=\"margin-top: 15px;margin-right: 0;margin-bottom: 15px;margin-left: 55px;\">We wish you a pleasant day!</p>\r\n </td>\r\n </tr>\r\n</table>' where `name` = 'vstConfirmedEditVisitor_en';