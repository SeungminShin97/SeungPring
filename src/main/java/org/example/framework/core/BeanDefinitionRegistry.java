package org.example.framework.core;

import org.example.framework.context.BeanDefinition;

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
     * 지정된 이름의 {@link BeanDefinition}을 반환한다.
     *
     * @param beanName 조회할 Bean의 이름
     * @return 해당 {@link BeanDefinition}
     */
    BeanDefinition getBeanDefinition(String beanName);

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
}
