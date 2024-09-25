package com.cinkciarz.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.cinkciarz.service.AccountService.*(..))")
    public void logMethodCall(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        log.info("Called createNewAccount with arguments: {}", args);
    }
}