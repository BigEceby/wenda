package com.LZH.dao;

import com.LZH.model.Comment;
import com.LZH.model.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by asus on 2017/4/5.
 */
@Mapper
public interface CommentDAO {
    String TABLE_NAME=" comment ";
    String INSERT_FIELDS=" user_id, content, created_date, entity_id, entity_type, status ";
    String SELECT_FIELDS=" id, " + INSERT_FIELDS;

    @Insert({"insert into",TABLE_NAME, "(",INSERT_FIELDS, ")values(#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})"})
    int addComment(Comment comment);


    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where entity_id=#{entityId} and entity_type=#{entityType} order by created_date desc"})
    List<Comment> selectCommentByEntitiy(@Param("entityId") int entityId,
                                         @Param("entityType") int entityType);

    @Select({"select count(id) from",TABLE_NAME,"where entity_id=#{entityId} and entity_type=#{entityType}"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Update({"update comment set status = #{status} where id=#{id}"})
    int updateStatust(@Param("id") int id , @Param("status") int status);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where id = #{id}"})
    Comment getCommentById(@Param("id") int id);

    @Select({"select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserCommentCount(int userId);
}
