package com.cs_liudi.community;

import com.cs_liudi.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveWordFilterTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Test
    public void testFilter(){
        String text = "我喜欢fuck，doI，做爱，嫖娼，赌博，开票，哈哈哈哈！！";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
        text = "我喜欢&fu&ck&，&do&I&，&做&爱&，&嫖&娼&，&赌&博&，&开&票&，哈哈哈哈！！";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
