package org.example.framework.was.adapter;

import org.example.framework.was.container.Servlet;
import org.example.framework.was.container.ServletContainer;
import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;

/**
 * {@link ServletAdapter}의 기본 구현체로,
 * Connector 계층에서 전달된 요청을
 * 서블릿 실행 진입점인 {@link Servlet}으로 위임한다.
 *
 * <p>
 * 이 어댑터는 HTTP 프로토콜 처리 영역과
 * 서블릿 실행 영역 사이의 경계를 담당하며,
 * Connector가 서블릿 구현 세부 사항을 알지 않도록
 * 분리하는 역할을 수행한다.
 * </p>
 *
 * <p>
 * 어댑터는 {@link Servlet} 인터페이스까지만 의존하며,
 * 필터 체인, 디스패처 서블릿, 컨트롤러 등
 * 내부 요청 처리 구조에는 관여하지 않는다.
 * </p>
 *
 * @see ServletAdapter
 * @see Servlet
 * @see <a href="https://github.com/apache/tomcat/blob/main/java/org/apache/catalina/connector/CoyoteAdapter.java">
 *      Apache Tomcat CoyoteAdapter
 *      </a>
 */
public class DefaultServletAdapter implements ServletAdapter {

    private final Servlet servlet;

    public DefaultServletAdapter(Servlet servlet) {
        this.servlet = servlet;
    }

    /**
     * Connector 계층에서 전달된 요청을
     * 서블릿 실행 진입점으로 위임한다.
     *
     * <p>
     * 이 호출은 서블릿 컨테이너 내부의
     * 필터 체인 및 디스패처 로직을 포함한
     * 전체 요청 처리 흐름의 시작 지점에 해당한다.
     * </p>
     */
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        servlet.service(request, response);
    }
}
