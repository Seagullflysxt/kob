package com.kob.backend.consumer.utils;

import com.kob.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;

public class JwtAuthentication {
    public static Integer getUserId(String token) {
        int userId = -1;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = Integer.parseInt(claims.getSubject());//如果能从token解析出来userid就认定合法，否则不合法
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userId;
    }
}
