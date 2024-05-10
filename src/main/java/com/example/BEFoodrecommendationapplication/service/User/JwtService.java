package com.example.BEFoodrecommendationapplication.service.User;

import com.example.BEFoodrecommendationapplication.entity.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String getEmailFromJWT(String token);
     int getUserIdFromJWT(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateToken(User userDetails);

    String generateToken(Map<String, Object> extraClaims, User userDetails);


    String buildToken(Map<String, Object> extraClaims, User userDetails, long expiration);

    boolean isTokenValid(String token, UserDetails userDetails);

    boolean isTokenExpired(String token);

    Date extractExpiration(String token);

    Claims extractAllClaims(String token);

    Key getSignInKey();
}
