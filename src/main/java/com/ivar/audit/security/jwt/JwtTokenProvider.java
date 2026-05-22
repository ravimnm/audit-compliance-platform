package com.ivar.audit.security.jwt;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;


@Component
public class JwtTokenProvider {
	@Value("${jwt.secret}")
	private String SECRET;
	
	public String generateToken(String userId, String role, String tenantId) {
	    return Jwts.builder()
	            .setSubject(userId)
	            .claim("role", role)
	            .claim("tenantId", tenantId)
	            .setIssuedAt(new Date())
	            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
	            .signWith(getSigningKey())
	            .compact();
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
	}
	
	public boolean validateToken(String token, String userId) {
		Claims claims = getClaims(token);
		return claims.getSubject().equals(userId) && !claims.getExpiration().before(new Date());
	}

	private Claims getClaims(String token) {
	    return Jwts.parser()
	            .verifyWith(getSigningKey())
	            .build()
	            .parseSignedClaims(token)
	            .getPayload();
	}
	public String getUserIdFromToken(String token) {
	    return getClaims(token).getSubject();
	}

	public String gettenantIdFromToken(String token) {
	    return getClaims(token).get("tenantId",String.class);
	}
	
	
	public String getRoleFromToken(String token) {
		return getClaims(token).get("role",String.class);
	}
	
	
}
