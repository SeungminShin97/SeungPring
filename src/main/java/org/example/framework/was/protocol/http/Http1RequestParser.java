package org.example.framework.was.protocol.http;

import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.was.protocol.core.RequestParser;

import java.io.*;

import org.example.framework.was.protocol.model.*;

/**
 * HTTP/1.1 요청을 파싱하여 {@link HttpRequest} 객체로 변환합니다.
 * <p>
 * Request Line → Header → Body 순으로 파싱합니다.
 *
 * <ul>
 *   <li>Request Line: 메서드, 경로, 버전 추출</li>
 *   <li>Header: ':' 기준으로 키-값 파싱</li>
 *   <li>Body: Content-Length 기반으로 바이트 읽기</li>
 * </ul>
 *
 * @throws HttpParsingException 파싱 실패, IO 오류, 형식 불일치 시 발생
 */
public class Http1RequestParser implements RequestParser {

    @Override
    public HttpMessage parse(InputStream inputStream) throws HttpParsingException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // Http 요청 라인 처리
        String[] requestParam = parseRequestLine(reader);

        // Http Header 처리
        HttpHeader header = parseHeader(reader);

        // Http Body 처리
        // Transfer-Encoding 은 배제
        HttpBody body;
        String lenValue = header.getFirst("Content-Length");
        if(lenValue != null && !lenValue.isEmpty()){
            long contentLength = Long.parseLong(lenValue);
            body = parseBody(inputStream, contentLength);
        } else
            body = HttpBody.empty();

        return new HttpRequest(header, body, requestParam[2], HttpMethod.from(requestParam[0]), requestParam[1]);
    }

    /**
     * HTTP 요청의 첫 줄(Request Line)을 파싱하여
     * {@link HttpMessage} 객체에 메서드, 경로, 버전을 설정한다.
     *
     * @param reader 요청 라인을 포함한 {@link BufferedReader}
     * @return [0]=Method, [1]=URI, [2]=Version
     * @throws HttpParsingException 요청 라인 형식 오류 또는 I/O 오류 발생 시
     */
    private String[] parseRequestLine(BufferedReader reader) throws HttpParsingException {
        try {
            String requestLine = reader.readLine();

            if (requestLine == null || requestLine.isEmpty()) {
                throw new HttpParsingException("Empty request line");
            }

            // 1. Request Line
            // ex) GET /posts/123?sort=desc&page=2 HTTP/1.1
            // requestParam[0] : Method
            // requestParam[1] : URI
            // requestParam[2] : Version
            String[] requestParam = requestLine.split(" ");
            if (requestParam.length != 3) {
                throw new HttpParsingException("Malformed request line: " + requestLine);
            }
            return requestParam;
        } catch (IOException e) {
            throw new HttpParsingException("I/O error while reading request line", e);
        } catch (IllegalArgumentException e) {
            throw new HttpParsingException("Unsupported HTTP method: " + e.getMessage(), e);
        }
    }

    /**
     * HTTP 요청 헤더를 파싱합니다.
     * <p>각 줄을 ":" 기준으로 키-값으로 분리하여 {@link HttpHeader}에 저장합니다.</p>
     *
     * @param reader 요청 헤더를 포함한 {@link BufferedReader}
     * @return 파싱된 헤더 객체
     * @throws HttpParsingException 형식 오류 또는 I/O 오류 발생 시
     */
    private HttpHeader parseHeader(BufferedReader reader) throws HttpParsingException {
        try {
            HttpHeader header = new HttpHeader();
            String line;
            while((line = reader.readLine()) != null && !line.isEmpty()) {
                int idx = line.indexOf(":");
                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                header.put(key, value);
            }
            return header;
        } catch (IOException e) {
            throw new HttpParsingException("I/O error while reading request line", e);
        }
    }

    /**
     * HTTP 요청 바디를 Content-Length 기반으로 읽습니다.
     * <p>Transfer-Encoding은 지원하지 않습니다.</p>
     *
     * @param inputStream 요청 본문이 포함된 {@link InputStream}
     * @param contentLength Content-Length 헤더 값 (바이트 단위)
     * @return 파싱된 {@link HttpBody} 객체
     * @throws HttpParsingException 바디 읽기 실패 또는 I/O 오류 발생 시
     */
    private HttpBody parseBody(InputStream inputStream, long contentLength) throws HttpParsingException {
        DataInputStream byteReader = new DataInputStream(inputStream);
        if(contentLength == 0)
            return HttpBody.empty();

        // 배열 최대 크기(int)를 넘어설 경우 스트리밍으로 처리해야됨, 구현 x
        if(contentLength > Integer.MAX_VALUE)
            throw new UnsupportedOperationException("Streaming body parsing not supported yet");

        try {
            byte[] data = new byte[(int) contentLength];
            byteReader.readFully(data);
            return new HttpBody(data);
        } catch (IOException e) {
            throw new HttpParsingException("Failed to read request body", e);
        }
    }
}

/**
 * GET /posts/123?sort=desc&page=2 HTTP/1.1
 * Host: example.com
 * User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64)
 * Accept: text/html,application/xhtml+xml,application/xml;q=0.9
 * Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
 * Accept-Encoding: gzip, deflate, br
 * Connection: keep-alive
 * Cookie: sessionId=abc123; theme=dark
 *
 *
 *
 *
 * POST /api/login HTTP/1.1
 * Host: example.com
 * Content-Type: application/json
 * Content-Length: 45
 *
 * {"username":"rnsdl","password":"1234"}
 *
 *
 *
 *
 * POST /submit HTTP/1.1
 * Host: example.com
 * Content-Type: application/x-www-form-urlencoded
 * Content-Length: 27
 *
 * name=seungmin&age=27
 */