package com.cs_liudi.community.dao;

import com.cs_liudi.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketmapper {
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values (#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);
    @Select({
            "select user_id,ticket,status,expired from login_ticket ",
            "where ticket = #{ticket}"
    })
    LoginTicket selectLoginTicketByticket(String ticket);
    @Update({
            "<script>",
            "update login_ticket set status = #{status} where ticket = #{ticket}",
            "<if test=\"ticket != null \">",
            "and 1=1",
            "</if>",
            "</script>"
    })
    int updateLoginTicketStatus(String ticket,int status);

}
