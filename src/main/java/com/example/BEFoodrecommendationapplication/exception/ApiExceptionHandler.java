package com.example.BEFoodrecommendationapplication.exception;

import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.util.StatusCode;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    @ResponseBody
    public ResponseEntity<Object> handleDataAccessException(DataAccessException ex) {
        Response errorResponse = Response.builder()
                .statusCode(StatusCode.UNAUTHORIZED.getCode())
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
    }
    @ExceptionHandler(value = {RecordNotFoundException.class})
    @ResponseBody
    public ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex) {
        Response errorResponse = Response.builder()
                .statusCode(StatusCode.UNAUTHORIZED.getCode())
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);

    }
    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseBody
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        Response errorResponse = Response.builder()
                .statusCode(StatusCode.UNAUTHORIZED.getCode())
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
    }
    @ExceptionHandler(value = {DuplicateDataException.class})
    @ResponseBody
    public ResponseEntity<Object> handleDuplicateDataException(DuplicateDataException ex) {
        Response errorResponse = Response.builder()
                .statusCode(StatusCode.UNAUTHORIZED.getCode())
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
    }
    @ExceptionHandler(value = {EntityNotFoundException.class})
    @ResponseBody
    public ResponseEntity<Object> handleEntityNotFoundException(Exception ex) {
        Response errorResponse = Response.builder()
                .statusCode(StatusCode.UNAUTHORIZED.getCode())
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
    }
}
