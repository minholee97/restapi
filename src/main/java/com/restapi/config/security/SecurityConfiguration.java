package com.restapi.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {
    private final JwtProvider jwtProvider;

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // 초기 설정은 비 인증시 로그인 폼으로 리다이렉트 되는데 REST API 이므로 disable
                .csrf().disable() // REST API 이므로 상태를 저장하지 않기 때문에 CSRF 보안을 설정할 필요가 없으므로 disable
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Jwt로 인증하므로 세션이 필요하지 않음
                .and()
                    .authorizeRequests() // URL 별 권한 관리 시작
                        .antMatchers(HttpMethod.POST, "/login", "/signup", "/reissue").permitAll() // 로그인, 회원가입은 누구나 허용
                        .antMatchers(HttpMethod.GET, "/exception/**").permitAll()
                        //.antMatchers("/*/users").hasRole("ADMIN")
                        .anyRequest().hasRole("USER") // 그 외 요청은 인증된 USER만 가능
                .and()
                    .exceptionHandling()

                    // jwt 토큰이 없거나 토큰이 틀린 경우 exceptionHandling
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint())

                    // 해당 자원에 접근하기 위한 권한이 부족한 경우 exceptionHandling
                    .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                    // jwt 인증 필터를 UsernamePasswordAuthenticationFilter 전에 추가
                    .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Swagger 관련 Url 예외처리
        return (web) -> web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
                "/swagger-ui/index.html", "/webjars/**", "/swagger/**", "/swagger-ui/**");
    }
}
