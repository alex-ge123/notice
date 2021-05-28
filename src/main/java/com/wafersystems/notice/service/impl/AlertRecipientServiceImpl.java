package com.wafersystems.notice.service.impl;

import com.wafersystems.notice.entity.AlertRecipient;
import com.wafersystems.notice.mapper.AlertRecipientMapper;
import com.wafersystems.notice.service.IAlertRecipientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 提醒接收人 服务实现类
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
@Service
public class AlertRecipientServiceImpl extends ServiceImpl<AlertRecipientMapper, AlertRecipient> implements IAlertRecipientService {

}
