/*
package com.LZH.async.Handler;

import com.LZH.async.EventHandler;
import com.LZH.async.EventModel;
import com.LZH.async.EventType;
import com.LZH.controller.LoginController;
import com.LZH.service.SearchService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

*/
/**
 * Created by asus on 2017/5/10.
 *//*

public class AddQuestionHandler implements EventHandler{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AddQuestionHandler.class);

    @Autowired
    SearchService searchService;

    @Override
    public void doHandle(EventModel eventModel) {
        try {
            searchService.indexQuestion(eventModel.getActorId(),eventModel.getExt("title"),eventModel.getExt("content"));
        } catch (Exception e) {
            logger.error("添加索引失败"+e.getMessage());
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.ADD_QUESTION);
    }
}
*/
