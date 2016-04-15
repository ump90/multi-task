package com.baidu.unbiz.multitask.spring.integration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by wangchongjie on 16/4/15.
 */
@Component
public class SpringContextAwarePojo implements ApplicationContextAware {

    // Spring应用上下文环境
    protected static ApplicationContext applicationContext;

    protected <T> T bean(String beanName) throws BeansException {
        return (T) applicationContext.getBean(beanName);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextAwarePojo.applicationContext = applicationContext;
    }

}
