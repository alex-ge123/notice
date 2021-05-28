package com.wafersystems.notice.service.impl;

import com.wafersystems.notice.entity.AlertConf;
import com.wafersystems.notice.mapper.AlertConfMapper;
import com.wafersystems.notice.service.IAlertConfService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 提醒配置表 服务实现类
 * </p>
 *
 * @author shennan
 * @since 2021-05-28
 */
@Service
public class AlertConfServiceImpl extends ServiceImpl<AlertConfMapper, AlertConf> implements IAlertConfService {

}
