package com.dhaliwal.notemind.exception;

public class FolderAlreadyExistsException extends RuntimeException {

    public FolderAlreadyExistsException(String message) {
        super(message);
    }
}