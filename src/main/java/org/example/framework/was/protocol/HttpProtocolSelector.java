package org.example.framework.was.protocol;

import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.exception.was.HttpVersionDetectionException;
import org.example.framework.was.protocol.core.RequestParser;
import org.example.framework.was.protocol.http.http1.Http1RequestParser;
import org.example.framework.was.protocol.model.HttpMethod;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
 * @see Http1RequestParser
 */
public class HttpProtocolSelector {

    /**
     * 입력 스트림의 시작 부분을 분석하여 적절한 프로토콜 버전을 감지합니다.
     * <p>
     * HTTP/1.1 프리페이스가 감지되면 {@link HttpProtocolVersion#HTTP_1_1}을 반환하며,
     * HTTP/2.0 프리페이스가 감지되면 {@link HttpProtocolVersion#HTTP_2_0}을 반환합니다.
     * 감지 후 스트림은 자동으로 reset됩니다.
     * * @param inputStream 클라이언트 연결로부터 받은 입력 스트림 (반드시 mark/reset을 지원해야 함)
     * @return 감지된 HTTP 프로토콜 버전 Enum
     * @throws IOException 스트림 처리 중 I/O 오류가 발생할 경우
     * @throws HttpVersionDetectionException 유효한 HTTP 프로토콜(1.1 또는 2.0 프리페이스)로 시작하지 않는 경우
     */
    public HttpProtocolVersion detect(InputStream inputStream) throws IOException, HttpVersionDetectionException {
        byte[] preface = inputStream.readNBytes(24);
        if(preface.length == 0)
            throw new HttpVersionDetectionException("Empty request preface");

        String text = new String(preface, StandardCharsets.US_ASCII);


        if(text.startsWith("PRI * HTTP/2.0"))
            return HttpProtocolVersion.HTTP_2_0;

        boolean isHttp1 = Arrays.stream(HttpMethod.values())
                .anyMatch(method -> text.startsWith(method.name() + " "));
        if(isHttp1)
            return HttpProtocolVersion.HTTP_1_1;

        throw new HttpVersionDetectionException("Unknown or unsupported protocol. " +
                "Not HTTP/1.1 or HTTP/2.0. Preface: " + text);
    }
}