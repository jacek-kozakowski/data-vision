package com.datavision.backend.auth.security;

import com.datavision.backend.auth.service.JwtService;
import com.datavision.backend.user.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader("Authorization");
        if (header == null || header.isEmpty() || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        try{
            final String token = header.substring(7);
            final String username = jwtService.getUsernameFromToken(token);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (username != null && auth == null) {
                var userDetails = customUserDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(token, userDetails)){
                    var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        }catch (Exception e){
            handlerExceptionResolver.resolveException(request, response, null, e);
            return;
        }


    }
}
