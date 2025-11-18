package com.example.chat_realtime_app.utils;

import java.util.ArrayList;
import java.util.List;

public class KeywordUtils {
    public static List<String> generateKeywords(String name){
        name = name.toLowerCase().trim();
        List<String> keywords = new ArrayList<>();

        //tách chuỗi
        String[] parts = name.split(" ");

        for(String part : parts){
            StringBuilder sb = new StringBuilder();
            for(char c : part.toCharArray()){
                sb.append(c);
                keywords.add(sb.toString());
            }
        }
        return keywords;
    }
}
