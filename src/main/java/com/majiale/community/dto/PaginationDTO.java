package com.majiale.community.dto;

import com.majiale.community.model.Question;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面显示问题类
 */
@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;  //
    private boolean showPrevious;   // 是否显示前一页编号
    private boolean showFirstPage;  // 是否显示第一页编号
    private boolean showNext;       // 是否显示下一页编号
    private boolean showEndPage;    // 是否显示最后页编号
    private Integer page;           // 当前页编号
    private List<Integer> pages = new ArrayList<>(); // 当前页可显示的页编号列表
    private Integer totalPage;      // 总页数

    public void setPagination(Integer totalPage, Integer page) {
        this.totalPage = totalPage;
        this.page = page;

        pages.add(page);
        for (int i = 1; i <= 3; i++) {
            if (page - i > 0) {
                pages.add(0, page - i);
            }
            if (page + i <= totalPage) {
                pages.add(page + i);
            }
        }

        // 是否展示上一页
        if (page == 1) {
            showPrevious = false;
        } else {
            showPrevious = true;
        }

        // 是否展示下一页
        if (page == totalPage) {
            showNext = false;
        } else {
            showNext = true;
        }

        // 是否展示第一页
        if (pages.contains(1)) {
            showFirstPage = false;
        } else {
            showFirstPage = true;
        }

        // 是否展示最后一页
        if (pages.contains(totalPage)) {
            showEndPage = false;
        } else {
            showEndPage = true;
        }
    }
}
