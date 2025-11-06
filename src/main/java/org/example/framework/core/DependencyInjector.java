package org.example.framework.core;

public interface DependencyInjector {
    
    void inject(Object target, BeanFactory beanFactory);
}
