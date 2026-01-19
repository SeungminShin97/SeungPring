package org.example.framework.was.protocol.model;

import org.example.framework.was.protocol.HttpProtocolVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {

    @Test
    @DisplayName("기본 생성자로 생성된 응답은 200 OK 상태를 가진다")
    void default_response_should_be_ok() {
        // given
        HttpResponse response = new HttpResponse(HttpProtocolVersion.HTTP_1_1);

        // then
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(200, response.getStatusCode());
        assertEquals("OK", response.getReason());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("writeBody를 호출하면 바디가 UTF-8 기준으로 설정되고 Content-Length가 자동으로 갱신된다")
    void writeBody_should_set_body_and_content_length() {
        // given
        HttpResponse response = new HttpResponse(HttpProtocolVersion.HTTP_1_1);

        // when
        response.writeBody("hello");

        // then
        assertEquals("hello", response.getBody().getAsString("UTF-8"));
        assertEquals("5", response.getHeader().getFirst("Content-Length"));
    }

    @Test
    @DisplayName("응답 상태는 setStatus를 통해 변경할 수 있다")
    void should_change_status_via_setter() {
        // given
        HttpResponse response = new HttpResponse(HttpProtocolVersion.HTTP_1_1);

        // when
        response.setStatus(HttpStatus.NOT_FOUND);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals(404, response.getStatusCode());
    }
}