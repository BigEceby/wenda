package com.LZH.service;

import com.LZH.dao.CommentDAO;
import com.LZH.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by asus on 2017/4/19.
 */
@Service
public class CommentSerivce {
    @Autowired
    CommentDAO commentDAO;

    @Autowired
    SensitiveService sensitiveService;

    public List<Comment> getCommentByEntity(int entityId, int entityType){
        return commentDAO.selectCommentByEntitiy(entityId,entityType);
    }

    public int addComment(Comment comment){
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDAO.addComment(comment) > 0 ? comment.getId() : 0;
    }

    public int getCommentCount(int entityId,int entityType){
        return commentDAO.getCommentCount(entityId,entityType);
    }

    public boolean deleteComment(int id){
        return commentDAO.updateStatust(id,1) > 0;
    }

    public Comment getCommentById(int id){
        return commentDAO.getCommentById(id);
    }

    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }
}
