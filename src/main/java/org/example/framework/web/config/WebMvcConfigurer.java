package org.example.framework.web.config;

import org.example.framework.web.interceptor.InterceptorRegistry;

/**
 * 웹 계층에 대한 사용자 정의 설정을 제공하기 위한 확장 포인트이다.
 *
 * <p>
 * 이 인터페이스는 애플리케이션이
 * 웹 요청 처리 과정에 개입할 수 있는 설정 훅을 제공하며,
 * 프레임워크는 구현체를 직접 호출하지 않고
 * 초기화 과정에서 일괄적으로 처리한다.
 * </p>
 *
 * <p>
 * 구현체는 일반적으로 인터셉터 등록과 같은
 * 웹 파이프라인 구성 작업을 수행한다.
 * </p>
 */
public interface WebMvcConfigurer {

    /**
     * 애플리케이션에서 사용할 {@link org.example.framework.web.interceptor.HandlerInterceptor}
     * 를 등록한다.
     *
     * <p>
     * 이 메서드는 애플리케이션 초기화 단계에서 한 번 호출되며,
     * 요청 처리 시점에는 호출되지 않는다.
     * </p>
     *
     * @param registry 인터셉터를 등록하기 위한 레지스트리
     */
    default void addInterceptors(InterceptorRegistry registry) {}
}
