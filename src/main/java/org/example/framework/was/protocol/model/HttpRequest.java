package org.example.framework.was.protocol.model;

import org.example.framework.was.protocol.HttpProtocolVersion;

/**
 * HTTP 요청 정보를 표현하는 클래스.
 * <p>
 * 요청 메서드({@link HttpMethod})와 요청 경로(path)를 포함하며,
 * 상위 클래스 {@link HttpMessage}를 통해 헤더와 바디에 접근할 수 있다.
 * <ul>
 *   <li>{@code method} : HTTP 메서드 (GET, POST, PUT, DELETE 등)</li>
 *   <li>{@code path} : 요청 대상 URI 경로</li>
 * </ul>
 */
public class HttpRequest extends HttpMessage{

    /** HTTP 요청 메서드 */
    private final HttpMethod method;

    /** 요청 경로 (예: /api/posts) */
    private final String path;

    public HttpRequest(HttpHeader header, HttpBody body, HttpProtocolVersion version, HttpMethod method, String path) {
        super(version, header, body);
        this.method = method;
        this.path = path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
