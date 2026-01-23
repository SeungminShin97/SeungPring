package org.example.framework.context.processor;

import org.example.framework.annotation.PostConstruct;
import org.example.framework.core.lifecycle.BeanPostProcessor;

import java.lang.reflect.Method;

/**
 * {@link org.example.framework.annotation.PostConstruct} 어노테이션이 선언된 메서드를 실행하는 후처리기.
 * <p>
 *   - 호출 시점: 초기화 이전(beforeInitialization) <br>
 *   - 전제: 의존성 주입 완료 상태
 * </p>
 */
public class PostConstructProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        Class<?> beanClass = bean.getClass();

        for (Method method : beanClass.getDeclaredMethods()) {

            if (!method.isAnnotationPresent(PostConstruct.class))
                continue;

            // @PostConstruct 메서드는 파라미터를 허용하지 않음
            if (method.getParameterCount() != 0) {
                throw new IllegalStateException(
                        "@PostConstruct method must have no arguments: " + beanClass.getName() + "#" + method.getName()
                );
            }

            try {
                method.setAccessible(true);
                method.invoke(bean);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to invoke @PostConstruct method: " + beanClass.getName() + "#" + method.getName(), e
                );
            }
        }

        return bean;
    }
}
