package org.example.framework.core.lifecycle;

/**
 * 모든 singleton Bean 인스턴스 생성이 완료된 이후
 * 추가 초기화 작업을 수행하기 위한 콜백 인터페이스입니다.
 *
 * <p>
 * 이 인터페이스를 구현한 Bean은
 * 컨테이너 초기화 과정의 마지막 단계에서
 * {@link #afterSingletonsInstantiated()}가 단 한 번 호출됩니다.
 * </p>
 *
 * <p>
 * 다른 Bean 전체에 의존하는 초기화 작업
 * (예: 매핑 테이블 구축, 캐시 초기화 등)에 사용됩니다.
 * </p>
 */
public interface SmartInitializingSingleton {

    /**
     * 모든 singleton Bean이 생성된 이후 호출됩니다.
     */
    void afterSingletonsInstantiated();
}
