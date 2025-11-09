package org.example.framework.context;

import java.beans.Introspector;

public class BeanDefinition {
    private final Class<?> beanClass;
    private final ScopeType scope;
    private final String beanName;

    public BeanDefinition(Class<?> clazz) {
        this(clazz, Introspector.decapitalize(clazz.getSimpleName()), ScopeType.SINGLETON);
    }

    public BeanDefinition(Class<?> clazz, String beanName) {
        this(clazz, beanName, ScopeType.SINGLETON);
    }

    public BeanDefinition(Class<?> clazz, ScopeType scope) {
        this(clazz, Introspector.decapitalize(clazz.getSimpleName()), scope);
    }

    public BeanDefinition(Class<?> clazz, String beanName, ScopeType scopeType) {
        this.beanClass = clazz;
        this.beanName = beanName;
        this.scope = scopeType;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public ScopeType getScope() {
        return scope;
    }

    public String getBeanName() { return beanName; }

    public boolean isSingleton() {
        return scope.equals(ScopeType.SINGLETON);
    }

    public boolean isPrototype() {
        return scope.equals(ScopeType.PROTOTYPE);
    }
}
