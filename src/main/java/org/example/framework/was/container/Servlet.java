package org.example.framework.was.container;

import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;

/**
 * WAS 내부에서 사용하는 <strong>서블릿 표준 계약(Contract)</strong>을 정의하는 인터페이스입니다.
 *
 * <p>
 * 이 인터페이스는 프레임워크 내부 요청/응답 처리 흐름과
 * 애플리케이션 레벨 비즈니스 로직 사이의
 * <strong>명확한 실행 경계</strong>를 형성합니다.
 * </p>
 *
 * <p>
 * 요청 처리 흐름 상 위치는 다음과 같습니다.
 * <pre>
 * Connector
 *   → Service
 *     → ServletContainer
 *       → Servlet (이 인터페이스)
 * </pre>
 * </p>
 *
 * @see <a href="https://github.com/jakartaee/servlet/blob/main/api/src/main/java/jakarta/servlet/Servlet.java">
 *      Jakarta Servlet Specification - Servlet Interface
 *      </a>
 */
public interface Servlet {

    /**
     * 서블릿 인스턴스 초기화를 수행합니다.
     *
     * <p>
     * 이 메서드는 WAS(ServletContainer)에 의해
     * 서블릿 등록 이후 단 한 번 호출됩니다.
     * </p>
     */
    default void init() throws Exception {}

    /**
     * 단일 HTTP 요청에 대한 처리를 수행합니다.
     *
     * <p>
     * 처리 결과는 {@link HttpResponse} 객체에 직접 기록되며,
     * 반환값은 존재하지 않습니다.
     * 이후 응답 객체는 상위 계층(ServletContainer → Service → Connector)으로
     * 전달되어 네트워크 응답으로 변환됩니다.
     * </p>
     *
     * @param request WAS 레벨에서 파싱된 HTTP 요청 객체
     * @param response 처리 결과를 기록할 HTTP 응답 객체
     * @throws Exception 서블릿 처리 중 발생하는 모든 예외
     */
    void service(HttpRequest request, HttpResponse response) throws Exception;

    /**
     * 서블릿 종료 시 리소스 정리를 수행합니다.
     */
    default void destroy() throws Exception {}
}
