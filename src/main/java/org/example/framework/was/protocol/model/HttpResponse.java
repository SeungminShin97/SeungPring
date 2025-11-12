package org.example.framework.was.protocol.model;

/**
 * HTTP 응답 정보를 표현하는 클래스.
 * <p>
 * 상위 클래스 {@link HttpMessage}를 통해 헤더와 바디에 접근할 수 있으며,
 * 상태 코드와 상태 메시지를 포함한다.
 * <ul>
 *   <li>{@code statusCode} : HTTP 상태 코드 (예: 200, 404, 500)</li>
 *   <li>{@code message} : 상태 메시지 (예: "OK", "Not Found")</li>
 * </ul>
 */
public class HttpResponse extends HttpMessage{

    /** 상태 코드 */
    private final int statusCode;

    /** 상태 메세지 */
    private final String message;

    public HttpResponse(HttpHeader header, HttpBody body, String version, int statusCode, String message) {
        super(version, header,body);
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
