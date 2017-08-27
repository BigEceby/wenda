package com.LZH.controller;

import com.LZH.model.*;
import com.LZH.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.text.View;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by asus on 2017/4/3.
 */
@Controller
public class IndexController {

    @Autowired
    WendaService wendaService;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;
    @Autowired
    CommentSerivce commentSerivce;

    @RequestMapping(path = {"/user/{userId}"})
    public String UserPageController(Model model,@PathVariable("userId") int userId){
        model.addAttribute("vos",getQuestions(userId,0,10));
        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("questionCount", questionService.getUserQuestionCount(userId));
        vo.set("commentCount", commentSerivce.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(EntityType.ENTITY_USER, userId));
        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        model.addAttribute("selfUser",hostHolder.getUser().getId());
        return "profile";
    }
    @RequestMapping(path = {"/index","/"})
    public String indexController(Model model){
        model.addAttribute("vos",getQuestions(0,0,10));
        return "index";
    }

    @RequestMapping(path = {"/detail"})
    public String detailController(){
        return "detail";
    }

    @RequestMapping("/velocity")
    public String velocityController(Model model){
        java.util.Date date = new java.util.Date();
        SimpleDateFormat  sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        model.addAttribute("time", sf.format(date.getTime()));
        List<String> list = Arrays.asList(new String[]{"Square","Circular","Triangle","Pentagram"});
        model.addAttribute("Shapes",list);
        return "velocity";
    }

    @RequestMapping("/ResponseBodyTest/{userID}")
    @ResponseBody
    public String responseBodyTest(@PathVariable("userID") int userID,
                                    @RequestParam(value = "type",defaultValue = "Normal") String type){
        return String.format("ResponseBodyTest userID: %d , type : %s",userID,type);
    }

    @RequestMapping("/request")
    @ResponseBody
    public String requestController(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session){
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod()+"<br>");
        sb.append(request.getRequestURI() + "<br>");
        sb.append(request.getQueryString() + "<br>");
        response.addCookie(new Cookie("username","LZH"));
        sb.append(request.getCookies()+"<br>");
        sb.append(wendaService.getMessage(1));
        return sb.toString();
    }

    private List<ViewObject> getQuestions(int userId, int offset, int limit){
        List<Question> questionList = questionService.getLatestQuestions(userId,offset,limit);
        List<ViewObject> viewObjectList = new ArrayList<ViewObject>();
        for(Question question : questionList){
            if (question.getContent().length() > 51)
                question.setContent(question.getContent().substring(0,50)+" ......");
            ViewObject vo = new ViewObject();
            vo.set("question",question);
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.set("user",userService.getUser(question.getUserId()));
            viewObjectList.add(vo);
        }
        return viewObjectList;
    }

    @RequestMapping("/redirect")
    public String redirect(){
        return "redirect:/index";
    }

    @RequestMapping("/Errorpage")
    public String ErrorPage(){
        throw new IllegalArgumentException("访问地址出错");
    }

    @ExceptionHandler()
    @ResponseBody
    public String error_page(Exception e){
        return "error page!" + e.getMessage();
    }
}
