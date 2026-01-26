package org.example.framework.context.proxy;

import org.example.framework.core.BeanFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 지연 로딩(Lazy Initialization)을 위한 프록시 호출 핸들러.
 *
 * <p>
 * 실제 빈 인스턴스를 즉시 생성하지 않고,
 * 프록시 객체에 대한 첫 메서드 호출 시점에 {@link BeanFactory}를 통해
 * 실제 대상 객체를 조회·초기화한다.
 * </p>
 *
 * <p>
 * 멀티스레드 환경에서 중복 생성을 방지하기 위해
 * double-checked locking 패턴을 사용한다.
 * </p>
 */
public class LazyInvocationHandler implements InvocationHandler {

    private final Class<?> realType;
    private final BeanFactory beanFactory;

    private volatile Object target;

    /**
     * LazyInvocationHandler 생성자.
     *
     * @param realType   실제 생성될 빈의 타입
     * @param beanFactory 빈 조회 및 생성을 담당하는 BeanFactory
     */
    public LazyInvocationHandler(Class<?> realType, BeanFactory beanFactory) {
        this.realType = realType;
        this.beanFactory = beanFactory;
    }

    /**
     * 프록시 객체에 대한 메서드 호출을 가로채 처리한다.
     *
     * <p>
     * {@link Object} 클래스의 메서드(toString, equals 등)는
     * 프록시 자체에 대해 바로 실행한다.
     * </p>
     *
     * <p>
     * 그 외의 경우, 실제 대상 객체가 아직 초기화되지 않았다면
     * 최초 1회 {@link BeanFactory#getBean(Class)} 호출을 통해
     * 대상 객체를 생성한 뒤 해당 메서드를 위임 호출한다.
     * </p>
     *
     * @param proxy  프록시 객체
     * @param method 호출된 메서드
     * @param args   메서드 인자
     * @return 메서드 실행 결과
     * @throws Throwable 대상 메서드 실행 중 발생한 예외
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getDeclaringClass() == Object.class) {
            return switch (method.getName()) {
                case "toString" -> "LazyProxy(" + realType.getSimpleName() + ")";
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> method.invoke(this, args);
            };
        }

        if(target == null) {
            synchronized (this) {
                if(target == null)
                    target = beanFactory.getBean(realType);
            }
        }

        return method.invoke(target, args);
    }
}
