package org.example.framework.was.protocol.http;

import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.core.HttpProtocolHandler;
import org.example.framework.was.protocol.http.http1.Http1ProtocolHandler;
import org.example.framework.was.protocol.http.http2.Http2ProtocolHandler;

/**
 * HTTP 프로토콜 버전({@link HttpProtocolVersion})에 따라
 * 적절한 {@link HttpProtocolHandler} 인스턴스를 반환하는 팩토리 클래스입니다.
 *
 * <p>
 * 이 클래스는 <strong>싱글톤(Singleton)</strong>으로 제공되며,
 * 내부적으로 HTTP/1.1 및 HTTP/2 프로토콜 핸들러를 미리 준비해 두고
 * 요청 시 감지된 버전에 맞춰 즉시 반환합니다.
 * </p>
 *
 * <p>
 * 지원하지 않는 HTTP 버전이 전달될 경우 {@link IllegalArgumentException} 이 발생합니다.
 * </p>
 */
public class HttpProtocolHandlerFactory {

    private final HttpProtocolHandler http1Handler;
    private final HttpProtocolHandler http2Handler;

    private HttpProtocolHandlerFactory(HttpProtocolHandler http1Handler, HttpProtocolHandler http2Handler) {
        this.http1Handler = http1Handler;
        this.http2Handler = http2Handler;
    }

    private static class Holder {
        static final HttpProtocolHandlerFactory INSTANCE = new HttpProtocolHandlerFactory(
                Http1ProtocolHandler.getInstance(),
                Http2ProtocolHandler.getInstance()
        );
    }

    public static HttpProtocolHandlerFactory getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 감지된 프로토콜 버전에 맞는 {@link HttpProtocolHandler} 인스턴스를 반환합니다.
     * <p>
     * @param version 클라이언트로부터 감지된 HTTP 프로토콜 버전
     * @return 해당 버전의 프로토콜 처리를 담당하는 핸들러
     */
    public HttpProtocolHandler getHandler(HttpProtocolVersion version) {
        if(version == HttpProtocolVersion.HTTP_1_1)
            return this.http1Handler;

        if(version == HttpProtocolVersion.HTTP_2_0)
            return this.http2Handler;

        throw new IllegalArgumentException("Unsupported Http Protocol Version!");
    }
}