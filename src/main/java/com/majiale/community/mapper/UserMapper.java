package com.majiale.community.mapper;

import com.majiale.community.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Insert("insert into user (account_id, name, token, gmt_create, gmt_modified, bio, avatar_url) " +
            "values (#{accountId}, #{name}, #{token}, #{gmtCreate}, #{gmtModified}, #{bio}, #{avatarUrl})")
    void insert(User user);

    @Select("select * from user where token = #{token}") // #代表在mybatis进行编译时，将下面形参的token放入
    User findByToken(@Param("token") String token); // 因为这里不是类所以前面加上了@Param

    @Select("select * from user where id = #{id}")
    User findById(@Param("id") Integer id);

    @Select("select * from user where account_id = #{accountId}")
    User findByAccountId(@Param("accountId") String accountId);

    @Update("update user set name = #{name}, token = #{token}, gmt_modified = #{gmtModified}, avatar_url = #{avatarUrl} where id = #{id}")
    void update(User user);

}
