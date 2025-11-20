package org.example.framework.was.protocol.http.http1;

import org.example.framework.exception.was.HttpWritingException;
import org.example.framework.was.common.ServerMetadata;
import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.model.HttpResponse;
import org.example.framework.was.utils.HeaderNameFormatter;
import org.example.framework.was.utils.HttpDateUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * HTTP/1.1 응답 전송을 위한 출력 버퍼.
 * <p>
 * 내부적으로 고정 크기 바이트 버퍼(8KB)를 유지하며,
 * 헤더/바디 데이터를 OutputStream으로 효율적으로 전송한다.
 * <br>
 * Writer가 stateless 구조를 유지하기 위해 상태(commit 여부, 버퍼 위치)는
 * 요청 단위 OutputBuffer 인스턴스가 보유한다.
 * </p>
 *
 * <h2>역할</h2>
 * <ul>
 *   <li>HTTP/1.1 Status-Line 및 헤더 문자열 생성 및 전송</li>
 *   <li>Content-Length, Content-Type, Date, Server 등의 필수 헤더 자동 처리</li>
 *   <li>바디 데이터 버퍼링 및 플러시</li>
 *   <li>commit 플래그로 헤더 중복 전송 방지</li>
 *   <li>OutputStream에 대한 최소 write() 호출로 성능 향상</li>
 * </ul>
 *
 * <h2>Tomcat Http11OutputBuffer 유사 구조</h2>
 * 이 클래스는 톰캣의 Http11OutputBuffer 동작 방식(버퍼링, commit, flush)을 단순화하여 구현한 것이다.
 */
public class OutputBuffer {

    private static final int DEFAULT_SIZE = 8192;
    private static final String RESPONSE_LINE_FORMAT = "%s %d %s\r\n";
    private static final String SERVER_NAME = ServerMetadata.SERVER_NAME;

    private final OutputStream outputStream;
    private final byte[] buffer = new byte[DEFAULT_SIZE];
    private int pos = 0;

    private boolean committed = false;

    public OutputBuffer(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * HTTP 응답 헤더를 전송한다.
     * <p>
     * 다음 순서로 헤더를 구성하여 보내며, 헤더 전송 후 commit 상태가 된다.
     * <ol>
     *   <li>Status-Line (HTTP/1.1 200 OK)</li>
     *   <li>Date 헤더</li>
     *   <li>Server 헤더</li>
     *   <li>Content-Length 헤더</li>
     *   <li>Content-Type 헤더</li>
     *   <li>사용자가 설정한 기타 헤더</li>
     *   <li>CRLF(빈 줄)를 통해 헤더 종료</li>
     * </ol>
     * <p>
     * 헤더는 즉시 flush() 되어야 하며(HTTP 프로토콜 규약),
     * commit 이후 헤더 재전송은 무시된다.
     * </p>
     *
     * @param response 전송할 HTTP 응답 객체
     * @throws IOException I/O 오류 발생 시
     * @throws HttpWritingException HTTP 버전 미지원 등 전송 불가능한 경우
     */
    public void sendHeaders(HttpResponse response) throws IOException, HttpWritingException {
        if(committed) return;

        if(response.getVersion() != HttpProtocolVersion.HTTP_1_1)
            throw new HttpWritingException("Unsupported HTTP version: only HTTP/1.1 is supported");


        String responseLine = String.format(
                RESPONSE_LINE_FORMAT,
                response.getVersion().getProtocolString(),
                response.getStatusCode(),
                response.getMessage()
        );

        // 필수 헤더
        String date = "Date: " + HttpDateUtil.now() + "\r\n";
        String server = "Server: " + SERVER_NAME + "\r\n";

        // Content-Length
        String contentLength = "Content-Length: " + response.getBody().getContentLengthLong() + "\r\n";

        // TODO: Connection 필드 구현

        // Content-Type
        String contentType = response.getHeader().getFirst("Content-Type");
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream"; // 기본값
        }
        String contentTypeHeader = "Content-Type: " + contentType + "\r\n";

        // 중복 방지를 위한 set
        final Set<String> mandatoryHeaders = Set.of(
                "DATE", "SERVER", "CONTENT-TYPE", "CONTENT-LENGTH" //, "CONNECTION"
        );

        // 나머지 헤더 필드
        StringBuilder otherHeaderFields = new StringBuilder();
        response.getHeader().getAll().forEach((k, v) ->{
            // 필수 헤더 목록에 없는 경우에만 추가
            if(!mandatoryHeaders.contains(k.toUpperCase())) {
                v.forEach(value -> {
                    otherHeaderFields.append(k).append(": ").append(value).append("\r\n");
                });
            }
        });

        String headerWithResponseLine =
                responseLine +
                date +
                server +
                contentTypeHeader +
                contentLength +
                otherHeaderFields +
                "\r\n";

        writeInternal(headerWithResponseLine.getBytes(StandardCharsets.UTF_8));

        flush();
        committed = true;
    }

    /**
     * HTTP 바디(payload)를 전송한다.
     * <p>
     * 헤더 전송(commit)이 완료된 이후에만 호출할 수 있다.
     * 내부 버퍼에 데이터를 적재하여 필요 시 자동 flush를 수행한다.
     * </p>
     *
     * @param data 응답 바디 데이터
     * @throws IOException I/O 오류 발생 시
     * @throws IllegalStateException 헤더가 아직 전송(commit)되지 않은 경우
     */
    public void writeBody(byte[] data) throws IOException {
        if (!committed) {
            throw new IllegalStateException("Headers not committed before body write");
        }
        writeInternal(data, 0, data.length);
    }

    /**
     * 내부 write 메서드.
     * <p>
     * 주어진 byte 배열의 지정된 구간(offset ~ len)을 내부 버퍼에 복사한다.
     * 버퍼가 가득 찬 경우 flushInternal()을 먼저 수행한다.
     * 톰캣 계열 WAS의 OutputBuffer와 동일한 버퍼링 전략을 사용한다.
     * </p>
     *
     * @param data 원본 바이트 배열
     * @param off  시작 offset
     * @param len  복사할 길이
     * @throws IOException I/O 오류 발생 시
     */
    private void writeInternal(byte[] data, int off, int len) throws IOException {
        int currentOffset = off;
        int remaining = len;

        while (remaining > 0) {
            // 버퍼에 남은 공간
            int availableSpace = buffer.length - pos;
            // 복사 가능한 공간
            int copyLength = Math.min(availableSpace, remaining);

            // 배열 복사
            System.arraycopy(data, currentOffset, buffer, pos, copyLength);
            pos += copyLength;
            currentOffset += copyLength;
            remaining -= copyLength;

            if(pos == buffer.length)
                flushInternal();
        }
    }

    /**
     * <p>
     * 배열 전체를 버퍼에 복사한다.
     * </p>
     *
     * @param data 전체 바이트 배열
     * @throws IOException I/O 오류 발생 시
     */
    private void writeInternal(byte[] data) throws IOException {
        writeInternal(data, 0, data.length);
    }

    /**
     * OutputBuffer의 전체 데이터를 모두 OutputStream으로 전송한다.
     * <p>
     * flushInternal()을 호출하여 내부 버퍼를 비운 뒤,
     * OutputStream.flush()를 호출한다.
     * </p>
     *
     * @throws IOException I/O 오류 발생 시
     */
    public void flush() throws IOException {
        flushInternal();
        outputStream.flush();
    }

    /**
     * 내부 버퍼의 내용을 OutputStream에 즉시 기록하고 버퍼를 비운다.
     * <p>
     * 바디 전송 중 버퍼가 가득 찰 때 자동으로 호출될 수 있다.
     * write() 호출 횟수를 최소화하여 성능 이점을 얻는다.
     * </p>
     *
     * @throws IOException I/O 오류 발생 시
     */
    protected void flushInternal() throws IOException {
        if (pos > 0) {
            outputStream.write(buffer, 0, pos);
            pos = 0;
        }
    }

}
