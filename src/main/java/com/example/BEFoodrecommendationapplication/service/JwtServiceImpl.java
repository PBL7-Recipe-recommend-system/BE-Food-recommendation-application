package com.example.BEFoodrecommendationapplication.service;

import com.example.BEFoodrecommendationapplication.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    @Override
    public String generateToken(User userDetails)
    {
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public String generateToken(Map<String, Object> extraClaims, User userDetails)
    {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }
    @Override
    public String generateRefreshToken(User userDetails)
    {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }
    @Override
    public String buildToken(Map<String, Object> extraClaims, User userDetails, long expiration) {
        Calendar vietnamCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        vietnamCalendar.setTimeInMillis(System.currentTimeMillis());

        Date currentTimeInVietnam = vietnamCalendar.getTime();
        Date expirationTimeInVietnam = new Date(currentTimeInVietnam.getTime() + expiration);

        extraClaims.put("userId", userDetails.getId());

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(currentTimeInVietnam)
                .setExpiration(expirationTimeInVietnam)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    @Override
    public boolean isTokenValid(String token, UserDetails userDetails)
    {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    @Override
    public boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);

        TimeZone vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(vietnamTimeZone);
        calendar.setTime(expirationDate);

        Date vietnamExpirationDate = calendar.getTime();

        return vietnamExpirationDate.before(new Date());
    }


    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public Claims extractAllClaims(String token)
    {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}