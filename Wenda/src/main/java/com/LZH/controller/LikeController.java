package com.LZH.controller;

import com.LZH.async.EventModel;
import com.LZH.async.EventProducer;
import com.LZH.async.EventType;
import com.LZH.model.Comment;
import com.LZH.model.EntityType;
import com.LZH.model.HostHolder;
import com.LZH.service.CommentSerivce;
import com.LZH.service.LikeService;
import com.LZH.util.WendaUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by asus on 2017/4/20.
 */
@Controller
public class LikeController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    CommentSerivce commentSerivce;

    @RequestMapping(path = "/like", method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser()==null)
            return WendaUtil.getJSONString(999);
        long likeCount = likeService.like(hostHolder.getUser().getId(),commentId, EntityType.ENTITY_COMMENT);
        Comment comment = commentSerivce.getCommentById(commentId);
        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(commentId)
                .setEntityType(EntityType.ENTITY_COMMENT)
                .setExt("questionId",String.valueOf(comment.getEntityId()))
                .setEntityOwnerId(comment.getUserId()));
        return WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }

    @RequestMapping(path = "/dislike", method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser()==null)
            return WendaUtil.getJSONString(999);
        long likeCount = likeService.dislike(hostHolder.getUser().getId(),commentId, EntityType.ENTITY_COMMENT);
        return WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }

}
