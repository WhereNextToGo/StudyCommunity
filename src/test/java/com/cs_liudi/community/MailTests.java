package com.cs_liudi.community;

import com.cs_liudi.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;
    @Test
    public void testMail(){
        mailClient.sendMail("1812016025@qq.com","何家俊牛皮！！","czc写的代码就是一坨！！");
    }
    @Test
    public void testHtml(){
        Context context = new Context();
        context.setVariable("username","liudi");
        String content = templateEngine.process("/mail/Thymeleaf_Mail_demo", context);
        System.out.println(content);
        mailClient.sendMail("1812016025@qq.com","HTML邮件测试",content);
    }
}
