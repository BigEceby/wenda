package com.LZH.service;

import com.LZH.controller.QuestionController;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by asus on 2017/4/14.
 */
@Service
public class SensitiveService implements InitializingBean{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SensitiveService.class);
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while((lineTxt = bufferedReader.readLine()) != null) {
                addWord(lineTxt.trim());
            }
            read.close();
            is.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件错误"+e.getMessage());
        }
    }

    private void addWord(String lineTxt){
        TrieNode tempNode = rootNode;
        for(int i = 0; i < lineTxt.length(); i++){
            Character ch = lineTxt.charAt(i);
            if(isSymbol(ch)){
                continue;
            }
            TrieNode node = tempNode.getSubNode(ch);
            if(node == null){
                node = new TrieNode();
                tempNode.addsubNode(ch , node);
            }
            tempNode = node;
            if(i == lineTxt.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    private class TrieNode{
        private boolean end = false;
        private Map<Character,TrieNode> subNodes = new HashMap<Character, TrieNode>();
        public void addsubNode(Character key, TrieNode node){
            subNodes.put(key,node);
        }
        TrieNode  getSubNode(Character key){
            return subNodes.get(key);
        }
        boolean isKeywordEnd(){
            return end;
        }
        void setKeywordEnd(boolean end){
            this.end = end;
        }
    }

    private TrieNode rootNode = new TrieNode();

    private boolean isSymbol(char c){
        int ic = (int)c;
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return text;
        }
        String replacement = "***";
        int begin = 0 ;
        int position = 0;
        TrieNode tempNode = rootNode;
        StringBuilder result = new StringBuilder();
        while(position < text.length()){
            char ch = text.charAt(position);
            if(isSymbol(ch)){
                if(tempNode==rootNode){
                    result.append(ch);
                    begin++;
                }
                position++;
                continue;
            }
            System.out.println(position+" : "+ch + " : " + begin);
            tempNode = tempNode.getSubNode(ch);
            if(tempNode == null){
                ch=text.charAt(begin);
                result.append(ch);
                position = begin + 1;
                begin = position;
                tempNode = rootNode;
            }
            else if(tempNode.isKeywordEnd()){
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            }
            else {
                position++;
            }
        }
        result.append(text.substring(begin));
        return result.toString();
    }

}
