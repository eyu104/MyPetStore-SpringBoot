package com.csu.mypetstore.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtUtil {
    //七天过期
    private static long expire = 604800;

    //32位秘钥
    private  static String secret = "abcdefghigklmnopqrstuvwxyzabcdef";

    //生成Token
    public static String generateToken(String username){
        Date now = new Date();
        Date expration = new Date(now.getTime() + 1000 * expire);
        return Jwts.builder()
                .setHeaderParam("type","JWT")
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expration)
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }

    public static Claims getClaimsByToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public static void main(String[] args) {

    }

}
