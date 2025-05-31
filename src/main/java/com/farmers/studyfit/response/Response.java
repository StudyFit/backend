package com.farmers.studyfit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;
@AllArgsConstructor
@Getter
@JsonPropertyOrder({"success","code","message","data"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private boolean success;
    private int code;
    private String message;
    private Object data;

    public Response(boolean success, int code, String msg) {
        this.success = success;
        this.code = code;
        this.message = msg;
    }

    public static Response success(String msg) {
        return new Response(true,OK.value(), msg,null);
    }
    public static Response success(String msg, Object data) {
        return new Response(true,OK.value(), msg, data);
    }

    public static Response failure(HttpStatus status,String msg) {
        return new Response(false, status.value(), msg, null);
    }
}
