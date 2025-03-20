package com.briup.cms.exception;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/20/15:09
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */
import com.briup.cms.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler2 {

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e) {
        Result result = null;
        if (e instanceof ServiceException) {
            log.error(e.getMessage());
            result = Result.failure(((ServiceException) e).getResultCode());
        } else if (e instanceof DuplicateKeyException) {
            result = Result.failure(500, "该数据已存在,请检查后重新输入!");
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            result = Result.failure(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "Content type 'application/x-www-form-urlencoded' not supported");
        } else {
            log.error(e.getMessage());
            result = Result.failure(500, "服务器意外错误：" + e.getMessage());
        }
        return result;
    }
}