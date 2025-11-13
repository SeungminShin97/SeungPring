package org.example.framework.was.protocol.core;

import org.example.framework.was.protocol.model.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

public interface ResponseWriter {

    void write(OutputStream outputStream, HttpResponse response) throws IOException;
}
