package com.example.BEFoodrecommendationapplication.service.User;

import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.repository.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    private final TokenRepository tokenRepository;
    @Override
    public int getUserIdFromJWT(String token) {
        Claims claims = extractAllClaims(token);
        return Integer.parseInt(claims.get("userId", String.class));
    }
    @Override
    public String getEmailFromJWT(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
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
    public String buildToken(Map<String, Object> extraClaims, User userDetails, long expiration) {


        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        extraClaims.put("email", userDetails.getEmail());
        extraClaims.put("userId", userDetails.getId());

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(Long.toString(userDetails.getId()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    @Override
    public boolean isTokenValid(String token, UserDetails user) {
        String email = getEmailFromJWT(token);

        boolean validToken = tokenRepository
                .findByToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);

        return (email.equals(user.getUsername())) && !isTokenExpired(token) && validToken;
    }


    @Override
    public boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);

        return expirationDate.before(new Date());
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