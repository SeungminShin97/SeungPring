package org.example.framework.context;

import org.example.framework.core.BeanDefinitionRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBeanDefinitionRegistry implements BeanDefinitionRegistry {

    private final Map<String, BeanDefinition> beanDefinitions;

    private MyBeanDefinitionRegistry() {
        beanDefinitions = new HashMap<>();
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        if(containsBeanDefinition(beanName))
           return;
        beanDefinitions.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanDefinitions.get(beanName);
    }

    @Override
    public List<String> getBeanDefinitionNames() {
        return new ArrayList<>(beanDefinitions.keySet());
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanDefinitions.size();
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitions.containsKey(beanName);
    }
}
