package com.LZH.async;

import com.LZH.util.JedisAdapter;
import com.LZH.util.RedisKeyUtil;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by asus on 2017/4/21.
 */
@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel){
        try {
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueue();
            jedisAdapter.lpush(key,json);
            System.out.println("add:" + json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
