package org.example.framework.context.processor;

import org.example.framework.context.MyApplicationContext;
import org.example.framework.core.lifecycle.ApplicationContextAware;
import org.example.framework.core.lifecycle.BeanPostProcessor;

/**
 * ApplicationContextAware 구현 Bean에
 * ApplicationContext를 주입하는 후처리기.
 *
 * - 호출 시점: 초기화 이전(beforeInitialization)
 * - 책임: 컨테이너 자신을 Bean에 전달
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {

    private final MyApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(MyApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if(bean instanceof ApplicationContextAware aware)
            aware.setApplicationContext(applicationContext);
        return bean;
    }
}
