package org.example.framework.core.lifecycle;

/**
 * Bean 생성 과정 중 확장 포인트를 제공하는 후처리기 인터페이스.
 *
 * - Bean 인스턴스 생성 및 의존성 주입 이후 호출된다.
 * - 초기화 전/후 단계에서 Bean을 가로채거나 교체할 수 있다.
 *
 * Aware 처리, @PostConstruct, 프록시(AOP) 확장의 기반이 된다.
 */
public interface BeanPostProcessor {

    /**
     * Bean 초기화 이전에 호출된다.
     *
     * - 의존성 주입 완료 이후
     * - @PostConstruct, InitializingBean 이전 단계
     *
     * @param bean 처리 대상 Bean 인스턴스
     * @param beanName Bean 이름
     * @return 원본 또는 교체된 Bean 인스턴스
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * Bean 초기화 이후에 호출된다.
     *
     * - @PostConstruct, InitializingBean 이후
     * - 프록시 생성(AOP)에 적합한 단계
     *
     * @param bean 처리 대상 Bean 인스턴스
     * @param beanName Bean 이름
     * @return 원본 또는 교체된 Bean 인스턴스
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
