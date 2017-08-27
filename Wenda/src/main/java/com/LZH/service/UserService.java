package com.LZH.service;

import com.LZH.dao.LoginTicketDAO;
import com.LZH.dao.UserDAO;
import com.LZH.model.HostHolder;
import com.LZH.model.LoginTicket;
import com.LZH.model.User;
import com.LZH.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by asus on 2017/4/5.
 */
@Service
public class UserService {
    @Autowired
    UserDAO userDAO;
    @Autowired
    LoginTicketDAO loginTicketDAO;
    @Autowired
    HostHolder hostHolder;

    public User getUser(int id){
        User user = new User();
        user.setId(id);
        return userDAO.selectUser(id);
    }

    public Map<String,String> register(String username,String password){
        Map<String,String> map = new HashMap<String,String>();
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(userDAO.selectByName(username)!=null){
            map.put("msg","用户已存在");
            return map;
        }
        User user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);

        String ticket = addTicket(user.getId());
        map.put("ticket",ticket);

        return map;
    }

    public Map<String,String> login(String username,String password){
        Map<String,String> map = new HashMap<String,String>();
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        User user = new User();
        user=userDAO.selectByName(username);
        if(user==null){
            map.put("msg","用户不存在");
            return map;
        }
        if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码错误");
            return map;
        }

        String ticket = addTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public String addTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }

    public User selectByName(String name){
        return userDAO.selectByName(name);
    }
}
