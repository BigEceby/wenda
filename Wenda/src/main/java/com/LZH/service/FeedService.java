package com.LZH.service;

import com.LZH.dao.FeedDAO;
import com.LZH.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by asus on 2017/6/28.
 */
@Service
public class FeedService {
    @Autowired
    FeedDAO feedDAO;

    public List<Feed> selectUserFeeds(int maxId, List<Integer> userIds, int count){
        return feedDAO.selectUserFeeds(maxId,userIds,count);
    }

    public Feed getFeedById(int id){
        return feedDAO.getFeedById(id);
    }

    public boolean addFeed(Feed feed){
        return feedDAO.addFeed(feed)>0;
    }
}
