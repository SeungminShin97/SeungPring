package org.example.framework.core;

import org.example.framework.LifeCycle.LifeCycle;

/**
 * 설정 가능(Application-level)한 ApplicationContext 인터페이스.
 *
 * <p>
 * 컨텍스트의 초기화와 종료를 제어하기 위한 표준 계약을 정의하며,
 * {@link ApplicationContext}의 조회 기능과
 * {@link LifeCycle}의 start/stop 흐름을 함께 연결한다.
 * </p>
 *
 * <p>
 * 일반적으로 {@code refresh()}를 통해 컨텍스트를 초기화하고,
 * {@code close()}를 통해 관리 중인 Bean과 자원을 정리한다.
 * </p>
 */

public interface ConfigurableApplicationContext extends ApplicationContext, LifeCycle {

    /**
     * ApplicationContext를 초기화한다.
     *
     * <p>
     * 일반적으로 다음 작업들을 포함한다.
     * </p>
     * <ul>
     *   <li>BeanDefinition 로딩</li>
     *   <li>BeanPostProcessor 등록</li>
     *   <li>singleton Bean 초기화</li>
     *   <li>라이프사이클 콜백 실행</li>
     * </ul>
     *
     * <p>
     * 컨텍스트 라이프사이클의 시작 지점이며,
     * 구현체에서는 템플릿 메서드 패턴으로 내부 단계를 분리한다.
     * </p>
     */
    void refresh();

    /**
     * ApplicationContext를 종료한다.
     *
     * <p>
     * 컨텍스트가 관리하던 자원을 정리하고,
     * singleton Bean에 대해 destroy 콜백을 실행한다.
     * </p>
     *
     * <p>
     * 일반적으로 {@code @PreDestroy}, {@code DisposableBean} 등의
     * 소멸 단계 훅이 이 시점에 호출된다.
     * </p>
     */
    void close();
}
