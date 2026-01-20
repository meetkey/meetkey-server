package com.meetkey.server.global.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetkey.server.domain.auth.exception.AuthErrorStatus;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.security.CustomUserDetails;
import com.meetkey.server.global.security.jwt.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        // 토큰이 없다면 다음 필터로 넘긴다
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authorization.substring(7);

        // 토큰 만료 여부 확인, 만료 시 다음 필터로 넘기지 않는다.
        try {
            jwtUtil.isExpired(accessToken);
        } catch (JwtException e) {
            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            response.setContentType("application/json;charset=UTF-8");

            BasicResponse<Object> errorResponse = BasicResponse.error(AuthErrorStatus.EXPIRED_TOKEN, null);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(errorResponse);

            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
            writer.close();

            return;
        }

        // 토큰이 access 인지 확인
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")){
            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        // 임시 인증 객체
        CustomUserDetails customUserDetails =
                new CustomUserDetails(username, role);

        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
