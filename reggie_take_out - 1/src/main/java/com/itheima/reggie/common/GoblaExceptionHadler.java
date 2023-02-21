package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;


@ControllerAdvice(annotations={RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GoblaExceptionHadler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> handleException(SQLIntegrityConstraintViolationException e) {
        if(e.getMessage().contains("Duplicate entry")) {
            String[] s = e.getMessage().split(" ");
            String msgg=s[2]+"已存在";
            return R.error(msgg);

        }
        return R.error("操作失败");
    }
    @ExceptionHandler(CustomEception.class)
    public R<String> handleException(CustomEception e) {

        return R.error(e.getMessage());
    }
}

