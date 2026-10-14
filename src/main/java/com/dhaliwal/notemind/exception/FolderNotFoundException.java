package com.dhaliwal.notemind.exception;

public class FolderNotFoundException extends RuntimeException {

    public FolderNotFoundException(String message) {
        super(message);
    }
}