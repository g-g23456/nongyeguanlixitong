package com.panduoma.demo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS(200, "Success"),
    ERROR(500, "Error");

    private Integer code;
    private String message;
}
