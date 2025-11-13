package org.example.framework.was.protocol.core;

import org.example.framework.was.protocol.model.HttpResponse;

import java.io.InputStream;
import java.io.OutputStream;

public interface HttpProtocolHandler {

    void process(InputStream inputStream, OutputStream outputStream);
}
