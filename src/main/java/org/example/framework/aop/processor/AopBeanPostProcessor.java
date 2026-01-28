package org.example.framework.aop.processor;

import org.example.framework.aop.advisor.Advisor;
import org.example.framework.aop.proxy.AopProxyFactory;
import org.example.framework.core.lifecycle.BeanPostProcessor;

import java.util.Arrays;
import java.util.List;

public class AopBeanPostProcessor implements BeanPostProcessor {

    private final List<Advisor> advisors;

    public AopBeanPostProcessor(List<Advisor> advisors) {
        this.advisors = advisors;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {

        boolean applicable = Arrays.stream(bean.getClass().getMethods())
                .anyMatch(method -> advisors.stream().anyMatch(a -> a.matches(method, bean.getClass())));

        if (!applicable)
            return bean;

        return AopProxyFactory.createProxy(bean, advisors);
    }
}