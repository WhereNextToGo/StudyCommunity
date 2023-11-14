package com.cs_liudi.community.controller;

import com.cs_liudi.community.entity.CommunityConstant;
import com.cs_liudi.community.entity.LoginTicket;
import com.cs_liudi.community.entity.User;
import com.cs_liudi.community.service.UserService;
import com.cs_liudi.community.util.CommunityUtils;
import com.cs_liudi.community.util.MailClient;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private Producer producer;

    @Value("{server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

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
        if (StringUtils.isBlank(kaptcha)|| StringUtils.isBlank(code) || !code.toLowerCase().equals(kaptcha)){
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

//    @RequestMapping("/forget/kaptcha")
//    public String getForgetKaptcha(@RequestParam("email") String email,Model model,HttpSession session) {
//        HashMap<String, Object> map = userService.sendForgetKaptchaMail(email);
//        if (!map.containsKey("code")){
//            model.addAttribute("emailMsg",map.get("emailMsg"));
//        }else{
//            session.setAttribute("forget_email_code",map.get("code"));
//            model.addAttribute("emailMsg","验证码发送成功");
//        }
//        return "/site/forget";
//    }

    //生成并返回忘记密码的验证码，设置五分钟的过期时间
    @RequestMapping(path = "/forget/code", method = RequestMethod.GET)
    @ResponseBody
    public String getForgetCode(String email, HttpSession session) {
        if (StringUtils.isBlank(email)) {
            return CommunityUtils.getJSONString(1, "邮箱不能为空！");
        }
        HashMap<String, Object> map = userService.sendForgetKaptchaMail(email);
        if (!map.containsKey("code")){
            return CommunityUtils.getJSONString(2, "邮箱错误！不存在！");
        }
        // 发送邮件
//        Context context = new Context();
//        context.setVariable("email", email);
//        String code = CommunityUtils.generateUUID().substring(0, 4);
//        context.setVariable("verifyCode", code);
//        String content = templateEngine.process("/mail/forget", context);
//        mailClient.sendMail(email, "找回密码", content);

        // 保存验证码
        session.setAttribute("verifyCode", map.get("code"));
        removeAttribute("verifyCode",session);
        return CommunityUtils.getJSONString(0);
    }
    @RequestMapping(path = "/forget",method = RequestMethod.POST)
    public String resetPassword(String email,String password,String verifyCode,Model model,HttpSession session){
        String kaptcha = (String) session.getAttribute("verifyCode");
        if (StringUtils.isBlank(verifyCode) || StringUtils.isBlank(kaptcha) || !verifyCode.toLowerCase().equals(kaptcha)){
            model.addAttribute("codeMsg","验证码错误");
            return "site/forget";
        }
        HashMap<String,Object> map = userService.resetPassword(email,password);
        if (null != map){

            return "redirect:/login";
        }else {
            model.addAttribute("emailMsg",map.get("emailMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "site/forget";
        }
    }


    private void removeAttribute(String name,HttpSession session){
        Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                   @Override
                   public void run() {
                       try {
                           session.removeAttribute(name);
                           timer.cancel();
                           logger.info("忘记密码请求的验证码过期，删除session中的验证码");
                       } catch (Exception e) {
                           logger.error("删除session中的验证码出错"+e.getMessage());
                       }
                   }
               },
    1*60*1000);
    }
}
