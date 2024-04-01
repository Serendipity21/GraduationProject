package com.miniprogram.miniprogrambackstage.controller;
import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.miniprogram.miniprogrambackstage.HttpResponse;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.http.HttpHeaders;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@RestController
public class StepController {
    @GetMapping("/getstep")
    public HttpResponse getStep(String encryptedDataStr, String ivStr, @RequestHeader("Authorization") String token)  {

        // 将 session_key 从 token 中解密并转为 base64
        byte[] secret = Base64.getDecoder().decode(JWT.decode(token).getClaim("session_key").asString());
        byte[] iv = Base64.getDecoder().decode(ivStr);
        byte[] encryptedData = Base64.getDecoder().decode(encryptedDataStr);

        try{
            // java 解密算法
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret,"AES");
            IvParameterSpec ivSpec =  new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec,ivSpec);
            String res = new String(cipher.doFinal(encryptedData));
            return new HttpResponse(res, 200);
        }catch (Exception e) {
            return new HttpResponse("错误", 400);
        }
    }
}
