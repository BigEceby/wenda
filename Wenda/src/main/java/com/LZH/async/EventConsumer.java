package com.LZH.async;

import com.LZH.controller.CommentController;
import com.LZH.util.JedisAdapter;
import com.LZH.util.RedisKeyUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by asus on 2017/4/21.
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType,List<EventHandler>> config = new HashMap<EventType,List<EventHandler>>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if(beans!=null){
            for (Map.Entry<String,EventHandler> entry : beans.entrySet()){
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                for (EventType eventType : eventTypes){
                    if(config.containsKey(eventType)){
                        config.get(eventType).add(entry.getValue());
                    } else {
                        config.put(eventType,new ArrayList<EventHandler>());
                        config.get(eventType).add(entry.getValue());
                    }
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String key = RedisKeyUtil.getEventQueue();
                    List<String> events = jedisAdapter.brpop(0,key);
                    for (String message:events){
                        System.out.println("get message:"+message);
                        if(message.equals(key)) {
                            continue;
                        }

                        EventModel eventModel = JSON.parseObject(message,EventModel.class);
                        if(!config.containsKey(eventModel.getType())){
                            logger.error("不能识别的事件"+eventModel.getType()+"  "+config.size());
                            continue;
                        }

                        for(EventHandler eventHandler : config.get(eventModel.getType())){
                            System.out.println("doHandle:"+eventHandler);
                            eventHandler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
