package com.restapi.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DefaultException(9999, "예외 발생"),
    UserNotFound(1000, "일치하는 사용자가 존재하지 않음"),
    EmailLoginFailed(1001, "가입하지 않은 아이디 또는 잘못된 비밀번호"),
    EmailSignupFailed(1002, "이미 가입된 이메일으로 회원 가입 시도"),
    AuthenticationEntrypoint(1003, "해당 자원에 접근하기 위한 권한이 없음"),
    AccessDenied(1004, "해당 자원에 접근하기 위한 권한이 충분하지 않음"),
    RefreshTokenException(1005, "리프레시 토큰이 DB에 없거나 일치하지 않음");

    private int code;
    private String message;
}
