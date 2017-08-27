package com.LZH.Aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Created by asus on 2017/4/3.
 */
@Aspect
@Component
public class LoggerAspect {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoggerAspect.class);
    @Before("execution(* com.LZH.controller.IndexController.*Controller(..))")
    public void beforeMethod(JoinPoint jp){
        StringBuilder sb = new StringBuilder();
        for(Object obj : jp.getArgs()){
            sb.append("||"+obj.toString());
        }
        logger.info("Before controller!"+sb);
    }
    @After("execution(* com.LZH.controller.IndexController.*Controller(..))")
    public void afterMethod(){
        logger.info("After controller!");
    }
}
