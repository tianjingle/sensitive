package com.scaffold.sensitive.utils;

import net.cnki.sensitive.word.SentisiveProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: tianjl
 * @Date: 2020/8/17 12:49
 * @Eamil: 2695062879@qq.com
 */
@Configuration
@ConditionalOnBean(SentisiveProperties.class)
public class SensitiveHelper {


    @Autowired
    private SentisiveProperties sentisiveProperties;

    /**
     * 所有的敏感詞
     */
    public List<String> allSensitive=new ArrayList<String>(2048);


    /**
     * 获取所有敏感词
     * @return
     */
    public List<String> getSensitives() {
        List<String> sensitives=sentisiveProperties.getSensitives();
        for (int i = 0; i < sensitives.size(); i++) {
            List<String> tempList=FileReadUtils.readFile("sensitive/"+sensitives.get(i));
            allSensitive.addAll(tempList);
        }
        return allSensitive;
    }
}
