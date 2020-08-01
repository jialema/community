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
     */
    public PaginationDTO list(Integer page, Integer size) {
        // 页面显示相关数据类
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = questionMapper.count();
        paginationDTO.setPagination(totalCount, page, size);
        if (page < 1) {
            page = 1;
        }
        if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        // size * (page - 1)
        Integer offset = size * (page - 1);
        // 从数据库查询相关数据
        List<Question> questions = questionMapper.list(offset, size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();


        // 对从数据库查询到当前页面应该显示数据的解析
        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO); // 快速question的属性值复制到questionDTO的属性上

            // 这段代码是为了解决图像显示问题
            String avatarUrl = user.getAvatarUrl();
            String newAvatarUrl = avatarUrl.replace("avatars2", "avatars5");
            user.setAvatarUrl(newAvatarUrl);

            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        // 将需要显示的问题列表放在页面显示数据类中
        paginationDTO.setQuestions(questionDTOList);

        return paginationDTO;
    }
}
