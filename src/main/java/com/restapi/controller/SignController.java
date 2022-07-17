package com.restapi.controller;

import com.restapi.dto.jwt.TokenDto;
import com.restapi.dto.jwt.TokenRequestDto;
import com.restapi.dto.sign.UserLoginRequestDto;
import com.restapi.dto.sign.UserSignupRequestDto;
import com.restapi.model.response.OneResult;
import com.restapi.service.ResponseService;
import com.restapi.service.security.SignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "1. SignUp / LogIn")
@RequiredArgsConstructor
@RestController
public class SignController {
    private final SignService signService;
    private final ResponseService responseService;

    @ApiOperation(value = "로그인", notes = "이메일로 로그인 수행")
    @PostMapping("/login")
    public OneResult<TokenDto> login (
            @ApiParam(value = "로그인 DTO", required = true) @RequestBody UserLoginRequestDto userLoginRequestDto) {
        TokenDto tokenDto = signService.login(userLoginRequestDto);
        return responseService.getOneResult(tokenDto);
    }

    @ApiOperation(value = "회원가입", notes = "회원가입 수행")
    @PostMapping("/signup")
    public OneResult<Long> signup (
            @ApiParam(value = "회원가입 DTO", required = true) @RequestBody UserSignupRequestDto userSignupRequestDto) {
        Long signupId = signService.signup(userSignupRequestDto);
        return responseService.getOneResult(signupId);
    }

    @ApiOperation(value = "Access Token, Refresh Token 재발급",
            notes = "Access Token 만료시 회원 검증 후 Refresh Token을 검증해서 두 Token을 재발급")
    @PostMapping("/reissue")
    public OneResult<TokenDto> reissue(
            @ApiParam(value = "토큰 재발급 DTO", required = true)
            @RequestBody TokenRequestDto tokenRequestDto) {
            return responseService.getOneResult(signService.reissue(tokenRequestDto));
    }
}
