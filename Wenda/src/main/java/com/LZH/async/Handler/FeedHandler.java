package com.LZH.async.Handler;

import com.LZH.async.EventHandler;
import com.LZH.async.EventModel;
import com.LZH.async.EventType;
import com.LZH.model.*;
import com.LZH.service.*;
import com.LZH.util.JedisAdapter;
import com.LZH.util.RedisKeyUtil;
import com.LZH.util.WendaUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by asus on 2017/4/24.
 */
@Component
public class FeedHandler implements EventHandler{
    @Autowired
    MessageService messageService;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FeedService feedService;

    @Autowired
    JedisAdapter jedisAdapter;

    private String buildFeedData(EventModel eventModel){
        /*
            Ramdom r = new Random();
            model.setActorId(1+r.nextInt(10));
         */
        Map<String,String> map = new HashMap<String, String>();
        User actor = userService.getUser(eventModel.getActorId());
        if(actor == null){
            return null;
        }
        map.put("userId",String.valueOf(actor.getId()));
        map.put("userHead",actor.getHeadUrl());
        map.put("name",actor.getName());

        if(eventModel.getType() == EventType.COMMENT || eventModel.getType() == EventType.FOLLOW && eventModel.getEntityType()== EntityType.ENTITY_QUESTION){
            Question question = questionService.getQuestion(eventModel.getEntityId());
            if (question == null) {
                return null;
            }
            map.put("questionId",String.valueOf(question.getId()));
            map.put("questionTitle",question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public void doHandle(EventModel eventModel) {
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setUserId(eventModel.getActorId());
        feed.setType(eventModel.getType().getValue());
        feed.setData(buildFeedData(eventModel));
        if (feed.getData() == null){
            return;
        }
        feedService.addFeed(feed);

        //给粉丝推
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER,eventModel.getActorId(),Integer.MAX_VALUE);
        followers.add(0);
        for (int follower : followers){
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey,String.valueOf(feed.getId()));
        }

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.FOLLOW,EventType.COMMENT});
    }
}
