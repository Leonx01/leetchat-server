package com.nileonx.leetchat_springboot.exception;

import com.nileonx.leetchat_springboot.common.BaseResponse;
import com.nileonx.leetchat_springboot.common.ErrorCode;
import com.nileonx.leetchat_springboot.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * Slf4j是用来记录日志信息的，lombok中自带的。
 */
//当系统 raise new BusinessException时会调用
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private ResultUtil ResultUtils;

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("BusinessException"+e.getMessage(),e);
        //return new BaseResponse(e.getCode(),e.getMessage(),e.getDescription());
        return ResultUtil.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        //集中处理
        log.error("RuntimeException",e);
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR,e.getMessage());
    }
}