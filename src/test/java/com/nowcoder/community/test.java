package com.nowcoder.community;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

/**
 * Date:2022/7/24 12:46
 * Author:jyq
 * Description:
 */
public class test {

    @Test
    public void substringTest() {
        String fileName = "123.png";
        int index = fileName.lastIndexOf(".");
        String suffix = fileName.substring(index);
        if(StringUtils.isBlank(suffix)) {
            System.out.println(suffix);
            System.out.println("1");
        } else {
            System.out.println(suffix);
            System.out.println("2");
        }
    }


}
