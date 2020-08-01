package com.majiale.community.controller;

import com.majiale.community.dto.PaginationDTO;
import com.majiale.community.dto.QuestionDTO;
import com.majiale.community.mapper.QuestionMapper;
import com.majiale.community.mapper.UserMapper;
import com.majiale.community.model.Question;
import com.majiale.community.model.User;
import com.majiale.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 主页面，返回index.html
 */
@Controller     // 把当前的类作为路由API的承载着
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    // @ResponseBody 这个注解的作用是直接使用“index”字符串作为页面，不是使用index.html
    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size) {
        // 服务器重启，网络掉线，导致的重新连接，因为浏览器没关，cookie一直保存，所以可以继续保持登录状态
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0)
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    User user = userMapper.findByToken(token);
                    if (user != null) {
                        request.getSession().setAttribute("user", user);
                    }
                }
            }

        // 根据页面来设置问题的显示
        PaginationDTO pagination = questionService.list(page, size);

        // 将数据放在前端可访问的位置
        model.addAttribute("pagination", pagination);
        return "index";
    }
}
