package org.example.framework.was.protocol.http.http2;

import org.example.framework.exception.todo.TODOCode;
import org.example.framework.exception.todo.TODOException;
import org.example.framework.exception.was.HttpWritingException;
import org.example.framework.was.adapter.ServletAdapter;
import org.example.framework.was.protocol.core.HttpProtocolHandler;
import org.example.framework.was.protocol.core.RequestParser;
import org.example.framework.was.protocol.core.ResponseWriter;
import org.example.framework.was.protocol.model.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Http2ProtocolHandler extends HttpProtocolHandler {

    private final ServletAdapter adapter;

    public Http2ProtocolHandler(ServletAdapter adapter) {
        super(
                Http2RequestParser.getInstance(),
                Http2ResponseWriter.getInstance()
        );
        this.adapter = adapter;
    }

    @Override
    public void process(InputStream inputStream, OutputStream outputStream) {
        throw new TODOException(TODOCode.MAIN_HTTP2, TODOCode.SUB_PROTOCOL_HANDLER);
    }

    @Override
    public void handleError(OutputStream outputStream, HttpStatus httpStatus, Throwable throwable) throws HttpWritingException, IOException {

    }
}
