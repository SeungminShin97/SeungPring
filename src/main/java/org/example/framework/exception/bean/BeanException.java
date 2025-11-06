package org.example.framework.exception.bean;

/**
 * Bean 처리 과정에서 발생하는 최상위 예외 클래스.
 * <p>
 * 모든 Bean 관련 예외의 공통 부모로,
 * 구체적인 예외들은 이 클래스를 상속받아 정의한다.
 * </p>
 */
public class BeanException extends RuntimeException {

    public BeanException(String message) {
        super(message);
    }

    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }
}
