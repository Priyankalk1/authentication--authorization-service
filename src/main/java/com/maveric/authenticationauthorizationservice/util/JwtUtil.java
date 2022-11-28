package com.maveric.authenticationauthorizationservice.util;

import com.maveric.authenticationauthorizationservice.dto.GateWayResponseDto;
import com.maveric.authenticationauthorizationservice.model.UserPrincipal;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JwtUtil.class);
    @Value("${jwt.secret}")
    private String secretKey;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) { //NOSONAR
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserPrincipal userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(),userDetails.getUser().get_id());
    }

    private String createToken(Map<String, Object> claims, String subject,String id) {
        log.info("Token creation!");
        return Jwts.builder().setClaims(claims).setId(id).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 7)) // 7 mins
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public GateWayResponseDto validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return new GateWayResponseDto(true,extractAllClaims(token));
        } catch (Exception ex) {
            return new GateWayResponseDto(false,null);
        }

    }
}
