package org.example.framework.was.container;

import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;

/**
 * WAS 내부에서 <strong>서블릿 표준(Servlet 계약)을 실행하는 컨테이너 경계</strong>입니다.
 *
 * <p>
 * 이 클래스는 네트워크 계층(WAS)과
 * 애플리케이션 계층(Servlet 구현) 사이의
 * <strong>명확한 실행 경계</strong>를 형성합니다.
 * </p>
 *
 * <p>
 * 요청 처리 흐름 상 위치는 다음과 같습니다.
 * <pre>
 * Connector
 *   → Service
 *     → ServletContainer
 *       → Servlet (표준 계약)
 * </pre>
 * </p>
 *
 * <p>
 * 본 컨테이너의 책임은 다음으로 한정됩니다.
 * <ul>
 *     <li>서블릿 표준 인터페이스에 대한 진입 지점 제공</li>
 *     <li>프레임워크 내부 요청/응답 객체를 서블릿 호출 경계로 전달</li>
 * </ul>
 * </p>
 *
 * @see Servlet
 * @see <a href="https://github.com/jakartaee/servlet/blob/main/api/src/main/java/jakarta/servlet/Servlet.java">
 *      Jakarta Servlet Specification - Servlet Interface
 *      </a>
 */
public class ServletContainer {

    private final Servlet servlet;

    /**
     * 실행 대상이 되는 Servlet 구현체를 주입받아 컨테이너를 초기화합니다.
     *
     * <p>
     * 이 컨테이너는 단일 Servlet 인스턴스를 기준으로 동작하며,
     * 어떤 Servlet을 사용할지에 대한 결정은
     * 상위 계층에서 이미 완료된 상태임을 전제로 합니다.
     * </p>
     *
     * @param servlet 서블릿 표준 계약을 구현한 실행 대상 객체
     */
    public ServletContainer(Servlet servlet) {
        this.servlet = servlet;
    }

    /**
     * 서블릿 표준 계약에 따라 요청 처리를 위임합니다.
     *
     * <p>
     * 이 메서드는 전체 요청 처리 흐름 중
     * <strong>WAS → Servlet 표준 경계 지점</strong>에 해당합니다.
     * </p>
     *
     * <p>
     * 처리 결과는 {@link HttpResponse} 객체에 직접 기록되며,
     * 반환값은 존재하지 않습니다.
     * 이후 응답 객체는 상위 계층(Service → Connector)으로 전달되어
     * 네트워크 응답으로 변환됩니다.
     * </p>
     *
     * @param request WAS 레벨에서 파싱된 HTTP 요청 객체
     * @param response 처리 결과를 기록할 HTTP 응답 객체
     */
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        servlet.service(request, response);
    }
}
