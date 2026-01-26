package org.example.framework.context.proxy;

import org.example.framework.core.BeanFactory;

import java.lang.reflect.Proxy;

/**
 * Lazy Proxy 생성을 담당하는 팩토리 클래스.
 *
 * <p>
 * JDK 동적 프록시를 기반으로,
 * 실제 빈을 즉시 생성하지 않고
 * 최초 메서드 호출 시점에 초기화되는
 * Lazy Proxy 객체를 생성한다.
 * </p>
 */
public class LazyProxyFactory {

    /**
     * Lazy Proxy 객체를 생성한다.
     *
     * <p>
     * 프록시는 반드시 인터페이스 기반이어야 하며,
     * 실제 구현 객체는 {@link LazyInvocationHandler}를 통해
     * 지연 초기화된다.
     * </p>
     *
     * @param interfaceType 프록시가 구현할 인터페이스 타입
     * @param realType      실제 생성될 빈의 구체 타입
     * @param beanFactory   실제 빈 생성을 담당하는 BeanFactory
     * @return Lazy Proxy 객체
     */
    @SuppressWarnings("unchecked")
    public static Object createLazyProxy(Class<?> interfaceType, Class<?> realType, BeanFactory beanFactory) {
        return Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class<?>[]{interfaceType}, new LazyInvocationHandler(realType, beanFactory));
    }
}
