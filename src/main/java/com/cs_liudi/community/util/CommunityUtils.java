package com.cs_liudi.community.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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

    /**
     * 判断是否为允许的上传文件类型,true表示允许
     */
    public static boolean checkFile(String fileName) {
        //设置允许上传文件类型
        String suffixList = "jpg,png,ico,bmp,jpeg";
        // 获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (suffixList.contains(suffix.trim().toLowerCase())) {
            return true;
        }
        return false;
    }

//    public static void main(String[] args) {
//        Map<String,Object> map = new HashMap<>();
//        map.put("name","张三");
//        map.put("age",30);
//        String res = getJSONString(0,"操作成功",map);
//        System.out.println(res);
//    }
//    /**
//     * 判断是否为允许的上传文件类型,true表示允许
//     */
//    public static boolean checkFileSize(Long size,int sizeLimit) {
//        return size <= sizeLimit;
//    }

}
