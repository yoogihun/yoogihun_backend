package com.ygh.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class Result {
    private int status;
    private Object message;
    private Object data;

    public Result() {
        this.status = HttpStatus.OK.value();
        this.message = HttpStatus.OK.getReasonPhrase();
    }

    public Result(Object data) {
        this.status = HttpStatus.OK.value();
        this.message = HttpStatus.OK.getReasonPhrase();
        this.data = data;
    }

    public Result(HttpStatus httpStatus){
        this.status = httpStatus.value();
        this.message = httpStatus.getReasonPhrase();
    }
}