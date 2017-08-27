package com.LZH.service;

import com.LZH.dao.MessageDAO;
import com.LZH.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by asus on 2017/4/19.
 */
@Service
public class MessageService {
    @Autowired
    MessageDAO messageDAO;

    @Autowired
    SensitiveService sensitiveService;

    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDAO.addMessage(message);
    }

    public List<Message> getConversationDetail(String conversationId, int offset, int limit){
        return messageDAO.getConversationDetail(conversationId,offset,limit);
    }

    public List<Message> getConversationList(int userId, int offset, int limit){
        return messageDAO.getConversationList(userId,offset,limit);
    }

    public int getConversationUnreadCount(String conversationId,int userId){
        return messageDAO.conversationUnreadCount(conversationId,userId);
    }

    public int changeConversationToRead(String conversationId, int userId){
        return messageDAO.changeConversationToRead(conversationId,userId);
    }
}
