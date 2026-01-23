package org.example.framework.core.lifecycle;

/**
 * Bean 소멸 시 정리 로직을 정의하는 인터페이스.
 */
public interface DisposableBean {
    void destroy();
}
