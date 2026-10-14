package com.dhaliwal.notemind.exception;

public class SearchTermIsEmptyException extends RuntimeException {

    public SearchTermIsEmptyException(String message) {
        super(message);
    }
}