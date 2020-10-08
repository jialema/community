package com.majiale.community.service;

import com.majiale.community.dto.CommentDTO;
import com.majiale.community.enums.CommentTypeEnum;
import com.majiale.community.enums.NotificationStatusEnum;
import com.majiale.community.enums.NotificationTypeEnum;
import com.majiale.community.exception.CustomizeErrorCode;
import com.majiale.community.exception.CustomizeException;
import com.majiale.community.mapper.*;
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

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 功能：插入评论到评论数据库表以及通知数据库表
     * @param comment 评论的信息
     * @param commentator 评论者的用户信息
     */
    @Transactional // 这个注解的作用是如果一条语句出现异常，已经执行的语句造成的结果都回滚到没执行前的状态
    public void insert (Comment comment, User commentator) {
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

            // 回复问题
            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);

            // 增加评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);

            // 创建通知
            createNotify(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question.getId());
        } else {
            // 回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);
            question.setCommentCount(1); // 将问题评论数设为1
            questionExtMapper.incCommentCount(question); // SQL语句在原来数据的基础上加上上述的1

            // 创建通知
            createNotify(comment, question.getCreator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_QUESTION, question.getId());
        }
    }

    /**
     * 功能：将评论信息保存到通知数据库表
     * @param comment 评论类实例，包含评论信息
     * @param receiver 接收者id - 问题或者评论创建者的id
     * @param notifierName 评论人的名字
     * @param outerTitle 被评论的问题名字
     * @param notificationType 评论的枚举类型实例
     * @param outerId 被评论的问题id
     */
    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType, Long outerId) {
        if (receiver == comment.getCommentator()) {
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterid(outerId);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }

    // 根据问题或评论id以及评论类型统计它的所有相关评论，id为问题id或者一级评论id，type表示评论的枚举类型-question或者comment
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
