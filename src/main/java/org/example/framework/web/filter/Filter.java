package org.example.framework.web.filter;

import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;

/**
 * 요청/응답 전후 처리를 담당하는 필터 인터페이스
 *
 * - DispatcherServlet 호출 전/후 공통 로직 수행
 * - 체인 기반 호출 구조를 가진다
 */
public interface Filter {

    /**
     * 필터 로직을 수행한다.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @param chain    다음 필터 또는 서블릿 호출 체인
     */
    void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) throws Exception;
}