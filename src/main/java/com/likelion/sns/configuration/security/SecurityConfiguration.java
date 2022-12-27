package com.likelion.sns.configuration.security;

import com.likelion.sns.service.UserService;
import com.likelion.sns.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {
    private final UserService userService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedEntryPoint customAccessDeniedEntryPoint;
    private final JwtTokenUtil jwtTokenUtil;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //log.info(httpSecurity);
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/swagger-resources/**", "/swagger-ui/**", "/swagger/**", "/webjars/**", "/v2/api-docs/**").permitAll()
                .antMatchers("/api/v1/hello", "/api/v1/users/join", "/api/v1/users/login").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()
                // /api/v1/users/*/role/change 요청에 대해서는 ROLE_ADMIN 역할을 가지고 있어야 한다.
                .antMatchers("/api/v1/users/**/role/change").hasAuthority("ADMIN")
                //나머지 요청은 모두 로그인 요구함
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                //USER 권한을 가진 사람이 ADMIN 권한을 가진 url에 대해 요청했을 때 처리
                .accessDeniedHandler(customAccessDeniedEntryPoint)
                //Filter 단에서 예외처리
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtTokenFilter(userService, jwtTokenUtil), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
