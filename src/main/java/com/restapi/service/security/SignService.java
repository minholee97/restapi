package com.restapi.service.security;

import com.restapi.advice.exception.EmailLoginFailedException;
import com.restapi.advice.exception.EmailSignupFailedException;
import com.restapi.advice.exception.RefreshTokenException;
import com.restapi.advice.exception.UserNotFoundException;
import com.restapi.config.security.JwtProvider;
import com.restapi.dto.jwt.TokenDto;
import com.restapi.dto.jwt.TokenRequestDto;
import com.restapi.dto.sign.UserLoginRequestDto;
import com.restapi.dto.sign.UserSignupRequestDto;
import com.restapi.entity.RefreshToken;
import com.restapi.entity.User;
import com.restapi.repository.RefreshTokenRepository;
import com.restapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenDto login(UserLoginRequestDto userLoginRequestDto) {
        User user = userRepository.findByEmail(userLoginRequestDto.getEmail()).orElseThrow(EmailLoginFailedException::new);

        if(!passwordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword()))
            throw new EmailLoginFailedException();

        TokenDto tokenDto = jwtProvider.createTokenDto(user.getUserId(), user.getRoles());

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenKey(user.getUserId())
                .token(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        return tokenDto;
    }

    @Transactional
    public Long signup(UserSignupRequestDto userSignupRequestDto) {
        if (userRepository.findByEmail(userSignupRequestDto.getEmail()).isPresent())
            throw new EmailSignupFailedException();
        return userRepository.save(userSignupRequestDto.toEntity(passwordEncoder)).getUserId();
    }

    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        if (!jwtProvider.validationToken(tokenRequestDto.getRefreshToken())) {
            throw new RefreshTokenException();
        }

        // AccessToken 에서 Username (pk) 가져오기
        String accessToken = tokenRequestDto.getAccessToken();
        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        // user pk로 유저 검색 / repo 에 저장된 Refresh Token 이 없음
        System.out.println(authentication.getName());
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenKey(user.getUserId())
                .orElseThrow(RefreshTokenException::new);

        // 리프레시 토큰 불일치 에러
        if (!refreshToken.getToken().equals(tokenRequestDto.getRefreshToken()))
            throw new RefreshTokenException();

        // AccessToken, RefreshToken 재발급, 저장
        TokenDto newCreatedToken = jwtProvider.createTokenDto(user.getUserId(), user.getRoles());
        RefreshToken updateRefreshToken = refreshToken.updateToken(newCreatedToken.getRefreshToken());
        refreshTokenRepository.save(updateRefreshToken);

        return newCreatedToken;
    }
}
