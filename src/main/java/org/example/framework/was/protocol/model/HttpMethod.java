package org.example.framework.was.protocol.model;

/**
 * HTTP 요청 메서드를 정의한 열거형.
 * <p>
 * 각 메서드는 요청의 목적과 의미를 나타내며,
 * 문자열로부터 {@link #from(String)} 메서드를 통해 변환할 수 있다.
 * <ul>
 *   <li>GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE</li>
 * </ul>
 */
public enum HttpMethod {
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    public static HttpMethod from(String name) {
        for(HttpMethod method : values()) {
            if(method.name().equalsIgnoreCase(name))
                return method;
        }
        throw new IllegalArgumentException("Unsupported HTTP method: " + name);
    }
}
