package org.example.framework.context.processor;

import org.example.framework.core.lifecycle.BeanPostProcessor;
import org.example.framework.core.lifecycle.InitializingBean;

/**
 * InitializingBean 인터페이스를 구현한 Bean의
 * afterPropertiesSet()을 호출하는 후처리기.
 *
 * <p>
 *  - 호출 시점: 초기화 이전(beforeInitialization)<br>
 *  - 실행 순서: @PostConstruct 이후
 * </p>
 */
public class InitializingBeanProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof InitializingBean initializingBean) {
            initializingBean.afterPropertiesSet();
        }
        return bean;
    }
}
