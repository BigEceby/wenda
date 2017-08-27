package com.LZH.util;

import com.LZH.model.Question;
import com.LZH.model.User;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.digester.annotations.rules.SetRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import javax.jws.soap.SOAPBinding;
import java.util.Date;
import java.util.List;

/**
 * Created by asus on 2017/5/15.
 */
@Service
public class CacheUtil {

    @Autowired
    JedisAdapter jedisAdapter;

    public int addUser(User user){
        String userKey = "User:"+user.getId();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name",user.getName());
        jsonObject.put("headUrl",user.getHeadUrl());
        jedisAdapter.hset("user",userKey,jsonObject.toString());
        return 1;
    }

    public int addQuestion(Question question){
        String questionKey = "Question:"+question.getId();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title",question.getTitle());
        jsonObject.put("content",question.getTitle());
        jsonObject.put("createdDate",question.getCreatedDate());
        jsonObject.put("userId",question.getUserId());
        jsonObject.put("commentCount",question.getCommentCount());
        jedisAdapter.hset("question",questionKey,jsonObject.toString());
        return 1;
    }

    public User getUser(int id){
        String s = jedisAdapter.hget("user","User:"+id);
        if (s == null)
            return null;
        User user = JSONObject.parseObject(s,User.class);
        user.setId(id);
        return user;
    }

    public Question getQuestion(int id){
        String s = jedisAdapter.hget("question","Question:"+id);
        if (s == null)
            return null;
        Question question = JSONObject.parseObject(s,Question.class);
        question.setId(id);
        return question;
    }
}
