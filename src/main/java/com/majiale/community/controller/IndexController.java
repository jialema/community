package com.majiale.community.controller;

import com.majiale.community.mapper.UserMapper;
import com.majiale.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 主页面，返回index.html
 */
@Controller     // 把当前的类作为路由API的承载着
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    // @ResponseBody 这个注解的作用是直接使用“index”字符串作为页面，不是使用index.html
    @GetMapping("/")
    public String index(HttpServletRequest request) {
        // 服务器重启，网络掉线，导致的重新连接，因为浏览器没关，cookie一直保存，所以可以继续保持登录状态
        Cookie[] cookies = request.getCookies();
        if (cookies == null) { return "index"; } // 第一次打开浏览器cookies为空
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("token")) {
                String token = cookie.getValue();
                User user = userMapper.findByToken(token);
                if (user != null) {
                    request.getSession().setAttribute("user", user);
                }
            }
        }

        return "index";
    }
}
