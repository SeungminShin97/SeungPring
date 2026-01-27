package org.example.framework.context.beanDefinition;

import org.example.framework.context.ScopeType;
import org.example.framework.context.capability.OrderCapable;

/**
 * 컨테이너에 등록되는 빈 메타데이터의 공통 추상 정의
 *
 * <p>
 * 빈의 생성 방식과 무관하게 공통으로 필요한 정보
 * (이름, 스코프, lazy 여부, 타입 정보)를 캡슐화한다.
 * </p>
 */
public abstract class BeanDefinition implements OrderCapable {
    private final ScopeType scope;
    private final String beanName;
    private final boolean lazyInit;
    private boolean isPrimary;
    private int order = Integer.MAX_VALUE;

    /**
     * BeanDefinition 기본 생성자
     *
     * @param beanName  빈 이름
     * @param scopeType 빈 스코프
     * @param lazyInit  lazy 초기화 여부
     */
    protected BeanDefinition(String beanName, ScopeType scopeType, boolean lazyInit) {
        this.beanName = beanName;
        this.scope = scopeType;
        this.lazyInit = lazyInit;
    }

    public ScopeType getScope() {
        return scope;
    }

    public String getBeanName() { return beanName; }

    public boolean isSingleton() {
        return scope.equals(ScopeType.SINGLETON);
    }

    public boolean isPrototype() {
        return scope.equals(ScopeType.PROTOTYPE);
    }

    public boolean isLazyInit() { return lazyInit; }

    /**
     * BeanDefinition의 구체 타입을 반환한다.
     */
    public abstract BeanDefinitionType getType();

    /**
     * 의존성 해석 및 타입 매칭에 사용될 대표 타입을 반환한다.
     */

    public abstract Class<?> getResolvableType();

    public boolean isPrimary() { return isPrimary; }

    public void setPrimary() {
        this.isPrimary = true;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }
}
