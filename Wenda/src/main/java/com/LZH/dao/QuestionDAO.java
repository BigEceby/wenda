package com.LZH.dao;

import com.LZH.model.Question;
import com.LZH.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by asus on 2017/4/5.
 */
@Mapper
public interface QuestionDAO {
    String TABLE_NAME=" question ";
    String INSERT_FIELDS=" title, content, created_date, user_id, comment_count ";
    String SELECT_FIELDS=" id, " + INSERT_FIELDS;

    @Insert({"insert into",TABLE_NAME, "(",INSERT_FIELDS, ")values(#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where id=#{id}"})
    Question selectById(int id);

    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Update({"update",TABLE_NAME,"set comment_count = #{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    @Select({"select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserQuestionCount(int userId);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where title LIKE CONCAT('%',#{keyword},'%')"})
    List<Question> SearchQuestions(@Param("keyword") String keyword);
}
