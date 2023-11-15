package com.cs_liudi.community.util;

import com.cs_liudi.community.entity.User;
import org.springframework.stereotype.Component;
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();


    public void setUser(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }
    public void clear(){
        users.remove();
    }
}
