package com.miniprogram.miniprogrambackstage.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniprogram.miniprogrambackstage.Constant;
import com.miniprogram.miniprogrambackstage.HttpResponse;
import com.miniprogram.miniprogrambackstage.WxLoginResponse;
import com.miniprogram.miniprogrambackstage.entity.User;
import com.miniprogram.miniprogrambackstage.service.UserService;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;


@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 登录映射
     *
     * @param  code wx.login() 返回值
     * @return  token 和当前登录用户在数据库中存储的用户数据
     * @throws IOException 异常
     */
    @PostMapping("/login")
    public HttpResponse logIn(@RequestBody String code) throws IOException {

        // 后端向其他服务器发送网络请求
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet("https://api.weixin.qq.com/sns/jscode2session");
            // 网络请求附带的参数
            URI uri = new URIBuilder(httpGet.getURI())
                    .addParameter("appid", "wx823dbd53a155fe1e")
                    .addParameter("secret", "af9920e66c5d54856281d3923b933abe")
                    .addParameter("js_code", code)
                    .addParameter("grant_type", "authorization_code")
                    .build();
            httpGet.setURI(uri);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)){
                HttpEntity entity = response.getEntity();

                // 将微信接口服务返回的 json 解析成自定义的 WxLoginResponse ，其中包含 Session_key 和 Openid 两个 String 类型的数据
                WxLoginResponse res = new ObjectMapper().readValue(EntityUtils.toString(entity), WxLoginResponse.class);

                String openid = res.getOpenid();

                // 数据库操作：查询该用户 openid 在 user 表中是否存在，若不存在则为其建立新用户数据
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(User::getOpenid, openid);
                if (userService.getOne(wrapper) == null) {
                    User user = new User(openid,"微信用户", Constant.DEFAULT_AVATAR_PATH,"","","");
                    userService.save(user);
                }
                // 查询该用户类的数据，并返回给前端
                User loginUser = userService.getOne(wrapper);

                String token = JWT.create()
                        .withSubject("MiniProgramBackstage")    // token 主题
                        .withIssuer("SportProgram") // token 的签发者
                        .withAudience("WXMiniProgram")  // token 的受众
                        .withClaim("session_key", res.getSession_key()) // 自定义数据
                        .withClaim("openid", openid)   // 自定义数据
                        .withIssuedAt(new Date(System.currentTimeMillis())) // 签发时间
                        .withExpiresAt(new Date(System.currentTimeMillis() + 3600000L))  // 过期时间
                        .sign(Algorithm.HMAC256("123"));    // 加密算法的秘钥
                HashMap<String, Object> responseMap = new HashMap<>();
                responseMap.put("token", token);
                responseMap.put("userInfo", loginUser);
                return new HttpResponse<>(responseMap, 200);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            httpClient.close();
        }
    }

    /**
     * 上传头像映射
     *
     * @param file 用户头像的临时路径
     * @param token 当前登录用户的token
     * @return 空数据
     * @throws IOException 异常
     */
    @PostMapping("/uploadfile")
    public HttpResponse uploadFile(@RequestParam("avatar") MultipartFile file, @RequestHeader("Authorization") String token) throws IOException {

        // 从 token 中解析出 openid
        String openid = JWT.decode(token).getClaim("openid").asString();

        // 数据库操作：从 user 表中查询出 openid 为当前用户的数据
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        // 将数据保存在类中
        User loginUser = userService.getOne(wrapper);

        // 如果服务器中有该用户的头像，则将旧头像删除
        if (loginUser.getAvatar() != null && !loginUser.getAvatar().isEmpty()) {
            File oldFile = new File("D:\\GraduationProject\\miniProgramAvatar\\" + loginUser.getAvatar());
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }

        String fileName = file.getOriginalFilename();
        // 向服务器中添加用户头像
        File newFile = new File("D:\\GraduationProject\\miniProgramAvatar\\" + fileName);
        file.transferTo(newFile);

        // 更改用户类中头像数据的路径
        loginUser.setAvatar(file.getOriginalFilename());
        // 更新用户类到数据库中
        userService.update(loginUser, wrapper);

        return new HttpResponse<>(fileName,200);
    }

    /**
     * 从服务器本地的指定路径获取文件
     *
     * @param filepath 文件路径
     * @return  文件
     */
    @GetMapping("/getfile/{filepath}")
    public ResponseEntity<Resource> getFile(@PathVariable String filepath){
        File file = new File("D:\\GraduationProject\\miniProgramAvatar\\"+filepath);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition","attachment; filename=" + file.getName());

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new FileSystemResource(file));
    }
}
