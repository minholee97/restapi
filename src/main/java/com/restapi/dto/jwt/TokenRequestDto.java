package com.restapi.dto.jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenRequestDto {
    String accessToken;
    String refreshToken;
    @Builder
    public TokenRequestDto(String accessToken, String refresshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refresshToken;
    }
}
