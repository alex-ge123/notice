package com.wafersystems.notice.service.impl;

import com.wafersystems.notice.entity.AlertRecord;
import com.wafersystems.notice.mapper.AlertRecordMapper;
import com.wafersystems.notice.service.IAlertRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 提醒记录表 服务实现类
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
@Service
public class AlertRecordServiceImpl extends ServiceImpl<AlertRecordMapper, AlertRecord> implements IAlertRecordService {

}
