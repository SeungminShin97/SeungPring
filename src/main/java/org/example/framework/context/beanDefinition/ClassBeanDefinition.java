package org.example.framework.context.beanDefinition;

import org.example.framework.annotation.LazyProxy;
import org.example.framework.context.ScopeType;
import org.example.framework.context.capability.LazyProxyCapable;

import java.beans.Introspector;
import java.lang.annotation.Annotation;

/**
 * 일반 클래스 기반 빈 정의
 *
 * <p>
 * @Component, @Controller 등의 클래스 레벨 빈을 표현한다.
 * </p>
 */
public class ClassBeanDefinition extends BeanDefinition implements LazyProxyCapable {

    private final Class<?> beanClass;
    private boolean lazyProxy;

    public ClassBeanDefinition(Class<?> clazz) {
        super(Introspector.decapitalize(clazz.getSimpleName()), ScopeType.SINGLETON, false);
        this.beanClass = clazz;
    }

    public ClassBeanDefinition(Class<?> clazz, String beanName) {
        super(beanName, ScopeType.SINGLETON, false);
        this.beanClass = clazz;
    }

    public ClassBeanDefinition(Class<?> clazz, ScopeType scope) {
        super(Introspector.decapitalize(clazz.getSimpleName()), scope, false);
        this.beanClass = clazz;
    }

    public ClassBeanDefinition(Class<?> clazz, String beanName, ScopeType scope) {
        super(beanName, scope, false);
        this.beanClass = clazz;
    }

    public ClassBeanDefinition(Class<?> beanClass, String beanName, ScopeType scope, boolean lazyInit) {
        super(beanName, scope, lazyInit);
        this.beanClass = beanClass;
    }

    @Override
    public BeanDefinitionType getType() {
        return BeanDefinitionType.CLASS;
    }

    @Override
    public Class<?> getResolvableType() {
        return beanClass;
    }

    @Override
    public void setLazyProxy() {
        this.lazyProxy = true;
    }

    @Override
    public boolean isLazyProxy() {
        return lazyProxy;
    }
}
