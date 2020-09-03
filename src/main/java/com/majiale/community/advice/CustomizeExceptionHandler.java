package com.majiale.community.advice;

import com.alibaba.fastjson.JSON;
import com.majiale.community.dto.ResultDTO;
import com.majiale.community.exception.CustomizeErrorCode;
import com.majiale.community.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @ControllerAdvice 和 @ExceptionHandler 配置进行异常处理
 * 这个类似乎不能处理路径异常，比如localhost:8887/mess
 * 该异常通过CustomizeController处理
 */
@ControllerAdvice
public class CustomizeExceptionHandler {

    @ExceptionHandler(Exception.class) // 所有的Exception都要处理，这里的Exception.class应该是YourException.class
    ModelAndView handle(HttpServletRequest request, Throwable e, Model model, HttpServletResponse response) {
        // 添加contentType判断，是因为使用postman工具进行调试，希望返回json格式的数据，而不是返回一个error页面
        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {
            ResultDTO resultDTO;
            // 返回 JSON
            if (e instanceof CustomizeException) {
                resultDTO = ResultDTO.errorOf((CustomizeException) e);
            } else {
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            }
            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("utf-8");
                PrintWriter writer = response.getWriter(); // 给前端写值
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioe) {
            }
            return null;
        } else {
            // 错误页面跳转
            if (e instanceof CustomizeException) {
                model.addAttribute("message", "傻逼");
            } else {
                model.addAttribute("message", "服务冒烟了，要不然你稍后试试！");
            }
            return new ModelAndView("error");
        }
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
