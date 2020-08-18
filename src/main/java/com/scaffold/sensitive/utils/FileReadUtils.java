package com.scaffold.sensitive.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: tianjl
 * @Date: 2020/8/17 12:53
 * @Eamil: 2695062879@qq.com
 */
public class FileReadUtils {


    public static List<String> readFile(String filePath) {
        List<String> list=new ArrayList<String>(1024);
        ClassPathResource  resource = new ClassPathResource(filePath);
        BufferedReader reader = null;
        try {
            InputStream inputStream = resource.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                list.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
