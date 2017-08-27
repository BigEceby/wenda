package com.LZH.service;

import com.LZH.dao.QuestionDAO;
import com.LZH.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by asus on 2017/4/5.
 */
@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    public List<Question> getLatestQuestions(int userId, int offset, int limit){
        return questionDAO.selectLatestQuestions(userId,offset,limit);
    }

    public List<Question> SearchQuestions(String keyword){
        return questionDAO.SearchQuestions(keyword);
    }

    public int addQuestion(Question question){
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
    }

    public Question getQuestion(int id){
        return questionDAO.selectById(id);
    }

    public int updateCommentCount(int id, int commentCount){
        return questionDAO.updateCommentCount(id,commentCount);
    }

    public int getUserQuestionCount(int userId) {
        return questionDAO.getUserQuestionCount(userId);
    }
}
