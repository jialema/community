package com.majiale.community.service;

import com.majiale.community.dto.PaginationDTO;
import com.majiale.community.dto.QuestionDTO;
import com.majiale.community.mapper.QuestionMapper;
import com.majiale.community.mapper.UserMapper;
import com.majiale.community.model.Question;
import com.majiale.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据页面编号和每个页面的问题数来从数据库读取问题进行显示
     * page：页编号
     * size：每页问题数
     * PaginationDTO：
     */
    public PaginationDTO list(Integer page, Integer size) {
        // 页面显示相关数据类
        PaginationDTO paginationDTO = new PaginationDTO();

        Integer totalPage;
        // 统计数据库有多少条问题记录
        Integer totalCount = questionMapper.count();

        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }

        // 根据总页数和当前页编号来设置显示相关参数
        paginationDTO.setPagination(totalPage, page);

        // size * (page - 1)
        Integer offset = size * (page - 1);
        // 从数据库查询相关数据
        List<Question> questions = questionMapper.list(offset, size);

        // 要显示的问题列表，QuestionDTO和question的区别是多了一个User类变量
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        // 为每个问题匹配到相关的用户信息
        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO); // 快速question的属性值复制到questionDTO的属性上

            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        // 将需要显示的问题列表放在页面显示数据类中
        paginationDTO.setQuestions(questionDTOList);

        return paginationDTO;
    }

    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        // 页面显示相关数据类
        PaginationDTO paginationDTO = new PaginationDTO();

        Integer totalPage;
        Integer totalCount = questionMapper.countByUserId(userId);

        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }

        paginationDTO.setPagination(totalPage, page);

        // size * (page - 1)
        Integer offset = size * (page - 1);
        // 从数据库查询相关数据
        List<Question> questions = questionMapper.listByUserId(userId, offset, size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        // 对从数据库查询到当前页面应该显示数据的解析
        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO); // 快速question的属性值复制到questionDTO的属性上

            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        // 将需要显示的问题列表放在页面显示数据类中
        paginationDTO.setQuestions(questionDTOList);

        return paginationDTO;
    }

    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.getById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO); // 快速question的属性值复制到questionDTO的属性上
        User user = userMapper.findById(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }
}
