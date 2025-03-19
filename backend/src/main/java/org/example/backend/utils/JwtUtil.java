package org.example.backend.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;


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
