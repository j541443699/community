package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Date:2022/7/12 18:12
 * Author:jyq
 * Description:
 */

/**
 * 用于演示一些小demo
 */

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot.";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    @RequestMapping("/http")
    //在方法里声明HttpServletRequest和HttpServletResponse两个类型的参数，dispatcherServlet调用该方法时，会自动将从底层就创建好的两个类型的对象传到该方法的参数中
    //之所以没有返回值，是因为通过response对象就可以直接向浏览器输出数据，而不需要返回值
    public void http(HttpServletRequest request, HttpServletResponse response) {
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());//获取请求路径
        Enumeration<String> enumeration = request.getHeaderNames();//获取请求头中的键
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);//获取请求头中键对应的值
            System.out.println(name + ": " + value);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");//网页类型的文本
        try (//圆括号，java7的新语法，将writer写进括号中时，编译的时候try/catch结构后面会自动添加finally，在finally中将writer进行close
                PrintWriter writer = response.getWriter();//response封装了一个向浏览器输出的输出流
        ){
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* SpringMVC用法 */
    // GET请求
    // 法一：/students?current=1&limit=20
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {//在不加注解@RequestParam的情况下，若方法中的参数名和请求路径中的参数名一致，就可以直接将请求路径中相应的值传入方法的参数中
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // 法二：/student/123
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }

    // POST请求
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age) {//方法参数名和表单中数据名一致，则会自动传过来
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    // 响应字符串，直接参见GET请求和POST请求

    // 响应HTML数据
    // 法一
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "张三");
        mav.addObject("age", 30);
        mav.setViewName("/demo/view");
        return mav;//返回给dispatcherServlet，model和view通过模板引擎进行网页渲染
    }

    // 法二
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name", "北京大学");
        model.addAttribute("age", 80);
        return "/demo/view";//view返回给dispatcherServlet，而dispatcherServlet本身又有方法中参数model的引用，所以dispatcherServlet依然可以将model和view通过模板引擎进行网页渲染
    }

    // 响应JSON数据（异步请求）
    // 浏览器用面向对象的JS语言解析对象，服务器的Java对象不可能直接转为JS对象，那么通过JSON字符串可以将Java对象和JS对象进行兼容，
    // 具体为服务器将Java对象转为JSON字符串发送给浏览器，然后浏览器将JSON字符串转为JS对象（Java对象 -> JSON字符串 -> JS对象）
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        return emp;//DispatcherServlet调用该方法时，看到注解@ResponseBody和返回类型Map<String, Object>，会自动将emp转成一个JSON字符串发送给浏览器
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "李四");
        emp.put("age", 24);
        emp.put("salary", 9000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "王五");
        emp.put("age", 25);
        emp.put("salary", 10000.00);
        list.add(emp);

        return list;
    }

    /* Cookie示例 */
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        // 创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置cookie生效的范围，只有设置的路径下客户端才会发cookie
        cookie.setPath("/community/alpha");
        // 设置cookie的生存时间，单位是秒
        cookie.setMaxAge(60 * 10);
        // 发送cookie
        response.addCookie(cookie);

        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {//@CookieValue("code")：从cookie中取key为code的值，赋给code参数
        System.out.println(code);
        return "get cookie";
    }

    /* session示例 */
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {// 在setSession方法中声明HttpSession参数，springMVC可以自动帮我们创建session并注入进来
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");
        return "set session";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    // ajax示例
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0, "操作成功！");
    }

}
