package com.itcrud.common.jwt;

import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Joker
 * @Desc:
 * @Date: 2018/12/18 19:39
 * @Modified By:
 * @Project_name: itcrud-commons
 * @Version 1.0
 */
public class JWTUtils {

    private final static String SECRET = "joker";//秘钥
    private final static String CLAIMS_KEY = "claims";
    private final static String OUT_EXPIRE = "OUT_EXPIRE";
    private final static String ALGORITHM_BASE64 = "AES";
    private final static String DEFAULT_ISSUER = "SYS";
    private final static String DEFAULT_SUBJECT = "JWT";

    //加密
    public static String encrypt(String claims, long ttlSecond, String issuer, String subject) {
        Map<String, Object> c = new HashMap<>();
        c.put(CLAIMS_KEY, claims);
        JwtBuilder jwtBuilder = Jwts.builder().setClaims(c)
                .setIssuedAt(new Date())
                .setIssuer(StringUtils.isNotBlank(issuer) ? issuer : DEFAULT_ISSUER)
                .setSubject(StringUtils.isNotBlank(subject) ? subject : DEFAULT_SUBJECT)
                .signWith(SignatureAlgorithm.HS256, generateKey())
                .setExpiration(ttlSecond >= 0 ? new Date(new Date().getTime() + ttlSecond * 1000) : new Date());
        return jwtBuilder.compact();
    }

    //解密
    public static String parse(String jwt) {
        try {
            Claims body = Jwts.parser().setSigningKey(generateKey()).parseClaimsJws(jwt).getBody();
            return (String) body.get(CLAIMS_KEY);
        } catch (ExpiredJwtException e) {
            return OUT_EXPIRE;
        }
    }

    //检查是否正确
    public static boolean check(String jwt) {
        return !OUT_EXPIRE.equals(parse(jwt));
    }

    //生成秘钥
    private static SecretKey generateKey() {
        byte[] byteSecret = Base64.decodeBase64(SECRET);
        return new SecretKeySpec(byteSecret, 0, byteSecret.length, ALGORITHM_BASE64);
    }

    //测试方法
    public static void main(String[] args) {
        String value1 = encrypt("{'user':'qq'}", 10, "Joker", "Joker测试");
        System.out.println(value1);
        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String value = parse(value1);
        System.out.println(value);
        System.out.println(check(value1));
    }
}
