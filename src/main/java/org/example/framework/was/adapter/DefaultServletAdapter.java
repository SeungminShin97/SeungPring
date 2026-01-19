package org.example.framework.was.adapter;

import org.example.framework.was.connector.Connector;
import org.example.framework.was.container.ServletContainer;
import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;
import org.example.framework.was.server.Service;

/**
 * {@link ServletAdapter}의 기본 구현체로,
 * Connector 계층에서 전달된 요청을 {@link Service} 계층으로 위임한다.
 *
 * <p>
 * 이 어댑터는 Connector가 ServletContainer를 직접 참조하지 않도록
 * 중간 연결 지점을 제공하는 역할만 수행하며,
 * 요청 처리 로직이나 정책에는 관여하지 않는다.
 * </p>
 *
 * <p>
 * 실제 요청 처리는 {@link Service}를 거쳐
 * {@link ServletContainer} 및 그 내부의 Servlet 구현체로 전달된다.
 * </p>
 *
 * @see ServletAdapter
 * @see Service
 * @see <a href="https://github.com/apache/tomcat/blob/main/java/org/apache/catalina/connector/CoyoteAdapter.java">
 *      Apache Tomcat CoyoteAdapter
 *      </a>
 */
public class DefaultServletAdapter implements ServletAdapter {

    private final Service service;

    public DefaultServletAdapter(Service service) {
        this.service = service;
    }

    /**
     * Connector 계층에서 전달된 요청을
     * {@link Service} 계층으로 위임한다.
     *
     * <p>
     * 이후 요청은 Service를 거쳐
     * {@link ServletContainer} 및 내부 Servlet 구현체로 전달된다.
     * </p>
     */
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        service.handle(request, response);
    }
}
