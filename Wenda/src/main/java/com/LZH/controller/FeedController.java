package com.LZH.controller;

import com.LZH.model.EntityType;
import com.LZH.model.Feed;
import com.LZH.model.HostHolder;
import com.LZH.service.FeedService;
import com.LZH.service.FollowService;
import com.LZH.util.JedisAdapter;
import com.LZH.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2017/6/29.
 */
@Controller
public class FeedController {
    @Autowired
    FeedService feedService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;

    @RequestMapping(path = {"/pullfeeds"},method = {RequestMethod.GET})
    private String getPullFeeds(Model model){
        int localUserId = hostHolder.getUser() ==null ? 0 : hostHolder.getUser().getId();
        List<Integer> followees = new ArrayList<>();
        if (localUserId != 0){
            followees = followService.getFollowees(EntityType.ENTITY_USER,localUserId,Integer.MAX_VALUE);
        }
        List<Feed> feeds = feedService.selectUserFeeds(Integer.MAX_VALUE,followees,10);
        model.addAttribute("feeds",feeds);
        return "feeds";
    }

    @RequestMapping(path = {"/pushfeeds"},method = {RequestMethod.GET})
    private String getPushFeeds(Model model){
        int localUserId = hostHolder.getUser() ==null ? 0 : hostHolder.getUser().getId();
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId),0,10);
        List<Feed> feeds = new ArrayList<>();
        for(String feedId : feedIds) {
            Feed feed = feedService.getFeedById(Integer.parseInt(feedId));
            if(feed == null){
                continue;
            }
            feeds.add(feed);
        }
        model.addAttribute("feeds",feeds);
        return "feeds";
    }
}
