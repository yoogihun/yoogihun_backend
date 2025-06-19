package com.ygh.common.exception;

public record BaseErrorResponse(
        int status,
        String code,
        String message
) {
}
