package org.example.framework.exception.was;

/**
 * Http 버전 감지 실패할 경우 발생하는 예외
 */
public class HttpVersionDetectionException extends Exception{

    public HttpVersionDetectionException(String message) {
        super(message);
    }

    public HttpVersionDetectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
