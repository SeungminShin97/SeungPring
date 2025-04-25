package org.example.framework.context;

import org.example.framework.core.BeanDefinitionRegistry;
import org.example.framework.exception.BeanException;

import java.util.*;

public class MyBeanDefinitionRegistry implements BeanDefinitionRegistry {

    private final Map<String, BeanDefinition> beanDefinitions;

    MyBeanDefinitionRegistry() {
        beanDefinitions = new HashMap<>();
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        Objects.requireNonNull(beanDefinition, "BeanDefinition is null");
        Objects.requireNonNull(beanName, "BeanName is null");

        if(containsBeanDefinition(beanName)) return;

        beanDefinitions.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        Objects.requireNonNull(beanName, "BeanName is null");
        BeanDefinition beanDefinition = beanDefinitions.get(beanName);
        if (beanDefinition == null)
            throw new BeanException(BeanException.BeanExceptionCode.BEANDEFINITION_NOT_FOUND);
        return beanDefinition;
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
