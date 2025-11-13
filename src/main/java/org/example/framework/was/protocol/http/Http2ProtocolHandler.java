package org.example.framework.was.protocol.http;

import org.example.framework.exception.todo.TODOCode;
import org.example.framework.exception.todo.TODOException;
import org.example.framework.was.protocol.core.HttpProtocolHandler;

import java.io.InputStream;
import java.io.OutputStream;

public class Http2ProtocolHandler implements HttpProtocolHandler {
    @Override
    public void process(InputStream inputStream, OutputStream outputStream) {
        throw new TODOException(TODOCode.MAIN_HTTP2, TODOCode.SUB_PROTOCOL_HANDLER);
    }
}
