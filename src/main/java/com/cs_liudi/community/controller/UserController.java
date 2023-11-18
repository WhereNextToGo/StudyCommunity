package com.cs_liudi.community.controller;

import com.cs_liudi.community.LoginRequired;
import com.cs_liudi.community.entity.CommunityConstant;
import com.cs_liudi.community.entity.User;
import com.cs_liudi.community.service.UserService;
import com.cs_liudi.community.util.CommunityUtils;
import com.cs_liudi.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Value("${server.servlet.context-path}")
    private String pathContext;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.uplaod}")
    private String uploadPath;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping("/setting")
    public String getUserSetting(){
        return "site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null){
            model.addAttribute("error","不能传输空文件");
            return "/site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        if (!CommunityUtils.checkFile(filename)){
            model.addAttribute("error","图片格式错误,图片格式应该为jpg,png,ico,bmp,jpeg");
            return "/site/setting";
        }
//        long size = headerImage.getSize();

        String suffix = filename.substring(filename.lastIndexOf("."));

        filename = CommunityUtils.generateUUID()+"."+suffix;
        String filePath = uploadPath + '/' +filename;
        File file = new File(filePath);
        try {
            headerImage.transferTo(file);
        } catch (IOException e) {
            logger.error("文件上传错误" + e.getMessage());
            throw new RuntimeException("文件上传错误,服务器发生异常",e);
        }
        User user = hostHolder.getUser();
        String webFilePath = domain+pathContext+"/user/header/"+filename;
        userService.setuserHeader(user.getId(),webFilePath);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response){
//        User user = hostHolder.getUser();
        filename = uploadPath + '/' + filename;
        String suffix = filename.substring(filename.lastIndexOf("."));
        response.setContentType("image/"+suffix);
        try (
                ServletOutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(filename);
                ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("获取头像失败" + e.getMessage());
//            throw new RuntimeException("获取头像失败,服务器发生异常",e);
        }

    }
    @LoginRequired
    @RequestMapping(path = "/resetPassword", method = RequestMethod.POST)
    public String settingPassword(String oldPassword, String newPassword, String confirmPassword, Model model){
        HashMap<String, Object> map = userService.changePassword(oldPassword, newPassword, confirmPassword);
        if (map == null){
            return "redirect:/index";
        }else{
            model.addAttribute("oldPasswordMsg",map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg",map.get("newPasswordMsg"));
            model.addAttribute("confirmPasswordMsg",map.get("confirmPasswordMsg"));
            return "/site/setting";
        }
    }

}
