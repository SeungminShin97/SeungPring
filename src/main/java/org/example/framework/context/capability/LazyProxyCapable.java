package org.example.framework.context.capability;

/**
 * 지연 로딩(Lazy Proxy) 적용 가능 여부를 나타내는 마커 인터페이스.
 *
 * <p>
 * 구현체는 자신이 Lazy Proxy 상태인지 여부를
 * 내부적으로 관리하며,
 * 컨테이너 또는 후처리 단계에서
 * 프록시 적용 여부 판단에 사용된다.
 * </p>
 */
public interface LazyProxyCapable {

    /**
     * 해당 객체를 Lazy Proxy 상태로 설정한다.
     *
     * <p>
     * 일반적으로 프록시 생성 또는 주입 과정에서 호출되며,
     * 이후 실제 빈 초기화를 지연시키는 기준 플래그로 사용된다.
     * </p>
     */
    void setLazyProxy();

    /**
     * 현재 객체가 Lazy Proxy 상태인지 여부를 반환한다.
     *
     * @return Lazy Proxy 적용 상태이면 true, 아니면 false
     */
    boolean isLazyProxy();
}
