package org.example.framework.context.beanDefinition;

import org.example.framework.context.ScopeType;


/**
 * @Configuration 클래스 기반 빈 정의
 */
public class ConfigurationBeanDefinition extends BeanDefinition{

    private final Class<?> configurationClass;

    public ConfigurationBeanDefinition(
            Class<?> configurationClass,
            String beanName,
            ScopeType scope,
            boolean lazyInit
    ) {
        super(beanName, scope, lazyInit);
        this.configurationClass = configurationClass;
    }

    @Override
    public BeanDefinitionType getType() {
        return BeanDefinitionType.CONFIGURATION;
    }

    @Override
    public Class<?> getResolvableType() {
        return configurationClass;
    }

}
