package org.example.framework.was.protocol.http.http1;

import org.example.framework.exception.was.HttpWritingException;
import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.model.HttpBody;
import org.example.framework.was.protocol.model.HttpHeader;
import org.example.framework.was.protocol.model.HttpResponse;
import org.example.framework.was.protocol.model.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OutputBufferWriteCallMetricTest {
    static class CountingOutputStream extends OutputStream {
        private final AtomicInteger writeCalls = new AtomicInteger();
        private final AtomicLong bytesWritten = new AtomicLong();

        @Override
        public void write(int b) {
            // OutputBuffer는 write(byte[],.. )를 사용하므로 보통 안 탐
            writeCalls.incrementAndGet();
            bytesWritten.incrementAndGet();
        }

        @Override
        public void write(byte[] b, int off, int len) {
            writeCalls.incrementAndGet();
            bytesWritten.addAndGet(len);
        }

        void reset() {
            writeCalls.set(0);
            bytesWritten.set(0);
        }

        int getWriteCalls() { return writeCalls.get(); }
        long getBytesWritten() { return bytesWritten.get(); }
    }

    @Test
    @DisplayName("바디 20000바이트 전송 시 OutputStream.write 호출 횟수를 측정한다(8KB 버퍼 기반)")
    void metric_write_calls_for_body() throws IOException, HttpWritingException {
        CountingOutputStream out = new CountingOutputStream();
        OutputBuffer buffer = new OutputBuffer(out);

        // 헤더 커밋
        HttpHeader header = new HttpHeader();
        header.put("Content-Type", "application/json");
        HttpResponse response = new HttpResponse(header, HttpBody.empty(), HttpProtocolVersion.HTTP_1_1, HttpStatus.OK);
        buffer.sendHeaders(response);

        // 헤더 write 영향 제거
        out.reset();

        int bodySize = 20000;
        byte[] body = new byte[bodySize];

        buffer.writeBody(body);
        buffer.flush();

        // 20000 / 8192 = 2 full flush + remainder flush(마지막 flush) => 총 3회가 기대값
        assertEquals(3, out.getWriteCalls());
        assertEquals(bodySize, out.getBytesWritten());

        System.out.println("[METRIC] bodySize=" + bodySize + ", writeCalls=" + out.getWriteCalls());
    }
}
