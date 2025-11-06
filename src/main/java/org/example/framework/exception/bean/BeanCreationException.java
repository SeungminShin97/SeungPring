package org.example.framework.exception.bean;

public class BeanCreationException extends BeanException{
    public BeanCreationException(String beanName) {
        super("Failed to create bean: " + beanName);
    }
}
