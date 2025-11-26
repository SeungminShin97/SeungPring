package org.example.framework.was.processor;

import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.exception.was.HttpVersionDetectionException;
import org.example.framework.exception.was.HttpWritingException;
import org.example.framework.was.protocol.HttpProtocolSelector;
import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.core.HttpProtocolHandler;
import org.example.framework.was.protocol.http.HttpProtocolHandlerFactory;
import org.example.framework.was.protocol.model.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocketProcessorTest {

    private SocketProcessor processor;

    @Mock
    Socket socket;
    @Mock
    InputStream inputStream;
    @Mock
    OutputStream outputStream;
    @Mock
    HttpProtocolSelector selector;
    @Mock
    HttpProtocolHandlerFactory factory;
    @Mock
    HttpProtocolHandler handler;


    @BeforeEach
    void setUp() throws IOException, HttpVersionDetectionException {
        // UnnecessaryStubbingException 방지
        lenient().when(socket.getInputStream()).thenReturn(inputStream);
        lenient().when(socket.getOutputStream()).thenReturn(outputStream);
        lenient().doNothing().when(socket).close();
        lenient().when(socket.isClosed()).thenReturn(true);
        lenient().when(selector.detect(any())).thenReturn(HttpProtocolVersion.HTTP_1_1);
        lenient().when(factory.getHandler(any())).thenReturn(handler);
    }


    @Test
    @DisplayName("요청 처리 성공 시 소켓을 닫고 정상 종료해야 한다")
    void should_process_request_and_close_socket_on_success() throws IOException, HttpVersionDetectionException, HttpWritingException, HttpParsingException {
        // === given ===
        doNothing().when(handler).process(any(), any());

        processor = new SocketProcessor(socket, selector, factory);

        // === when ===
        processor.run();

        // === then ===
        verify(socket).close();
        assertTrue(socket.isClosed());
    }

    @Test
    @DisplayName("Request Line 파싱 실패 시 로그만 남겨야 한다")
    void should_log_when_parsing_request_line_fails() throws IOException, HttpVersionDetectionException, HttpWritingException {
        // === given ===
        doThrow(new HttpVersionDetectionException())
                .when(selector).detect(eq(inputStream));
        
        processor = new SocketProcessor(socket, selector, factory);

        // === when ===
        processor.run();

        // === then ===
        verify(socket).close();
        assertTrue(socket.isClosed());
        
        // 예외가 먼저 발생했으므로 핸들러를 얻으려는 시도를 하면 안된다.
        verify(factory, never()).getHandler(any());

        // 에러 응답 로직이 호출되면 안된다
        verify(handler, never()).handleError(any(), any(), any());
    }

    @Test
    @DisplayName("요청 파싱 실패 시 400 에러 응답을 전송해야 한다")
    void should_send_400_response_when_parsing_fails() throws IOException, HttpWritingException, HttpParsingException {
        // === given ===
        when(factory.getHandler(any())).thenReturn(handler);
        doThrow(new HttpParsingException()).when(handler).process(inputStream, outputStream);

        processor = new SocketProcessor(socket, selector, factory);

        // === when ===
        processor.run();

        // === then ===
        verify(socket).close();
        assertTrue(socket.isClosed());

        verify(handler).handleError(
                eq(outputStream),
                eq(HttpStatus.BAD_REQUEST),
                any(HttpParsingException.class)
        );
    }

    @Test
    @DisplayName("응답 작성 중 오류 발생 시 500 에러를 전송해야 한다.")
    void should_send_500_response_when_writing_fails() throws IOException, HttpWritingException, HttpParsingException {
        // === given ===
        doThrow(new HttpWritingException())
                .when(handler).process(inputStream,outputStream);

        processor = new SocketProcessor(socket, selector, factory);

        // === when ===
        processor.run();

        // === then ===
        verify(socket).close();
        assertTrue(socket.isClosed());

        verify(handler).handleError(
                eq(outputStream),
                eq(HttpStatus.INTERNAL_SERVER_ERROR),
                any(HttpWritingException.class)
        );
    }

    @Test
    @DisplayName("예상치 못한 내부 예외 발생 시 500 에러를 전송해야 한다.")
    void should_send_500_response_when_uncaught_exception_occurs() throws IOException, HttpVersionDetectionException, HttpWritingException, HttpParsingException {
        // === given ===
        doThrow(new IllegalStateException()).when(handler).process(inputStream, outputStream);

        processor = new SocketProcessor(socket, selector, factory);

        // === when ===
        processor.run();

        // === then ===
        verify(socket).close();
        assertTrue(socket.isClosed());

        verify(handler).handleError(
                eq(outputStream),
                eq(HttpStatus.INTERNAL_SERVER_ERROR),
                any(IllegalStateException.class)
        );
    }

    @Test
    @DisplayName("HTTP 버전 감지 실패 시 에러 응답 없이 로그만 남기고 종료해야 한다.")
    void should_log_error_when_version_detection_fails() throws IOException, HttpVersionDetectionException, HttpWritingException {
        // === given ===
        doThrow(new HttpVersionDetectionException()).when(selector).detect(inputStream);

        processor = new SocketProcessor(socket, selector, factory);

        // === when ===
        processor.run();

        // === then ===
        verify(socket).close();
        assertTrue(socket.isClosed());

        verify(factory, never()).getHandler(any());

        verify(handler, never()).handleError(any(), any(), any());
    }

    @Test
    @DisplayName("400 응답 전송 중 I/O 오류 발생 시 500 에러로 재시도 해야 한다")
    void should_fallback_to_500_when_initial_error_response_fails() throws IOException, HttpWritingException, HttpParsingException {
        // === given ===
        doThrow(new HttpParsingException()).when(handler).process(any(), any());

        // 첫 번째 handleError(400) → 예외 발생
        // 두 번째 handleError(500) → 정상 동작
        doThrow(new HttpWritingException())
                .doNothing()
                .when(handler).handleError(any(), any(), any());

        processor = new SocketProcessor(socket, selector, factory);

        // === when ===
        processor.run();

        // === then ===
        verify(socket).close();
        assertTrue(socket.isClosed());

        InOrder inOrder = inOrder(handler);

        inOrder.verify(handler).handleError(
                eq(outputStream),
                eq(HttpStatus.BAD_REQUEST),
                any(HttpParsingException.class)
        );

        inOrder.verify(handler).handleError(
                eq(outputStream),
                eq(HttpStatus.INTERNAL_SERVER_ERROR),
                any(HttpWritingException.class)
        );
    }

    @Test
    @DisplayName("소켓 I/O 예외 발생 시 에러 응답 없이 로그만 남기고 종료해야 한다")
    void should_log_error_when_socket_io_exception_occurs() throws IOException, HttpWritingException {
        // === given ===
        doThrow(new IOException()).when(socket).getOutputStream();

        processor = new SocketProcessor(socket, selector, factory);

        // === when ===
        processor.run();

        // === then ===
        verify(socket).close();
        assertTrue(socket.isClosed());

        verify(handler, never()).handleError(any(), any(), any());
    }

    @Test
    @DisplayName("핸들러가 null일 경우 에러 응답 전송을 시도하지 않아야 한다")
    void should_not_send_error_when_handler_is_null() throws IOException, HttpWritingException {
        // === given ===
        when(factory.getHandler(any())).thenReturn(null);

        processor = new SocketProcessor(socket, selector, factory);

        // === when ===
        processor.run();

        // === then ===
        verify(socket).close();
        assertTrue(socket.isClosed());

        verify(handler, never()).handleError(any(), any(), any());
    }

    @Test
    @DisplayName("스트림이 null일 경우 에러 응답 전송을 시도하지 않아야 한다.")
    void should_not_send_error_when_stream_is_null() throws IOException, HttpWritingException {
        // === given ===
        when(socket.getInputStream()).thenReturn(null);

        processor = new SocketProcessor(socket, selector, factory);

        // === when ===
        processor.run();

        // === then ===
        verify(socket).close();
        assertTrue(socket.isClosed());

        verify(handler, never()).handleError(any(), any(), any());
    }
}