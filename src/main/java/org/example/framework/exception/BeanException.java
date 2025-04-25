package org.example.framework.exception;

public class BeanException extends RuntimeException {
    private final BeanExceptionCode code;

    public BeanException(BeanExceptionCode code) {
        super(code.errorMessage);
        this.code = code;
    }


    public enum BeanExceptionCode {
        BEANDEFINITION_NOT_FOUND("BeanDefinition is null");

        private final String errorMessage;

        BeanExceptionCode(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
