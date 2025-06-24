package com.sms.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sms.dto.UserDto;
import com.sms.repository.UserRepository;
import com.sms.security.CustomUserDetailsService;
import com.sms.service.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        logger.debug("Processing request: {} {}", method, requestPath);

        // Skip filter untuk endpoint yang tidak memerlukan autentikasi
        if (shouldSkipFilter(requestPath)) {
            logger.debug("Skipping JWT filter for path: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;
        boolean tokenProcessed = false;

        // Cek Authorization header
        if (hasAuthorizationBearer(request)) {
            token = getAccessToken(request);
            logger.debug("Found token in Authorization header");
            tokenProcessed = processToken(token, request);
        }
        // Cek cookie jika tidak ada di header
        else if (hasCookieToken(request)) {
            token = getCookieToken(request);
            logger.debug("Found token in cookie");
            tokenProcessed = processToken(token, request);
        } else {
            logger.debug("No token found in request");
        }

        if (token != null && !tokenProcessed) {
            logger.warn("Token found but not processed successfully");
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkipFilter(String requestPath) {
        return requestPath.equals("/login") ||
                requestPath.equals("/logout") ||
                requestPath.startsWith("/docs/") ||
                requestPath.equals("/") ||
                requestPath.equals("/error");
    }

    private boolean processToken(String token, HttpServletRequest request) {
        try {
            if (!jwtUtils.validateAccessToken(token)) {
                logger.debug("Token validation failed");
                return false;
            }

            if (isTokenBlacklisted(token)) {
                logger.debug("Token is blacklisted");
                return false;
            }

            setAuthenticationContext(token, request);
            logger.debug("Authentication context set successfully");
            return true;

        } catch (Exception e) {
            logger.error("Error processing token: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenBlacklisted(String token) {
        if (tokenBlacklistService == null) {
            logger.debug("TokenBlacklistService not available");
            return false;
        }
        try {
            boolean isBlacklisted = tokenBlacklistService.isTokenBlacklisted(token);
            logger.debug("Token blacklist check result: {}", isBlacklisted);
            return isBlacklisted;
        } catch (Exception e) {
            logger.error("Error checking token blacklist: {}", e.getMessage());
            return false;
        }
    }

    private boolean hasCookieToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getCookieToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (ObjectUtils.isEmpty(header) || !header.startsWith("Bearer")) {
            return false;
        }
        return true;
    }

    private String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = header.split(" ")[1].trim();
        return token;
    }

    private void setAuthenticationContext(String token, HttpServletRequest request) {
        UserDto userDetails = userDetailsService.loadUserByUsername(jwtUtils.getEmailFromToken(token));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}