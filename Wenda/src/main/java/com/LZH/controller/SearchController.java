package com.LZH.controller;

import com.LZH.async.EventModel;
import com.LZH.async.EventType;
import com.LZH.model.Comment;
import com.LZH.model.EntityType;
import com.LZH.model.Question;
import com.LZH.model.ViewObject;
import com.LZH.service.FollowService;
import com.LZH.service.QuestionService;
/*import com.LZH.service.SearchService;*/
import com.LZH.service.UserService;
import com.LZH.util.WendaUtil;
import org.hibernate.validator.constraints.URL;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2017/5/10.
 */
@Controller
public class SearchController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SearchController.class);

/*    @Autowired
    SearchService searchService;*/

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;

    @Autowired
    QuestionService questionService;

    @RequestMapping(path = "/search")
    public String like(Model model,@RequestParam("q") String keyword,
                       @RequestParam(value = "offset" , defaultValue = "0") int offset,
                       @RequestParam(value = "limit" , defaultValue = "10") int limit){
        List<ViewObject> viewObjectList = null;
        try {
           /* List<Question> questionList = searchService.searchQuestion(keyword,offset,limit,"<em>","</em>");*/
            List<Question> questionList = questionService.SearchQuestions(keyword);
            System.out.println(questionList.size());
            viewObjectList = new ArrayList<ViewObject>();
            for(Question question : questionList){
/*                Question q = questionService.getQuestion(question.getId());
                question.setCreatedDate(q.getCreatedDate());
                question.setCommentCount(q.getCommentCount());
                question.setUserId(q.getUserId());
                if(question.getTitle()==null)
                    question.setTitle(q.getTitle());
                if (question.getContent()==null)
                    question.setContent(q.getContent());*/
                int start = question.getTitle().indexOf(keyword);
                int end = start + keyword.length();
                question.setTitle(question.getTitle().substring(0,start) + "<em>" + keyword + "</em>" + question.getTitle().substring(end));
                if (question.getContent().length() > 51)
                    question.setContent(question.getContent().substring(0,50)+" ......");
                ViewObject vo = new ViewObject();
                vo.set("question",question);
                vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
                vo.set("user",userService.getUser(question.getUserId()));
                viewObjectList.add(vo);
            }
            model.addAttribute("vos",viewObjectList);
            model.addAttribute("keyword",keyword);
        } catch (Exception e) {
            logger.error("搜索出错"+e.getMessage());
        }
        return "result";
    }
}
