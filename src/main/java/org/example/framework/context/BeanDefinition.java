package org.example.framework.context;

import java.beans.Introspector;

public class BeanDefinition {
    private final Class<?> beanClass;
    private final ScopeType scope;
    private final String beanName;
    private final boolean lazyInit;

    public BeanDefinition(Class<?> clazz) {
        this(clazz, Introspector.decapitalize(clazz.getSimpleName()), ScopeType.SINGLETON, false);
    }

    public BeanDefinition(Class<?> clazz, String beanName) {
        this(clazz, beanName, ScopeType.SINGLETON, false);
    }

    public BeanDefinition(Class<?> clazz, ScopeType scope) {
        this(clazz, Introspector.decapitalize(clazz.getSimpleName()), scope, false);
    }

    public BeanDefinition(Class<?> clazz, String beanName, ScopeType scopeType) {
        this(clazz, beanName, scopeType, false);
    }

    public BeanDefinition(Class<?> clazz, String beanName, ScopeType scopeType, boolean lazyInit) {
        this.beanClass = clazz;
        this.beanName = beanName;
        this.scope = scopeType;
        this.lazyInit = lazyInit;
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

    public boolean isLazyInit() { return lazyInit; }
}
