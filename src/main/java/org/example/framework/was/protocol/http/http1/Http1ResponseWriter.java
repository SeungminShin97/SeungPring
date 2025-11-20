package org.example.framework.was.protocol.http.http1;

import org.example.framework.exception.was.HttpWritingException;
import org.example.framework.was.protocol.core.ResponseWriter;
import org.example.framework.was.protocol.model.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

/**
 * HTTP/1.1 응답을 직렬화(serialize)하여 {@link OutputStream}으로 전송하는 구현체. <br>
 * 싱글톤 패턴을 사용
 */
public class Http1ResponseWriter implements ResponseWriter {

    private Http1ResponseWriter() {}

    private static class Holder {
        static Http1ResponseWriter INSTANCE = new Http1ResponseWriter();
    }

    public static Http1ResponseWriter getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * HTTP/1.1 규칙에 따라 응답을 직렬화하여 {@link OutputStream}으로 전송한다.
     * <p>
     * 헤더 전송 후, 바디가 존재할 경우 바디도 함께 출력하며,
     * 마지막에는 flush를 수행하여 모든 내용이 실제 스트림에 기록되도록 한다.
     * </p>
     *
     * @param outputStream 응답을 기록할 출력 스트림
     * @param response     직렬화할 HTTP 응답 객체
     * @throws IOException 스트림 쓰기 과정에서 오류가 발생한 경우
     * @throws HttpWritingException HTTP 응답 형식이 잘못되었거나 작성 과정에서 실패한 경우
     */
    @Override
    public void write(OutputStream outputStream, HttpResponse response) throws IOException, HttpWritingException {
        OutputBuffer outputBuffer = new OutputBuffer(outputStream);
        outputBuffer.sendHeaders(response);

        if(!response.getBody().isEmpty()) {
            byte[] bodyData = response.getBody().getData();
            outputBuffer.writeBody(bodyData);
            outputBuffer.flush();
        }
    }
}
