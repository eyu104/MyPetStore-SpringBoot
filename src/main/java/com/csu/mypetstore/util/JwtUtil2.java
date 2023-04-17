package com.csu.mypetstore.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil2 {
    //创建默认的秘钥和算法，供无参的构造方法使用
    private static final String defaultbase64EncodedSecretKey = "UvvMlt1B96H1TprgC$SKShGMKHTF#Nm1";
    private static final SignatureAlgorithm defaultsignatureAlgorithm = SignatureAlgorithm.HS256;

    public JwtUtil2() {
        this(defaultbase64EncodedSecretKey,defaultsignatureAlgorithm);
    }

    private final String base64EncodedSecretKey;
    private final SignatureAlgorithm signatureAlgorithm;

    @Autowired
    private RedisUtil redisUtil;

    public JwtUtil2(String secretKey, SignatureAlgorithm signatureAlgorithm){
        this.base64EncodedSecretKey = Base64.encodeBase64String(secretKey.getBytes());
        this.signatureAlgorithm = signatureAlgorithm;
    }

    /*
     * @Description: 生成jwt字符串
     * jwt字符串包括三个部分
     *  1. header
     *      -当前字符串的类型，一般都是“JWT”
     *      -哪种算法加密，“HS256”或者其他的加密算法
     *      所以一般都是固定的，没有什么变化
     *  2. payload
     *      一般有四个最常见的标准字段（下面有）
     *      iat：签发时间，也就是这个jwt什么时候生成的
     *      jti：JWT的唯一标识
     *      iss：签发人，一般都是username或者userId
     *      exp：过期时间
     * @Author: LZY
     * @Date: 2021/4/19 21:54
     * @Params:
     * @Return:
     */
    public String encode(String iss, long ttlMillis, Map<String, Object> claims) {
        //iss签发人，ttlMillis生存时间，claims是指还想要在jwt中存储的一些非隐私信息
        if (claims == null) {
            claims = new HashMap<>();
        }
        long nowMillis = System.currentTimeMillis();

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())//2. 这个是JWT的唯一标识，一般设置成唯一的，这个方法可以生成唯一标识
                .setIssuedAt(new Date(nowMillis))//1. 这个地方就是以毫秒为单位，换算当前系统时间生成的iat
                .setSubject(iss)//3. 签发人，也就是JWT是给谁的（逻辑上一般都是username或者userId）
                .signWith(signatureAlgorithm, base64EncodedSecretKey);//这个地方是生成jwt使用的算法和秘钥
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);//4. 过期时间，这个也是使用毫秒生成的，使用当前时间+前面传入的持续时间生成
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    /*
     * @Description: encode的反方向，传入jwtToken生成对应的username和password等字段。Claim就是一个map，存储的是拿到荷载部分所有的键值对
     * @Author: LZY
     * @Date: 2021/4/19 21:57
     * @Params: [jwtToken]
     * @Return: io.jsonwebtoken.Claims
     */
    public Claims decode(String jwtToken) {

        // 得到 DefaultJwtParser
        return Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(base64EncodedSecretKey)
                // 设置需要解析的 jwt
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    //判断jwtToken是否合法
    public boolean isVerify(String jwtToken) {

        //这个是官方的校验规则，这里只写了一个”校验算法“，可以自己加
        Algorithm algorithm = null;
        switch (signatureAlgorithm) {
            case HS256:
                algorithm = Algorithm.HMAC256(Base64.decodeBase64(base64EncodedSecretKey));
                break;
            default:
                throw new RuntimeException("not support this algorithm");
        }
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            verifier.verify(jwtToken);  // 校验不通过会抛出异常
            //判断合法的标准：1. 头部和荷载部分没有篡改过。2. 没有过期
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /*
     * @Description: 将token存入cache
     * @Author: LZY
     * @Date: 2021/4/24 14:09
     * @Params:
     * @Return:
     */
    public void addTokenToCache(Map<Object, Object> token){
        String jwtToken = (String) token.get("token");
        Claims claims = decode(jwtToken);
        String userId = (String) claims.get("username");
        Date date = claims.getExpiration();
        long nowMillis = System.currentTimeMillis();
        long expMillis = date.getTime() - nowMillis;
        if (expMillis > 0){
            String key = userId + "token";
            Map<String, Object> value = new HashMap<>();
            value.put("token", jwtToken);
            redisUtil.hmset(key, value, expMillis/1000);
        }
    }

    /*
     * @Description: 根据用户id获得用户存储在redis中的token相关信息
     * @Author: LZY
     * @Date: 2021/4/24 14:37
     * @Params:
     * @Return:
     */
    public Map<Object, Object> getTokenFromCache(String userId){
        String key = userId + "token";
        return redisUtil.hmget(key);
    }

    /*
     * @Description: 如何要将token设置失效，则将该token从redis中删除
     * @Author: LZY
     * @Date: 2021/4/25 10:59
     * @Params:
     * @Return:
     */
    public void deleteToken(String userId){
        String key = userId + "token";
        redisUtil.deleteKey(key);
    }
}
