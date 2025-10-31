package org.example.framework.context;

public class BeanDefinition {
    private Class<?> beanClass;
    private ScopeType scope;
    private String beanName;

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
