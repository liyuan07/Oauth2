package org.example.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.config.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Autowired
    public SecurityConfig(JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter) {
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
    }

    private static AuthorizationManager<RequestAuthorizationContext> hasIpAddress() {
        IpAddressMatcher ipAddressMatcher = new IpAddressMatcher("127.0.0.1");
        return (authentication, context) -> {
            HttpServletRequest request = context.getRequest();
            return new AuthorizationDecision(ipAddressMatcher.matches(request));
        };
    }

    /// 在通过jwt token获得用户信息的时候需要使用authenticationManager，或者context信息，所以需要显示注入
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /// 这是一个方法引用，用于禁用 CSRF 保护。在前后端分离的应用中，由于不使用传统的表单提交，通常会禁用 CSRF 保护（不开这个接受不了post请求）
                .csrf(AbstractHttpConfigurer::disable)
                /// sessionManagement：用于配置会话管理策略。
                /// 会话创建策略设置为无状态（STATELESS）。在无状态的会话管理中，Spring Security 不会创建或使用 HTTP 会话来存储用户的认证信息，适用于基于令牌（如 JWT）的认证方式。
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        /// 允许所有用户访问 /user/account/token/ 和 /user/account/register/ 这两个路径，通常用于用户登录和注册接口。
                        .requestMatchers("/user/account/token", "/user/account/register","/testjson" , "/testxxx","/user/{userId}").permitAll()
                        /// 限制只有特定 IP 地址（在 hasIpAddress 方法中定义）的请求才能访问 /pk/start/game 和 /pk/receive/bot/move 路径
                        // .requestMatchers("/pk/start/game", "/pk/receive/bot/move").access(hasIpAddress())
                        /// 允许所有用户发送 OPTIONS 请求。OPTIONS 请求通常用于获取服务器支持的请求方法和头部信息，在跨域请求中经常会用到
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        /// 除了上述允许的请求外，其他所有请求都需要用户进行认证才能访问
                        .anyRequest().authenticated()
                );
        /// 在默认UsernamePasswordAuthenticationFilter之前添加一个jwtAuthenticationTokenFilter
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        /// 表示所有以 /websocket/ 开头的路径都会被忽略
        return (web -> web.ignoring().requestMatchers("/websocket/**"));
    }
}

