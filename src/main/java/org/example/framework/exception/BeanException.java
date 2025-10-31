package org.example.framework.exception;

/**
 * Bean 처리 과정에서 발생하는 런타임 예외 클래스.
 * <p>
 * 각 예외는 {@link BeanExceptionCode}를 통해 구체적인 원인을 식별하며,
 * 프레임워크 내부에서 일관된 방식으로 예외를 관리하기 위해 사용된다.
 * </p>
 */
public class BeanException extends RuntimeException {

    /** 예외 식별 코드*/
    private final BeanExceptionCode code;

    public BeanException(BeanExceptionCode code) {
        super(code.errorMessage);
        this.code = code;
    }

    public BeanException(BeanExceptionCode code, Throwable cause) {
        super(code.errorMessage, cause);
        this.code = code;
    }

    /**
     * 예외 코드를 반환한다.
     *
     * @return {@link BeanExceptionCode}
     */
    public BeanExceptionCode getCode() { return code; }

    /**
     * 예외 메시지를 "[CODE] message" 형식으로 반환한다.
     *
     * @return 포맷팅된 예외 메시지
     */
    @Override
    public String getMessage() {
        return "[" + code.name() + "] " + code.getErrorMessage();
    }

    /**
     * Bean 관련 예외 코드를 정의하는 열거형.
     * <p>
     * 각 항목은 구체적인 오류 원인을 설명하는 메시지를 가진다.
     * </p>
     */
    public enum BeanExceptionCode {
        BEANDEFINITION_NOT_FOUND("BeanDefinition is null");

        private final String errorMessage;

        BeanExceptionCode(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        /**
         * 예외 설명 메시지를 반환한다.
         *
         * @return 오류 메시지
         */
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
