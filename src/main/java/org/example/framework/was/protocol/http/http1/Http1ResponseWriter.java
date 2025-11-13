package org.example.framework.was.protocol.http.http1;

import org.example.framework.exception.todo.TODOCode;
import org.example.framework.exception.todo.TODOException;
import org.example.framework.was.protocol.core.ResponseWriter;
import org.example.framework.was.protocol.model.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

public class Http1ResponseWriter implements ResponseWriter {

    @Override
    public void write(OutputStream outputStream, HttpResponse response) throws IOException {
        throw new TODOException(TODOCode.MAIN_HTTP1, TODOCode.SUB_RESPONSE_WRITER);
    }
}
