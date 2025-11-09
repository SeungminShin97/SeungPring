package org.example.framework.core;

/**
 * {@code BeanFactory}는 Bean 인스턴스를 조회, 생성 및 메타데이터를 확인하기 위한 핵심 인터페이스다.
 * <p>컨테이너 내부에서 Bean의 생명주기를 관리하며, 이름 또는 타입 기반으로 Bean을 검색할 수 있다.</p>
 */
public interface BeanFactory {

    /**
     * 지정된 이름의 Bean 정의나 외부에서 등록된 싱글톤 인스턴스가 존재하는지 확인한다.
     *
     * @param name Bean 이름
     * @return 존재하면 true, 아니면 false
     */
    boolean containsBean(String name);

    /**
     * 지정된 이름의 Bean의 싱글톤 인스턴스가 존재하는지 확인한다.
     * @param name Bean 이름
     * @return 존재하면 true, 아니면 false
     */
    boolean containsSingleton(String name);

    /**
     * 지정된 Bean 이름과 연결된 모든 별칭(alias)을 반환한다.
     *
     * @param name Bean 이름
     * @return 별칭 배열, 없으면 빈 배열
     */
    String[] getAliases(String name);

    /**
     * 지정된 타입과 일치하거나 호환되는 Bean을 반환한다.
     *
     * @param requiredType 찾을 Bean 타입
     * @param <T>          반환 타입
     * @return Bean 인스턴스
     */
    <T> T getBean(Class<T> requiredType);

    /**
     * 지정된 이름의 Bean을 반환한다.
     *
     * @param beanName Bean 이름
     * @return Bean 인스턴스
     */
    Object getBean(String beanName);

    /**
     * 지정된 이름의 Bean 타입을 반환한다.
     *
     * @param name Bean 이름
     * @return Bean의 클래스 타입
     */
    Class<?> getType(String name);

    /**
     * 해당 Bean이 프로토타입(호출 시마다 새 인스턴스 생성)인지 확인한다.
     *
     * @param name Bean 이름
     * @return 프로토타입이면 true, 아니면 false
     */
    boolean isPrototype(String name);

    /**
     * 해당 Bean이 싱글톤(한 번만 생성되고 재사용)인지 확인한다.
     *
     * @param name Bean 이름
     * @return 싱글톤이면 true, 아니면 false
     */
    boolean isSingleton(String name);

    /**
     * 지정된 이름의 Bean이 주어진 타입과 일치하거나 호환되는지 확인한다.
     *
     * @param name        Bean 이름
     * @param typeToMatch 비교할 타입
     * @return 타입이 일치하거나 호환되면 true, 아니면 false
     */
    boolean isTypeMatch(String name, Class<?> typeToMatch);
}
