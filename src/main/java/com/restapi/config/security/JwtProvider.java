package com.restapi.config.security;

import com.restapi.advice.exception.AuthenticationEntryPointException;
import com.restapi.dto.jwt.TokenDto;
import com.restapi.service.security.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.Base64UrlCodec;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {

    @Value("jwt.secret")
    private String secretKey; // application.properties에 작성한 secret key 불러오기, 가급적 별도의 파일에 작성하는게 좋음
    private String ROLES = "roles";
    private final Long accessTokenValidMillisecond = 60 * 60 * 1000L;            //  1 hour
    private final Long refreshTokenValidMillisecond = 14 * 24 * 60 * 60 * 1000L; // 24 hour
    private final CustomUserDetailsService userDetailsService;

    @PostConstruct
    protected void init() { // 의존성 주입 이후, secret key를 Base64 인코딩
        secretKey = Base64UrlCodec.BASE64URL.encode(secretKey.getBytes(StandardCharsets.UTF_8));
        //secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // Jwt 생성
    public TokenDto createTokenDto(Long userPk, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userPk)); // User 구분을 위해 정보 claims에 저장
        claims.put(ROLES, roles);
        Date now = new Date(); // 생성 날짜
        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims) // Claim 정보에 부가 정보를 세팅
                .setIssuedAt(now) // 발행 날짜 세팅
                .setExpiration(new Date(now.getTime() + accessTokenValidMillisecond)) // 만료 날짜 세팅
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setExpiration(new Date(now.getTime() + refreshTokenValidMillisecond)) // 만료 날짜 세팅
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpireDate(accessTokenValidMillisecond)
                .build();
    }

    // Jwt로 인증정보 조회
    public Authentication getAuthentication (String token) {
        Claims claims = parseClaims(token);
        if (claims.get(ROLES) == null) { // 권한 정보가 없음
            throw new AuthenticationEntryPointException();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Jwt 토큰 복호화
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // HTTP Request Header에서 Token 파싱 ( "X-AUTH-TOKEN: jwt" )
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    // jwt의 유효성, 만료일자 검사
    public boolean validationToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error(e.toString());
            return false;
        }
    }
}
