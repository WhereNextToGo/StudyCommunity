package com.cs_liudi.community;

import com.cs_liudi.community.dao.DiscussPostMapper;
import com.cs_liudi.community.dao.UserMapper;
import com.cs_liudi.community.entity.DiscussPost;
import com.cs_liudi.community.entity.User;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);
        user = userMapper.selectByUserName("niuke");
        System.out.println(user);
        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("nowcoder150@sina.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/101t.png");
        user.setCreateTime(new Date());
        int row = userMapper.insertUser(user);
        System.out.println(row);
        System.out.println(user.getId());
    }
    @Test
    public void testUpdateUser(){
        int row = userMapper.updateHeader(150,"http://images.nowcoder.com/head/102t.png");
        System.out.println(row);
        row = userMapper.updateStatus(150,1);
        System.out.println(row);
        row = userMapper.updatePassword(150,"123456abc");
        System.out.println(row);
    }

    @Test
    public void testDeleteUser(){
        int row = userMapper.deleteUserById(150);
        System.out.println(row);
    }

    @Test
    public void testSelectDiscussPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149,0,10);
        for (DiscussPost dp: discussPosts) {
            System.out.println(dp);
        }
        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }
}
