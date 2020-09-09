package com.majiale.community.controller;

import com.majiale.community.dto.CommentCreateDTO;
import com.majiale.community.dto.CommentDTO;
import com.majiale.community.dto.ResultDTO;
import com.majiale.community.enums.CommentTypeEnum;
import com.majiale.community.exception.CustomizeErrorCode;
import com.majiale.community.mapper.CommentMapper;
import com.majiale.community.model.Comment;
import com.majiale.community.model.User;
import com.majiale.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @ResponseBody // 这个注解自动将返回的对象序列化成json格式，发送到前端
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request) { // 拟采用json格式进行内容传输

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }

        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY); // 返回的信息在community.js中的success中获得
        }

        // 获取前端的评论信息
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId()); // 评论所属的问题id
        comment.setContent(commentCreateDTO.getContent()); // 评论的内容
        comment.setType(commentCreateDTO.getType()); // 评论的类型，一级评论还是二级评论
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(user.getId()); // 评论的用户id
        comment.setLikeCount(0L); // 点赞数
        commentService.insert(comment);
        return ResultDTO.okOf();
    }

    /**
     * 以下代码是二级评论展开按钮的响应
     * List<CommentDTO>是泛型类的类型参数
     */
    @ResponseBody
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable(name = "id") Long id) {
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }

}
