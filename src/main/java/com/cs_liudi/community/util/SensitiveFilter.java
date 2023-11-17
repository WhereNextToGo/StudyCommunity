package com.cs_liudi.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    //设置默认敏感词代替词 ***
    private static final String REPLACEMRNT_WOED = "***";
    //设置根节点
    private TrieNode RootNode = new TrieNode();
    //在实例化之后构建前缀树
    @PostConstruct
    private void init(){
        try(
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String keyword;
            while ((keyword = bufferedReader.readLine()) != null){
                this.addKeyWord(keyword);
            }
        } catch (Exception e) {
            logger.error("加载敏感词文件失败"+e.getMessage());
        }
    }

    private void addKeyWord(String keyword){
        TrieNode tempNode = RootNode;
        for (int i = 0; i <keyword.length() ; i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null){
                //初始化节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            //指向子节点，进入下一轮循环
            tempNode = subNode;
            //设置结束标志
            if (i == keyword.length() - 1){
                subNode.setKeyWordEnd(true);
            }
        }
    }

    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }
        //指针1
        TrieNode tempNode = RootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();
        while (position < text.length()){
            char c = text.charAt(position);
            if (isSymbol(c)){
                if (tempNode == RootNode){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
                sb.append(text.charAt(begin));
                position=++begin;
                tempNode = RootNode;
            }else if (tempNode.isKeyWordEnd()){
                sb.append(REPLACEMRNT_WOED);
                begin = ++position;
                tempNode = RootNode;
            }else {
                position++;
            }
        }
        sb.append(text.substring(begin));
        return sb.toString();

    }
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c)&&(c < 0x2E80 || c > 0x9FFF);
    }


    //前缀树内部类
    private class TrieNode{

        private boolean isKeyWordEnd = false;

        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        public void addSubNode(Character c ,TrieNode node){
            subNodes.put(c,node);
        }
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }


    }
}
