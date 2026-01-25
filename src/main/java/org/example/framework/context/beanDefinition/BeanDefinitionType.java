package org.example.framework.context.beanDefinition;

import org.example.framework.annotation.Bean;
import org.example.framework.annotation.Configuration;

public enum BeanDefinitionType {

    /**
     * 일반 클래스 기반 빈
     */
    CLASS,

    /**
     * {@link Configuration} 클래스 자체를 나타내는 빈
     */
    CONFIGURATION,

    /**
     * {@link Bean} 메서드 기반 팩토리 빈
     */
    METHOD
}
