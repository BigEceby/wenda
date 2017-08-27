package com.LZH.controller;

import com.LZH.async.EventModel;
import com.LZH.async.EventProducer;
import com.LZH.async.EventType;
import com.LZH.model.*;
import com.LZH.service.CommentSerivce;
import com.LZH.service.FollowService;
import com.LZH.service.QuestionService;
import com.LZH.service.UserService;
import com.LZH.util.WendaUtil;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/4/23.
 */
@Controller
public class FollowController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FollowController.class);
    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentSerivce commentSerivce;

    @RequestMapping(path = {"/followUser"},method = {RequestMethod.POST})
    @ResponseBody
    public String follow(Model model, @RequestParam("userId") int userId){
        if(hostHolder.getUser()==null)
            return WendaUtil.getJSONString(999);
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER,userId);
        eventProducer.fireEvent(new EventModel().setActorId(hostHolder.getUser().getId())
                .setEntityId(userId).setEntityOwnerId(userId)
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityId(userId)
                .setType(EventType.FOLLOW));
        return WendaUtil.getJSONString(ret ? 0 : 1 ,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }

    @RequestMapping(path = {"/unfollowUser"},method = {RequestMethod.POST})
    @ResponseBody
    public String unfollow(Model model, @RequestParam("userId") int userId){
        if(hostHolder.getUser()==null)
            return WendaUtil.getJSONString(999);
        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER,userId);
        eventProducer.fireEvent(new EventModel().setActorId(hostHolder.getUser().getId())
                .setEntityId(userId).setEntityOwnerId(userId)
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityId(userId)
                .setType(EventType.UNFOLLOW));
        return WendaUtil.getJSONString(ret ? 0 : 1 ,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }

    @RequestMapping(path = {"/followQuestion"},method = {RequestMethod.POST})
    @ResponseBody
    public String followQuestion(Model model, @RequestParam("questionId") int questionId){
        if(hostHolder.getUser()==null)
            return WendaUtil.getJSONString(999);

        Question question = questionService.getQuestion(questionId);
        if(question == null){
            return WendaUtil.getJSONString(1,"问题不存在");
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION,questionId);
        eventProducer.fireEvent(new EventModel().setActorId(hostHolder.getUser().getId())
                .setEntityId(questionId).setEntityOwnerId(question.getUserId())
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityId(questionId)
                .setType(EventType.FOLLOW));
        Map<String ,Object> info = new HashMap<String, Object>();
        info.put("headUrl",hostHolder.getUser().getHeadUrl());
        info.put("name",hostHolder.getUser().getName());
        info.put("id",hostHolder.getUser().getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString(ret ? 0 : 1 ,info);
    }

    @RequestMapping(path = {"/unfollowQuestion"},method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(Model model, @RequestParam("questionId") int questionId){
        if(hostHolder.getUser()==null)
            return WendaUtil.getJSONString(999);
        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION,questionId);
        eventProducer.fireEvent(new EventModel().setActorId(hostHolder.getUser().getId())
                .setEntityId(questionId).setEntityOwnerId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityId(questionId)
                .setType(EventType.UNFOLLOW));
        return WendaUtil.getJSONString(ret ? 0 : 1 ,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION)));
    }

    @RequestMapping(path = {"/user/{uid}/followees"},method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId){
        List<Integer> followeeIds = followService.getFollowees(EntityType.ENTITY_USER,userId,0,10);
        if(hostHolder.getUser()!=null){
            model.addAttribute("followees",getUsersInfo(hostHolder.getUser().getId(),followeeIds));
        } else {
            model.addAttribute("followees",getUsersInfo(0,followeeIds));
        }
        model.addAttribute("followeeCount",followService.getFolloweeCount(EntityType.ENTITY_USER,userId));
        model.addAttribute("curUser",userService.getUser(userId));
        return "followees";
    }

    @RequestMapping(path = {"/user/{uid}/followQuestion"},method = {RequestMethod.GET})
    public String followquestion(Model model, @PathVariable("uid") int userId){
        List<Integer> followeeIds = followService.getFollowees(EntityType.ENTITY_QUESTION,userId,0,10);
        if(hostHolder.getUser()!=null){
            model.addAttribute("vos",getQuestionInfo(hostHolder.getUser().getId(),followeeIds));
        } else {
            model.addAttribute("vos",getQuestionInfo(0,followeeIds));
        }
        model.addAttribute("followeeCount",followService.getFolloweeCount(EntityType.ENTITY_QUESTION,userId));
        model.addAttribute("curUser",userService.getUser(userId));
        return "followQuestion";
    }

    @RequestMapping(path = {"/user/{uid}/followers"},method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid") int userId){
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER,userId,0,10);
        if(hostHolder.getUser()!=null){
            model.addAttribute("followers",getUsersInfo(hostHolder.getUser().getId(),followerIds));
        } else {
            model.addAttribute("followers",getUsersInfo(0,followerIds));
        }
        model.addAttribute("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,userId));
        model.addAttribute("curUser",userService.getUser(userId));
        return "followers";
    }

    private List<ViewObject> getUsersInfo(int localUserId,List<Integer> ids){
        List<ViewObject> userInfos = new ArrayList<>();
        for(Integer id : ids){
            User user = userService.getUser(id);
            if(user == null)
                continue;
            ViewObject vo = new ViewObject();
            vo.set("user",user);
            vo.set("commentCount", commentSerivce.getUserCommentCount(id));
            vo.set("questionCount", questionService.getUserQuestionCount(id));
            vo.set("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,id));
            vo.set("followeeCount",followService.getFolloweeCount(EntityType.ENTITY_USER,id));
            if(localUserId!=0){
                vo.set("followed",followService.isFollower(localUserId,EntityType.ENTITY_USER,id));
            } else {
                vo.set("followed",false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }

    private List<ViewObject> getQuestionInfo(int localUserId,List<Integer> ids){
        List<ViewObject> questionInfos = new ArrayList<>();
        for(Integer id : ids){
            Question question = questionService.getQuestion(id);
            if(question == null)
                continue;
            ViewObject vo = new ViewObject();
            if(question.getContent().length() > 51)
                question.setContent(question.getContent().substring(0,50)+" ......");
            vo.set("question",question);
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.set("user",userService.getUser(question.getUserId()));
            questionInfos.add(vo);
        }
        return questionInfos;
    }
}
