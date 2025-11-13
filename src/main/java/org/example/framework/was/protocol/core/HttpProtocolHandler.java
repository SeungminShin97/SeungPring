package org.example.framework.was.protocol.core;

import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.was.protocol.model.HttpResponse;

public interface HttpProtocolHandler {

    HttpResponse process(HttpRequest request);
}
