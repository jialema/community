package com.majiale.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 主页面，返回index.html
 */
@Controller     // 把当前的类作为路由API的承载着
public class IndexController {
    // @ResponseBody 这个注解的作用是直接使用“index”字符串作为页面，不是使用index.html
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
