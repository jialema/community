package com.majiale.community.controller;

import com.majiale.community.dto.AccessTokenDTO;
import com.majiale.community.dto.GithubUser;
import com.majiale.community.mapper.UserMapper;
import com.majiale.community.model.User;
import com.majiale.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 使用GitHub进行授权登录
 */
@Controller
public class AuthorizeController {

    @Autowired  // 通过@component将GithubProvider放在Spring容器中，使用@Autowired将Spring容器中实例加载到当前使用的上下文
    private GithubProvider githubProvider;

    @Value("${github.client.id}") // 取 application.properties 中的值
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback") // @RequestParam 要求参数，如果浏览器中没有这个信息，会出错
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state,
                           HttpServletRequest request) { // Spring会自动把上下文中的request写到这个参数中，
                                                         // 这个参数似乎代表浏览器客户端的请求，http请求头中的信息都
                                                         // 封装在这个对象中，可以通过这个对象提供的方法获得客户端请求的所有信息
        // 创建AccessTokenDTO类，其存储和GitHub交互的数据
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);

        // 和GitHub进行交互，使用数据进行交互
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);

        // 根据返回的用户信息
        if(githubUser != null) {
            User user = new User();
            user.setToken(UUID.randomUUID().toString());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setName(githubUser.getName());
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);

            // 登陆成功，写cookie和session

             request.getSession().setAttribute("user", githubUser); // 将用户信息写入session，因此前端可以访问这个信息
            return "redirect:/"; // 重定向到根目录
        }else {
            // 登录失败，重新登录
        }
        return "redirect:/";

    }
}
