package com.restapi.advice;

import com.restapi.advice.exception.*;
import com.restapi.service.ResponseService;
import com.restapi.model.response.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice // 모든 @Controller에서 발생하는 예외를 잡아 처리함, Json 형식으로 에러 응답
@RequiredArgsConstructor
public class ExceptionAdvice {
    private final ResponseService responseService;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult defaultException(HttpServletRequest request, Exception e) {
        return responseService.getFailResult(ErrorCode.DefaultException.getCode(), ErrorCode.DefaultException.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult userNotFoundException(HttpServletRequest request, UserNotFoundException e) {
        return responseService.getFailResult(ErrorCode.UserNotFound.getCode(), ErrorCode.UserNotFound.getMessage());
    }

    @ExceptionHandler(EmailLoginFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult emailLoginFailedException(HttpServletRequest request, EmailLoginFailedException e) {
        return responseService.getFailResult(ErrorCode.EmailLoginFailed.getCode(), ErrorCode.EmailLoginFailed.getMessage());
    }

    @ExceptionHandler(EmailSignupFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult emailSignupFailedException(HttpServletRequest request, EmailSignupFailedException e) {
        return responseService.getFailResult(ErrorCode.EmailSignupFailed.getCode(), ErrorCode.EmailSignupFailed.getMessage());
    }

    @ExceptionHandler(AuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult authenticationEntrypointException(HttpServletRequest request, AuthenticationEntryPointException e) {
        return responseService.getFailResult(ErrorCode.AuthenticationEntrypoint.getCode(), ErrorCode.AuthenticationEntrypoint.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult accessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        return responseService.getFailResult(ErrorCode.AccessDenied.getCode(), ErrorCode.AccessDenied.getMessage());
    }

    @ExceptionHandler(RefreshTokenException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult refreshTokenException(HttpServletRequest request, RefreshTokenException e) {
        return responseService.getFailResult(ErrorCode.RefreshTokenException.getCode(), ErrorCode.RefreshTokenException.getMessage());
    }
}
