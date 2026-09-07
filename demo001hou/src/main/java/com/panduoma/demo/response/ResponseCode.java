package com.panduoma.demo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS(200, "Success"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    ERROR(500, "Error");

    private Integer code;
    private String message;
}
