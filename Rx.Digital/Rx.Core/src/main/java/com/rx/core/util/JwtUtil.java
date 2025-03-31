package com.rx.core.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private long expirationSeconds;

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(String username, Map<String, Object> claims) {
		return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expirationSeconds * 1000))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	// 驗證簽章（Signature 是否有效），若 JWT 結構錯誤或密鑰不對，會拋例外
	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	// 只是抓出 sub（Subject）欄位，也沒驗證什麼商業邏輯
	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	// 只檢查兩件事:
	// 1.token.subject == 傳入的 username
	// 2. token.exp > 現在（也就是沒過期）
	public boolean isTokenValid(String token, String username) {
		return username.equals(extractUsername(token)) && extractAllClaims(token).getExpiration().after(new Date());
	}
}
