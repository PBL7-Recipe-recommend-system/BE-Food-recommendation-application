package com.example.BEFoodrecommendationapplication.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = {RecordNotFoundException.class})
    @ResponseBody
    public ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }
    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseBody
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }
    @ExceptionHandler(value = {DuplicateDataException.class})
    @ResponseBody
    public ResponseEntity<Object> handleDuplicateDataException(DuplicateDataException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }
    @ExceptionHandler(value = {EntityNotFoundException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleEntityNotFoundException(Exception ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }
}
