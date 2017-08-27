package com.LZH.async.Handler;

import com.LZH.async.EventHandler;
import com.LZH.async.EventModel;
import com.LZH.async.EventType;
import com.LZH.model.Message;
import com.LZH.model.User;
import com.LZH.service.MessageService;
import com.LZH.service.UserService;
import com.LZH.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by asus on 2017/4/21.
 */
@Component
public class LikeHandler implements EventHandler{

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(eventModel.getActorId());
        message.setContent("用户"+ user.getName() +"赞了你的回答http://127.0.0.1:8080/question/"+eventModel.getExt("questionId"));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
