package com.example.absensireact.exception;

import com.example.absensireact.service.TelegramNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final TelegramNotificationService telegramBotService;

    @Autowired
    public GlobalExceptionHandler(TelegramNotificationService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        String errorMessage = String.format(
                "🚨 *Peringatan Error * 🚨\n\n" +
                        "❌ Error: %s",
                ex.getMessage()
        );

        telegramBotService.sendErrorNotification(errorMessage);

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        String errorMessage = String.format(
                "❗ General Error: %s",
                ex.getMessage()
        );
        telegramBotService.sendErrorNotification(errorMessage);
        return new ResponseEntity<>("Terjadi kesalahan: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
