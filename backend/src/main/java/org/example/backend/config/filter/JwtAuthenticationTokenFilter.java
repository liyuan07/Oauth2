package org.example.backend.config.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.mapper.UserMapper;
import org.example.backend.pojo.User;
import org.example.backend.service.impl.utils.UserDetailsImpl;
import org.example.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*
        从 HTTP 请求头中获取名为 Authorization 的字段值。
        * 在使用 JWT 进行身份验证时，客户端通常会在请求头的 Authorization 字段中携带 JWT，
        * 格式一般为 Bearer <token>，这里的 <token> 就是实际的 JWT 字符串。
        * */
        String authorization = request.getHeader("Authorization");
        /*
        * !StringUtils.hasText(authorization)：StringUtils.hasText 是 Spring 框架提供的一个工具方法，用于判断字符串是否不为 null、长度不为 0 且不只包含空白字符。
        * 如果 authorization 不满足这些条件，说明请求头中没有有效的 Authorization 信息。
        * */
        if(!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        String userid;
        try{
            Claims claims = JwtUtil.parseJWT(authorization.substring(7));
            userid = claims.getSubject();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        User user = userMapper.selectById(Integer.parseInt(userid));
        if(user == null){
            /// 这里基本不会运行。既要jwt验证通过，又找不到对应的user，也就是说user注销了，又拿着之前的jwt token回来登录。
            throw new RuntimeException("用户名未登录");
        }

        /*
        * 使用 JWT（JSON Web Token）进行验证之后，通常不需要再通过账户名和密码进行登录
        *
        * 一旦 SecurityContextHolder 中已经设置了有效的认证信息，后续 Spring Security 默认的表单登录认证流程（如 UsernamePasswordAuthenticationFilter）在处理该请求时
        * 会发现 SecurityContextHolder 中已经有认证信息，就不会再重复进行认证操作，也就不会再调用这三行代码对应的逻辑。
        * */
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        /// UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password); 使用账户名密码登录的默认实现
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, null);
        SecurityContextHolder.getContext().setAuthentication(token);
        /// 将请求和响应对象传递给过滤器链中的下一个过滤器继续处理。
        filterChain.doFilter(request, response);
    }

}
