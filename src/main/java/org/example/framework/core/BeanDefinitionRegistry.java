package org.example.framework.core;

import org.example.framework.context.beanDefinition.BeanDefinition;
import org.example.framework.exception.bean.NoSuchBeanDefinitionException;

import java.util.Collection;
import java.util.List;

/**
 * {@link BeanDefinitionRegistry}는 Bean 정의 정보를 저장하고 조회하는 역할을 담당한다.
 * <p>{@link BeanDefinition}은 Bean의 메타데이터(클래스, 스코프, 이름 등)를 담으며,
 * 이 인터페이스는 {@link BeanDefinition}을 등록·조회·검사하는 기본 기능을 제공한다.</p>
 */
public interface BeanDefinitionRegistry {

    /**
     * 주어진 이름으로 {@link BeanDefinition}을 등록한다.
     *
     * @param beanName        Bean의 고유 이름
     * @param beanDefinition  등록할 {@link BeanDefinition}
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    /**
     * 주어진 이름으로 {@link BeanDefinition}을 등록한다.
     *
     * @param beanDefinition  등록할 {@link BeanDefinition}
     */
    void registerBeanDefinition(BeanDefinition beanDefinition);

    /**
     * 지정된 이름의 {@link BeanDefinition}을 반환한다.
     *
     * @param beanName 조회할 Bean의 이름
     * @return 해당 {@link BeanDefinition}
     */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * 지정된 이름의 {@link BeanDefinition}을 반홚한다.
     *
     * @param type 조회할 Bean의 타입
     * @return 해당 {@link BeanDefinition}
     */
    BeanDefinition getBeanDefinition(Class<?> type);

    /**
     * 현재 등록된 모든 Bean 이름을 반환한다.
     *
     * @return Bean 이름 목록
     */
    List<String> getBeanDefinitionNames();


    /**
     * 등록된 {@link BeanDefinition}의 개수를 반환한다.
     *
     * @return 등록된 {@link BeanDefinition} 수
     */
    int getBeanDefinitionCount();

    /**
     * 지정된 이름의 BeanDefinition이 등록되어 있는지 확인한다.
     *
     * @param beanName 확인할 Bean 이름
     * @return 존재하면 true, 아니면 false
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * 등록된 모든 {@link BeanDefinition}을 반환한다.
     *
     * @return {@link BeanDefinition} 컬렉션
     */
    Collection<BeanDefinition> getBeanDefinitions();

    /**
     * 지정된 타입에 할당 가능한 {@link BeanDefinition}을 반환한다.<br>
     *
     * 등록된 모든 {@link BeanDefinition}을 대상으로
     * {@code type.isAssignableFrom(def.getResolvableType())} 조건을 기준으로
     * 타입 호환 여부를 검사한다.
     *
     * @param type 조회할 Bean 타입
     * @return 요청된 타입에 할당 가능한 {@link BeanDefinition}
     */
    BeanDefinition getBeanDefinitionByType(Class<?> type);

    /**
     * 지정한 타입에 할당 가능한 모든 {@link BeanDefinition}을 반환한다.
     * @param type 조회할 상위 타입 또는 인터페이스
     * @return 타입에 할당 가능한 모든 BeanDefinition 목록 (없을 경우 빈 리스트)
     */
    List<BeanDefinition> getBeanDefinitionsByType(Class<?> type);

    /**
     * 지정한 타입에 대해 단 하나의  {@link BeanDefinition}을 결정하여 반환한다.
     *
     * <p>
     * 내부적으로 {@link #getBeanDefinitionsByType(Class)}를 사용하여 후보를 수집한 뒤,
     * 다음 규칙에 따라 단일 Bean을 선택한다:
     * </p>
     *
     * <ol>
     *   <li>후보가 1개인 경우 → 해당 Bean 반환</li>
     *   <li>후보가 여러 개인 경우 → {@code @Primary}가 지정된 Bean이 1개면 선택</li>
     *   <li>{@code @Primary}가 없거나 2개 이상인 경우 → 예외 발생</li>
     * </ol>
     *
     * <p>
     * 이 메서드는 생성자 주입, 단일 타입 필드 주입,
     * {@code @Bean} 메서드 파라미터 주입 등
     * <b>단일 Bean이 필요한 모든 경우의 표준 진입점</b>이다.
     * </p>
     *
     * @param type 조회할 상위 타입 또는 인터페이스
     * @return 선택된 단일 BeanDefinition
     * @throws NoSuchBeanDefinitionException 후보가 존재하지 않는 경우
     * @throws IllegalStateException 단일 Bean을 결정할 수 없는 경우
     */
    BeanDefinition resolveSingleBeanByType(Class<?> type);
}
