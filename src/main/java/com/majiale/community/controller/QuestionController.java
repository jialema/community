package com.majiale.community.controller;

import com.majiale.community.dto.CommentDTO;
import com.majiale.community.dto.QuestionDTO;
import com.majiale.community.mapper.QuestionMapper;
import com.majiale.community.service.CommentService;
import com.majiale.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id, Model model) {

        QuestionDTO questionDTO = questionService.getById(id);

        // 统计某个问题的所有评论
        List<CommentDTO> comments = commentService.listByQuestionId(id);

        // 累加阅读数
        questionService.incView(id);
        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);
        return "question";
    }
}
