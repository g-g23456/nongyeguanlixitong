package com.panduoma.demo.response;

public class Result<T> {
    private Integer code;
    private String message;
    private T data;


    private Result(Integer code){
        this.code = code;
    }
    private Result(Integer code, String message){
        this.code = code;
        this.message = message;
    }
    private Result(Integer code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
    public T getData() {
        return data;
    }

    public static <T> Result<T> success(){
        return new Result<>(ResponseCode.SUCCESS.getCode());
    }
    public static <T> Result<T> success(String message){
        return new Result<>(ResponseCode.SUCCESS.getCode(), message);
    }
    public static <T> Result<T> data(T data){
        return new Result<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }
    public static <T> Result<T> error(String message){
        return new Result<>(ResponseCode.ERROR.getCode(), message);
    }
    public static <T> Result<T> error(Integer code, String message){
        return new Result<>(code, message);
    }
}
