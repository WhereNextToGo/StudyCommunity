package com.cs_liudi.community.dao;

import com.cs_liudi.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectById(int id);
    User selectByUserName(String username);
    User selectByEmail(String email);
    int insertUser(User user);
    int updateStatus(int id,int status);
    int updateHeader(int id,String headerUrl);
    int updatePassword(int id, String password);
    int deleteUserById(int id);
}
