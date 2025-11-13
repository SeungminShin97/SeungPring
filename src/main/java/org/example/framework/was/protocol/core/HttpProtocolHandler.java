package org.example.framework.was.protocol.core;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * HTTP 프로토콜 버전을 처리하는 로직의 추상 기반 클래스입니다.
 * <p>
 * 모든 구체적인 핸들러(예: HTTP/1.1, HTTP/2.0)는 이 클래스를 상속받아
 * 요청 파싱, 비즈니스 로직 호출, 응답 작성 기능을 구현해야 합니다.
 * 이 클래스는 프로토콜 처리에 필요한 핵심 의존성({@link RequestParser}, {@link ResponseWriter})을 관리합니다.
 */
public abstract class HttpProtocolHandler {

    /**
     * 클라이언트 요청을 WAS의 내부 모델({@code HttpRequest})로 변환하는 파서입니다.
     * WAS 시작 시 {@link RequestParser}의 싱글톤 구현체(예: {@code Http1RequestParser})가 주입됩니다.
     */
    protected final RequestParser requestParser;

    /**
     * WAS의 응답 모델({@code HttpResponse})을 클라이언트에게 전송할 프로토콜 형식으로 직렬화하는 라이터입니다.
     * WAS 시작 시 {@link ResponseWriter}의 싱글톤 구현체(예: {@code Http1ResponseWriter})가 주입됩니다.
     */
    protected final ResponseWriter responseWriter;
    // TODO: CORE: protected final Dispatcher dispatcher; // 추후 추가될 Dispatcher

    /**
     * 구체적인 프로토콜 핸들러가 사용할 핵심 의존성을 주입받는 생성자입니다.
     * <p>
     * 이 생성자는 IoC 컨테이너에 의해 호출되어 싱글톤 인스턴스를 필드에 할당합니다.
     *
     * @param requestParser 요청 파서 싱글톤 인스턴스
     * @param responseWriter 응답 라이터 싱글톤 인스턴스
     */
    protected HttpProtocolHandler(RequestParser requestParser, ResponseWriter responseWriter) {
        this.requestParser = requestParser;
        this.responseWriter = responseWriter;
    }

    /**
     * 클라이언트로부터 받은 입력 스트림({@code InputStream})을 처리하고
     * 응답({@code OutputStream})을 전송하는 추상 메서드입니다.
     * <p>
     * 구체적인 프로토콜 구현체는 이 메서드 내에서 파싱, 로직 수행, 응답 작성을 관리해야 합니다.
     *
     * @param inputStream 클라이언트 요청 데이터 스트림
     * @param outputStream 클라이언트에게 응답을 보낼 스트림
     * @throws Exception 처리 과정 중 파싱 오류, I/O 오류 또는 서버 로직 오류 발생 시
     */
    public abstract void process(InputStream inputStream, OutputStream outputStream) throws Exception;
}