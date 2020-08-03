package com.majiale.community.dto;

import com.majiale.community.model.User;
import lombok.Data;

/**
 * QuestionDTO相比Question类多了一个User类的变量属性，
 * 所以这个类的作用是将question和user联系起来
 */
@Data
public class QuestionDTO {
    private Integer id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private User user;
}
