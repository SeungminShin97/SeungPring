package org.example.framework.core;

import org.example.framework.core.lifecycle.BeanPostProcessor;

/**
 * BeanFactory의 구성/라이프사이클 확장용 인터페이스.
 *
 * - BeanPostProcessor 등록
 * - singleton 관리(선인스턴스화/파기)
 *
 * Context 초기화 단계에서만 사용된다.
 */
public interface ConfigurableBeanFactory extends BeanFactory {

    /** Bean 초기화 파이프라인에 Processor를 등록한다. */
    void addBeanPostProcessor(BeanPostProcessor processor);

    /** Lazy 제외 singleton Bean을 선인스턴스화한다. */
    void preInstantiateSingletons();

    /** singleton Bean을 파기한다. */
    void destroySingletons();
}
