package org.example.framework.was.protocol.http.http1;

import org.example.framework.exception.was.HttpParsingException;
import org.example.framework.exception.was.HttpWritingException;
import org.example.framework.was.protocol.core.HttpProtocolHandler;
import org.example.framework.was.protocol.core.RequestParser;
import org.example.framework.was.protocol.core.ResponseWriter;
import org.example.framework.was.protocol.model.HttpMessage;
import org.example.framework.was.protocol.model.HttpResponse;

import java.io.IOException;
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
    public void process(InputStream inputStream, OutputStream outputStream) throws HttpParsingException, UnsupportedCharsetException, HttpWritingException, IOException {
        HttpMessage request = super.requestParser.parse(inputStream);
        //TODO: 디스패처 서블릿 구현 시 교체
        HttpResponse response = new HttpResponse(null, null, null, 200, null);
        super.responseWriter.write(outputStream, response);
    }
}