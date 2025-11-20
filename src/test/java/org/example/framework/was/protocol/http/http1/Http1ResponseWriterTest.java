package org.example.framework.was.protocol.http.http1;

import org.example.framework.exception.was.HttpWritingException;
import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.model.HttpBody;
import org.example.framework.was.protocol.model.HttpHeader;
import org.example.framework.was.protocol.model.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class Http1ResponseWriterTest {

    private ByteArrayOutputStream outputStream;
    private final Http1ResponseWriter writer = Http1ResponseWriter.getInstance();

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
    }

    private HttpResponse getResponse(HttpBody body) {
        HttpHeader header = new HttpHeader();
        header.put("test", "test");
        return new HttpResponse(header, body, HttpProtocolVersion.HTTP_1_1, 200, "OK");
    }

    @Test
    @DisplayName("바디가 비어있을 때, 헤더만 전송해야 한다.")
    void should_skip_body_when_body_is_empty() throws HttpWritingException, IOException {
        // given
        HttpResponse response = getResponse(HttpBody.empty());

        // when
        writer.write(outputStream, response);

        // then
        String result = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(result.startsWith(HttpProtocolVersion.HTTP_1_1.getProtocolString()));
        assertTrue(result.endsWith("\r\n\r\n"));
        assertTrue(result.contains("Content-Length: 0\r\n"));
    }

    @Test
    @DisplayName("바디가 있을 경우, 헤더와 바디 둘 다 전송해야 한다.")
    void should_write_body_and_header_when_body_exists() throws HttpWritingException, IOException {
        // given
        String dummyBody = "testsetsetsetst";
        byte[] data = dummyBody.getBytes(StandardCharsets.UTF_8);
        HttpBody body = new HttpBody(data);
        HttpResponse response = getResponse(body);

        // when
        writer.write(outputStream, response);

        // then
        String result = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(result.startsWith(HttpProtocolVersion.HTTP_1_1.getProtocolString()));
        assertTrue(result.endsWith(dummyBody));
        assertTrue(result.contains("Content-Length: " + dummyBody.length() + "\r\n"));
    }
}