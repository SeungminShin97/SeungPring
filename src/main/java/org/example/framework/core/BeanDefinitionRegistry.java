package org.example.framework.core;

import org.example.framework.context.BeanDefinition;

import java.util.List;

public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    BeanDefinition getBeanDefinition(String beanName);

    List<String> getBeanDefinitionNames();

    int getBeanDefinitionCount();

    boolean containsBeanDefinition(String beanName);
}
