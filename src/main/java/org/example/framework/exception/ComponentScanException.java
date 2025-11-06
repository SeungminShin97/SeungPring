package org.example.framework.exception;

/**
 * ComponentScan 중 발생하는 예외 클래스
 */
public class ComponentScanException extends RuntimeException{
    private static final String PREFIX = "Failed to scan components: ";

    public ComponentScanException(String message) {
        super(PREFIX + message);
    }

    public ComponentScanException(String message, Throwable cause) {
        super(PREFIX + message, cause);
    }
}
