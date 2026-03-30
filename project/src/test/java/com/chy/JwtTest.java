package com.chy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    @Test
    public void testGen(){
        Map<String,Object> claims=new HashMap<>();
        claims.put("id",1);
        claims.put("username","chy");
        //生成Jwt的代码
        String token= JWT.create()
                .withClaim("username",claims)//添加载荷，改变失效
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60*2))//设置过期时间.过期失效
                .sign(Algorithm.HMAC256("chy"));//指定算法，配置密钥
        System.out.println(token);
    }
    @Test
    public void testParse(){
        String token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJ1c2VybmFtZSI6eyJpZCI6MSwidXNlcm5hbWUiOiJjaHkifSwiZXhwIjoxNzQ4MTcxNTc0fQ." +
                "yjKHhIdmpGTlOssrxwYzLSHWXYJ8S2E82fFQODCcN80";//防篡改失效
        JWTVerifier jwtVerifier= JWT.require(Algorithm.HMAC256("chy")).build();

        DecodedJWT decodeJWT = jwtVerifier.verify(token);//验证token，生成解析后的JWT对象
        Map<String, Claim> claims = decodeJWT.getClaims();
        System.out.println(claims.get("username"));


    }
}
