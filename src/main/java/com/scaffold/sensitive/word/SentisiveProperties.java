package com.scaffold.sensitive.word;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: tianjl
 * @Date: 2020/8/17 14:12
 * @Eamil: 2695062879@qq.com
 */
@Configuration
@ConfigurationProperties(prefix="net.cnki.sensitive")
public class SentisiveProperties {

    public List<String> sensitives=new ArrayList<>(16);

    //敏感詞文件名
    private String sensitiveFile;


    public List<String> getSensitiveFile() {
        if (sensitiveFile.contains(",")){
            sensitives= Arrays.asList(sensitiveFile.split(","));
        }else{
            sensitives.add(sensitiveFile);
        }
        return sensitives;
    }

    public void setSensitiveFile(String sensitiveFile) {
        if (sensitiveFile.contains(",")||sensitiveFile.contains(".")){

        }else{
            System.err.println("输入的文件不合适");
            System.exit(1);
        }
        this.sensitiveFile = sensitiveFile;
    }

    public List<String> getSensitives() {
        return getSensitiveFile();
    }
}
