package com.majiale.community.controller;

import com.majiale.community.dto.NotificationDTO;
import com.majiale.community.enums.NotificationTypeEnum;
import com.majiale.community.model.Notification;
import com.majiale.community.model.User;
import com.majiale.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 根据profile.html中点击选中的回复项进行跳转处理
     */
    @GetMapping("/notification/{id}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "id") Long id) {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        // 根据当前用户和选中的问题id读取通知项
        NotificationDTO notificationDTO = notificationService.read(id, user);

        // 根据被回复的问题id进行跳转
        if (NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType() ||
                NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()) {
            return "redirect:/question/" + notificationDTO.getOuterid();
        } else {
            return "redirect:/";
        }
    }
}
