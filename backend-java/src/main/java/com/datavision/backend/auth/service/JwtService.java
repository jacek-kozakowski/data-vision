package com.datavision.backend.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Getter
    @Value("${security.jwt.expiration-time}")
    private Long expirationTime;

    public String generateToken(UserDetails userDetails){
        return generateToken(userDetails, expirationTime);
    }

    private String generateToken(UserDetails userDetails, long expirationTime){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("Invalid secret key");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String getUsernameFromToken(String token){
        return extractClaim(token, Claims::getSubject);
    }
    private Date getExpirationDateFromToken(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    private boolean isTokenExpired(String token){
        return getExpirationDateFromToken(token).before(new Date(System.currentTimeMillis()));
    }
    public boolean isTokenValid(String token, UserDetails userDetails){
        String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && verifySignature(token));
    }
    private boolean verifySignature(String token){
        try{
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        }catch (JwtException e){
            return false;
        }
    }

}
