package com.restapi.dto.sign;

import com.restapi.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Getter
public class UserSignupRequestDto {
    private String email;
    private String password;
    private String name;

    @Builder
    public UserSignupRequestDto(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .roles(Collections.singletonList("ROLE_USER")) // security에서 검증할떄 USER
                .build();
    }
}
