package org.example.framework.was.protocol.model;

import org.example.framework.was.protocol.HttpProtocolVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {


    @Test
    @DisplayName("HTTP 요청은 메서드와 경로 정보를 그대로 보존한다")
    void should_preserve_method_and_path() {
        // given
        HttpHeader header = new HttpHeader();
        HttpBody body = HttpBody.empty();

        HttpRequest request = new HttpRequest(
                header,
                body,
                HttpProtocolVersion.HTTP_1_1,
                HttpMethod.GET,
                "/test"
        );

        // then
        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals("/test", request.getPath());
    }

    @Test
    @DisplayName("HTTP 요청은 헤더와 바디 객체를 그대로 참조한다")
    void should_preserve_header_and_body_reference() {
        // given
        HttpHeader header = new HttpHeader();
        HttpBody body = new HttpBody("data".getBytes());

        HttpRequest request = new HttpRequest(
                header,
                body,
                HttpProtocolVersion.HTTP_1_1,
                HttpMethod.POST,
                "/submit"
        );

        // then
        assertSame(header, request.getHeader());
        assertSame(body, request.getBody());
    }

}