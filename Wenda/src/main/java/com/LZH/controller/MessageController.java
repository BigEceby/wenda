package com.LZH.controller;

import com.LZH.model.HostHolder;
import com.LZH.model.Message;
import com.LZH.model.User;
import com.LZH.model.ViewObject;
import com.LZH.service.MessageService;
import com.LZH.service.UserService;
import com.LZH.util.WendaUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by asus on 2017/4/19.
 */
@Controller
public class MessageController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String getComversationList(Model model){
        if(hostHolder.getUser() == null){
            return "redirect:/reglogin";
        }
        int localUserId = hostHolder.getUser().getId();
        List<Message> conversationListList = messageService.getConversationList(localUserId,0,10);
        List<ViewObject> conversations = new ArrayList<ViewObject>();
        for(Message message : conversationListList){
            ViewObject vo = new ViewObject();
            vo.set("message",message);
            int targetId = message.getFromId()==localUserId ? message.getToId() : message.getFromId();
            vo.set("user", userService.getUser(targetId));
            vo.set("unread", messageService.getConversationUnreadCount(message.getConversationId(),localUserId));
            conversations.add(vo);
        }
        model.addAttribute("conversations",conversations);
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String getConversationDetail(Model model,@RequestParam("conversationId") String conversationId){
        try {
            List<Message> messageList = messageService.getConversationDetail(conversationId,0,10);
            List<ViewObject> messages = new ArrayList<ViewObject>();
            messageService.changeConversationToRead(conversationId,hostHolder.getUser().getId());
            for(Message message : messageList){
                ViewObject vo = new ViewObject();
                vo.set("message",message);
                vo.set("user",userService.getUser(message.getFromId()));
                messages.add(vo);
            }
            model.addAttribute("messages",messages);
        } catch (Exception e) {
            logger.error("获取详情失败" + e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                              @RequestParam("content") String content){
        try {
            if(hostHolder.getUser() == null){
                return WendaUtil.getJSONString(999,"未登录");
            }
            User user = userService.selectByName(toName);
            if(user == null){
                return WendaUtil.getJSONString(999,"用户不存在");
            }

            Message message = new Message();
            message.setContent(content);
            message.setToId(user.getId());
            message.setFromId(hostHolder.getUser().getId());
            message.setCreatedDate(new Date());
            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("消息发送失败" + e.getMessage());
            return WendaUtil.getJSONString(1,"消息发送失败");
        }
    }
}
