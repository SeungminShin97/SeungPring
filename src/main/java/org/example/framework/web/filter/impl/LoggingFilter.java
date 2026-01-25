package org.example.framework.web.filter.impl;

import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;
import org.example.framework.web.filter.Filter;
import org.example.framework.web.filter.FilterChain;

/**
 * 요청 시작/종료 로그를 출력하는  필터
 */
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) throws Exception {
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("[FILTER]" + request.getPath() + " " + elapsed + "ms");
        }
    }
}
