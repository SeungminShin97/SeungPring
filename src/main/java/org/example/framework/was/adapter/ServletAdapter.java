package org.example.framework.was.adapter;

import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;

/**
 * HTTP 프로토콜 처리 영역과 MVC/서블릿 컨테이너 영역을 연결하는 어댑터 인터페이스입니다.
 *
 * <p>
 * 이 인터페이스는 HTTP 요청 파싱 및 응답 직렬화와 같은
 * 프로토콜 처리 로직과,
 * 컨트롤러 실행 및 요청 디스패치와 같은
 * 애플리케이션 로직 간의 책임 경계를 명확히 분리하기 위해 정의됩니다.
 * </p>
 *
 * <p>
 * 구현체는 {@code HttpRequest}와 {@code HttpResponse}를
 * 서블릿 컨테이너 또는 디스패처가 처리할 수 있도록 전달하며,
 * HTTP 프로토콜 세부 사항에는 관여하지 않습니다.
 * </p>
 * @see <a href="https://github.com/apache/tomcat/blob/main/java/org/apache/coyote/Adapter.java">Apache Coyote Adapter</a>
 */
public interface ServletAdapter {

    /**
     * 파싱이 완료된 HTTP 요청을 MVC/서블릿 컨테이너 영역으로 전달하여
     * 실제 요청 처리를 수행합니다.
     *
     * <p>
     * 이 메서드는 HTTP 프로토콜 처리 흐름의 종료 지점이자,
     * 애플리케이션 요청 처리 흐름의 시작 지점입니다.
     * </p>
     *
     * <p>
     * 요청 처리 결과는 반드시 {@code HttpResponse} 객체에 반영되어야 하며,
     * 반환값은 사용하지 않습니다.
     * </p>
     *
     * @param request  WAS 내부 요청 모델
     * @param response WAS 내부 응답 모델
     */
    void service(HttpRequest request, HttpResponse response);
}
