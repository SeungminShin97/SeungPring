package org.example.framework.was.protocol.http;

import org.example.framework.exception.todo.TODOCode;
import org.example.framework.exception.todo.TODOException;
import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.was.protocol.core.RequestParser;
import org.example.framework.was.protocol.model.HttpMessage;

import java.io.InputStream;

public class Http2RequestParser implements RequestParser {

    @Override
    public HttpMessage parse(InputStream inputStream) throws HttpParsingException {
        throw new TODOException(TODOCode.MAIN_HTTP2, TODOCode.SUB_REQUEST_PARSER);
    }
}
