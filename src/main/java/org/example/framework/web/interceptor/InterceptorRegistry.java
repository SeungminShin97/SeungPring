package org.example.framework.web.interceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link HandlerInterceptor}를 등록하고 관리하는 레지스트리이다.
 *
 * <p>
 * 이 클래스는 애플리케이션 초기화 단계에서
 * {@link org.example.framework.web.config.WebMvcConfigurer}에 의해 구성되며,
 * 이후 요청 처리 과정에서는 읽기 전용으로 사용된다.
 * </p>
 *
 * <p>
 * 등록된 인터셉터들의 실행 순서는
 * {@link #addInterceptor(HandlerInterceptor)} 호출 순서를 따른다.
 * </p>
 */
public class InterceptorRegistry {

    private final List<HandlerInterceptor> interceptors = new ArrayList<>();

    /**
     * 주어진 {@link HandlerInterceptor}를 레지스트리에 등록한다.
     *
     * <p>
     * 인터셉터는 등록된 순서대로 실행되며,
     * 이 메서드는 초기화 단계에서만 호출되는 것을 전제로 한다.
     * </p>
     *
     * @param interceptor 등록할 인터셉터
     * @return 메서드 체이닝을 위한 현재 {@code InterceptorRegistry} 인스턴스
     */
    public InterceptorRegistry addInterceptor(HandlerInterceptor interceptor) {
        this.interceptors.add(interceptor);
        return this;
    }

    /**
     * 등록된 {@link HandlerInterceptor} 목록을 반환한다.
     *
     * <p>
     * 반환되는 리스트는 수정 불가능하며,
     * 요청 처리 과정에서 안전하게 순회할 수 있다.
     * </p>
     *
     * @return 불변 {@link HandlerInterceptor} 리스트
     */
    public List<HandlerInterceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }
}
