package com.example.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.entity.RestBean;
import com.example.entity.dto.AccountDTO;
import com.example.entity.vo.response.AuthorizeVO;
import com.example.filter.JwtAuthorizeFilter;
import com.example.service.AccountService;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {


    @Resource
    private JwtUtils jwtUtils;
    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;

    @Resource
    AccountService accountService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(conf -> conf.requestMatchers("/api/auth/**").permitAll().anyRequest().authenticated()
                ).formLogin(conf -> conf.loginProcessingUrl("/api/auth/login").successHandler(
                        this::handleProcess).failureHandler(this::handleProcess))
                .logout(conf -> conf.logoutUrl("/api/auth/logout").logoutSuccessHandler(this::onLogoutSuccess))
                .exceptionHandling(conf -> conf.authenticationEntryPoint(this::handleProcess)
                        .accessDeniedHandler(this::handleProcess))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    public void handleProcess(HttpServletRequest request,
                              HttpServletResponse response,
                              Object exceptionOrAuthentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        if (exceptionOrAuthentication instanceof AccessDeniedException accessDeniedException) {
            writer.write(RestBean.forbidden(accessDeniedException.getMessage()).asJsonString());
        } else if (exceptionOrAuthentication instanceof Exception exception) {
            writer.write(RestBean.unauthorized(exception.getMessage()).asJsonString());
        } else if (exceptionOrAuthentication instanceof Authentication authentication) {
            User user = (User) authentication.getPrincipal();
            AccountDTO account = accountService.findAccountByNameOrEmail(user.getUsername());
            String token = jwtUtils.createJwt(user, account.getId(), account.getUsername());
            if (StringUtils.isBlank(token)) {
                writer.write(RestBean.forbidden("登录验证频繁,请稍后再试").asJsonString());
            } else {
                AuthorizeVO authorizeVO = account.asViewObject(AuthorizeVO.class, v -> {
                    v.setExpire(jwtUtils.expireTime());
                    v.setToken(token);
                });
                writer.write(RestBean.success(authorizeVO).asJsonString());
            }
        }
    }
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        response.setContentType("application/json;charset=utf-8");
//        User user = (User) authentication.getPrincipal();
//        AccountDTO account = accountService.findAccountByNameOrEmail(user.getUsername());
//        String token = jwtUtils.createJwt(user, account.getId(), account.getUsername());
//        AuthorizeVO authorizeVO = account.asViewObject(AuthorizeVO.class,v->{
//            v.setExpire(jwtUtils.expireTime());
//            v.setToken(token);
//        });
//
////        AuthorizeVO authorizeVO = AuthorizeVO.builder().username(account.getUsername()).role(account.getRole()).token(token).expire(jwtUtils.expireTime()).build();
//        response.getWriter().write(RestBean.success(authorizeVO).asJsonString());
//    }

//    public void onUnauthorized(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
//        response.setContentType("application/json;charset=utf-8");
//        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
//    }

//    public void onAccessDeny(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//        response.setContentType("application/json;charset=utf-8");
//        response.getWriter().write(RestBean.forbidden(accessDeniedException.getMessage()).asJsonString());
//    }

//    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
//        response.setContentType("application/json;charset=utf-8");
//        response.getWriter().write(RestBean.failure(401, exception.getMessage()).asJsonString());
//    }


    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        response.getWriter().println("Success");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if (jwtUtils.invalidateJwt(authorization)) {
            writer.write(RestBean.success().asJsonString());
        } else {
            writer.write(RestBean.failure(400, "退出登录失败").asJsonString());
        }

    }
}