package com.restapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonResponse {
    SUCCESS(0, "API 호출 성공"),
    FAIL(-1, "API 호출 실패");

    private int code;
    private String message;
}
