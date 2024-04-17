package com.example.BEFoodrecommendationapplication.exception;

public class DuplicateDataException extends RuntimeException {

    public DuplicateDataException(String message) {
        super(message);
    }
}