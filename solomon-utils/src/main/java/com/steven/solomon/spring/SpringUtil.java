package com.steven.solomon.spring;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

import com.steven.solomon.verification.ValidateUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具类
 */
@Configuration
@Order(1)
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    public SpringUtil(ApplicationContext applicationContext) {
        SpringUtil.context = applicationContext;
    }

    /**
     * Spring在bean初始化后会判断是不是ApplicationContextAware的子类
     * 如果该类是,setApplicationContext()方法,会将容器中ApplicationContext作为参数传入进去
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtil.context == null) {
            SpringUtil.context = applicationContext;
        }
    }

    public static void setContext(ApplicationContext applicationContext) {
        if (SpringUtil.context == null) {
            SpringUtil.context = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * 通过Name返回指定的Bean
     */
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static <T> T getBean(String name, Class<T> beanClass) {
        return context.getBean(name, beanClass);
    }

    /**
     * 根据注解找到使用注解的类
     *
     * @param annotationType 注解class
     */
    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return context.getBeansWithAnnotation(annotationType);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return context.getBeansOfType(type);
    }

    /**
     * 读取#{}和${}值
     */
    public static String getElValue(String elKey,String defaultValue){
        return ValidateUtils.getOrDefault(getElValue(elKey),defaultValue);
    }

    /**
     * 读取#{}和${}值
     */
    public static String getElValue(String elKey){
        if(ValidateUtils.isNotEmpty(elKey) && ValidateUtils.isELExpression(elKey)){
            elKey = ValidateUtils.extractPropertyName(elKey);
            if(ValidateUtils.isNotEmpty(elKey)){
                return context.getEnvironment().getProperty(elKey);
            }
        }
        return elKey;
    }

    public static <T> Map<String, ParameterizedTypeReference<?>> getAllMQServicesWithGenerics(Class<T> clazz) {
        Map<String, ParameterizedTypeReference<?>> result = new HashMap<>();
        Map<String, T> beans = context.getBeansOfType(clazz);

        for (Map.Entry<String, T> entry : beans.entrySet()) {
            ParameterizedTypeReference<?> typeRef = new ParameterizedTypeReference<T>() {};
            result.put(entry.getKey(), typeRef);
        }
        return result;
    }
}
