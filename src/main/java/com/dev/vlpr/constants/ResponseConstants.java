package com.dev.vlpr.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum ResponseConstants {
    RECEIVED_UPDATE_IS_NULL("Received update is null"),
    RECEIVED_UNSUPPORTED_MESSAGE_TYPE("Received unsupported message type"),
    UNSUPPORTED_MESSAGE_TYPE("unsupported message type"),
    FILE_ACCEPTED_PROCESSED("File accepted, processed... ");

    private final String message;

}
