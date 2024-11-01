package com.example.absensireact.exception;

import com.example.absensireact.service.TelegramNotificationService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


//@Aspect
//@Component
public class ExceptionHandlerAspect {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerAspect.class);

    private final TelegramNotificationService telegramNotificationService;

    public ExceptionHandlerAspect(TelegramNotificationService telegramNotificationService) {
        this.telegramNotificationService = telegramNotificationService;
    }

    @AfterThrowing(pointcut = "execution(* com.example..*(..))", throwing = "ex")
    public void logAfterThrowingAllMethods(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().toShortString();
        String errorMessage = String.format(
                "Exception in %s:\nMessage: %s",
                methodName,
                ex.getMessage()
        );

        // Log the error
        logger.error("Exception in method {}: {}", methodName, ex.getMessage());

        // Send error message to Telegram
        telegramNotificationService.sendErrorNotification(errorMessage);
    }
}
