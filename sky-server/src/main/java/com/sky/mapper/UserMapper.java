package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据openId查询用户信息
     * @param openId
     * @return
     */
    @Select("select * from user where openid = #{openId}")
    User seleteByOpenId(String openId);

    /**
     * 新增用户
     * @param user
     */
    @Insert("insert into user (openid, create_time) values (#{openid}, #{createTime});")
    void insert(User user);
}
