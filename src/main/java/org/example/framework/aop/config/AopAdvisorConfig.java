package org.example.framework.aop.config;

import org.example.framework.annotation.Bean;
import org.example.framework.annotation.Configuration;
import org.example.framework.aop.advisor.Advisor;
import org.example.framework.aop.annotation.Profiled;
import org.example.framework.aop.annotation.Retry;
import org.example.framework.aop.interceptor.ProfiledInterceptor;
import org.example.framework.aop.interceptor.RetryInterceptor;
import org.example.framework.aop.pointcut.AnnotationPointcut;

@Configuration
public class AopAdvisorConfig {

    @Bean
    public Advisor retryAdvisor() {
        return new Advisor(
                new AnnotationPointcut(Retry.class),
                new RetryInterceptor()
        );
    }

    @Bean
    public Advisor profiledAdvisor() {
        return new Advisor(
                new AnnotationPointcut(Profiled.class),
                new ProfiledInterceptor()
        );
    }
}