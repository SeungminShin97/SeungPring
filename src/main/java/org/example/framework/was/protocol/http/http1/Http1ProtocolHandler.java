package org.example.framework.was.protocol.http.http1;

import org.example.framework.exception.todo.TODOCode;
import org.example.framework.exception.todo.TODOException;
import org.example.framework.was.protocol.core.HttpProtocolHandler;
import org.example.framework.was.protocol.core.RequestParser;
import org.example.framework.was.protocol.core.ResponseWriter;

import java.io.InputStream;
import java.io.OutputStream;

public class Http1ProtocolHandler extends HttpProtocolHandler {

    public Http1ProtocolHandler(RequestParser requestParser, ResponseWriter responseWriter) {
        super(requestParser, responseWriter);
    }

    @Override
    public void process(InputStream inputStream, OutputStream outputStream) {
        throw new TODOException(TODOCode.SUB_PROTOCOL_HANDLER);
    }
}
