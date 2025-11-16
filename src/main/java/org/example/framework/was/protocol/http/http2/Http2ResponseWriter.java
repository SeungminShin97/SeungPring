package org.example.framework.was.protocol.http.http2;

import org.example.framework.exception.todo.TODOCode;
import org.example.framework.exception.todo.TODOException;
import org.example.framework.was.protocol.core.ResponseWriter;
import org.example.framework.was.protocol.model.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

public class Http2ResponseWriter implements ResponseWriter {

    private Http2ResponseWriter() {}

    private static class Holder {
        static final Http2ResponseWriter INSTANCE = new Http2ResponseWriter();
    }

    public static Http2ResponseWriter getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void write(OutputStream outputStream, HttpResponse response) throws IOException {
        throw new TODOException(TODOCode.MAIN_HTTP2, TODOCode.SUB_RESPONSE_WRITER);
    }
}
