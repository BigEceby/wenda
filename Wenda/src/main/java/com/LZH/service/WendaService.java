package com.LZH.service;

import org.springframework.stereotype.Service;

/**
 * Created by asus on 2017/4/3.
 */
@Service
public class WendaService {
    public String getMessage(int msg){
        return "Message:"+msg;
    }
}
