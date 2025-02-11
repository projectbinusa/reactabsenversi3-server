package com.example.absensireact.exception;

import com.example.absensireact.service.TelegramNotificationService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;


//@Aspect
//@Component
public class ExceptionHandlerAspect {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerAspect.class);

    private final TelegramNotificationService telegramNotificationService;

    public ExceptionHandlerAspect(TelegramNotificationService telegramNotificationService) {
        this.telegramNotificationService = telegramNotificationService;
    }

    @AfterThrowing(pointcut = "execution(* com.example..*(..))", throwing = "ex")
    public void logAfterThrowingAllMethods(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().toShortString();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] methodArgs = joinPoint.getArgs();

        // Log error di server
        logger.error("Exception in {}: {}", methodName, ex.getMessage(), ex);

        // Kirim error ke Telegram dengan parameter yang benar
        telegramNotificationService.sendErrorNotificationForException(className, methodName, methodArgs, ex);
    }
}
