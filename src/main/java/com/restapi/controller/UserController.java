package com.restapi.controller;

import com.restapi.service.ResponseService;
import com.restapi.service.UserService;
import com.restapi.dto.UserRequestDto;
import com.restapi.dto.UserResponseDto;
import com.restapi.model.response.CommonResult;
import com.restapi.model.response.ManyResult;
import com.restapi.model.response.OneResult;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"2. User"}) // 제목
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ResponseService responseService;

    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "X-AUTH-TOKEN",
                    value = "로그인 성공 후 AccessToken",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 조회 (아이디)", notes = "고유 아이디로 회원을 검색한다.")
    @GetMapping("/user/id/{userId}")
    public OneResult<UserResponseDto> findUserById(@ApiParam(value = "회원 ID", required = true) @PathVariable Long userId) {
        return responseService.getOneResult(userService.findById(userId));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "X-AUTH-TOKEN",
                    value = "로그인 성공 후 AccessToken",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 조회 (이메일)", notes = "이메일로 회원을 검색한다.")
    @GetMapping("/user/email/{email}")
    public OneResult<UserResponseDto> findUserByEmail(@ApiParam(value = "회원 Email", required = true) @PathVariable String email) {
        return responseService.getOneResult(userService.findByEmail(email));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "X-AUTH-TOKEN",
                    value = "로그인 성공 후 AccessToken",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "모든 회원 조회", notes = "모든 회원 목록을 출력한다.")
    @GetMapping("/users")
    public ManyResult<UserResponseDto> findAllUser() {
        return responseService.getManyResult(userService.findAllUser());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "X-AUTH-TOKEN",
                    value = "로그인 성공 후 AccessToken",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 수정", notes = "회원 정보를 수정한다.") // email unq (22.07.16)
    @PutMapping("/user")
    public CommonResult modify(@ApiParam(value = "회원 아이디", required = true) @RequestParam Long userId,
                                  @ApiParam(value = "회원 이름", required = true) @RequestParam String name) {
        UserRequestDto user = UserRequestDto.builder()
                .name(name)
                .build();
        userService.update(userId, user);
        return responseService.getSuccessResult();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "X-AUTH-TOKEN",
                    value = "로그인 성공 후 AccessToken",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 삭제", notes = "회원 삭제합니다.")
    @DeleteMapping("/user/{userId}")
    public CommonResult delete(@ApiParam(value = "회원 아이디", required = true) @PathVariable Long userId) {
        userService.delete(userId);
        return responseService.getSuccessResult();
    }


}
