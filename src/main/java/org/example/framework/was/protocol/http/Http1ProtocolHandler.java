package org.example.framework.was.protocol.http;

import org.example.framework.exception.todo.TODOCode;
import org.example.framework.exception.todo.TODOException;
import org.example.framework.was.protocol.core.HttpProtocolHandler;
import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;

public class Http1ProtocolHandler implements HttpProtocolHandler {

    @Override
    public HttpResponse process(HttpRequest request) {
        throw new TODOException(TODOCode.SUB_PROTOCOL_HANDLER);
    }
}
