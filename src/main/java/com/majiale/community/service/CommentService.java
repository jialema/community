package com.majiale.community.service;

import com.majiale.community.dto.CommentDTO;
import com.majiale.community.enums.CommentTypeEnum;
import com.majiale.community.exception.CustomizeErrorCode;
import com.majiale.community.exception.CustomizeException;
import com.majiale.community.mapper.CommentMapper;
import com.majiale.community.mapper.QuestionExtMapper;
import com.majiale.community.mapper.QuestionMapper;
import com.majiale.community.mapper.UserMapper;
import com.majiale.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 创建CommentService的原因是，因为既需要往comment表中插入数据，又要往question表中插入数据，所以将它们封装起来。
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional // 这个注解的作用是如果一条语句出现异常，已经执行的语句造成的结果都回滚到没执行前的状态
    public void insert (Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        // 根据评论类型来插入评论或者抛异常，判断当前评论是回复问题还是回复评论
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            // 回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
        } else {
            // 回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1); // 将问题评论数设为1
            questionExtMapper.incCommentCount(question); // SQL语句在原来数据的基础上加上上述的1
        }
    }

    // 根据问题id以及评论类型统计它的所有相关评论
    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_create desc"); // 按照时间倒序排序
        List<Comment> comments = commentMapper.selectByExample(commentExample); // 根据问题id和评论类型来选择所有相关评论

        if (comments.size() == 0) {
            return new ArrayList<>();
        }

        // 获取去重的评论人，stream().map 相当于简化了for循环
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators); // 获取所有的评论人id（去重后）

        // 获取评论人并转换为Map
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        // 转换comment为commentDTO，避免了既需要循环userIds又要循环comments的两层for循环
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator())); // 使用map根据用户id获取用户完整信息
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
