package com.majiale.community.mapper;

import com.majiale.community.dto.QuestionQueryDTO;
import com.majiale.community.model.Question;
import com.majiale.community.model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionExtMapper {
    int incView(Question record); // 浏览数

    int incCommentCount(Question record); // 评论数

    List<Question> selectRelated(Question question);

    Integer countBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);
}