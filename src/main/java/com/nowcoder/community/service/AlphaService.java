package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Date:2022/7/12 20:04
 * Author:jyq
 * Description:
 */
@Service
// @Scope("prototype") //容器管理bean的作用域，默认是singleton，即单例，多例的话prototype
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public AlphaService() {
        System.out.println("实例化AlphaService");
    }

    @PostConstruct //表示下面的方法在构造器之后使用
    public void init() {
        System.out.println("初始化AlphaService");
    }

    @PreDestroy //表示在销毁对象之前调用下面的方法
    public void destroy() {
        System.out.println("销毁AlphaService");
    }

    public String find() {
        return alphaDao.select();
    }
}
