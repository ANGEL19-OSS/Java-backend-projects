package com.warehouse.pim.exception;

public class DuplicateProductNameException extends RuntimeException {

    public DuplicateProductNameException(String message) {
        super(message);
    }
}
