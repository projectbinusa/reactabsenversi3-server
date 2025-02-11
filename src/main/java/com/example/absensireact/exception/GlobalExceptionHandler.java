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

        telegramBotService.sendErrorNotificationLogin(errorMessage);

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        StackTraceElement stackTraceElement = ex.getStackTrace()[0]; // Ambil informasi error pertama
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();
        String fileName = stackTraceElement.getFileName();
        int lineNumber = stackTraceElement.getLineNumber();

        // Format error message untuk Telegram
        String errorMessage = String.format(
                "🚨 *GENERAL API ERROR* 🚨\n" +
                        "📌 *Class*: `%s`\n" +
                        "🔗 *Method*: `%s`\n" +
                        "📂 *File*: `%s`\n" +
                        "📍 *Line*: `%d`\n" +
                        "❌ *Exception*: `%s`\n" +
                        "📄 *Message*: `%s`",
                className, methodName, fileName, lineNumber, ex.getClass().getSimpleName(), ex.getMessage()
        );

        // Kirim notifikasi ke Telegram
        telegramBotService.sendErrorNotificationForException(className, methodName, new Object[]{}, ex);

        return new ResponseEntity<>("Terjadi kesalahan: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
