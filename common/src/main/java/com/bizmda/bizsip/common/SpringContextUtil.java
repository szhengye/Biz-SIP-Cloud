package com.bizmda.bizsip.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext; // Spring应用上下文环境

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    public static Object getBean(String name, Class<?> requiredType)
            throws BeansException {
        return applicationContext.getBean(name, requiredType);
    }
    public static <T>T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

}
