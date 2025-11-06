package org.example.framework.exception.bean;

public class BeanCreationException extends BeanException {
    private static final String PREFIX = "Failed to create bean: ";

    public BeanCreationException(String beanName) {
        super(PREFIX + beanName);
    }

    public BeanCreationException(String beanName, Throwable e) {
        super(PREFIX + beanName, e);
    }
}
