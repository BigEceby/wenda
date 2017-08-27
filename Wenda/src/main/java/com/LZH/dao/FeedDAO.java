package com.LZH.dao;

import com.LZH.model.Comment;
import com.LZH.model.Feed;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by asus on 2017/6/28.
 */
@Mapper
public interface FeedDAO {
    String TABLE_NAME=" feed ";
    String INSERT_FIELDS=" user_id, data, created_date, type ";
    String SELECT_FIELDS=" id, " + INSERT_FIELDS;

    @Insert({"insert into",TABLE_NAME, "(",INSERT_FIELDS, ")values(#{userId},#{data},#{createdDate},#{type})"})
    int addFeed(Feed feed);


    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);


    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where id = #{id}"})
    Feed getFeedById(@Param("id") int id);


}
