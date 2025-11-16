package org.example.framework.was.protocol.http.http2;

import org.example.framework.exception.todo.TODOCode;
import org.example.framework.exception.todo.TODOException;
import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.was.protocol.core.RequestParser;
import org.example.framework.was.protocol.model.HttpMessage;

import java.io.InputStream;

public class Http2RequestParser implements RequestParser {

    private Http2RequestParser() {}

    private static class Holder {
        static final Http2RequestParser INSTANCE = new Http2RequestParser();
    }

    public static Http2RequestParser getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public HttpMessage parse(InputStream inputStream) throws HttpParsingException {
        throw new TODOException(TODOCode.MAIN_HTTP2, TODOCode.SUB_REQUEST_PARSER);
    }
}
