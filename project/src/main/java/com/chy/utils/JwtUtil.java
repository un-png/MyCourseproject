package com.chy.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT工具类，用于生成、解析和验证JWT令牌
 */
@Component
public class JwtUtil {
    // 密钥，用于签名和验证JWT
    private static final String SECRET_KEY = "your-secret-key";
    // 令牌过期时间，单位为毫秒（24小时）
    private static final long EXPIRATION_TIME = 86400000; // 24小时

    /**
     * 生成JWT令牌
     *
     * @param username 用户名
     * @return 返回生成的JWT令牌
     */
    public static String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    /**
     * 从JWT令牌中提取用户名
     *
     * @param token JWT令牌
     * @return 返回令牌中的用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * 验证JWT令牌是否有效
     *
     * @param token JWT令牌
     * @return 如果令牌有效返回true，否则返回false
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
