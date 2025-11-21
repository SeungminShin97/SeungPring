package org.example.framework.was.processor;

import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.exception.was.HttpVersionDetectionException;
import org.example.framework.exception.was.HttpWritingException;
import org.example.framework.was.protocol.HttpProtocolSelector;
import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.core.HttpProtocolHandler;
import org.example.framework.was.protocol.http.HttpProtocolHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 클라이언트와 연결된 소켓 하나를 처리하는 Runnable.
 * <p>
 * 역할:
 * <ul>
 *     <li>HTTP 버전 감지</li>
 *     <li>버전에 맞는 프로토콜 핸들러 생성</li>
 *     <li>요청 처리 실행</li>
 *     <li>예외 발생 시 에러 응답 전송</li>
 * </ul>
 */
public class SocketProcessor implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(SocketProcessor.class);

    private final Socket socket;
    private final HttpProtocolSelector selector;
    private final HttpProtocolHandlerFactory handlerFactory;

    public SocketProcessor(Socket socket, HttpProtocolSelector selector, HttpProtocolHandlerFactory handlerFactory) {
        this.socket = socket;
        this.selector = selector;
        this.handlerFactory = handlerFactory;
    }

    /**
     * {@inheritDoc}
     * 소켓 스트림을 읽어 HTTP 버전을 감지하고,
     * 해당 버전에 맞는 프로토콜 핸들러를 이용해 요청을 처리한다.
     * <p>
     * 처리 중 발생하는 주요 예외(I/O, 파싱, 쓰기)는
     * 적절한 HTTP 상태코드로 변환하여 에러 응답을 보낸다.
     */
    @Override
    public void run() {
        InputStream in = null;
        OutputStream out = null;
        HttpProtocolHandler handler = null;
        try (socket) {
            in = socket.getInputStream();
            out = socket.getOutputStream();

            HttpProtocolVersion version = selector.detect(in);
            handler = handlerFactory.getHandler(version);

            handler.process(in, out);
        } catch (IOException e) {
            log.error("[SocketProcessor] Socket I/O Error, connection closed by client/network: {}", e.getMessage());
        } catch (HttpVersionDetectionException e) {
            log.error("[SocketProcessor] Cannot detect Http Version, connection closed by client/network: {}", e.getMessage());
        } catch (HttpParsingException e) {
            sendErrorResponse(handler, out, 400, "Bad Request", e);
        } catch (HttpWritingException e) {
            sendErrorResponse(handler, out, 500, "Internal Server Error", e);
        } catch (Exception e) {
            log.error("[SocketProcessor] Uncaught Internal Server Exception (500): {}", e.getMessage(), e);
            sendErrorResponse(handler, out, 500, "Internal Server Error", e);
        }
    }

    /**
     * 예외 상황에서 클라이언트에게 에러 응답을 전송한다.
     *
     * @param handler     사용 중인 프로토콜 핸들러
     * @param out         소켓 OutputStream
     * @param statusCode  HTTP 상태 코드
     * @param message     상태 메시지
     * @param throwable   발생한 예외
     *
     * 핸들러가 null이거나 스트림이 없으면 응답 전송을 생략한다.
     * 첫 응답 실패 시 500 에러로 한 번 더 재전송을 시도한다.
     */
    private void sendErrorResponse(HttpProtocolHandler handler, OutputStream out, int statusCode, String message, Throwable throwable) {
        if(handler == null || out == null) {
            log.error("[SocketProcessor] Cannot send error response; handler or outputStream is null.");
            return;
        }

        // 1차 시도
        try {
            handler.handleError(out, statusCode, message, throwable);
            log.warn("[SocketProcessor] Successfully sent {} response.", statusCode);
        } catch (Exception ex) {
            log.error("[Fatal] Failed to send {} response. Attempting 500 fallback: {}", statusCode, ex.getMessage());

            // 2차 시도
            try {
                handler.handleError(out, 500, "Internal Server Error", ex);
                log.warn("[Error Handler] Successfully sent 500 fallback response.");
            } catch (Exception finalEx) {
                log.error("[Critical] Failed to send 500 fallback response. Connection is likely broken: {}", finalEx.getMessage());
            }
        }
    }
}