package org.example.framework.was.protocol;

import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.was.protocol.core.RequestParser;
import org.example.framework.was.protocol.http.Http1RequestParser;
import org.example.framework.was.protocol.model.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * HTTP 프로토콜 버전을 감지하고, 해당 버전에 맞는 {@link RequestParser} 구현체를 선택하여 반환하는 컴포넌트입니다.
 * <p>
 * 클라이언트 연결로부터 받은 {@link InputStream}의 시작 부분(프리페이스)을 읽어
 * HTTP/1.1, HTTP/2.0 등의 프로토콜 종류를 판별합니다.
 * <p>
 * 감지 후에는 {@link InputStream#reset()}을 호출하여 스트림 포인터를 되돌려,
 * 반환된 {@link RequestParser}가 스트림을 처음부터 읽을 수 있도록 준비합니다.
 *
 * @see org.example.framework.was.protocol.core.RequestParser
 * @see org.example.framework.was.protocol.http.Http1RequestParser
 */
public class HttpProtocolSelector {

    /**
     * 입력 스트림의 시작 부분을 분석하여 적절한 RequestParser를 선택합니다.
     * <p>
     * HTTP/1.1인 경우 {@link Http1RequestParser}를 반환하며,
     * HTTP/2.0 프리페이스가 감지되면 현재 지원하지 않는다는 예외를 던집니다.
     * 감지 후 스트림은 자동으로 reset됩니다.
     * * @param inputStream 클라이언트 연결로부터 받은 입력 스트림 (반드시 mark/reset을 지원해야 함)
     * @return 감지된 프로토콜에 맞는 RequestParser 인스턴스
     * @throws IOException 스트림 처리 중 I/O 오류가 발생할 경우
     * @throws HttpParsingException 유효한 HTTP 프로토콜(1.1 또는 2.0 프리페이스)로 시작하지 않는 경우
     * @throws UnsupportedEncodingException HTTP/2.0 요청이 들어왔으나 현재 WAS가 이를 처리하지 못할 경우
     */
    public RequestParser detect(InputStream inputStream) throws IOException, HttpParsingException {
        inputStream.mark(24);
        byte[] preface = inputStream.readNBytes(24);
        String text = new String(preface);
        inputStream.reset();

        // TODO: HTTP/2.0 추가
        if(text.startsWith("PRI * HTTP/2.0"))
            throw new UnsupportedEncodingException("HTTP2.0 not supported");

        boolean isHttp1 = Arrays.stream(HttpMethod.values())
                .anyMatch(method -> text.startsWith(method.name() + " "));
        if(isHttp1)
            return new Http1RequestParser();

        throw new HttpParsingException("Unknown or unsupported protocol. " +
                "Not HTTP/1.1 or HTTP/2.0. Preface: " + text);
    }
}