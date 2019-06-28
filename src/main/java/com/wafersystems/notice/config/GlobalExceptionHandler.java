package com.wafersystems.notice.config;

import com.wafersystems.virsical.common.core.constant.CommonConstants;
import com.wafersystems.virsical.common.core.exception.BusinessException;
import com.wafersystems.virsical.common.core.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理
 *
 * @author tandk
 * @date 2019/1/18
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
  /**
   * 全局异常.
   *
   * @param e the e
   * @return R
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public R handleGlobalException(Exception e) {
    log.error("【系统异常】{}", e.getMessage(), e);
    return R.builder()
      .msg(e.getLocalizedMessage())
      .code(CommonConstants.FAIL)
      .build();
  }

  /**
   * BusinessException
   *
   * @param e the e
   * @return R
   */
  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public R handleBusinessException(BusinessException e) {
    return R.builder()
      .msg(e.getLocalizedMessage())
      .code(CommonConstants.FAIL)
      .build();
  }
}

