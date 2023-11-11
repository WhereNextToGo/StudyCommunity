package com.cs_liudi.community.service;

import com.cs_liudi.community.dao.UserMapper;
import com.cs_liudi.community.entity.CommunityConstant;
import com.cs_liudi.community.entity.User;
import com.cs_liudi.community.util.CommunityUtils;
import com.cs_liudi.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant{
    @Autowired
    private UserMapper userMapper;

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
}
