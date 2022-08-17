package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date:2022/7/24 14:16
 * Author:jyq
 * Description:
 */

@Target(ElementType.METHOD) // 声明自定义注解可以写在方法上
@Retention(RetentionPolicy.RUNTIME) // 声明自定义注解在程序运行的时候才有效
public @interface LoginRequired {
}
