package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Date:2022/7/19 17:44
 * Author:jyq
 * Description:
 */
public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5加密
    // hello -> abc123def456（低级加密）
    // hello + 3e4a8 -> abc123def456abc （高级加密。在hello后面加上一个随机字符串3e4a8再进行加密，黑客的库里很难有加密后的这一字符串，大大提高安全性）
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {//key为null，空串，或者空格时，都返回null
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /* fastjson */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 25);
        System.out.println(getJSONString(0, "ok", map));
    }

}
