package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Date:2022/8/8 20:06
 * Author:jyq
 * Description:
 */

@ControllerAdvice(annotations = Controller.class) // annotations = Controller.class表示只扫描带有controller注解的bean，这样该注解可以对所有的controller进行异常的统一处理
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})// 该注解所修饰的方法会在Controller出现异常后被调用，用于处理捕获到的异常
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常：" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        String xRequestedWith = request.getHeader("x-requested-with");// 获取请求的种类
        if ("XMLHttpRequest".equals(xRequestedWith)) {// 若为ajax请求，那么返回时需要以xml或者json形式返回
            // application/plain：返回一个json格式的普通字符串，但浏览器端需要人为地将字符串转为JSON对象，即letter.js中的'data = $.parseJSON(data);'
            response.setContentType("application/plain;charset=utf-8");
            // 而设为application/json时：返回一个json格式的普通字符串，浏览器会自动将其转化为JSON对象，不适用于ajax请求
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常！"));
        } else {// 为普通请求，则返回时以网页返回
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
