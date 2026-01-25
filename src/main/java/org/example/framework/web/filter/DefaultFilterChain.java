package org.example.framework.web.filter;

import org.example.framework.was.container.Servlet;
import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;

import java.util.List;

/**
 * {@link FilterChain}의 기본 구현체
 *
 * <p>
 * 등록된 {@link Filter} 목록을 순차적으로 실행한 뒤,
 * 모든 필터 처리가 완료되면 최종적으로 {@link Servlet}을 호출한다.
 * </p>
 *
 * <p>
 * 필터 실행 순서는 생성 시 전달된 리스트의 순서를 따른다.
 * 각 필터는 {@link #doFilter(HttpRequest, HttpResponse)} 호출을 통해
 * 다음 필터 또는 서블릿 실행을 위임한다.
 * </p>
 */
public class DefaultFilterChain implements FilterChain{

    private final List<Filter> filters;
    private final Servlet servlet;
    private int index = 0;

    /**
     * 필터 체인을 생성한다.
     *
     * @param filters 실행될 필터 목록
     * @param servlet 필터 체인 종료 후 호출될 서블릿
     */
    public DefaultFilterChain(List<Filter> filters, Servlet servlet) {
        this.filters = filters;
        this.servlet = servlet;
    }

    /**
     * 다음 필터를 실행하거나,
     * 모든 필터 실행이 완료된 경우 서블릿을 호출한다.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @throws Exception 필터 또는 서블릿 실행 중 발생한 예외
     */
    @Override
    public void doFilter(HttpRequest request, HttpResponse response) throws Exception {
        if(index < filters.size()) {
            Filter nextFilter = filters.get(index++);
            nextFilter.doFilter(request, response, this);
            return;
        }

        servlet.service(request, response);
    }
}
