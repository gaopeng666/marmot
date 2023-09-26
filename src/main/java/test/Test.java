package test;

import cn.hutool.jwt.JWT;

import java.time.ZoneId;
import java.util.TimeZone;

public class Test {
    public static void main(String[] args) {
        // 密钥
        byte[] key = "1234567890".getBytes();

        String token = JWT.create()
                .setPayload("sub", "1234567890")
                .setPayload("name", "looly")
                .setPayload("admin", true)
                .setKey(key)
                .sign();
    }
}
