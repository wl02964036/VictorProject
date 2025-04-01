package com.rx.webapi.interceptor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rx.core.bean.SystemUser;
import com.rx.core.dao.SystemUserDao;
import com.rx.core.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private SystemUserDao systemUserDao;

	private static final AntPathMatcher pathMatcher = new AntPathMatcher();

	private static final List<String> IGNORE_FRONTEND = List.of("/login", // angular登入畫面
            "/logout", // angular登出畫面
            "/manage", // angular內嵌畫面
            "/manage/**",
            "/index.html",
            "/", // 根目錄
            "/*.js", "/*.css", "/*.ico", // 頂層靜態資源
            "/**/*.js", "/**/*.css", "/assets/**", // 子資料夾
            "/favicon.ico" // angular內嵌畫面
            );

	private static final List<String> WHITELIST = List.of("/angular/wav/**", "/angular/login", "/angular/reloadCaptcha",
			"/angular/captchaImage", "/angular/captchaAudio", "/angular/captchaNumber");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String path = request.getRequestURI();

		// 如果是frontend路徑，直接放行
		boolean isFrontend = IGNORE_FRONTEND.stream().anyMatch(whitelistPath -> pathMatcher.match(whitelistPath, path));

		if (isFrontend) {
			chain.doFilter(request, response);
			return;
		}

		// 如果是白名單，直接放行
		boolean isWhitelisted = WHITELIST.stream().anyMatch(whitelistPath -> pathMatcher.match(whitelistPath, path));

		if (isWhitelisted) {
			chain.doFilter(request, response);
			return;
		}

		final String authHeader = request.getHeader("Authorization");

		if (authHeader == null || authHeader.isBlank()) {
			sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "No Authorization header");
			return;
		}

		if (!authHeader.startsWith("Bearer ")) {
			sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
			return;
		}

		String[] parts = authHeader.split(" ");
		if (parts.length != 2) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token format");
			return;
		}

		String tokenType = parts[0];
		String tokenValue = parts[1];

		try {
			String username = jwtUtil.extractUsername(tokenValue);
			
		    if (!jwtUtil.isTokenValid(tokenValue, username)) {
		        sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token 已過期，請重新登入");
		        return;
		    }

			Optional<SystemUser> userOpt = systemUserDao.findByUserName(username);
			if (userOpt.isEmpty()) {
				sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, username + " 帳號不存在");
				return;
			}

			// 建立 Spring Security 認證物件
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userOpt.get(), null, List.of() // 這裡可以加 roles
			);
			SecurityContextHolder.getContext().setAuthentication(auth);
		} catch (ExpiredJwtException e) {
	        sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token 已過期，請重新登入");
			return;
		} catch (Exception ex) {
			sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token parsing error: " + ex.getMessage());
			return;
		}

		chain.doFilter(request, response);
	}

	private void sendJsonError(HttpServletResponse response, int statusCode, String message) throws IOException {
		response.setStatus(statusCode);
		response.setContentType("application/json");
		response.getWriter().write("{\"status\":\"no_auth\",\"message\":\"" + message + "\"}");
	}
}
