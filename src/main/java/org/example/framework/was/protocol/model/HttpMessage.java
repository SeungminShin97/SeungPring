package org.example.framework.was.protocol.model;

import java.net.http.HttpResponse;

/**
 * HTTP 메시지의 공통 구조를 정의하는 추상적 기반 클래스.
 * <p>
 * 요청({@link HttpRequest})과 응답({@link HttpResponse})이 공통으로 가지는
 * 프로토콜 버전, 헤더, 바디 정보를 포함한다.
 * <ul>
 *   <li>{@code version} : HTTP 프로토콜 버전 (예: HTTP/1.1, HTTP/2.0)</li>
 *   <li>{@code header} : 메시지 헤더 정보</li>
 *   <li>{@code body} : 메시지 바디 데이터</li>
 * </ul>
 */
public class HttpMessage {

    /** HTTP 프로토콜 버전 (예: HTTP/1.1, HTTP/2.0) */
    protected final String version;

    /** HTTP 헤더 */
    protected final HttpHeader header;

    /** HTTP 바디 */
    protected final HttpBody body;

    protected HttpMessage(String version, HttpHeader header, HttpBody body) {
        this.version = version;
        this.header = header;
        this.body = body;
    }

    public String getVersion() {
        return version;
    }

    public HttpHeader getHeader() {
        return header;
    }

    public HttpBody getBody() {
        return body;
    }
}
