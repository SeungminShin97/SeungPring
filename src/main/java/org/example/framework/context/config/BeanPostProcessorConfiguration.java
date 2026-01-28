package org.example.framework.context.config;

import org.example.framework.annotation.Bean;
import org.example.framework.annotation.Configuration;
import org.example.framework.annotation.Order;
import org.example.framework.aop.advisor.Advisor;
import org.example.framework.aop.processor.AopBeanPostProcessor;
import org.example.framework.context.processor.InitializingBeanProcessor;
import org.example.framework.context.processor.PostConstructProcessor;
import org.example.framework.core.lifecycle.BeanPostProcessor;

import java.util.List;

@Configuration
public class BeanPostProcessorConfiguration {

    @Bean
    @Order(100)
    public BeanPostProcessor postConstructProcessor() {
        return new PostConstructProcessor();
    }

    @Bean
    @Order(200)
    public BeanPostProcessor initializingBeanProcessor() {
        return new InitializingBeanProcessor();
    }

    @Bean
    @Order(1000)
    public BeanPostProcessor aopBeanPostProcessor(
            List<Advisor> advisors
    ) {
        return new AopBeanPostProcessor(advisors);
    }
}
