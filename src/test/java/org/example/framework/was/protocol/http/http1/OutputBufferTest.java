package org.example.framework.was.protocol.http.http1;

import org.example.framework.exception.was.HttpWritingException;
import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.model.HttpBody;
import org.example.framework.was.protocol.model.HttpHeader;
import org.example.framework.was.protocol.model.HttpResponse;
import org.example.framework.was.protocol.model.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OutputBufferTest {

    private ByteArrayOutputStream stream;
    @Spy private OutputBuffer buffer;

    @BeforeEach
    void setup() {
        stream = new ByteArrayOutputStream();
        buffer = Mockito.spy(new OutputBuffer(stream));
    }

    @Test
    @DisplayName("지원하지 않는 HTTP 버전(HTTP/2)이 감지되었을 때 HttpWritingException을 던져야 한다")
    void should_throw_when_unsupported_protocol_version_is_given() throws HttpWritingException, IOException {
        // given
        HttpResponse mockResponse = Mockito.mock(HttpResponse.class);
        when(mockResponse.getVersion()).thenReturn(HttpProtocolVersion.HTTP_2_0);

        // when & then
        assertThrows(HttpWritingException.class, () -> buffer.sendHeaders(mockResponse));
    }

    @Test
    @DisplayName("헤더 커밋(sendHeaders) 전에 writeBody를 호출하면 IllegalStateException을 던져야 한다")
    void should_throw_when_write_body_before_committed() {
        // given
        byte[] data = new byte[0];

        // when & then
        assertThrows(IllegalStateException.class, () -> buffer.writeBody(data));
    }

    @Test
    @DisplayName("필수 헤더와 커스텀 헤더를 포함한 전체 헤더가 HTTP 형식에 맞게 정확히 작성되어야 한다")
    void should_write_all_headers_in_correct_http_format() throws HttpWritingException, IOException {
        // given
        HttpHeader header = new HttpHeader();
        header.put("Content-Type", "application/json");
        header.put("X-Custom-Header", "ABC123");

        HttpBody body = HttpBody.empty();

        HttpResponse response = new HttpResponse(header, body, HttpProtocolVersion.HTTP_1_1, HttpStatus.OK);

        // when
        buffer.sendHeaders(response);
        buffer.flush();

        // then
        String result = stream.toString(StandardCharsets.UTF_8);
        assertTrue(result.startsWith("HTTP/1.1 200 OK\r\n"));
        assertTrue(result.contains("Content-Type: application/json\r\n"));
        assertTrue(result.contains("X-Custom-Header: ABC123\r\n"));
        assertTrue(result.contains("Content-Length: 0\r\n"));
        assertTrue(result.contains("Date: "));
        assertTrue(result.endsWith("\r\n\r\n"));
    }

    @Test
    @DisplayName("헤더 커밋 후, 바디 데이터가 OutputBuffer의 flush 시점에 정확히 작성되어야 한다")
    void should_write_body_data_after_headers_committed() throws HttpWritingException, IOException {
        // given
        HttpHeader header = new HttpHeader();
        header.put("Content-Type", "application/json");
        header.put("X-Custom-Header", "ABC123");

        String expectedBody = "This is dummy data";
        byte[] data = expectedBody.getBytes(StandardCharsets.UTF_8);
        HttpBody body = new HttpBody(data);

        HttpResponse response = new HttpResponse(header, body, HttpProtocolVersion.HTTP_1_1, HttpStatus.OK);
        buffer.sendHeaders(response);
        buffer.flush();

        // when
        buffer.writeBody(response.getBody().getData());
        buffer.flush();

        // then
        String result = stream.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Content-Length: " + data.length + "\r\n"));

        int bodyStartIndex = result.indexOf("\r\n\r\n") + 4;
        String actualBody = result.substring(bodyStartIndex);
        assertEquals(expectedBody, actualBody);
    }

    @Test
    @DisplayName("필수 헤더가 중복되었을 경우 하나만 출력되어야 한다")
    void should_write_one_fields_when_headers_duplicated() throws HttpWritingException, IOException {
        // given
        HttpHeader header = new HttpHeader();
        header.put("Content-Type", "text/html; charset=utf-8");
        header.put("X-Custom-Header", "ShouldBeIncluded");
        header.put("X-Custom-Header", "ShouldBeIncludedWhenHeadersAreDuplicated");  // 필수 헤더가 아닌 경우 중복 출력 되어야 함

        HttpBody body = HttpBody.empty();
        HttpResponse response = new HttpResponse(header, body, HttpProtocolVersion.HTTP_1_1, HttpStatus.OK);

        // when
        buffer.sendHeaders(response);
        buffer.flush();

        // then
        String result = stream.toString(StandardCharsets.UTF_8);

        String contentTypeLine = "Content-Type: text/html; charset=utf-8\r\n";

        long count = result.lines()
                .filter(line -> line.trim().startsWith("Content-Type:"))
                .count();

        assertTrue(result.contains(contentTypeLine));
        assertEquals(1, count);
        assertTrue(result.contains("X-Custom-Header: ShouldBeIncluded\r\n"));
        assertTrue(result.contains("X-Custom-Header: ShouldBeIncludedWhenHeadersAreDuplicated\r\n"));
    }

    @Test
    @DisplayName("writeInternal 호출 시 버퍼 크기(8192)를 초과하면 자동으로 flushInternal을 호출해야 한다")
    void should_auto_flushInternal_when_overflow() throws HttpWritingException, IOException {
        // given
        int defaultBufferSize = 8192; // 내부 버퍼 사이즈
        int overflowSize = 20000;
        int expectedFlushCount = 2;
        byte[] largeData = new byte[overflowSize];

        HttpResponse mockResponse = mock(HttpResponse.class);
        HttpHeader mockHeader = mock(HttpHeader.class);
        HttpBody mockBody = mock(HttpBody.class);

        when(mockResponse.getVersion()).thenReturn(HttpProtocolVersion.HTTP_1_1);

        when(mockResponse.getHeader()).thenReturn(mockHeader);
        when(mockResponse.getHeader().getFirst(any())).thenReturn("");

        when(mockResponse.getBody()).thenReturn(mockBody);
        when(mockResponse.getBody().getContentLengthLong()).thenReturn(0L);

        buffer.sendHeaders(mockResponse);

        Mockito.reset(buffer);

        // when
        buffer.writeBody(largeData);

        // then
        verify(buffer, times(expectedFlushCount)).flushInternal();

        buffer.flush();
        int headerSize = stream.toString(StandardCharsets.UTF_8).indexOf("\r\n\r\n") + 4;
        assertEquals(overflowSize, stream.size() - headerSize);
    }
}