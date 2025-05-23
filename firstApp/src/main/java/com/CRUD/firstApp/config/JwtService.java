package com.CRUD.firstApp.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "POlpvOtmAdwf9V3fBnD6rywW2DvZ2cLLPeJU3ccZu/QoOZHbRnP4mF8TibRKGrz8LvUEFgI7b8wAfrozZzgNoJaoilUNrJIkX2OEA/c2SdEpOlsJF3xZyU2DIL9H7PpdsnJVDYL9tYhM2zFmBsje+5eDtvmgGZw+8tkFEpAOiXKDfGHAUmYGCChS/Vy3mTOZr1rA8rCXAQHHPwMSlzRJZe+CJRCdGyL/QCMlgiSGyut8fnK4dp5pgNXj2yv+DQaOgij07pmCiaG8hW+4DsCgPVQiuEAO2rPG0iRlabu6+4piKoLyiAD/tSnx4J62y+BkFSpZHEYEL8lYq6QWrCbSaYpvC7KBo6506XtG8cmgymA=\n";
    public String extractUserEmail(String token ) {
        return extractClaim(token, Claims::getSubject);

    }

    public <T> T extractClaim(String token, Function<Claims,T>claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keybytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keybytes);
    }
}
