package org.example.framework.was.protocol.http.http1;

import org.example.framework.was.protocol.HttpProtocolVersion;
import org.example.framework.was.protocol.model.HttpRequest;

public final class KeepAlivePolicy {

    private KeepAlivePolicy() {}

    public static boolean shouldKeepAlive(HttpRequest request) {
        if(request == null) return false;

        HttpProtocolVersion version = request.getVersion();
        String connection = request.getHeader().getFirst("Connection");

        if(version == HttpProtocolVersion.HTTP_1_0)
            return hasToken(connection, "keep-alive");

        if(version == HttpProtocolVersion.HTTP_1_1)
            return !hasToken(connection, "close");

        return false;
    }

    private static boolean hasToken(String headerValue, String token) {
        if(headerValue == null || headerValue.isBlank()) return false;

        String[] parts = headerValue.split(",");
        for(String part : parts)
            if(part != null && part.trim().equalsIgnoreCase(token)) return true;

        return false;
    }
}
