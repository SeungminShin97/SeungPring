package org.example.framework.exception.was;

/**
 * Http 파싱에 실패할 경우 던지는 예외 <br>
 * 잡는 곳에서 400 Bad Request 응답으로 변환해야 한다.
 */
public class HttpParsingException extends Exception{

    public HttpParsingException(String message) {
        super(message);
    }

    public HttpParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
