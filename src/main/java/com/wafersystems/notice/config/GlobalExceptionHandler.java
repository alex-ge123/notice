package com.wafersystems.notice.config;

import com.pig4cloud.pigx.common.core.constant.CommonConstants;
import com.pig4cloud.pigx.common.core.exception.BusinessException;
import com.pig4cloud.pigx.common.core.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 *
 * @author tandk
 * @date 2019/1/18
 */
@ControllerAdvice
@Slf4j
@ResponseBody
public class GlobalExceptionHandler {
  @ExceptionHandler(value = Exception.class)
  public Object exceptionHandler(HttpServletRequest request, Exception exception) {
    if (exception instanceof BusinessException) {
      log.warn("【业务异常】", exception);
    } else {
      log.error("【系统异常】", exception);
    }
    return R.builder().code(CommonConstants.FAIL).msg(exception.getMessage()).build();
  }
}
