package org.example.framework.context;

public class BeanDefinition {
    private Class<?> beanClass;
    private ScopeType scope;
    private String beanName;

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public ScopeType getScope() {
        return scope;
    }

    public String getBeanName() { return beanName; }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public void setScope(ScopeType scope) {
        this.scope = scope;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public boolean isSingleton() {
        return scope.equals(ScopeType.SINGLETON);
    }

    public boolean isPrototype() {
        return scope.equals(ScopeType.PROTOTYPE);
    }
}
