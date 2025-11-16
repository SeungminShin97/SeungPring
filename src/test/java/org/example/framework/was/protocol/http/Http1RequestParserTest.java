package org.example.framework.was.protocol.http;

import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.was.protocol.http.http1.Http1RequestParser;
import org.example.framework.was.protocol.model.HttpHeader;
import org.example.framework.was.protocol.model.HttpMethod;
import org.example.framework.was.protocol.model.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Http1RequestParserTest {

    private Http1RequestParser parser;

    @BeforeEach
    void setUp() {
        parser = Http1RequestParser.getInstance();
    }

    @Test
    @DisplayName("바디 없는 유효한 GET 요청을 성공적으로 파싱해야 함")
    void should_Parse_Valid_Get_Request_Without_Body() throws HttpParsingException {
        // Given
        String rawRequest = "GET /index.html HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // When
        HttpRequest request = (HttpRequest) parser.parse(inputStream);

        // Then
        assertNotNull(request);
        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals("/index.html", request.getPath());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals("localhost", request.getHeader().getFirst("Host"));
        assertTrue(request.getBody().isEmpty());
        assertSame(request.getBody().getContentLengthLong(), 0L);
    }

    @Test
    @DisplayName("Content-Length를 가진 POST 요청과 바디를 성공적으로 파싱해야 함")
    void should_Parse_Post_Request_With_Body() throws HttpParsingException {
        // Given
        String requestBody = "name=seungmin&age=27";
        String rawRequest = "POST /submit HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "Content-Length: " + requestBody.length() + "\r\n" +
                "\r\n" +
                requestBody;

        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // When
        HttpRequest request = (HttpRequest) parser.parse(inputStream);

        // Then
        assertNotNull(request);
        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals("/submit", request.getPath());
        assertFalse(request.getBody().isEmpty());

        // 바디 내용 확인
        String parsedBody = request.getBody().getAsString(StandardCharsets.UTF_8.name());
        assertEquals(requestBody, parsedBody);
    }

    @Test
    @DisplayName("잘못된 형식의 요청 라인 입력 시 HttpParsingException을 발생시켜야 함")
    void should_Throw_Exception_For_Malformed_RequestLine() {
        // Given: 요소가 2개만 있는 잘못된 요청 라인
        String rawRequest = "GET /index.html\r\n" +
                "Host: localhost\r\n" +
                "\r\n";

        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // When & Then
        assertThrows(HttpParsingException.class, () -> {
            parser.parse(inputStream);
        });
    }

    @Test
    @DisplayName("여러 개의 동일한 헤더 키(예: Set-Cookie)를 성공적으로 파싱해야 함")
    void should_Parse_Multiple_Headers_With_Same_Key() throws HttpParsingException {
        // Given
        String rawRequest = "GET /session HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Set-Cookie: session=abc123; HttpOnly\r\n" +
                "Set-Cookie: user=guest; Max-Age=3600\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // When
        HttpRequest request = (HttpRequest) parser.parse(inputStream);

        // Then
        assertNotNull(request);
        HttpHeader header = request.getHeader();

        // "Set-Cookie" 키에 연결된 모든 값이 리스트 형태로 반환되는지 확인
        List<String> cookies = header.get("Set-Cookie");
        assertNotNull(cookies);
        assertEquals(2, cookies.size());
        assertTrue(cookies.contains("session=abc123; HttpOnly"));
        assertTrue(cookies.contains("user=guest; Max-Age=3600"));

        assertEquals("localhost", header.getFirst("Host"));
    }

    @Test
    @DisplayName("HTTP 헤더 키는 대소문자를 구분하지 않고 파싱되어야 함")
    void should_Handle_Case_Insensitive_Header_Keys() throws HttpParsingException {
        // Given: Content-Length 헤더를 제거하여 GET 요청에 바디가 없음을 명시
        String rawRequest = "GET / HTTP/1.1\r\n" +
                "Content-Type: application/json\r\n" +
                // "content-length: 10\r\n" + // 이 줄을 제거하거나 주석 처리
                "UseR-AgenT: CustomAgent\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // When
        HttpRequest request = (HttpRequest) parser.parse(inputStream);
        HttpHeader header = request.getHeader();

        // Then
        assertNotNull(request);
        assertTrue(request.getBody().isEmpty()); // 바디가 비어있음을 확인

        assertEquals("application/json", header.getFirst("Content-Type"));
        assertEquals("CustomAgent", header.getFirst("user-agent"));
        assertEquals("CustomAgent", header.getFirst("User-Agent"));
    }

    @Test
    @DisplayName("헤더 값의 앞뒤 공백이 올바르게 제거되어 파싱되어야 함")
    void should_Trim_Whitespace_From_Header_Values() throws HttpParsingException {
        // Given
        // 헤더 키와 값 주변에 의도적인 공백 추가
        String rawRequest = "GET / HTTP/1.1\r\n" +
                "  Cache-Control  :  no-cache, private  \r\n" +
                " Accept :   application/json \r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // When
        HttpRequest request = (HttpRequest) parser.parse(inputStream);

        // Then
        assertNotNull(request);
        HttpHeader header = request.getHeader();

        // 공백이 제거된 깔끔한 값만 저장되었는지 확인
        assertEquals("no-cache, private", header.getFirst("Cache-Control"));
        assertEquals("application/json", header.getFirst("Accept"));
    }

    @Test
    @DisplayName("Content-Length가 0인 POST 요청의 바디는 비어있어야 함")
    void should_Parse_Post_Request_With_Zero_ContentLength() throws HttpParsingException {
        // Given
        String rawRequest = "POST /empty HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n" +
                "실제로는데이터가있어도무시되어야함";

        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // When
        HttpRequest request = (HttpRequest) parser.parse(inputStream);

        // Then
        assertNotNull(request);
        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals("/empty", request.getPath());

        // 바디가 비어있는지 확인
        assertTrue(request.getBody().isEmpty());
        assertEquals(0, request.getBody().getContentLengthLong());
    }

    @Test
    @DisplayName("Transfer-Encoding: chunked 요청 시 UnsupportedOperationException을 발생시켜야 함")
    void should_Throw_Exception_For_Chunked_Transfer_Encoding() {
        // Given
        String rawRequest = "POST /upload HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Transfer-Encoding: chunked\r\n" +
                "\r\n" +
                "5\r\nhello\r\n0\r\n\r\n"; // 청크 데이터 예시

        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // When & Then

         assertThrows(UnsupportedOperationException.class, () -> {
             parser.parse(inputStream);
         });

    }
}