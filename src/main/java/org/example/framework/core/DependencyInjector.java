package org.example.framework.core;

/**
 * {@code DependencyInjector}는 Bean 인스턴스에 의존성을 주입하기 위한 전략 인터페이스다.
 * <p>구현체는 리플렉션 등을 활용해 필드나 메서드에 {@code @Inject}, {@code @Autowired} 등의
 * 주입 어노테이션이 붙은 의존성을 해석하고 {@link BeanFactory}를 통해 주입 대상을 조회·설정한다.</p>
 */
public interface DependencyInjector {

    /**
     * 주어진 객체에 필요한 의존성을 주입한다.
     *
     * @param target       주입 대상 객체
     * @param beanFactory  의존성 조회에 사용할 BeanFactory
     */
    void inject(Object target, BeanFactory beanFactory);
}
