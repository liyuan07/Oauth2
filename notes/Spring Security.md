# Spring Security + JWT

## 1 关键类

1. UserDetailsService和UserDetailsServiceImpl

```java
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>();
        userQueryWrapper.eq("username", username);
        User user = userMapper.selectOne(userQueryWrapper);
        if(user == null) { throw new UsernameNotFoundException(username); }
        return new UserDetailsImpl(user);
    }
}
```

2. UserDetails和UserDetailsImpl

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    //自己加的
    private User user;
    //自己加的
    private static final Logger logger= LoggerFactory.getLogger(UserDetailsImpl.class);

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        logger.info(user.getPassword() + "user.getpassword");
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

```

3. SecurityConfig

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

当 Spring 应用启动，Spring 容器会扫描所有带有 `@Configuration` 注解的类，并对其中的 `@Bean` 方法进行处理，进行自动调用。在这个过程中，`passwordEncoder()` 方法会被调用，从而在 Spring 容器中创建一个 `PasswordEncoder` 类型的 Bean 实例。

在 Spring Security 的配置里，通常会把 `PasswordEncoder` 配置到认证管理器（`AuthenticationManager`）中。当配置认证管理器时，Spring 会从容器中获取 `PasswordEncoder` Bean 实例。

**注册或密码更新**。在用户注册新账户或者更新密码的过程中，需要对用户输入的明文密码进行加密。此时，Spring 应用会从 Spring 容器中获取 `PasswordEncoder` Bean 实例，调用其 `encode()` 方法对密码进行加密。

**认证过程**。在用户登录时，Spring Security 会获取用户输入的明文密码和数据库中存储的加密密码，然后使用 `PasswordEncoder` 的 `matches()` 方法来比对这两个密码是否一致。例如，当 `AuthenticationManager` 进行认证时，会调用 `PasswordEncoder` 的 `matches()` 方法完成密码验证。







## 2 使用jwt登录认证授权过程

### 1 表单登录

当用户使用表单登录（常见的用户名和密码登录方式）时，Spring Security 会拦截登录请求。

默认情况下，表单登录的请求会被 `UsernamePasswordAuthenticationFilter` 处理。这个过滤器会从请求中提取用户名和密码，并将用户名和密码保存到上下文信息中，然后创建一个 `UsernamePasswordAuthenticationToken` 对象，接着将这个对象传递给 `AuthenticationManager` 进行认证。



### 2 认证过程

当用户发起登录请求时，Spring Security 的认证管理器（`AuthenticationManager`）会调用 `UserDetailsService` 来加载用户的 `UserDetails` 信息。

`AuthenticationManager` 在认证过程中会调用 `UserDetailsService` 的 `loadUserByUsername` 方法，也就是你代码中的 `UserDetailsServiceImpl` 类的 `loadUserByUsername` 方法，根据用户输入的用户名来加载对应的 `UserDetails` 实例。





### 3 授权过程

在授权阶段，Spring Security 会通过 `UserDetails` 获取用户的权限信息（使用`AuthenticationManager` 将 `UserDetails` 中的密码与用户输入的密码进行比对），进而判断用户是否有权限访问特定资源。

```java
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

// 假设这是认证服务类
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthenticationService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public void authenticateUser(String username, String password) {
        // 创建认证令牌
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

        // 调用认证管理器进行认证
        Authentication authentication = authenticationManager.authenticate(token);

        // 将认证信息存储到安全上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 加载用户详细信息
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 这里Spring Security会调用userDetails的getUsername()和getPassword()方法进行用户验证
        // 实际调用由DaoAuthenticationProvider完成
    }
}
```





## JWT核心类

1. JwtUtils

```java
@Component
public class JwtUtil {
    public static final long JWT_TTL = 60 * 60 * 1000L * 24 * 14;  // 有效期14天
    public static final String JWT_KEY = "SDFGjhdsfalshdfHFdsjkdsfds121232131afasdfac";

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        /// 选择签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        /// 生成用于签名的密钥
        SecretKey secretKey = generalKey();
        /// 获取当前时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        /// 处理有效时长，如果传入的 ttlMillis 为 null，则使用默认的有效时长
        if (ttlMillis == null) {
            ttlMillis = JwtUtil.JWT_TTL;
        }
        /// 计算过期时间
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        /// 构建 JwtBuilder 对象并设置相关信息
        return Jwts.builder()
                /// 设置 JWT 的唯一标识符（JWT ID）.防止 JWT 被重复使用，例如在某些安全机制中，会记录已经使用过的 JWT ID，当再次接收到相同 ID 的 JWT 时，会拒绝处理
                .setId(uuid)
                /// 该方法用于设置 JWT 的主题（Subject）.主题可以帮助验证者明确 JWT 的使用对象。例如，在一个用户认证系统中，JWT 的主题可以是用户的 ID，这样在验证 JWT 时，就可以根据主题来确定是哪个用户的身份信息
                .setSubject(subject)
                /// 此方法用于设置 JWT 的签发者（Issuer），参数 "sg" 是一个字符串，表示签发 JWT 的实体或组织。签发者信息可以帮助验证者判断 JWT 的来源是否可信。
                .setIssuer("sg")
                /// JWT 的签发时间（Issued At），也就是 JWT 被创建的时间。
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, secretKey)
                .setExpiration(expDate);
    }

    public static SecretKey generalKey() {
        //// 1. 对预定义的密钥字符串进行 Base64 解码
        byte[] encodeKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        /// 2. 根据解码后的字节数组创建 SecretKey 对象
        return new SecretKeySpec(encodeKey, 0, encodeKey.length, "HmacSHA256");
    }

    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parserBuilder()
                //// 1 设置用于验证签名的密钥
                .setSigningKey(secretKey)
                /// 2 构建解析器实例
                .build()
                /// 3 解析传入的 JWT 字符串
                .parseClaimsJws(jwt)
                /// 4 获取解析结果中的声明信息
                .getBody();
    }
}

```

2. JwtAuthenticationTokenFilter

```java
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

```

3. SecurityConfig

```java
```

