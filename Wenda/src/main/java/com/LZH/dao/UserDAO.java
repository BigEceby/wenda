package com.LZH.dao;

import com.LZH.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

/**
 * Created by asus on 2017/4/5.
 */
@Mapper
@Component
public interface UserDAO {
    String TABLE_NAME="user";
    String INSERT_FIELDS=" name, password, salt, head_url ";
    String SELECT_FIELDS=" id, " + INSERT_FIELDS;

    @Insert({"insert into",TABLE_NAME, "(",INSERT_FIELDS, ")values(#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    @Select({"select",SELECT_FIELDS," from ", TABLE_NAME, " where id=#{id}"})
    User selectUser(int id);

    @Update({"update ",TABLE_NAME," set password = #{password} where id=#{id}"})
    int updatePassword(User user);

    @Delete({"delete from ",TABLE_NAME," where id=#{id}"})
    void deleteById(User user);

    @Select({"select",SELECT_FIELDS," from ", TABLE_NAME, " where name=#{username}"})
    User selectByName(String username);
}
