package org.example.framework.web.filter;

import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;

/**
 * 필터 호출 흐름을 제어하는 체인 인터페이스
 */
public interface FilterChain {

    /**
     * 다음 필터 또는 최종 서블릿을 호출한다.
     */
    void doFilter(HttpRequest request, HttpResponse response) throws Exception;
}