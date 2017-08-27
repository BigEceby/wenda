package com.LZH.controller;

import com.LZH.async.EventModel;
import com.LZH.async.EventProducer;
import com.LZH.async.EventType;
import com.LZH.model.Comment;
import com.LZH.model.EntityType;
import com.LZH.model.HostHolder;
import com.LZH.model.Question;
import com.LZH.service.CommentSerivce;
import com.LZH.service.QuestionService;
import com.LZH.util.WendaUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Created by asus on 2017/4/19.
 */
@Controller
public class CommentController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentSerivce commentSerivce;

    @Autowired
    QuestionService questionService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = "/addComment" , method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                              @RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            if(hostHolder.getUser()!=null) {
                comment.setUserId(hostHolder.getUser().getId());
            }else{
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
            }
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            commentSerivce.addComment(comment);

            int count = commentSerivce.getCommentCount(comment.getEntityId(),comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(),count);
            eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId())
                    .setEntityId(questionId));
        } catch (Exception e) {
            logger.error("评论出错！"+e.getMessage());
        }
        return "redirect:/question/"+questionId;
    }
}
