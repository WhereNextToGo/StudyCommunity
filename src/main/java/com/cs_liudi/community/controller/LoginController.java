package com.cs_liudi.community.controller;

import com.cs_liudi.community.entity.CommunityConstant;
import com.cs_liudi.community.entity.LoginTicket;
import com.cs_liudi.community.entity.User;
import com.cs_liudi.community.service.UserService;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private Producer producer;

    @Value("{server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRgister(){
        return "/site/register";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMessage",map.get("usernameMessage"));
            model.addAttribute("passwordMessage",map.get("passwordMessage"));
            model.addAttribute("emailMessage",map.get("emailMessage"));
            return "/site/register";
        }
    }
    @RequestMapping(path = "/activation/{id}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("id") int id, @PathVariable("code") String activationCode){
        int result = userService.activation(id,activationCode);
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的帐号可以正常使用了！");
            model.addAttribute("target","/login");
        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作,该邮箱已经被激活过了！");
            model.addAttribute("target","/index");
        }else{
            model.addAttribute("msg","激活失败，您提供的激活码不正确！");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String login(){
        return "/site/login";
    }

    @RequestMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        //往session里存入验证码
        session.setAttribute("kaptcha",text);
        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
        } catch (Exception e) {
           logger.error("响应验证码失败"+e.getMessage());
        }
    }
    @RequestMapping(path="/login",method = RequestMethod.POST)
    public String login(String username,String password,String code, boolean rememberme,Model model,
                        HttpSession session,HttpServletResponse response){
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha)|| StringUtils.isBlank(code) || !kaptcha.toLowerCase().equals(code)){
            model.addAttribute("codeMsg","验证码错误！");
            return "site/login";
        }
        int expriedSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        HashMap<String, Object> map = userService.CheckLogin(username, password, expriedSeconds);
        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expriedSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
//            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            return "site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }

    @RequestMapping(path = "/forget",method = RequestMethod.GET)
    public String forgetPassword(){
        return "site/forget";
    }
    @RequestMapping("/forget/kaptcha")
    public void getForgetKaptcha(@RequestParam("email") String email,Model model,HttpSession session) {
        HashMap<String, Object> map = userService.sendForgetKaptchaMail(email);
        if (!map.containsKey("code")){
            model.addAttribute("emailMsg",map.get("emailMsg"));
        }else{
            session.setAttribute("forget_email_code",map.get("code"));
            model.addAttribute("emailMsg","验证码发送成功");
        }
    }


}
