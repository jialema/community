package com.majiale.community.dto;

import com.majiale.community.model.User;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id; // 评论id
    private Long parentId; // 问题id
    private Integer type; // 评论的类型，是一级评论还是二级评论
    private Long commentator; // 评论人id
    private Long gmtCreate; // 创造时间
    private Long gmtModified; // 修改时间
    private Long likeCount; // 点赞数
    private Integer commentCount; // 评论数
    private String content; // 评论内容
    private User user; // 评论人
}
