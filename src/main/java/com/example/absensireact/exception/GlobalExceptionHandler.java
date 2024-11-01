package com.example.absensireact.exception;

import com.example.absensireact.service.TelegramNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final TelegramNotificationService telegramBotService;

    @Autowired
    public GlobalExceptionHandler(TelegramNotificationService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e, HttpServletRequest request) {
        String errorMessage = String.format(
                "Error occurred at path: %s\nMessage: %s",
                request.getRequestURI(),
                e.getMessage()
        );

        // Send error message to Telegram
        telegramBotService.sendErrorNotification(errorMessage);

        // Log the exception (optional)
        e.printStackTrace();

        return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
