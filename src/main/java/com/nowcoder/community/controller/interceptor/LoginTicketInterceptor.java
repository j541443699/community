package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Date:2022/7/22 15:38
 * Author:jyq
 * Description:
 */

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");

        if (ticket != null) {
            // 查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 检查凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求中持有用户
                hostHolder.setUser(user); //多线程相关，实现线程隔离

                // 构建用户认证的结果，并存入SecurityContext，以便于Security进行授权。
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user.getId()));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            // 用于在模板视图上显示用户数据，传给index.html
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 在请求结束时清理用户数据
        hostHolder.clear();
        // 清理security所需的token
        // SecurityContextHolder.clearContext();// 这里应该注释掉
        // 1.因为首先，在浏览器登录页面输入管理员账号密码，然后发起登录请求（这里Filter对该访问路径直接放行）时，
        // 先进行拦截处理，此时ticket为null，也未生成Authentication，之后在处理登录请求，登录成功同时生成一个新的ticket，之后便进入首页。
        // 2.然后，在首页中点进某个帖子时又发起一次请求（这里Filter对该访问路径直接放行），进行拦截处理，此时ticket不为null，
        // 生成Authentication存入SecurityContext，之后在处理查看帖子详情请求，之后进入帖子详情页面。
        // 3.若没注释掉前面的代码，那么前面处理完查看帖子详情的请求后，SecurityContext会清空，
        // 那么点击置顶按钮时，spring security会读取SecurityContext，发现没有认证信息，那么就会给浏览器返回“你还没有登录哦！”的提示。
        // 4.所以，应该注释掉前面的'SecurityContextHolder.clearContext();'。
    }
}
