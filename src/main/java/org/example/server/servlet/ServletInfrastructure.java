package org.example.server.servlet;


import org.example.framework.core.ApplicationContext;
import org.example.framework.was.container.Servlet;
import org.example.framework.was.container.ServletContainer;
import org.example.framework.web.DispatcherServlet;
import org.example.framework.web.filter.Filter;
import org.example.framework.web.filter.FilterRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

/**
 * Servlet 계층의 실행 구성을 담당하는 인프라 조립기.
 *
 * <p>
 * IoC 컨텍스트로부터 {@link DispatcherServlet}을 조회하고,
 * 이를 {@link ServletContainer}로 감싸
 * WAS 계층에 전달할 실행 가능한 {@link Servlet}을 구성한다.
 * </p>
 */
public class ServletInfrastructure {
    private static final Logger log = LoggerFactory.getLogger(ServletInfrastructure.class);

    private final DispatcherServlet dispatcherServlet;
    private final ServletContainer servletContainer;

    public ServletInfrastructure(ApplicationContext context) {
        this.dispatcherServlet = context.getBean(DispatcherServlet.class);

        List<FilterRegistration> registrations = context.getBeansOfType(FilterRegistration.class);

        List<Filter> filters = registrations.stream()
                .sorted(Comparator.comparingInt(FilterRegistration::order))
                .map(FilterRegistration::filter)
                .toList();

        this.servletContainer = new ServletContainer(dispatcherServlet, filters);

        log.info("[Servlet] DispatcherServlet initialized");
    }

    /**
     * WAS 계층에 전달할 최종 Servlet을 반환한다.
     */
    public Servlet servlet() {
        return servletContainer;
    }
}
