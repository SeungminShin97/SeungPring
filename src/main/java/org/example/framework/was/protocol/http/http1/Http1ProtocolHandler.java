package org.example.framework.was.protocol.http.http1;

import org.example.framework.exception.todo.TODOCode;
import org.example.framework.exception.todo.TODOException;
import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.was.protocol.core.HttpProtocolHandler;
import org.example.framework.was.protocol.core.RequestParser;
import org.example.framework.was.protocol.core.ResponseWriter;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.UnsupportedCharsetException;

public class Http1ProtocolHandler extends HttpProtocolHandler {

    private Http1ProtocolHandler(RequestParser requestParser, ResponseWriter responseWriter) {
        super(requestParser, responseWriter);
    }

    private static class Holder {
        static final Http1ProtocolHandler INSTANCE = new Http1ProtocolHandler(
                Http1RequestParser.getInstance(),
                Http1ResponseWriter.getInstance()
        );
    }

    public static Http1ProtocolHandler getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void process(InputStream inputStream, OutputStream outputStream) throws HttpParsingException, UnsupportedCharsetException {
        super.requestParser.parse(inputStream);
        throw new TODOException(TODOCode.SUB_PROTOCOL_HANDLER);
    }
}