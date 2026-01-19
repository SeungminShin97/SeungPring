package org.example.framework.web.adapter;

import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;

/**
 * {@code HandlerAdapter}는 특정 Handler 타입을 실제로 실행하기 위한
 * 어댑터 전략 인터페이스다.
 *
 * <p>{@link org.example.framework.web.mapping.HandlerMapping}을 통해 선택된
 * Handler를 받아, 요청과 응답을 전달하여 실행을 위임한다.</p>
 */
public interface HandlerAdapter {

    /**
     * 주어진 handler를 이 어댑터가 지원하는지 여부를 반환한다.
     *
     * @param handler 실행 대상 Handler
     * @return 지원 가능하면 true, 아니면 false
     */
    boolean supports(Object handler);

    /**
     * 주어진 handler를 실행한다.
     *
     * @param request  현재 HTTP 요청
     * @param response 현재 HTTP 응답
     * @param handler  실행 대상 Handler
     * @return handler 실행 결과
     * @throws Exception handler 실행 중 발생한 예외
     */
    Object handle(HttpRequest request, HttpResponse response, Object handler) throws Exception;
}
