UPDATE `mail_template` SET `content` = '<table style=\"margin:-16px auto;\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"600\" bgcolor=\"#FFFFFF\">\r\n	<tr>\r\n		<td colspan=\"2\">\r\n			<table align=\"center\" class=\"banner\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\"height=\"100\">\r\n				<tr>\r\n					<td colspan=\"2\" bgcolor=\"#F2F2F2\">\r\n 						<table cellpadding=\"0\" cellspacing=\"0\" style=\"margin:10px;\" width=\"580\"bgcolor=\"#FFFFFF\">\r\n  							<tr>\r\n   								<td align=\"left\" colspan=\"2\"style=\"font-size:14pt; line-height:30px;\">\r\n   									<#if emailType?? && (emailType == \"0-0\" || emailType == \"0-1\" || emailType == \"3-0\" || emailType == \"3-1\")>\r\n										<span style=\"margin-left:10px;\">Application for service</span>\r\n									<#elseif emailType?? && (emailType == \"1-0\" || emailType == \"1-1\" || emailType == \"2-0\" || emailType == \"2-1\")>\r\n										<span style=\"margin-left:10px;\">Notification of the results of confirmation</span>\r\n									<#elseif emailType?? && (emailType == \"4-0\" || emailType == \"4-1\")>\r\n    									<span style=\"margin-left:10px;\">Application for meeting services cancelled</span>\r\n   									</#if>\r\n    							</td>\r\n   							</tr>\r\n							<tr>\r\n								<td align=\"left\" style=\"font-size:12pt; line-height:30px;\">\r\n									<#if emailType?? && (emailType == \"0-0\" || emailType == \"0-1\" || emailType == \"3-0\" || emailType == \"3-1\")>\r\n										<span style=\"margin-left:10px;\">Hi ${callEn!\'\'} :</span>\r\n										<br/>\r\n										<span class=\"k2\" style=\"margin-left:30px;\">You have received an application for meeting service. Please check it soon and give your feedback.</span>\r\n									<#elseif emailType?? && (emailType == \"1-0\"|| emailType == \"1-1\")>\r\n										<span style=\"margin-left:10px;\">Hi ${callEn!\'\'} :</span>\r\n										<br/>\r\n										<span class=\"k2\" style=\"margin-left:30px;\">Your application for meeting service has been approved.</span>\r\n									<#elseif emailType?? && (emailType == \"2-0\" || emailType == \"2-1\")>\r\n										<span style=\"margin-left:10px;\">Hi ${callEn!\'\'} :</span>\r\n										<br/>\r\n										<span class=\"k2\" style=\"margin-left:30px;\">Your application for meeting service has not been approved. Reason :${refuseRemark!\'\'}</span>\r\n									<#elseif emailType?? && (emailType == \"4-0\"|| emailType == \"4-1\")>\r\n										<span style=\"margin-left:10px;\">Hi ${callEn!\'\'} :</span>\r\n										<br/>\r\n										<span class=\"k2\" style=\"margin-left:30px;\">Some meeting services has been cancelled.</span>\r\n									</#if>\r\n								</td>\r\n							</tr>\r\n						</table>\r\n					</td>\r\n				</tr>\r\n			</table>\r\n		</td>\r\n	</tr>\r\n	<tr valign=\"top\">\r\n		<td colspan=\"2\" bgcolor=\"#F2F2F2\" style=\"padding: 0 10px 10px 10px;\">\r\n			<table align=\"center\" valign=\"top\" cellspacing=\"0\" cellpadding=\"0\" width=\"580\"border=\"0\" bgcolor=\"#FFFFFF\">\r\n				<tr valign=\"top\" align=\"center\">\r\n					<td colspan=\"2\">\r\n						<table cellpadding=\"10\" cellspacing=\"0\"style=\"border-bottom:solid 1px #f2f2f2; border-top:1px solid #f2f2f2; border-collapse:collapse;\"width=\"580\">\r\n 							<tr>\r\n  								<td align=\"right\" style=\"color:#b4b4b4;\" width=\"25%\">\r\n   									<span>Title&nbsp;:</span>\r\n  								</td>\r\n 								<td width=\"75%\" style=\"word-break:break-all\">${meetingName!\'\'}</td>\r\n 							</tr>\r\n							<tr>\r\n 								<td align=\"right\" style=\"color:#b4b4b4;\" width=\"25%\">\r\n  									<span>Room&nbsp;:</span>\r\n 								</td>\r\n								<td width=\"75%\">${roomName!\'\'}</td>\r\n							</tr>\r\n							<tr>\r\n								<td align=\"right\" style=\"color:#b4b4b4;\" width=\"25%\">\r\n 									<span>Start&nbsp;:</span>\r\n								</td>\r\n								<td width=\"75%\">${meetingStartTime!\'\'}</td>\r\n							</tr>\r\n							<tr>\r\n								<td align=\"right\" style=\"color:#b4b4b4;\" width=\"25%\">\r\n									<span>End&nbsp;:</span>\r\n								</td>\r\n								<td width=\"75%\">${meetingEndTime!\'\'}</td>\r\n							</tr>\r\n							<tr>\r\n								<td align=\"right\" style=\"color:#b4b4b4;\" width=\"25%\">\r\n									<span>Host&nbsp;:</span>\r\n								</td>\r\n								<td width=\"75%\">${creatorUserEn!\'\'}</td>\r\n							</tr>\r\n							<tr>\r\n								<td align=\"right\" style=\"color:#b4b4b4;\" valign=\"top\" width=\"25%\">\r\n									<span>Service name&nbsp;:</span>\r\n								</td>\r\n								<td width=\"75%\">\r\n									<table width=\"80%\">\r\n										<tr style=\"background: #F8F6F5;\">\r\n											<td align=\"left\" width=\"40%\" height=\"40px\"style=\"border-bottom: 1px solid #E4E4E4;\">Service</td>\r\n											<#if emailType?? && (emailType==\"0-1\" || emailType==\"1-1\" || emailType==\"2-1\" || emailType==\"3-1\" || emailType == \"4-1\")>\r\n												<td align=\"left\" width=\"20%\" height=\"40px\"style=\"border-bottom: 1px solid #E4E4E4;\">Level</td>\r\n											</#if>\r\n											<td align=\"left\" width=\"25%\" height=\"40px\"style=\"border-bottom: 1px solid #E4E4E4;\">Room</td>\r\n											<td align=\"right\" width=\"15%\" height=\"40px\"style=\"border-bottom: 1px solid #E4E4E4;\">Number</td>\r\n										</tr>\r\n										${svcApplies!\'\'}\r\n									</table>\r\n								</td>\r\n							</tr>\r\n						</table>\r\n					</td>\r\n				</tr>\r\n			</table>\r\n		</td>\r\n	</tr>\r\n</table>\r\n'  WHERE `name` = 'smtSvcApply_en' AND `category` = 'smt';
UPDATE `mail_template` SET `content` = '<table align=\"center\" border=\"0\" style=\"margin-top: -16px;margin-bottom: -16px; margin-left: auto;margin-right: auto; font-size:16px; color:#333333;background: #f9f9f9; font-family: Helvetica, Tahoma, Arial, \'PingFang SC\', STXihei, \'华文细黑\', \'Microsoft YaHei\', \'微软雅黑\', SimSun, \'宋体\', sans-serif; border-collapse: collapse; border-spacing: 0;\" >\r\n    <tr>\r\n        <td bgcolor=\"#EFEFEF\">\r\n           <table cellpadding=\"0\" cellspacing=\"0\" style=\"margin:10px; pading: 5px; width: 580px; background-color: #FFFFFF\">\r\n                <tr>\r\n                     <td align=\"left\" colspan=\"2\"style=\"font-size:20px; line-height:30px;\">\r\n                        <div style=\"margin-left:10px;display: block;font-family: PingFang SC;font-weight: 500;color: rgba(51,51,51,1);\">\r\n                            <#if emailType??>\r\n                                <#if emailType==\'1\'>\r\n                                    <div>Invited of Meeting</div>\r\n                                <#elseif emailType==\'2\'>\r\n                                    <div>Updated of Meeting</div>\r\n                                <#elseif emailType==\'3\' || emailType==\'10\'>\r\n                                    <div>Canceled of Meeting</div>\r\n                                <#elseif emailType==\'4\'>\r\n                                    <div>Reminder of Meeting</div>\r\n                                <#elseif emailType==\'6\' || emailType==\'7\'>\r\n                                    <div>ACK of Meeting</div>\r\n                                <#elseif emailType==\'8\'>\r\n                                    <div>Meeting scheduled successfully</div>\r\n                                <#elseif emailType==\'9\'>\r\n                                    <div>Updated of Meeting</div>\r\n                                <#elseif emailType==\'15\' || emailType==\'22\' || emailType==\'23\'>\r\n                                    <div>Infrared Device Reminding</div>\r\n                                <#elseif emailType==\'18\'>\r\n                                    <div>Reminder of Meeting</div>\r\n                                <#elseif emailType==\'5\' || emailType==\'11\' || emailType==\'12\' || emailType==\'13\' || emailType==\'40\' || emailType==\'41\'>\r\n                                    <div>Notice of Meeting</div>\r\n                                <#elseif emailType==\'66\'>\r\n                                    <div>Review Message</div>\r\n                                <#elseif emailType==\'77\' || emailType==\'78\' || emailType==\'88\' || emailType==\'99\'>\r\n                                    <div>Meeting service notice</div>\r\n                                <#elseif emailType==\'101\' || emailType==\'102\'>\r\n                                    <div>Room maintain notice</div>\r\n                                <#elseif emailType==\'103\' || emailType==\'104\'>\r\n                                    <div>Room status notice</div>\r\n                                <#elseif emailType==\'20\' || emailType==\'21\'>\r\n                                    <div>Meeting Room Automatic Release</div>\r\n                                </#if>\r\n                            </#if>\r\n                        </span>\r\n                     </td>\r\n                </tr>\r\n                <tr>\r\n                    <td colspan=\"2\" align=\"left\"style=\"font-size:16px; line-height:30px;color: rgba(51,51,51,1);\">\r\n                        <#if emailType?? && (emailType!=\'15\' && emailType!=\'17\' && emailType!=\'20\' && emailType!=\'21\')>\r\n                            <span style=\"margin-left:10px;\">\r\n                                Hi ${callEn!\'\'},\r\n                            </span>\r\n                        </#if>\r\n                    </td>\r\n                </tr>\r\n                <tr>\r\n                    <td align=\"left\" style=\"font-size: 16px; line-height: 30px;color: rgba(51,51,51,1);\">\r\n                        <p class=\"k2\"style=\"margin-left: 10px; margin-right: 10px; margin-top: 0px; margin-bottom: 10px; word-break: break-all;\" width=\"80%\">\r\n                            <#if emailType??>\r\n                                <#if emailType==\'1\'>\r\n                                    <#if roomName?? && roomName==\'\' && recurring==\'10\'>\r\n                                        ${creatorUserEn!\'\'} booked a meeting, which will start in ${meetingTime!\'\'}.\r\n                                    <#else>\r\n                                        ${creatorUserEn!\'\'} booked a meeting, which will start in ${meetingTime!\'\'}, in ${roomName!\'\'}.\r\n                                    </#if>\r\n                                <#elseif emailType==\'2\'>\r\n                                    Your meeting of [${meetingName!\'\'}] has been modified.\r\n                                <#elseif emailType==\'3\'>\r\n                                    <#if cancelType==\'0\'>\r\n                                        Your meeting of [${meetingName!\'\'}] has been canceled.\r\n                                    <#elseif cancelType==\'1\'>\r\n                                        Your [${meetingName!\'\'}] because of no sign in, ${roomName!\'\'} has been released.\r\n                                    <#elseif cancelType==\'2\'>\r\n                                        Your [${meetingName!\'\'}] because of no people accept, ${roomName!\'\'} has been released.\r\n                                    <#elseif cancelType==\'3\'>\r\n                                        Your [${meetingName!\'\'}] because of room maintain, ${roomName!\'\'} has been released.\r\n                                    <#elseif cancelType==\'4\'>\r\n                                        Your [${meetingName!\'\'}] because of room has been out of service, ${roomName!\'\'} has been released.\r\n                                    </#if>\r\n                                <#elseif emailType==\'4\'>\r\n                                    Your meeting of [${meetingName!\'\'}] will start in ${meetingTime!\'\'}, please make preparations before the meeting.\r\n                                <#elseif emailType==\'5\'>\r\n                                    Due to the adjustment of the list of participants, you don\'t need to attend this meeting. If you have any objection, please contact the meeting organizer.\r\n                                <#elseif emailType==\'6\'>\r\n                                    ${receiverEn!\'\'} has accepted the invitation on the meeting.\r\n                                <#elseif emailType==\'7\'>\r\n                                    ${receiverEn!\'\'} has refused the invitation on the meeting.\r\n                                <#elseif emailType==\'8\'>\r\n                                    <#if regularTypeEnUs?? && regularTypeEnUs!=\'\'>\r\n                                        You have successfully booked a circular meeting, which will start in ${meetingTime!\'\'}, in ${roomName!\'\'}.\r\n                                    <#else>\r\n                                        <#if roomName?? && roomName==\'\' && recurring==\'10\'>\r\n                                            You have successfully booked a meeting, which will start in ${meetingTime!\'\'}.\r\n                                        <#else>\r\n                                            You have successfully booked a meeting, which will start in ${meetingTime!\'\'}, in ${roomName!\'\'}.\r\n                                        </#if>\r\n                                    </#if>\r\n                                <#elseif emailType==\'9\'>\r\n                                    <#if regularTypeEnUs?? && regularTypeEnUs!=\'\'>\r\n                                        Your circular meeting\r\n                                    <#else>\r\n                                        Your meeting of\r\n                                    </#if>\r\n                                    [${meetingName!\'\'}] has been modified.\r\n                                <#elseif emailType==\'10\'>\r\n                                    <#if regularTypeEnUs?? && regularTypeEnUs!=\'\'>\r\n                                        Your circular meeting\r\n                                    <#else>\r\n                                        Your meeting of\r\n                                    </#if>\r\n                                    <#if cancelType==\'0\'>\r\n                                        [${meetingName!\'\'}] has been canceled.\r\n                                    <#elseif cancelType==\'1\'>\r\n                                        [${meetingName!\'\'}] because of no sign in, ${roomName!\'\'} has been released.\r\n                                    <#elseif cancelType==\'2\'>\r\n                                        [${meetingName!\'\'}] because of no people accept, ${roomName!\'\'} has been released.\r\n                                    <#elseif cancelType==\'3\'>\r\n                                        [${meetingName!\'\'}] because of room maintain, ${roomName!\'\'} has been released.\r\n                                    <#elseif cancelType==\'4\'>\r\n                                        [${meetingName!\'\'}] because of room has been out of service, ${roomName!\'\'} has been released.\r\n                                    </#if>\r\n                                <#elseif emailType==\'11\'>\r\n                                    Your meeting application has been approved!\r\n                                <#elseif emailType==\'12\'>\r\n                                    <#if refuseRemark?? && refuseRemark != \'\'>\r\n                                        Your meeting application has been rejected on the grounds that: ${refuseRemark!\'\'}.\r\n                                    <#else>\r\n                                        Your meeting application has been rejected.\r\n                                    </#if>\r\n                                <#elseif emailType==\'13\'>\r\n                                    The appointment you have created has not been audited!\r\n                                <#elseif emailType==\'16\'>\r\n                                    You have a meeting application to confirm.\r\n                                <#elseif emailType==\'23\'>\r\n                                    It was detected that the meeting room used for meetings was in an unmanned state for a long time, please select\r\n                                    <a href=\"${receiptUrl!\'\'}\"style=\"line-height: 15px; border-radius:5px; padding:5px 10px; text-decoration:none; font-size:10pt; background:#0467BA; color:#ffffff;margin-left: 5px; \">End meeting</a>\r\n                                <#elseif emailType==\'18\'>\r\n                                    The meeting will be terminated as scheduled. Do you want to extend the meeting?\r\n                                <#elseif emailType==\'20\'>\r\n                                    Infrared sensing device detects that the meeting room used for the following meetings has been unmanned for a long time. The conference has been released. Thank you.\r\n                                <#elseif emailType==\'21\'>\r\n                                    Infrared sensing device detects that the meeting room used for the following meetings has been unmanned for a long time. The conference has been released automatically. Thank you.\r\n                                <#elseif emailType==\'22\'>\r\n                                    The following meeting you have booked has started, but the infrared sensor has detected that the [${roomName!\'\'}] meeting room has been unattended for a long time. The meeting room will be reserved for you for ${minuteTime!\'\'} minutes. If the meeting room is still unattended or unsigned, the resources of the meeting room will be released.\r\n                                <#elseif emailType==\'66\'>\r\n                                    There\'s a meeting you need to approve.\r\n                                <#elseif emailType==\'77\'>\r\n                                    <span class=\"k2\">\r\n                                        [${meetingName!\'\'}] will start in ${meetingTime!\'\'}, Please check the seat arrangement in advance.\r\n                                    </span>\r\n                                <#elseif emailType==\'78\'>\r\n                                    <span class=\"k2\">\r\n                                        [${meetingName!\'\'}] will start in ${meetingTime!\'\'}, Your seat has been removed.\r\n                                    </span>\r\n                                <#elseif emailType==\'88\'>\r\n                                    <span class=\"k2\">\r\n                                        [${meetingName!\'\'}] will start in ${meetingTime!\'\'}, Please arrange seat in advance.\r\n                                    </span>\r\n                                <#elseif emailType==\'99\'>\r\n                                    The meeting in [${roomName!\'\'}] is over. Please clean it in time.\r\n                                <#elseif emailType==\'101\'>\r\n                                    The meeting room you booked will be maintained. Please modify the meeting.\r\n                                <#elseif emailType==\'102\'>\r\n                                    The meeting room you booked has been completed ahead of schedule. Please pay attention to the meeting.\r\n                                <#elseif emailType==\'103\'>\r\n                                    The meeting room you booked has been service. Please pay attention to the meeting.\r\n                                <#elseif emailType==\'104\'>\r\n                                    The conference room you reserved has been suspended. Please choose another conference room to reserve a meeting :${refuseRemark!\'\'}\r\n                                <#elseif emailType==\'40\'>\r\n                                    <span class=\"k2\">\r\n                                        You have a meeting to be held in ${minuteTime!\'\'} minutes,please make preparations before the meeting.\r\n                                    </span>\r\n                                <#elseif emailType==\'41\'>\r\n                                    <span class=\"k2\">\r\n                                        Your meeting has started for ${minuteTime!\'\'} minutes,please make preparations before the meeting.\r\n                                    </span>\r\n                                <#else>\r\n                                    The meeting room you booked has been out of service. Please modify the meeting.\r\n                                </#if>\r\n                            </#if>\r\n                        </p>\r\n                    </td>\r\n                    <td align=\"right\" style=\"padding-right:5px;\">\r\n                        <#if receiptUrl?? && receiptUrl != \'\'>\r\n                            <#if urlType==\'0\'>\r\n                                <#if recurring==\'5\'>\r\n                                    <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                        <a href=\"${receiptUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center; width: 24px;\">\r\n                                            <div style=\"text-align:center\">\r\n                                                <span style=\"text-align:center; color:#ffffff; \">Confirm</span>\r\n                                            </div>\r\n                                        </a>\r\n                                    </div>\r\n                                <#elseif recurring==\'12\'>\r\n                                    <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                        <a href=\"${joinUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center; width: 24px;\">\r\n                                            <div style=\"text-align:center\">\r\n                                                <span style=\"text-align:center; color:#ffffff; \">Confirm</span>\r\n                                            </div>\r\n                                        </a>\r\n                                    </div>\r\n                                <#else>\r\n                                    <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                        <a href=\"${receiptUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center; width: 24px;\">\r\n                                            <div style=\"text-align:center\">\r\n                                                <span style=\"text-align:center; color:#ffffff; \">Confirm</span>\r\n                                            </div>\r\n                                        </a>\r\n                                    </div>\r\n                                </#if>\r\n                            <#elseif urlType==\'1\'>\r\n                                <#if personType==\'0\' && recurring==\'5\'>\r\n                                    <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                        <a href=\"${receiptUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center;  width: 24px;\">\r\n                                            <div style=\"text-align:center\">\r\n                                                <span style=\"text-align:center; color:#ffffff; \">Join</span>\r\n                                            </div>\r\n                                        </a>\r\n                                    </div>\r\n                                <#elseif personType==\'0\'>\r\n                                    <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                        <a href=\"${receiptUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center;  width: 24px;\">\r\n                                            <div style=\"text-align:center\">\r\n                                                <span style=\"text-align:center; color:#ffffff; \">Start</span>\r\n                                            </div>\r\n                                        </a>\r\n                                    </div>\r\n                                <#elseif personType==\'1\'>\r\n                                    <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                        <a href=\"${receiptUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center; width: 24px; \">\r\n                                            <div style=\"text-align:center\">\r\n                                                <span style=\"text-align:center; color:#ffffff; \">Join</span>\r\n                                            </div>\r\n                                        </a>\r\n                                    </div>\r\n                                </#if>\r\n                            <#elseif urlType==\'2\'>\r\n                                <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                    <a href=\"${receiptUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center; width: 24px; \">\r\n                                        <div style=\"text-align:center\">\r\n                                            <span style=\"text-align:center; color:#ffffff; \">Delay</span>\r\n                                        </div>\r\n                                    </a>\r\n                                </div>\r\n                            <#elseif urlType==\'3\' && emailType != \'23\'>\r\n                                <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                    <a href=\"${receiptUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center; width: 24px; \">\r\n                                        <div style=\"text-align:center\">\r\n                                            <span style=\"text-align:center; color:#ffffff; \">End</span>\r\n                                        </div>\r\n                                    </a>\r\n                                </div>\r\n                            </#if>\r\n                        <#elseif receiptUrl?? && receiptUrl = \'\'>\r\n                            <#if urlType==\'0\'>\r\n                                <#if recurring==\'12\'  && emailType != \'10\'>\r\n                                    <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                        <a href=\"${joinMeetingUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center; width: 24px;\">\r\n                                            <div style=\"text-align:center\">\r\n                                                <span style=\"text-align:center; color:#ffffff; \">StartMeeting</span>\r\n                                            </div>\r\n                                        </a>\r\n                                    </div>\r\n                                </#if>\r\n                            <#else>\r\n                                <#if recurring==\'12\'  && emailType != \'10\'>\r\n                                    <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                        <a href=\"${joinUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center; width: 24px;\">\r\n                                            <div style=\"text-align:center\">\r\n                                                <span style=\"text-align:center; color:#ffffff; \">JoinMeeting</span>\r\n                                            </div>\r\n                                        </a>\r\n                                    </div>\r\n                                </#if>\r\n                            </#if>\r\n                        </#if>\r\n                    </td>\r\n                </tr>\r\n            </table>\r\n        </td>\r\n    </tr>\r\n    <tr valign=\"top\">\r\n        <td bgcolor=\"#EFEFEF\">\r\n            <table cellpadding=\"10\" cellspacing=\"0\"style=\"border-bottom:solid 1px #EFEFEF; border-top:1px solid #EFEFEF; border-collapse:collapse;background: #FFFFFF;width: 580px;margin:0px 10px 10px 10px\">\r\n                <#if emailType?? && emailType !=\' 99\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">Title :</td>\r\n                        <td width=\"75%\"style=\"word-break:break-all;color: #333333;font-size: 16px;\">${meetingName!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n                <tr>\r\n                    <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">Room :</td>\r\n                    <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">\r\n                        <#if roomName?? && roomName!=\'\'>\r\n                            ${roomName!\'\'}\r\n                        <#else>\r\n                            None\r\n                        </#if>\r\n                    </td>\r\n                </tr>\r\n                <tr>\r\n                    <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">Start :</td>\r\n                    <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">${meetingStartTime!\'\'}</td>\r\n                </tr>\r\n                <tr>\r\n                    <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">End :</td>\r\n                    <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">${meetingEndTime!\'\'}</td>\r\n                </tr>\r\n                <#if netMeetingId?? && netMeetingId != \'\' && emailType !=\'99\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">\r\n                            <span style=\"display: block\">\r\n                                Meeting ID&nbsp;:\r\n                            </span>\r\n                        </td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">${netMeetingId!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n                <#if joinPassword?? && joinPassword != \'\' && emailType !=\'99\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">\r\n                            <span style=\"display: block\">\r\n                               Password&nbsp;:\r\n                            </span>\r\n                        </td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">${joinPassword!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n                <#if joinUrl?? && joinUrl != \'\' && emailType !=\'99\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">\r\n                            <span style=\"display: block\">\r\n                                Invitation URL&nbsp;:\r\n                            </span>\r\n                        </td>\r\n                        <#if recurring==\'11\'>\r\n                            <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">\r\n                            <a href=${joinUrl!\'\'} target=\"_blank\" style=\"display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow: hidden;word-break: break-word;\r\n                            \">${joinUrl!\'\'}</a>\r\n                            </td>\r\n                        <#else>\r\n                            <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">\r\n                            <a href=${joinUrl!\'\'} target=\"_blank\" style=\"display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow: hidden;word-break: break-word;\r\n                            \">${joinUrl!\'\'}</a>\r\n                            </td>\r\n                        </#if>\r\n                    </tr>\r\n                </#if>\r\n                <#if regularTypeEnUs?? && regularTypeEnUs != \'\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#b4b4b4;\" width=\"25%\">Periodic mode :</td>\r\n                        <td width=\"75%\">${regularTypeEnUs!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n                <#if regularTypeEnUs?? && regularTypeEnUs != \'\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#b4b4b4;\" width=\"25%\">Repeat range :</td>\r\n                        <td width=\"75%\">${regularRangeEnUs!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n                <tr>\r\n                    <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">Host :</td>\r\n                    <td width=\"75%\" style=\"color: #333333;font-size: 16px;\"> ${holderEn!\'\'}</td>\r\n                </tr>\r\n                <#if emailType?? && emailType !=\'99\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">Attendees :</td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px; word-break: break-all;\">${attendeeEn!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n                <#if emailType?? && emailType !=\'99\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">Remark :</td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;white-space:pre-wrap;word-wrap: break-word;\">${meetingRemark!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n\r\n                <#if emailType?? && emailType != \'99\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">Type :</td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">\r\n                            <#if recurring==\'1\'>\r\n                                General meeting\r\n                            <#elseif recurring==\'2\'>\r\n                                Recurrence meeting\r\n                            <#elseif recurring==\'3\'>\r\n                                Webex meeting\r\n                            <#elseif recurring==\'4\'>\r\n                                Recurrence meeting&nbsp;&nbsp;&nbsp;&nbsp;Webex meeting\r\n                            <#elseif recurring==\'5\'>\r\n                                Video meeting\r\n                            <#elseif recurring==\'8\'>\r\n                                Zoom meeting\r\n                            <#elseif recurring==\'6\'>\r\n                                Recurrence meeting&nbsp;&nbsp;&nbsp;&nbsp;Video meeting\r\n                            <#elseif recurring==\'10\'>\r\n                                Tencent meeting\r\n                            <#elseif recurring==\'11\'>\r\n                                Teams meeting\r\n                            <#elseif recurring==\'12\'>\r\n                                ${typeNameEn!\'\'}\r\n                            </#if>\r\n                        </td>\r\n                    </tr>\r\n                </#if>\r\n                <#if emailType?? && (emailType==\'3\' || emailType==\'10\')>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"26%\">Cancel reason :</td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">${cancelReason!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n                <#if operateLink?? && operateLink != \'\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"26%\">\r\n                            <span style=\"display: block\">\r\n                                Seat link :\r\n                            </span>\r\n                        </td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">\r\n                            <a href=\"${operateLink!\'\'}\" style=\"color: #0467BAB\"> ${operateLink!\'\'}</a>\r\n                        </td>\r\n                    </tr>\r\n                </#if>\r\n                <#if faceUrl?? && faceUrl != \'\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"26%\">\r\n                            <span style=\"display: block\">\r\n                                Upload Photo :\r\n                            </span>\r\n                        </td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">\r\n                            <a href=\"${faceUrl!\'\'}\" style=\"color: #0D75CD\">Click to upload face recognition photos</a>\r\n                        </td>\r\n                    </tr>\r\n                </#if>\r\n                <#if qrCode?? && qrCode != \'\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"26%\">QR Code :</td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">Please align the QR code with the scanner</td>\r\n                    </tr>\r\n                    <tr>\r\n                        <td></td>\r\n                        <td>\r\n                            <img src=\"data:image/png;base64,${qrCode!\'\'}\" width=\"200\" height=\"200\" />\r\n                        </td>\r\n                    </tr>\r\n                </#if>\r\n            </table>\r\n        </td>\r\n    </tr>\r\n</table>\r\n'  WHERE `name` = 'smtMeeting_en' AND `category` = 'smt';