package org.example.framework.exception.was;

/**
 * Http 출력에 실패할 경우 던지는 예외 <br>
 * 잡는 곳에서 500 Internal Server Error 응답으로 변환해야 한다.
 */
public class HttpWritingException extends Exception {

    public HttpWritingException() {}

    public HttpWritingException(String message) {
        super(message);
    }

    public HttpWritingException(String message, Throwable cause) {
        super(message, cause);
    }
}
