package org.example.framework.exception.bean;

public class CircularDependencyException extends BeanCreationException {
    private static final String PREFIX = "Circular dependency detected for bean: ";

    public CircularDependencyException(String beanName) {
        super(PREFIX + beanName);
    }

    public CircularDependencyException(String beanName, Throwable cause) {
        super(PREFIX + beanName, cause);
    }
}

