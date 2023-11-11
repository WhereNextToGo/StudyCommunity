package com.cs_liudi.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtils {
    //获取UUID
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }
    //MD5加密
    public static String MD5(String key){
        if (StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
