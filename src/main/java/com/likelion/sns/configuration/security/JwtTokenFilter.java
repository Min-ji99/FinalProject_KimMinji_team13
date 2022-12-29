package com.likelion.sns.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.sns.domain.dto.ErrorResponse;
import com.likelion.sns.domain.dto.Response;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.service.UserService;
import com.likelion.sns.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final String TOKEN_NOT_FOUND="토큰이 존재하지 않습니다.";
    private final String NO_BAERER_TYPE="Bearer Token이 아닙니다.";
    private final String INVALID_TOKEN="토큰이 유효하지 않습니다.";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header=request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization : {}", header);

        if(header == null){
            ErrorResponse errorResponse=new ErrorResponse(ErrorCode.INVALID_TOKEN, TOKEN_NOT_FOUND);
            request.setAttribute("exception", errorResponse);
            filterChain.doFilter(request, response);
            return;
        }
        if(!header.startsWith("Bearer ")){
            ErrorResponse errorResponse=new ErrorResponse(ErrorCode.INVALID_TOKEN, NO_BAERER_TYPE);
            request.setAttribute("exception", errorResponse);
            filterChain.doFilter(request, response);
            return;
        }
        String token=header.split(" ")[1].trim();
        if(jwtTokenUtil.validateToken(token)){
            String username=jwtTokenUtil.getUsername(token);

            User user=userService.getUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), null, List.of(new SimpleGrantedAuthority(user.getRole().name())));
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }else{
            ErrorResponse errorResponse=new ErrorResponse(ErrorCode.INVALID_TOKEN, INVALID_TOKEN);
            request.setAttribute("exception", errorResponse);
        }
        filterChain.doFilter(request, response);
    }
}
