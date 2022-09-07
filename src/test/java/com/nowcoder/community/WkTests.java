package com.nowcoder.community;

import java.io.IOException;

/**
 * Date:2022/9/7 16:10
 * Author:jyq
 * Description:
 */

public class WkTests {
    public static void main(String[] args) {
        String cmd = "d:/data/javaProjects/nowcoderCommunity/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com d:/data/javaProjects/nowcoderCommunity/community/data/wk-images/3.png";
        try {
            Runtime.getRuntime().exec(cmd);// 异步执行
            System.out.println("ok.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
