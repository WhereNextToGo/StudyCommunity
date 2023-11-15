package com.cs_liudi.community.service;

import com.cs_liudi.community.dao.LoginTicketmapper;
import com.cs_liudi.community.dao.UserMapper;
import com.cs_liudi.community.entity.CommunityConstant;
import com.cs_liudi.community.entity.LoginTicket;
import com.cs_liudi.community.entity.User;
import com.cs_liudi.community.util.CommunityUtils;
import com.cs_liudi.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.ejb.Timeout;
import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class UserService implements CommunityConstant{

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketmapper loginTicketmapper;

    @Autowired
    private MailClient mailClient;

    @Resource
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String doamin;

    @Value("${server.servlet.context-path}")
    private String contextPath;
    //查找用户
    public User findUserById(int id){
        return userMapper.selectById(id);
    }
    //注册用户
    public Map<String,Object> register(User user){
        HashMap<String, Object> map = new HashMap<>();
        if (user == null){
            throw new IllegalArgumentException();
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMessage","用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMessage","密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMessage","邮箱不能为空");
            return map;
        }
        if (userMapper.selectByUserName(user.getUsername()) != null){
            map.put("usernameMessage","用户名已存在");
            return map;
        }
        if (userMapper.selectByEmail(user.getEmail()) != null){
            map.put("emailMessage","邮箱已存在");
            return map;
        }
        user.setSalt(CommunityUtils.generateUUID().substring(0, 5));
//        String password = CommunityUtils.MD5(user.getPassword()+user.getSalt());
        user.setPassword(CommunityUtils.MD5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtils.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        Context context = new Context();
        context.setVariable("email",user.getEmail());
        context.setVariable("username",user.getUsername());
        //http://localhost:8081/community/activation/101/activationcode
        context.setVariable("url",doamin+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode());
        String content = templateEngine.process("/mail/activation", context);

        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }
    //激活用户
    public int activation(int id,String activationCode){
        User user = userMapper.selectById(id);
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(activationCode)){
            userMapper.updateStatus(id,1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }
    //处理登录业务
    public HashMap<String,Object> CheckLogin(String username, String password,  int expiredSeconds){
        HashMap<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        User user = userMapper.selectByUserName(username);
        if (user == null){
            map.put("usernameMsg","用户名不存在！");
            return map;
        }
        if (user.getStatus() == 0){
            map.put("usernameMsg","用户未激活！");
            return map;
        }
        password = CommunityUtils.MD5(password+user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确！");
            return map;
        }
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(user.getId());
        ticket.setStatus(0);
        ticket.setTicket(CommunityUtils.generateUUID());
        ticket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketmapper.insertLoginTicket(ticket);
        map.put("ticket",ticket.getTicket());
        return map;
    }
    //处理退出登录业务
    public void logout(String ticket){
        loginTicketmapper.updateLoginTicketStatus(ticket,1);
    }

    //处理忘记密码业务
    public HashMap<String,Object> forgetPassword(String email,String password){
        HashMap<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(email)){
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "邮箱不存在！");
            return map;
        }

        userMapper.updatePassword(user.getId(),password);
        return map;
    }

    public HashMap<String,Object> sendForgetKaptchaMail(String email){
        HashMap<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(email)){
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "邮箱不存在！");
            return map;
        }
        String code = CommunityUtils.generateUUID().substring(0,4);
        Context context = new Context();
        context.setVariable("email",email);
        context.setVariable("code",code);
        String content = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(email,"找回密码",content);
        map.put("code",code);
        return map;
    }


    public HashMap<String,Object> resetPassword(String email,String password){
        HashMap<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(email)){
            map.put("emailMsg","邮箱不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        User user = userMapper.selectByEmail(email);
        userMapper.updatePassword(user.getId(),CommunityUtils.MD5(password+user.getSalt()));
        return map;
    }

    public LoginTicket getLoginTickget(String ticket){
        return loginTicketmapper.selectLoginTicketByticket(ticket);
    }
}
