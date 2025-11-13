package org.example.framework.was.protocol.http;

import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.core.HttpProtocolHandler;
import org.example.framework.was.protocol.core.RequestParser;
import org.example.framework.was.protocol.core.ResponseWriter;
import org.example.framework.was.protocol.http.http1.Http1ProtocolHandler;
import org.example.framework.was.protocol.http.http1.Http1RequestParser;
import org.example.framework.was.protocol.http.http1.Http1ResponseWriter;
import org.example.framework.was.protocol.http.http2.Http2ProtocolHandler;
import org.example.framework.was.protocol.http.http2.Http2RequestParser;
import org.example.framework.was.protocol.http.http2.Http2ResponseWriter;

/**
 * HTTP 프로토콜 버전({@link HttpProtocolVersion})을 기반으로 요청 처리를 담당할
 * 적절한 {@link HttpProtocolHandler} 구현체를 반환하는 팩토리 클래스입니다.
 * <p>
 * **[주의 - 임시 구현]**
 * 현재는 기능 테스트 및 구조 학습을 위해 요청 시마다 의존성 객체({@link RequestParser}, {@link ResponseWriter})를
 * 새로 생성({@code new})하는 임시 구조입니다. 이 방식은 **스레드 안정성을 위반하며 GC 부하를 유발**합니다.
 * <p>
 * **[TODO: 싱글톤 전환]**
 * 추후 WAS의 IoC 컨테이너가 구현되면, 이 클래스는 모든 의존성을 생성자로 주입받고 (DI),
 * 요청 시에는 미리 생성된 **싱글톤 핸들러**를 반환하도록 변경될 예정입니다.
 */
public class HttpProtocolHandlerFactory {

    // 임시 구현을 위해 필드를 유지하나, 싱글톤 전환 시 final 필드(예: http11Handler)만 남게 됩니다.
    private RequestParser requestParser;
    private ResponseWriter responseWriter;

    /**
     * 감지된 프로토콜 버전에 맞는 {@link HttpProtocolHandler} 인스턴스를 반환합니다.
     * <p>
     * @param version 클라이언트로부터 감지된 HTTP 프로토콜 버전
     * @return 해당 버전의 프로토콜 처리를 담당하는 핸들러
     */
    public HttpProtocolHandler getHandler(HttpProtocolVersion version) {
        if(version == HttpProtocolVersion.HTTP_1_1) {
            requestParser = new Http1RequestParser();
            responseWriter = new Http1ResponseWriter();
            return new Http1ProtocolHandler(requestParser, responseWriter);
        }

        if(version == HttpProtocolVersion.HTTP_2_0) {
            requestParser = new Http2RequestParser();
            responseWriter = new Http2ResponseWriter();
            return new Http2ProtocolHandler(requestParser, responseWriter);
        }

        throw new IllegalArgumentException("Unsupported Http Protocol Version!");
    }
}