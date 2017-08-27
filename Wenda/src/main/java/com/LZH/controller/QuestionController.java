package com.LZH.controller;

import com.LZH.async.EventModel;
import com.LZH.async.EventProducer;
import com.LZH.async.EventType;
import com.LZH.model.*;
import com.LZH.service.*;
import com.LZH.util.CacheUtil;
import com.LZH.util.WendaUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.View;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by asus on 2017/4/13.
 */
@Controller
public class QuestionController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Autowired
    CommentSerivce commentSerivce;
    @Autowired
    LikeService likeService;
    @Autowired
    FollowService followService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    CacheUtil cacheUtil;

    @RequestMapping(value="/question/add", method={RequestMethod.POST})
    @ResponseBody
    public String addQuestion( @RequestParam("title") String title,@RequestParam("content") String content){
        try{
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            if(hostHolder.getUser() == null){
                return WendaUtil.getJSONString(999);
            }else {
                question.setUserId(hostHolder.getUser().getId());
            }
            if(questionService.addQuestion(question)>0){
                eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION).setActorId(question.getUserId())
                                                            .setEntityId(question.getId()).setEntityType(EntityType.ENTITY_QUESTION)
                                                            .setExt("title",question.getTitle()).setExt("content",question.getContent()));
                return WendaUtil.getJSONString(0);
            }
        }catch (Exception e){
            logger.error("发布问题失败！"+e.getMessage());
        }
        return WendaUtil.getJSONString(1,"失败");
    }

    @RequestMapping(path = "/question/{qid}")
    public String questionDetail(Model model,@PathVariable("qid") int qid){

        Question question;//= cacheUtil.getQuestion(qid);
       // if (question==null)
                question=questionService.getQuestion(qid);
        model.addAttribute("question",question);
        List<Comment> commentList = commentSerivce.getCommentByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> comments = new ArrayList<ViewObject>();
        for(Comment comment : commentList){
            ViewObject vo = new ViewObject();
            vo.set("comment",comment);
            if(hostHolder.getUser()==null){
                vo.set("liked",0);
            } else {
                vo.set("liked",likeService.getLikeStatus(hostHolder.getUser().getId(),comment.getId(),EntityType.ENTITY_COMMENT));
            }
            vo.set("likeCount",likeService.getLikeCount(comment.getId(),EntityType.ENTITY_COMMENT));
            User user = cacheUtil.getUser(comment.getUserId());
            if (user == null) {
                user = userService.getUser(comment.getUserId());
                cacheUtil.addUser(user);
            }
            vo.set("user",user);
            comments.add(vo);
        }
        model.addAttribute("comments",comments);

        List<ViewObject> followUsers = new ArrayList<ViewObject>();
        // 获取关注的用户信息
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 20);
        for (Integer userId : users) {
            ViewObject vo = new ViewObject();
            User u = userService.getUser(userId);
            if (u == null) {
                continue;
            }
            vo.set("name", u.getName());
            vo.set("headUrl", u.getHeadUrl());
            vo.set("id", u.getId());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers", followUsers);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
        } else {
            model.addAttribute("followed", false);
        }


        return "detail";
    }
}
