package com.majiale.community.mapper;

import com.majiale.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("insert into user (account_id, name, token, gmt_create, gmt_modified, bio, avatar_url) " +
            "values (#{accountId}, #{name}, #{token}, #{gmtCreate}, #{gmtModified}, #{bio}, #{avatarUrl})")
    void insert(User user);

    @Select("select * from user where token = #{token}") // #代表在mybatis进行编译时，将下面形参的token放入
    User findByToken(@Param("token") String token); // 因为这里不是类所以前面加上了@Param

    @Select("select * from user where id = #{id}")
    User findById(@Param("id") Integer id);
}
