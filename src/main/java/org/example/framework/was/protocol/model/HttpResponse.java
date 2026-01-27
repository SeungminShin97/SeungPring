package org.example.framework.was.protocol.model;

import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.web.response.ErrorResponse;

import java.nio.charset.StandardCharsets;

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

    /** HTTP 상태 */
    private HttpStatus httpStatus;

    public HttpResponse(HttpHeader header, HttpBody body, HttpProtocolVersion version, HttpStatus httpStatus) {
        super(version, header,body);
        this.httpStatus = httpStatus;
    }

    public HttpResponse(HttpProtocolVersion version) {
        this(new HttpHeader(), HttpBody.empty(), version, HttpStatus.OK);
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }

    public int getStatusCode() {
        return httpStatus.code();
    }

    public String getReason() {
        return httpStatus.reason();
    }

    public void setStatus(HttpStatus status) {
        this.httpStatus = status;
    }

    public void writeBody(String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        super.body = new HttpBody(bytes);
        super.header.setContentLength(bytes.length);
    }

    public void writeJson(ErrorResponse e) {
        String json = """
    {
      "status": %d,
      "error": "%s",
      "message": "%s",
      "path": "%s",
      "timestamp": %d
    }
    """.formatted(
                e.status(),
                escape(e.error()),
                escape(e.message()),
                escape(e.path()),
                e.timestamp()
        );

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        super.body = new HttpBody(bytes);
        super.header.setContentLength(bytes.length);
        super.header.setContentType("application/json; charset=UTF-8");
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}
