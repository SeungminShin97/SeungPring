package org.example.framework.web.interceptor;

import java.util.List;

/**
 * 하나의 HTTP 요청을 처리하기 위한 실행 체인을 표현하는 객체이다.
 *
 * <p>
 * {@code HandlerExecutionChain}은 실제 요청을 처리할 Handler와,
 * 해당 요청 처리 과정 전반에 적용될
 * {@link HandlerInterceptor} 목록을 함께 보관한다.
 * </p>
 *
 * <p>
 * 이 클래스는 {@link org.example.framework.web.mapping.HandlerMapping}에 의해 생성되며,
 * {@link org.example.framework.web.DispatcherServlet}에서
 * 요청 처리 흐름을 제어하는 데 사용된다.
 * </p>
 *
 * <p>
 * 인터셉터의 실행 순서 및 호출 시점은
 * {@code DispatcherServlet}에 의해 결정되며,
 * 본 클래스는 실행 순서나 로직을 직접 제어하지 않는다.
 * </p>
 */
public class HandlerExecutionChain {

    /**
     * 실제 요청을 처리할 Handler 객체.
     *
     * <p>
     * 일반적으로 {@link org.example.framework.web.HandlerMethod} 인스턴스이며,
     * {@link org.example.framework.web.adapter.HandlerAdapter}에 의해 실행된다.
     * </p>
     */
    private final Object handler;

    /**
     * 요청 처리 과정에 적용될 인터셉터 목록.
     *
     * <p>
     * 인터셉터는 등록된 순서대로 {@code preHandle} / {@code postHandle}이 호출되며,
     * {@code afterCompletion}은 역순으로 호출된다.
     * </p>
     */
    private final List<HandlerInterceptor> interceptors;

    /**
     * 실행 대상 Handler와 인터셉터 목록을 포함하는 실행 체인을 생성한다.
     *
     * @param handler      실제 요청을 처리할 Handler
     * @param interceptors 요청 처리 과정에 적용될 인터셉터 목록
     */
    public HandlerExecutionChain(Object handler, List<HandlerInterceptor> interceptors) {
        this.handler = handler;
        this.interceptors = interceptors != null ? interceptors : List.of();
    }

    /**
     * 실행 대상 Handler를 반환한다.
     *
     * @return 요청을 처리할 Handler 객체
     */
    public Object getHandler() {
        return handler;
    }

    /**
     * 적용될 {@link HandlerInterceptor} 목록을 반환한다.
     *
     * @return 인터셉터 리스트
     */
    public List<HandlerInterceptor> getInterceptors() {
        return interceptors;
    }
}
