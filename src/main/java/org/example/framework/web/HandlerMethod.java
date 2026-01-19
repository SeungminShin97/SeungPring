package org.example.framework.web;

import java.lang.reflect.Method;

/**
 * {@code HandlerMethod}는 컨트롤러 Bean과
 * 실제 실행될 메서드를 함께 감싸는 실행 단위 객체다.
 *
 * <p>{@link org.example.framework.web.mapping.HandlerMapping}에 의해
 * 선택되며, {@link org.example.framework.web.adapter.HandlerAdapter}를 통해
 * 실제 호출된다.</p>
 */
public class HandlerMethod {

    private final Object bean;
    private final Method method;

    public HandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }
}
