package com.example.absensireact.exception;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RelationExistsException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleRelationExistsException(RelationExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(ChangeSetPersister.NotFoundException notFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(notFoundException.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequest(BadRequestException badRequestException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(badRequestException.getMessage()));
    }

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<ErrorResponse> internalError(InternalErrorException internalErrorException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(internalErrorException.getMessage()));
    }
}
