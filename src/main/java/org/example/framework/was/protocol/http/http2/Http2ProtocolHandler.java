package org.example.framework.was.protocol.http.http2;

import org.example.framework.exception.todo.TODOCode;
import org.example.framework.exception.todo.TODOException;
import org.example.framework.was.protocol.core.HttpProtocolHandler;
import org.example.framework.was.protocol.core.RequestParser;
import org.example.framework.was.protocol.core.ResponseWriter;

import java.io.InputStream;
import java.io.OutputStream;

public class Http2ProtocolHandler extends HttpProtocolHandler {

    public Http2ProtocolHandler(RequestParser requestParser, ResponseWriter responseWriter) {
        super(requestParser, responseWriter);
    }

    @Override
    public void process(InputStream inputStream, OutputStream outputStream) {
        throw new TODOException(TODOCode.MAIN_HTTP2, TODOCode.SUB_PROTOCOL_HANDLER);
    }
}
