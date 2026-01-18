package org.example.framework.was.protocol.http;

import org.example.framework.was.adapter.ServletAdapter;
import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.core.HttpProtocolHandler;
import org.example.framework.was.protocol.http.http1.Http1ProtocolHandler;
import org.example.framework.was.protocol.http.http2.Http2ProtocolHandler;

/**
 * HTTP 프로토콜 버전에 따라
 * 적절한 {@link HttpProtocolHandler}를 생성해 반환하는 팩토리.
 *
 * <p>
 * Factory와 Adapter는 서버 수명(singleton),
 * Handler는 요청 단위로 생성된다.
 * </p>
 */
public class HttpProtocolHandlerFactory {

    private final ServletAdapter adapter;

    private HttpProtocolHandlerFactory(ServletAdapter adapter) {
        this.adapter = adapter;
    }

    public static HttpProtocolHandlerFactory create(ServletAdapter adapter) {
        return new HttpProtocolHandlerFactory(adapter);
    }

    /**
     * 감지된 프로토콜 버전에 맞는 {@link HttpProtocolHandler} 인스턴스를 반환합니다.
     * <p>
     * @param version 클라이언트로부터 감지된 HTTP 프로토콜 버전
     * @return 해당 버전의 프로토콜 처리를 담당하는 핸들러
     */
    public HttpProtocolHandler getHandler(HttpProtocolVersion version) {
        if(version == HttpProtocolVersion.HTTP_1_1)
            return new Http1ProtocolHandler(adapter);

        if(version == HttpProtocolVersion.HTTP_2_0)
            return new Http2ProtocolHandler(adapter);

        throw new IllegalArgumentException("Unsupported Http Protocol Version!");
    }
}