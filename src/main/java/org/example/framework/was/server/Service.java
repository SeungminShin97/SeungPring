package org.example.framework.was.server;

import org.example.framework.was.container.ServletContainer;

/**
 * WAS 내부에서 <strong>Connector와 Servlet Container를 연결하는 논리적 서비스 단위</strong>입니다.
 *
 * <p>
 * 이 클래스는 요청 처리 흐름 상
 * <strong>Connector 계층과 Container 계층 사이의 결합 지점</strong>을 담당합니다.
 * Connector는 직접 Container 구현을 알지 않으며,
 * 반드시 Service를 통해 Container에 접근합니다.
 * </p>
 *
 * <p>
 * 본 설계는 Tomcat의 {@code Service} / {@code StandardService} 구조를 참고하였으며,
 * Connector ↔ Container 사이의 중간 계층을 두어
 * 역할 분리와 구조적 확장 가능성을 확보하는 것을 목표로 합니다.
 * </p>
 *
 * @see ServletContainer
 * @see <a href="https://github.com/apache/tomcat/blob/main/java/org/apache/catalina/Service.java">
 *      Apache Tomcat Service Interface
 *      </a>
 * @see <a href="https://github.com/apache/tomcat/blob/main/java/org/apache/catalina/core/StandardService.java">
 *      Apache Tomcat StandardService Implementation
 *      </a
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
