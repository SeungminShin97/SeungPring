package org.example.framework.web.interceptor;

import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;

public interface HandlerInterceptor {

    /**
     * 컨트롤러 실행 전
     * false 반환 시 이후 체인 중단
     */
    default boolean preHandle(HttpRequest request, HttpResponse response, Object handler) throws Exception {
        return true;
    }

    /**
     * 컨트롤러 실행 후, 응답 작성 전
     */
    default void postHandle(HttpRequest request, HttpResponse response, Object handler) throws Exception {
    }

    /**
     * 요청 완료 후 (예외 포함)
     */
    default void afterCompletion(
            HttpRequest request,
            HttpResponse response,
            Object handler,
            Exception ex
    ) throws Exception {
    }
}
