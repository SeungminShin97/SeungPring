package org.example.framework.was.protocol.http.http1;

import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.exception.was.HttpWritingException;
import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.core.HttpProtocolHandler;
import org.example.framework.was.protocol.core.RequestParser;
import org.example.framework.was.protocol.core.ResponseWriter;
import org.example.framework.was.protocol.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

/**
 * HTTP/1.1 프로토콜 처리를 위한 구체적인 핸들러 구현체.
 * <p>
 * HTTP/1.1 규약에 따라 요청을 파싱하고 응답을 작성하는 역할을 담당하며,
 * HttpProtocolHandler의 기능을 상속받아 사용한다. 싱글턴으로 구현되었다.
 */
public class Http1ProtocolHandler extends HttpProtocolHandler {

    private Http1ProtocolHandler(RequestParser requestParser, ResponseWriter responseWriter) {
        super(requestParser, responseWriter);
    }

    private static class Holder {
        static final Http1ProtocolHandler INSTANCE = new Http1ProtocolHandler(
                Http1RequestParser.getInstance(),
                Http1ResponseWriter.getInstance()
        );
    }

    public static Http1ProtocolHandler getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * HTTP/1.1 요청을 처리하고 응답을 전송한다.
     * <p>
     * 1. {@link RequestParser}를 이용해 요청을 파싱한다.
     * 2. (TODO: 디스패처 서블릿 호출)
     * 3. {@link ResponseWriter}를 이용해 응답을 클라이언트에게 전송한다.
     *
     * @param inputStream 클라이언트로부터의 입력 스트림 (요청 데이터)
     * @param outputStream 클라이언트로의 출력 스트림 (응답 데이터)
     * @throws HttpParsingException 요청 메시지 파싱 중 오류 발생 시
     * @throws UnsupportedCharsetException 지원하지 않는 문자셋 사용 시
     * @throws HttpWritingException 응답 작성 중 오류 발생 시
     * @throws IOException 소켓 I/O 작업 중 오류 발생 시
     */
    @Override
    public void process(InputStream inputStream, OutputStream outputStream) throws HttpParsingException, UnsupportedCharsetException, HttpWritingException, IOException {
        System.out.println("start parsing");
        HttpMessage request = super.requestParser.parse(inputStream);
        System.out.println("parsing success");
        //TODO: 디스패처 서블릿 구현 시 교체
        String message = "SeungPring OK";
        byte[] bodyBytes = message.getBytes(StandardCharsets.UTF_8);
        HttpBody body = new HttpBody(bodyBytes);

        HttpHeader header = new HttpHeader();
        header.put("Content-Type", "text/plain; charset=UTF-8");
        header.put("Content-Length", String.valueOf(bodyBytes.length));

        HttpResponse response = new HttpResponse(
                header,
                body,
                HttpProtocolVersion.HTTP_1_1,
                HttpStatus.OK
        );
        System.out.println("start write");
        super.responseWriter.write(outputStream, response);
        System.out.println("write success");
    }

    /**
     * HTTP/1.1 규약에 맞는 에러 응답 메시지를 생성하고 전송한다.
     * <p>
     * 에러 메시지(HTML 형식)와 {@code Connection: close} 헤더를 포함한 완전한 HTTP 응답을 작성한다.
     *
     * @param outputStream 클라이언트로의 출력 스트림
     * @param httpStatus Http 상태
     * @param throwable 에러의 원인이 된 예외 객체
     * @throws HttpWritingException 응답 작성 중 오류 발생 시
     * @throws IOException 소켓 I/O 작업 중 오류 발생 시
     */
    @Override
    public void handleError(OutputStream outputStream, HttpStatus httpStatus, Throwable throwable) throws HttpWritingException, IOException {
        String bodyFormat = String.format("<h1>Error %d: %s</h1>\n<p>%s.</p>", httpStatus.code(), httpStatus.reason(), throwable.getMessage());
        byte[] data = bodyFormat.getBytes(StandardCharsets.UTF_8);
        HttpBody body = new HttpBody(data);

        HttpHeader header = new HttpHeader();
        header.put("Content-Type", "text/html; charset=utf-8");
        header.put("Connection", "close");

        HttpResponse response = new HttpResponse(header, body, HttpProtocolVersion.HTTP_1_1, httpStatus);
        super.responseWriter.write(outputStream, response);
    }
}