-- ----------------------------
-- Table structure for mail_microsoft_record
-- ----------------------------
CREATE TABLE `mail_microsoft_record`  (
  `uuid` varchar(255) NOT NULL COMMENT 'uuid',
  `eventid` text NOT NULL COMMENT '事件id',
  `createtime` timestamp(0) DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`uuid`) USING BTREE
) COMMENT = 'microsoft事件id记录表';