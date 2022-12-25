package com.likelion.sns.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenUtil {
    private final String secretKey;
    private final long expireTimeMs;

    public JwtTokenUtil(@Value("${jwt.token.secret}")String secretKey) {
        this.secretKey = secretKey;
        this.expireTimeMs = 1000*60*60;
    }

    public String createToken(String userName){
        Claims claims= Jwts.claims();
        claims.put("userName", userName);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expireTimeMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean isExpired(String token) {
        Date expiredDate=extractClaims(token).getExpiration();
        return expiredDate.before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public String getUsername(String token) {
        return extractClaims(token).get("userName").toString();
    }
    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        }catch(SecurityException | MalformedJwtException e){
            log.error("Invalid Jwt signature");
            return false;
        }catch (UnsupportedJwtException e){
            log.error("Unsupported Jwt token");
            return false;
        }catch(IllegalArgumentException e){
            log.error("Jwt token is invalid");
            return false;
        }catch(ExpiredJwtException e){
            return false;
        }
    }
}
