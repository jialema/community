package com.majiale.community.controller;

import com.majiale.community.dto.PaginationDTO;
import com.majiale.community.dto.QuestionDTO;
import com.majiale.community.mapper.UserMapper;
import com.majiale.community.model.Question;
import com.majiale.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
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
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size,
                        @RequestParam(name = "search", required = false) String search) {

        // 根据页面来设置问题的显示，page和size都通过URL传入
        PaginationDTO pagination = questionService.list(search, page, size);

        // 将数据放在前端可访问的位置
        model.addAttribute("pagination", pagination);
        model.addAttribute("search", search);
        return "index";
    }
}
