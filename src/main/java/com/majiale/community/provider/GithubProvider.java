package com.majiale.community.provider;

import com.alibaba.fastjson.JSON;
import com.majiale.community.dto.AccessTokenDTO;
import com.majiale.community.dto.GithubUser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component // 把当前类初始化到Spring容器的上下文
@Slf4j
public class GithubProvider {
    // 携带数据获取accessToken,通过post请求并返回accessToken
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        // 使用alibaba的fastjson
        RequestBody body = RequestBody.create(JSON.toJSONBytes(accessTokenDTO), mediaType);

        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)  // 这里的body貌似就是get请求中的？后使用&分隔的参数
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String token = string.split("&")[0].split("=")[1];
            return token;

        } catch (Exception e) {
            log.error("getAccessToken error,{}", accessTokenDTO, e);
        }
        return null;
    }


    // 携带accessToken,通过get请求并返回用户信息
    public GithubUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
//        System.out.println("https://api.github.com/user?access_token=" + accessToken);
        Request request = new Request.Builder()
//                .url("https://api.github.com/user?access_token=" + accessToken)
                .url("https://api.github.com/user")
                .header("Authorization", "token " + accessToken)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            // 将string的json解析成java的类对象，兼容将下划线变量匹配到驼峰命名变量
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
