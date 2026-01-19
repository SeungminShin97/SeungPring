package org.example.framework.was.server;

import org.example.framework.was.container.ServletContainer;
import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;

/**
 * WAS 내부에서 Connector 계층과 {@link ServletContainer}를 연결하는
 * 논리적 서비스 계층이다.
 *
 * <p>
 * Connector는 Service를 통해서만 Container에 접근하며,
 * 이를 통해 네트워크 계층과 서블릿 실행 계층 간의
 * 의존성을 분리한다.
 * </p>
 *
 * <p>
 * Service는 향후 필터 체인, 공통 정책 처리 등과 같은
 * 요청 전·후 처리 확장을 위한 진입 지점으로 사용될 수 있다.
 * </p>
 *
 * @see ServletContainer
 * @see <a href="https://github.com/apache/tomcat/blob/main/java/org/apache/catalina/Service.java">
 *      Apache Tomcat Service Interface
 *      </a>
 */
public class Service {

    private final ServletContainer container;

    /**
     * Service 계층에서 사용할 ServletContainer를 주입받아 초기화합니다.
     *
     * <p>
     * 이 시점 이후 Connector 계층은
     * Service를 통해서만 Container에 접근하게 되며,
     * 직접적인 의존 관계는 형성하지 않습니다.
     * </p>
     *
     * @param container 요청 처리를 담당하는 ServletContainer
     */
    public Service(ServletContainer container) {
        this.container = container;
    }

    /**
     * Connector 계층에서 전달된 요청을
     * 연결된 {@link ServletContainer}로 위임한다.
     */
    public void handle(HttpRequest request, HttpResponse response) throws Exception {
        container.service(request, response);
    }

    /**
     * Service가 관리하는 ServletContainer를 반환합니다.
     *
     * <p>
     * 이 메서드는 전체 요청 처리 흐름 중
     * <strong>Connector → Container 진입 지점</strong>에 해당하며,
     * 반환된 Container는 이후 요청 처리 전 과정을 담당합니다.
     * </p>
     *
     * @return 현재 Service에 연결된 ServletContainer
     */
    public ServletContainer getContainer() {
        return container;
    }
}
