package com.cs_liudi.community;

import com.cs_liudi.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Test
    public void testMail(){
        mailClient.sendMail("1812016025@qq.com","何家俊牛皮！！","czc写的代码就是一坨！！");
    }
}
