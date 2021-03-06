package com.majiale.community.controller;

import com.majiale.community.dto.PaginationDTO;
import com.majiale.community.model.User;
import com.majiale.community.service.NotificationService;
import com.majiale.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    /**
     * 个人信息显示界面 /profile/{action}
     * @param page 页面编号
     * @param size 页面问题数
     * @return profile.html
     */
    @GetMapping("/profile/{action}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "action") String action,
                          Model model,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size) {
        // 服务器重启，网络掉线，导致的重新连接，因为浏览器没关，cookie一直保存，所以可以继续保持登录状态
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        // 根据个人信息页面的属性action来设置前端数据
        if ("questions".equals(action)) {
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的问题");
            // 将当前用户的所有问题按页面获取
            PaginationDTO paginationDTO = questionService.list(user.getId(), page, size);
            // 将数据放在前端可访问的位置
            model.addAttribute("pagination", paginationDTO);
        } else if ("replies".equals(action)) { // 最新回复或通知界面
            PaginationDTO paginationDTO = notificationService.list(user.getId(), page, size);
            model.addAttribute("section", "replies");
            model.addAttribute("pagination", paginationDTO);
            model.addAttribute("sectionName", "最新回复");
        }

        return "profile";
    }
}
