package org.example.framework.context.beanDefinition;

import org.example.framework.context.ScopeType;

import java.lang.reflect.Method;

public class MethodBeanDefinition extends BeanDefinition{

    private final Method factoryMethod;
    private final String configurationBeanName;
    private final Class<?> returnType;

    /**
     * @Bean 메서드 기반 빈 정의
     *
     * <p>
     * Configuration 빈을 통해 생성되는 팩토리 메서드 기반 빈을 표현한다.
     * </p>
     */
    public MethodBeanDefinition(
            String beanName,
            ScopeType scope,
            boolean lazyInit,
            Method factoryMethod,
            String configurationBeanName
    ) {
        super(beanName, scope, lazyInit);
        this.factoryMethod = factoryMethod;
        this.configurationBeanName = configurationBeanName;
        this.returnType = factoryMethod.getReturnType();
    }

    @Override
    public BeanDefinitionType getType() {
        return BeanDefinitionType.METHOD;
    }

    @Override
    public Class<?> getResolvableType() {
        return returnType;
    }

    /**
     * 빈 생성을 담당하는 팩토리 메서드를 반환한다.
     */
    public Method getFactoryMethod() {
        return factoryMethod;
    }

    /**
     * 해당 메서드를 소유한 Configuration 빈 이름을 반환한다.
     */
    public String getConfigurationBeanName() {
        return configurationBeanName;
    }
}
