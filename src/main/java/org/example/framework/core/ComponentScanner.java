package org.example.framework.core;

import org.example.framework.context.BeanDefinition;

import java.util.Set;

/**
 * {@code ComponentScanner}는 지정된 패키지나 클래스 경로를 스캔하여
 * Bean으로 등록할 대상 클래스를 탐색하는 역할을 한다.
 * <p>주로 {@code @Component}, {@code @Service}, {@code @Repository} 등의
 * 어노테이션이 붙은 클래스를 검색해 {@link BeanDefinition}으로 등록하기 위한
 * 초기 단계에서 사용된다.</p>
 */
public interface ComponentScanner {

    /**
     * 구성 요소(컴포넌트) 클래스를 스캔하여 반환한다.
     *
     * @return 스캔된 클래스의 집합
     */
    Set<Class<?>> scan(String... basePackages);
}
