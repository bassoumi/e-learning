package com.CRUD.firstApp.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.antlr.v4.runtime.Token;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "POlpvOtmAdwf9V3fBnD6rywW2DvZ2cLLPeJU3ccZu/QoOZHbRnP4mF8TibRKGrz8LvUEFgI7b8wAfrozZzgNoJaoilUNrJIkX2OEA/c2SdEpOlsJF3xZyU2DIL9H7PpdsnJVDYL9tYhM2zFmBsje+5eDtvmgGZw+8tkFEpAOiXKDfGHAUmYGCChS/Vy3mTOZr1rA8rCXAQHHPwMSlzRJZe+CJRCdGyL/QCMlgiSGyut8fnK4dp5pgNXj2yv+DQaOgij07pmCiaG8hW+4DsCgPVQiuEAO2rPG0iRlabu6+4piKoLyiAD/tSnx4J62y+BkFSpZHEYEL8lYq6QWrCbSaYpvC7KBo6506XtG8cmgymA=\n";


    // final method that return the string that we need , name , email , .....
       public String extractUserName(String token) {
           return extractClaim(token,Claims::getSubject);
       }

       //extract the calims of the body of the token based on what the extractToken function need to extract
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
           final Claims claims = extractAllClaims(token);
           return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
           return generateToken(new HashMap<>(),userDetails);
    }

    // extraClaim is used if you want to add extra data in your token , password , id , role, + your username
    public String generateToken(Map<String, Object> extraClaims,UserDetails userDetails) {
           return Jwts
                   .builder()
                   .setClaims(extraClaims)
                   .setSubject(userDetails.getUsername())
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 ))
                   .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                   .compact();


    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
           final String userName = extractUserName(token);
           return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);

    }

    // return true if the date is all ready expired
      private boolean isTokenExpired(String token) {
           return extractExprication(token).before(new Date());
      }

      // return the date from the token
    private Date extractExprication(String token) {
           return extractClaim(token,Claims::getExpiration);
    }

    //extract the body of the token after verify that the token is valid
    private Claims extractAllClaims(String token) {
           return Jwts
                   .parserBuilder()
                   .setSigningKey(getSignKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();

    }


    // decode your secret key to test and verify your token
    private Key getSignKey() {
           byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
           return Keys.hmacShaKeyFor(keyBytes);
    }
}
