UPDATE `mail_template` SET `content` = '<table align=\"center\" border=\"0\" style=\"margin-top: -16px;margin-bottom: -16px; margin-left: auto;margin-right: auto; font-size:16px; color:#333333;background: #f9f9f9; font-family: Helvetica, Tahoma, Arial, \'PingFang SC\', STXihei, \'华文细黑\', \'Microsoft YaHei\', \'微软雅黑\', SimSun, \'宋体\', sans-serif; border-collapse: collapse; border-spacing: 0;\" >\r\n    <tr>\r\n        <td bgcolor=\"#EFEFEF\">\r\n           <table cellpadding=\"0\" cellspacing=\"0\" style=\"margin:10px; pading: 5px; width: 580px; background-color: #FFFFFF\">\r\n                <tr>\r\n                     <td align=\"left\" colspan=\"2\"style=\"font-size:20px; line-height:30px;\">\r\n                        <div style=\"margin-left:10px;display: block;font-family: PingFang SC;font-weight: 500;color: rgba(51,51,51,1);\">\r\n                            <#if emailType??>\r\n                                <#if emailType==\'1\'>\r\n                                    <div>参会通知</div>\r\n                                <#elseif emailType==\'2\'>\r\n                                    <div>会议修改通知</div>\r\n                                <#elseif emailType==\'3\' || emailType==\'10\'>\r\n                                    <div>会议取消通知</div>\r\n                                <#elseif emailType==\'4\'>\r\n                                    <div>会议提醒通知</div>\r\n                                <#elseif emailType==\'6\' || emailType==\'7\'>\r\n                                    <div>会议回执通知</div>\r\n                                <#elseif emailType==\'8\'>\r\n                                    <div>会议创建通知</div>\r\n                                <#elseif emailType==\'9\'>\r\n                                    <div>会议修改通知</div>\r\n                                <#elseif emailType==\'15\' || emailType==\'22\' || emailType==\'23\'>\r\n                                    <div>红外感知设备提醒</div>\r\n                                <#elseif emailType==\'18\'>\r\n                                    <div>会议提醒</div>\r\n                                <#elseif emailType==\'5\' || emailType==\'11\' || emailType==\'12\' || emailType==\'13\' || emailType==\'40\' || emailType==\'41\'>\r\n                                    <div>会议通知</div>\r\n                                <#elseif emailType==\'66\'>\r\n                                    <div>会议审核通知</div>\r\n                                <#elseif emailType==\'77\' || emailType==\'78\' || emailType==\'88\' || emailType==\'99\'>\r\n                                    <div>会议服务通知</div>\r\n                                <#elseif emailType==\'101\' || emailType==\'102\'>\r\n                                    <div>会议室维护通知</div>\r\n                                <#elseif emailType==\'103\' || emailType==\'104\'>\r\n                                    <div>会议室状态通知</div>\r\n                                <#elseif emailType==\'20\' || emailType==\'21\'>\r\n                                    <div>会议室自动释放通知</div>\r\n                                </#if>\r\n                            </#if>\r\n                        </span>\r\n                     </td>\r\n                </tr>\r\n                <tr>\r\n                    <td colspan=\"2\" align=\"left\"style=\"font-size:16px; line-height:30px;color: rgba(51,51,51,1);\">\r\n                        <#if emailType?? && (emailType!=\'15\' && emailType!=\'17\' && emailType!=\'20\' && emailType!=\'21\')>\r\n                            <span style=\"margin-left:10px;\">\r\n                                ${callName!\'\'},\r\n                            </span>\r\n                        </#if>\r\n                    </td>\r\n                </tr>\r\n                <tr>\r\n                    <td align=\"left\" style=\"font-size: 16px; line-height: 30px;color: rgba(51,51,51,1);\">\r\n                        <p class=\"k2\"style=\"margin-left: 10px; margin-right: 10px; margin-top: 0px; margin-bottom: 10px; word-break: break-all;\" width=\"80%\">\r\n                            <#if emailType??>\r\n                                <#if emailType==\'1\'>\r\n                                    <#if roomName?? && roomName==\'\' && recurring==\'10\'>\r\n                                        ${creatorUser!\'\'}创建了一个会议，将于${meetingTime!\'\'}召开。\r\n                                    <#else>\r\n                                        ${creatorUser!\'\'}创建了一个会议，将于${meetingTime!\'\'}在${roomName!\'\'}召开。\r\n                                    </#if>\r\n                                <#elseif emailType==\'2\'>\r\n                                    您的[${meetingName!\'\'}]已被修改，请及时关注会议信息。\r\n                                <#elseif emailType==\'3\'>\r\n                                    <#if cancelType==\'0\'>\r\n                                        您的[${meetingName!\'\'}]已被取消。\r\n                                    <#elseif cancelType==\'1\'>\r\n                                        您的[${meetingName!\'\'}]因为没有签到，${roomName!\'\'}已被释放。\r\n                                    <#elseif cancelType==\'2\'>\r\n                                        您的[${meetingName!\'\'}]因为参会人全部拒绝，${roomName!\'\'}已被释放。\r\n                                    <#elseif cancelType==\'3\'>\r\n                                        您的[${meetingName!\'\'}]因为会议室维护，${roomName!\'\'}已被释放。\r\n                                    <#elseif cancelType==\'4\'>\r\n                                        您的[${meetingName!\'\'}]因为会议室停用，${roomName!\'\'}已被释放。\r\n                                    </#if>\r\n                                <#elseif emailType==\'4\'>\r\n                                    您的[${meetingName!\'\'}]将于${meetingTime!\'\'},召开，请做好会前准备。\r\n                                <#elseif emailType==\'5\'>\r\n                                    由于会议参会人名单调整，您不需要参加此次会议，如有异议，请联系会议组织者。\r\n                                <#elseif emailType==\'6\'>\r\n                                    参会者:${receiver!\'\'}确认参加该会议。\r\n                                <#elseif emailType==\'7\'>\r\n                                    参会者:${receiver!\'\'}拒绝参加该会议，原因:${refuseRemark!\'\'}\r\n                                <#elseif emailType==\'8\'>\r\n                                    <#if regularTypeZhCn?? && regularTypeZhCn!=\'\'>\r\n                                        您成功创建了一个循环会议，将于${meetingTime!\'\'},在${roomName!\'\'}召开。\r\n                                    <#else>\r\n                                        <#if roomName?? && roomName==\'\' && recurring==\'10\'>\r\n                                            您成功创建了一个会议，将于${meetingTime!\'\'}召开。\r\n                                        <#else>\r\n                                            您成功创建了一个会议，将于${meetingTime!\'\'},在${roomName!\'\'}召开。\r\n                                        </#if>\r\n                                    </#if>\r\n                                <#elseif emailType==\'9\'>\r\n                                    <#if regularTypeZhCn?? && regularTypeZhCn!=\'\'>\r\n                                        您的循环会议\r\n                                    <#else>\r\n                                        您的\r\n                                    </#if>\r\n                                    [${meetingName!\'\'}]已被修改，请及时关注会议信息。\r\n                                <#elseif emailType==\'10\'>\r\n                                    <#if regularTypeZhCn?? && regularTypeZhCn!=\'\'>\r\n                                        您的循环会议\r\n                                    <#else>\r\n                                        您的\r\n                                    </#if>\r\n                                    <#if cancelType==\'0\'>\r\n                                        [${meetingName!\'\'}]已被取消。\r\n                                    <#elseif cancelType==\'1\'>\r\n                                        [${meetingName!\'\'}]因为没有签到，${roomName!\'\'}已被释放。\r\n                                    <#elseif cancelType==\'2\'>\r\n                                        [${meetingName!\'\'}]因为参会人全部拒绝，${roomName!\'\'}已被释放。\r\n                                    <#elseif cancelType==\'3\'>\r\n                                        [${meetingName!\'\'}]因为会议室维护，${roomName!\'\'}已被释放。\r\n                                    <#elseif cancelType==\'4\'>\r\n                                        [${meetingName!\'\'}]因为会议室停用，${roomName!\'\'}已被释放。\r\n                                    </#if>\r\n                                <#elseif emailType==\'11\'>\r\n                                    您创建的会议已经审批通过！\r\n                                <#elseif emailType==\'12\'>\r\n                                    您创建的会议已经审批拒绝!\r\n                                    <#if refuseRemark?? && refuseRemark != \'\'>\r\n                                        原因:${refuseRemark!\'\'}。\r\n                                    </#if>\r\n                                <#elseif emailType==\'13\'>\r\n                                    您创建的会议还未进行审批!\r\n                                <#elseif emailType==\'15\'>\r\n                                    检测到会议使用的会议室长时间处于无人状态，请选择\r\n                                    <a href=\"${operateLink!\'\'}\" style=\"color: #0467BAB\">结束会议</a>\r\n                                    或\r\n                                    <a href=\"${cancelReason!\'\'}\">继续开会</a>\r\n                                    ！\r\n                                <#elseif emailType==\'16\'>\r\n                                    您有一个会议申请需要确认。\r\n                                <#elseif emailType==\'23\'>\r\n                                    检测到会议使用的会议室长时间处于无人状态，请选择\r\n                                    <a href=\"${operateLink!\'\'}\"style=\"line-height: 15px; border-radius:5px; padding:5px 10px; text-decoration:none; font-size:10pt; background:#0467BA; color:#ffffff;margin-left: 5px; \">结束会议</a>\r\n                                <#elseif emailType==\'18\'>\r\n                                    您有一个会议即将结束，是否需要延长会议？\r\n                                <#elseif emailType==\'20\'>\r\n                                    红外感知设备检测到以下会议所使用的会议室长时间处于无人状态，会议已经被释放，谢谢！\r\n                                <#elseif emailType==\'21\'>\r\n                                    红外感知设备检测到以下会议所使用的会议室长时间处于无人状态，会议已经被自动释放，谢谢！\r\n                                <#elseif emailType==\'22\'>\r\n                                    您预订的如下会议已经开始，但红外感知设备检测到会议室[${roomName!\'\'}]长时间处于无人状态，该会议室将会继续为您保留${minuteTime!\'\'}分钟，若仍旧无人活动或无人签到，会议室资源将会被释放。\r\n                                <#elseif emailType==\'66\'>\r\n                                    有一个会议，需要您来审核。\r\n                                <#elseif emailType==\'77\'>\r\n                                    <span class=\"k2\">\r\n                                        [${meetingName!\'\'}]将于${meetingTime!\'\'}召开,请提前查看座位安排。\r\n                                    </span>\r\n                                <#elseif emailType==\'78\'>\r\n                                    <span class=\"k2\">\r\n                                        [${meetingName!\'\'}]将于${meetingTime!\'\'}召开,您的座位已被移除。\r\n                                    </span>\r\n                                <#elseif emailType==\'88\'>\r\n                                    <span class=\"k2\">\r\n                                        [${meetingName!\'\'}]将于${meetingTime!\'\'}召开,请您提前安排座位。\r\n                                    </span>\r\n                                <#elseif emailType==\'99\'>\r\n                                    [${roomName!\'\'}]的会议已经结束，请您及时进行清洁。\r\n                                <#elseif emailType==\'101\'>\r\n                                    您预订的会议室将进行维护，请修改该会议。\r\n                                <#elseif emailType==\'102\'>\r\n                                    您预订的会议室已提前结束维护，请关注该会议。\r\n                                <#elseif emailType==\'103\'>\r\n                                    您预订的会议室已启用，请关注该会议。\r\n                                <#elseif emailType==\'104\'>\r\n                                    您预订的会议室已停用，请选择其他会议室预定会议:${refuseRemark!\'\'}\r\n                                <#elseif emailType==\'40\'>\r\n                                    <span class=\"k2\">\r\n                                        您的${meetingName!\'\'}还有${minuteTime!\'\'}分钟召开,请您尽快参加会议。\r\n                                    </span>\r\n                                <#elseif emailType==\'41\'>\r\n                                    <span class=\"k2\">\r\n                                        您的${meetingName!\'\'}已经开始了${minuteTime!\'\'}分钟,请您尽快参加会议。\r\n                                    </span>\r\n                                <#else>\r\n                                    您预订的会议室已停用，推荐使用以下会议室:${refuseRemark!\'\'}\r\n                                </#if>\r\n                            </#if>\r\n                        </p>\r\n                    </td>\r\n                    <td align=\"right\" style=\"padding-right:5px;\">\r\n                        <#if receiptUrl?? && receiptUrl != \'\'>\r\n                            <#if urlType==\'0\'>\r\n                                <#if recurring==\'5\'>\r\n                                    <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                        <a href=\"${receiptUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center; width: 24px;\">\r\n                                            <div style=\"text-align:center\">\r\n                                                <span style=\"text-align:center; color:#ffffff; \">确认并加入</span>\r\n                                            </div>\r\n                                        </a>\r\n                                    </div>\r\n                                <#else>\r\n                                    <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                        <a href=\"${receiptUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center; width: 24px;\">\r\n                                            <div style=\"text-align:center\">\r\n                                                <span style=\"text-align:center; color:#ffffff; \">点击确认</span>\r\n                                            </div>\r\n                                        </a>\r\n                                    </div>\r\n                                </#if>\r\n                            <#elseif urlType==\'1\'>\r\n                                <#if personType==\'0\' && recurring==\'5\'>\r\n                                    <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                        <a href=\"${receiptUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center;  width: 24px;\">\r\n                                            <div style=\"text-align:center\">\r\n                                                <span style=\"text-align:center; color:#ffffff; \">加入会议</span>\r\n                                            </div>\r\n                                        </a>\r\n                                    </div>\r\n                                <#elseif personType==\'0\'>\r\n                                    <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                        <a href=\"${receiptUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center;  width: 24px;\">\r\n                                            <div style=\"text-align:center\">\r\n                                                <span style=\"text-align:center; color:#ffffff; \">开启会议</span>\r\n                                            </div>\r\n                                        </a>\r\n                                    </div>\r\n                                <#elseif personType==\'1\'>\r\n                                    <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                        <a href=\"${receiptUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center; width: 24px; \">\r\n                                            <div style=\"text-align:center\">\r\n                                                <span style=\"text-align:center; color:#ffffff; \">加入会议</span>\r\n                                            </div>\r\n                                        </a>\r\n                                    </div>\r\n                                </#if>\r\n                            <#elseif urlType==\'2\'>\r\n                                <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                    <a href=\"${receiptUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center; width: 24px; \">\r\n                                        <div style=\"text-align:center\">\r\n                                            <span style=\"text-align:center; color:#ffffff; \">延长会议</span>\r\n                                        </div>\r\n                                    </a>\r\n                                </div>\r\n                            <#elseif urlType==\'3\'>\r\n                                <div style=\"width: 75px; border-radius:5px; background:#0467BA; \">\r\n                                    <a href=\"${receiptUrl!\'\'}\" style=\"line-height: 30px; text-decoration:none; border-radius:5px; font-size:12px; align:center; width: 24px; \">\r\n                                        <div style=\"text-align:center\">\r\n                                            <span style=\"text-align:center; color:#ffffff; \">结束会议</span>\r\n                                        </div>\r\n                                    </a>\r\n                                </div>\r\n                            </#if>\r\n                        </#if>\r\n                    </td>\r\n                </tr>\r\n            </table>\r\n        </td>\r\n    </tr>\r\n    <tr valign=\"top\">\r\n        <td bgcolor=\"#EFEFEF\">\r\n            <table cellpadding=\"10\" cellspacing=\"0\"style=\"border-bottom:solid 1px #EFEFEF; border-top:1px solid #EFEFEF; border-collapse:collapse;background: #FFFFFF;width: 580px;margin:0px 10px 10px 10px\">\r\n                <#if emailType?? && emailType !=\' 99\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">会议主题 :</td>\r\n                        <td width=\"75%\"style=\"word-break:break-all;color: #333333;font-size: 16px;\">${meetingName!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n                <tr>\r\n                    <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">会议室 :</td>\r\n                    <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">\r\n                        <#if roomName?? && roomName!=\'\'>\r\n                            ${roomName!\'\'}\r\n                        <#else>\r\n                            无\r\n                        </#if>\r\n                    </td>\r\n                </tr>\r\n                <tr>\r\n                    <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">开始时间 :</td>\r\n                    <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">${meetingStartTime!\'\'}</td>\r\n                </tr>\r\n                <tr>\r\n                    <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">结束时间 :</td>\r\n                    <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">${meetingEndTime!\'\'}</td>\r\n                </tr>\r\n                <#if netMeetingId?? && netMeetingId != \'\' && emailType !=\'99\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">\r\n                            <span style=\"display: block\">\r\n                                会议ID&nbsp;:\r\n                            </span>\r\n                        </td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">${netMeetingId!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n                <#if joinPassword?? && joinPassword != \'\' && emailType !=\'99\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">\r\n                            <span style=\"display: block\">\r\n                               参会密码&nbsp;:\r\n                            </span>\r\n                        </td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">${joinPassword!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n                <#if joinUrl?? && joinUrl != \'\' && emailType !=\'99\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">\r\n                            <span style=\"display: block\">\r\n                                邀请链接&nbsp;:\r\n                            </span>\r\n                        </td>\r\n                        <#if recurring==\'11\'>\r\n                            <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">\r\n                            <a href=${joinUrl!\'\'} target=\"_blank\" style=\"display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow: hidden;word-break: break-word;\r\n                            \">${joinUrl!\'\'}</a></td>\r\n                        <#else>\r\n                            <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">${joinUrl!\'\'}</td>\r\n                        </#if>\r\n                    </tr>\r\n                </#if>\r\n                <#if regularTypeZhCn?? && regularTypeZhCn != \'\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#b4b4b4;\" width=\"25%\">定期模式 :</td>\r\n                        <td width=\"75%\">${regularTypeZhCn!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n                <#if regularTypeZhCn?? && regularTypeZhCn != \'\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#b4b4b4;\" width=\"25%\">重复范围 :</td>\r\n                        <td width=\"75%\">${regularRangeZhCn!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n                <tr>\r\n                    <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">主持人 :</td>\r\n                    <td width=\"75%\" style=\"color: #333333;font-size: 16px;\"> ${holder!\'\'}</td>\r\n                </tr>\r\n                <#if emailType?? && emailType !=\'99\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">参会人 :</td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px; word-break: break-all;\">${attendee!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n                <#if emailType?? && emailType !=\'99\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">会议备注 :</td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;white-space:pre-wrap;word-wrap: break-word;\">${meetingRemark!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n\r\n                <#if emailType?? && emailType != \'99\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"25%\">会议类型 :</td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">\r\n                            <#if recurring==\'1\'>\r\n                                普通会议\r\n                            <#elseif recurring==\'2\'>\r\n                                循环会议\r\n                            <#elseif recurring==\'3\'>\r\n                                Webex会议\r\n                            <#elseif recurring==\'4\'>\r\n                                循环会议&nbsp;&nbsp;&nbsp;&nbsp;Webex会议\r\n                            <#elseif recurring==\'5\'>\r\n                                视频会议\r\n                            <#elseif recurring==\'8\'>\r\n                                Zoom会议\r\n                            <#elseif recurring==\'6\'>\r\n                                循环会议&nbsp;&nbsp;&nbsp;&nbsp;视频会议\r\n                            <#elseif recurring==\'10\'>\r\n                                騰訊会议\r\n                            <#elseif recurring==\'11\'>\r\n                                Teams会议\r\n                            </#if>\r\n                        </td>\r\n                    </tr>\r\n                </#if>\r\n                <#if emailType?? && (emailType==\'3\' || emailType==\'10\')>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"26%\">取消理由 :</td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">${cancelReason!\'\'}</td>\r\n                    </tr>\r\n                </#if>\r\n                <#if operateLink?? && operateLink != \'\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"26%\">\r\n                            <span style=\"display: block\">\r\n                                座位链接 :\r\n                            </span>\r\n                        </td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">\r\n                            <a href=\"${operateLink!\'\'}\" style=\"color: #0467BAB\"> ${operateLink!\'\'}</a>\r\n                        </td>\r\n                    </tr>\r\n                </#if>\r\n                <#if faceUrl?? && faceUrl != \'\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"26%\">\r\n                            <span style=\"display: block\">\r\n                                上传照片 :\r\n                            </span>\r\n                        </td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">\r\n                            <a href=\"${faceUrl!\'\'}\" style=\"color: #0D75CD\">点击上传人脸识别照片</a>\r\n                        </td>\r\n                    </tr>\r\n                </#if>\r\n                <#if qrCode?? && qrCode != \'\'>\r\n                    <tr>\r\n                        <td align=\"right\" style=\"color:#999999;font-size: 16px !important;\" width=\"26%\">开门二维码 :</td>\r\n                        <td width=\"75%\" style=\"color: #333333;font-size: 16px;\">请通过二维码扫描签到开门</td>\r\n                    </tr>\r\n                    <tr>\r\n                        <td></td>\r\n                        <td>\r\n                            <img src=\"data:image/png;base64,${qrCode!\'\'}\" width=\"200\" height=\"200\" />\r\n                        </td>\r\n                    </tr>\r\n                </#if>\r\n            </table>\r\n        </td>\r\n    </tr>\r\n</table>' WHERE `name` = 'smtMeeting_zh_CN' AND `category` = 'smt';