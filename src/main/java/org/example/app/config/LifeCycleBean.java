package org.example.app.config;

import org.example.framework.annotation.Component;
import org.example.framework.annotation.PostConstruct;
import org.example.framework.annotation.PreDestroy;
import org.example.framework.core.lifecycle.DisposableBean;
import org.example.framework.core.lifecycle.InitializingBean;

@Component
public class LifeCycleBean
        implements InitializingBean, DisposableBean {

    @PostConstruct
    public void postConstruct() {
        System.out.println("[LifeCycle] @PostConstruct");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("[LifeCycle] InitializingBean");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("[LifeCycle] @PreDestroy");
    }

    @Override
    public void destroy() {
        System.out.println("[LifeCycle] DisposableBean.destroy");
    }
}
