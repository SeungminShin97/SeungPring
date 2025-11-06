package org.example.framework.exception.bean;

public class NoSuchBeanDefinitionException extends BeanException{
    public NoSuchBeanDefinitionException(String beanName) {
        super("No BeanDefinition found for bean name: " + beanName);
    }
}
