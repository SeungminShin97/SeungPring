package org.example.framework.exception.bean;

public class NoSuchBeanDefinitionException extends BeanException {
    private static final String PREFIX = "No BeanDefinition found for bean name: ";

    public NoSuchBeanDefinitionException(String beanName) {
        super(PREFIX + beanName);
    }

    public NoSuchBeanDefinitionException(String beanName, Throwable cause) {
        super(PREFIX + beanName, cause);
    }
}
