package org.example.framework.was.adapter;

import org.example.framework.was.connector.Connector;
import org.example.framework.was.container.ServletContainer;
import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;

/**
 * {@link ServletAdapter}의 기본 구현체입니다.
 *
 * <p>
 * 이 구현체는 WAS 외부(Connector 계층)에서 전달된 요청을
 * 내부 Servlet Container 계층으로 위임하기 위한
 * <strong>어댑터(Adapter) 역할</strong>을 수행합니다.
 * </p>
 *
 * <p>
 * 본 클래스의 책임은 <em>요청 흐름을 연결하는 것</em>까지이며,
 * 실제 요청 처리 로직이나 서블릿 선택, 필터 처리에는 관여하지 않습니다.
 * 이를 통해 Connector 계층과 Container 계층 사이의
 * 의존성을 명확히 분리합니다.
 * </p>
 *
 * <p>
 * 내부적으로 {@link Connector}를 통해
 * Service → Container로 흐름을 전달하며,
 * 이 과정에서 요청/응답 객체는 그대로 전달됩니다.
 * </p>
 *
 * <p>
 * 이 구조는 Tomcat의 Connector → Engine(Container) 흐름과 유사하며,
 * Connector가 직접 컨테이너 로직을 알지 않도록
 * 중간 어댑터 계층을 두는 설계 의도를 가집니다.
 * </p>
 *
 * @see ServletAdapter
 * @see Connector
 * @see <a href="https://github.com/apache/tomcat/blob/main/java/org/apache/catalina/connector/CoyoteAdapter.java">
 *      Apache Tomcat CoyoteAdapter
 *      </a>
 */
public class DefaultServletAdapter implements ServletAdapter {

    private final ServletContainer container;

    /**
     * Connector 계층과의 연결을 담당하는 어댑터를 생성합니다.
     *
     * @param container
     */
    public DefaultServletAdapter(ServletContainer container) {
        this.container = container;
    }

    /**
     * Connector 계층에서 전달된 요청을
     * Servlet Container 계층으로 위임합니다.
     *
     * <p>
     * 처리 결과는 {@link HttpResponse} 객체에 직접 기록되며,
     * 반환값은 존재하지 않습니다.
     * 이후 응답 객체는 Connector 계층으로 다시 전달되어
     * 네트워크 레벨 응답으로 변환됩니다.
     * </p>
     *
     * @param request 파싱이 완료된 HTTP 요청 객체
     * @param response 처리 결과를 기록할 HTTP 응답 객체
     */
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        container.service(request, response);
    }
}
