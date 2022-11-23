package com.dev.vlpr.constants;

public enum ResponseConstants {
    RECEIVED_UPDATE_IS_NULL("Received update is null"),
    RECEIVED_UNSUPPORTED_MESSAGE_TYPE("received unsupported message type")
    ;

    private String message;
    ResponseConstants(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ResponseTg{" +
                "message='" + message + '\'' +
                '}';
    }
}
