package com.meetkey.server.global.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetkey.server.domain.auth.repository.RefreshTokenRepository;
import com.meetkey.server.domain.member.service.MemberService;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import com.meetkey.server.global.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository  refreshTokenRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestUri = request.getRequestURI();
        if (!requestUri.startsWith("/logout")) {
            chain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")){
            chain.doFilter(request, response);
            return;
        }

        String refresh = request.getHeader("refresh");

        if (refresh == null){
            anywayLogout(response);
            return;
        }

        try {
            jwtUtil.isValid(refresh, false);
        } catch (ExpiredJwtException e){
            anywayLogout(response);
            return;
        }

        if (refreshTokenRepository.findById(refresh).isEmpty()){
            anywayLogout(response);
            return;
        }

        refreshTokenRepository.delete(refresh);

        anywayLogout(response);
    }

    // 로그아웃 시 토큰이 잘못되었더라도, 어차피 로그아웃 시킬 것이기 때문에 OK 응답.
    private void anywayLogout(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        BasicResponse<Object> errorResponse = BasicResponse.success(CommonSuccessStatus._OK, null);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(errorResponse);

        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
        writer.close();
    }
}
