package com.aassoua.order_service.message;

public enum Messages {
    NOTHING_SAVED("Nothing was saved to file, please try again");

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}