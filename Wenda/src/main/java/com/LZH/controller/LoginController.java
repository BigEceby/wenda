package com.LZH.controller;

import com.LZH.Aspect.LoggerAspect;
import com.LZH.model.Question;
import com.LZH.model.ViewObject;
import com.LZH.service.QuestionService;
import com.LZH.service.UserService;
import com.LZH.service.WendaService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/4/3.
 */
@Controller
public class LoginController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/reg/"},method = {RequestMethod.POST})
    public String reg(Model model,HttpServletResponse response,
                      @RequestParam ("username") String username,
                      @RequestParam ("password") String password){

        try {
            Map<String,String> map = userService.register(username,password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                return "redirect:/";
            }
            else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
           logger.error("注册异常："+e.getMessage());
           return "login";
        }
    }

    @RequestMapping(path = {"/login/"},method = {RequestMethod.POST})
    public String login(Model model,HttpServletResponse response,
                      @RequestParam ("username") String username,
                      @RequestParam (value = "next", required = false) String next,
                      @RequestParam ("password") String password){

        try {
            Map<String,String> map = userService.login(username,password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                if(StringUtils.isNotBlank(next)){
                    return "redirect:" + next;
                }
                return "redirect:/";
            }
            else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }


        } catch (Exception e) {
            logger.error("登陆异常："+e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/reglogin"},method = {RequestMethod.GET})
    public String reg(Model model,@RequestParam (value = "next", required = false) String next){
        model.addAttribute("next",next);
        return "login";
    }

    @RequestMapping(path = {"/logout"})
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }
}
