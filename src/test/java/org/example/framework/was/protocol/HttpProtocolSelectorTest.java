package org.example.framework.was.protocol;

import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.exception.was.HttpVersionDetectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class HttpProtocolSelectorTest {

    private HttpProtocolSelector selector;

    @BeforeEach
    void setUp() {
        this.selector = new HttpProtocolSelector();
    }

    @Test
    @DisplayName("[비표준] 감지에 필요한 최소 길이 미만의 데이터에 대해 예외를 던져야 한다")
    void should_throw_when_data_size_is_short() {
        // Given
        String rawRequest = "veryShort!\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.US_ASCII));
        InputStream bufferedIn = new BufferedInputStream(inputStream, 24);

        // When & Then
        assertThrows(HttpVersionDetectionException.class, () -> selector.detect(bufferedIn));
    }

    @Test
    @DisplayName("[비표준] HTTP 메서드로 시작하지 않는 임의 데이터에 대해 예외를 던져야 한다.")
    void should_throw_for_illegal_data() {
        // Given
        String rawRequest = "asaflisfaslfnal nflj aefae hagesaasfas\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.US_ASCII));
        InputStream bufferedIn = new BufferedInputStream(inputStream, 24);

        // When & Then
        assertThrows(HttpVersionDetectionException.class, () -> selector.detect(bufferedIn));
    }

    @Test
    @DisplayName("[HTTP/1.1] 표준 GET 요청 시 HttpProtocolVersion.HTTP_1_1을 반환해야 한다.")
    void should_return_http1RequestParser_for_standard_get_request() throws IOException, HttpVersionDetectionException {
        // Given
        String rawRequest = "GET /health HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Connection: close\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.US_ASCII));
        InputStream bufferedIn = new BufferedInputStream(inputStream, 24);

        // When
        HttpProtocolVersion version = selector.detect(bufferedIn);

        // Then
        assertEquals(HttpProtocolVersion.HTTP_1_1, version);
    }

    @Test
    @DisplayName("[HTTP/1.1] 표준 POST 요청 시 HttpProtocolVersion.HTTP_1_1을 반환해야 한다.")
    void should_return_http1RequestParser_for_standard_post_request() throws IOException, HttpVersionDetectionException {
        // Given
        String rawRequest = "POST /health HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Connection: close\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.US_ASCII));
        InputStream bufferedIn = new BufferedInputStream(inputStream, 24);

        // When
        HttpProtocolVersion version = selector.detect(bufferedIn);

        // Then
        assertEquals(HttpProtocolVersion.HTTP_1_1, version);
    }

    @Test
    @DisplayName("[HTTP/2.0] HTTP/2 감지 시 HttpProtocolVersion.HTTP_2_0을 반환해야 한다.")
    void should_throw_when_detect_http2_is_detected() throws IOException, HttpVersionDetectionException {
        // Given
        String rawRequest = "PRI * HTTP/2.0\\r\\n\\r\\nSM\\r\\n\\r\\n" +
                "\r\n\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.US_ASCII));
        InputStream bufferedIn = new BufferedInputStream(inputStream, 24);

        // When
        HttpProtocolVersion version = selector.detect(bufferedIn);

        // Then
        assertEquals(HttpProtocolVersion.HTTP_2_0, version);
    }

    @Test
    @DisplayName("[스트림 관리] 감지 후 InputStream의 포인터가 처음 위치로 정확히 복구되어야 한다")
    void shouldResetStreamPointer_afterDetection() throws Exception {
        // given: 충분히 긴 HTTP/1.1 요청
        String fullRequest = "GET /test HTTP/1.1\r\nHost: localhost\r\n\r\n";
        InputStream is = new BufferedInputStream(new ByteArrayInputStream(fullRequest.getBytes(StandardCharsets.US_ASCII)));

        // when: 감지 로직 실행 (24바이트를 읽었다가 되돌림)
        selector.detect(is);

        // then: 파서가 되돌아간 포인터에서 전체 요청을 다시 읽을 수 있는지 검증
        byte[] readAfterReset = is.readNBytes(fullRequest.getBytes().length);
        String textAfterReset = new String(readAfterReset);

        assertEquals(fullRequest, textAfterReset,
                "감지 후 다시 읽은 내용은 원본 요청 전체와 일치해야 한다 (reset() 동작 검증)");
    }

}