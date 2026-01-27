package org.example.framework.context;

import org.example.framework.context.beanDefinition.BeanDefinition;
import org.example.framework.core.BeanDefinitionRegistry;
import org.example.framework.exception.bean.NoSuchBeanDefinitionException;

import java.util.*;

/**
 * {@link BeanDefinitionRegistry}의 기본 구현체.
 * <p>내부적으로 {@link HashMap}을 사용해 Bean 이름과 {@link BeanDefinition}을 관리한다.</p>
 */
public class MyBeanDefinitionRegistry implements BeanDefinitionRegistry {

    private final Map<String, BeanDefinition> beanDefinitions;

    MyBeanDefinitionRegistry() {
        beanDefinitions = new HashMap<>();
    }


    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        Objects.requireNonNull(beanDefinition, "BeanDefinition is null");
        Objects.requireNonNull(beanName, "BeanName is null");

        if(containsBeanDefinition(beanName)) return;

        beanDefinitions.put(beanName, beanDefinition);
    }

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        registerBeanDefinition(beanDefinition.getBeanName(), beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        Objects.requireNonNull(beanName, "BeanName is null");
        BeanDefinition beanDefinition = beanDefinitions.get(beanName);
        if (beanDefinition == null)
            throw new NoSuchBeanDefinitionException(beanName);
        return beanDefinition;
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> type) {
        Objects.requireNonNull(type, "Type is null");

        BeanDefinition found = null;

        for(BeanDefinition beanDefinition : beanDefinitions.values()) {
            if(beanDefinition.getResolvableType() == type) {
                // 2개 이상의 후보 검색 시 예외처리
                if(found != null)
                    throw new IllegalStateException("Multiple beans found for type " + type.getName());
                found = beanDefinition;
            }
        }
        if(found == null)
            throw new NoSuchBeanDefinitionException(type.getName());
        return found;
    }

    @Deprecated
    @Override
    public BeanDefinition getBeanDefinitionByType(Class<?> type) {
        List<BeanDefinition> candidates = beanDefinitions.values().stream()
                .filter(def -> type.isAssignableFrom(def.getResolvableType()))
                .toList();

        if(candidates.isEmpty())
            throw new NoSuchBeanDefinitionException(type.getSimpleName());

        if(candidates.size() > 1)
            throw new IllegalStateException("Multiple beans found for type: " + type.getName() + " -> " +
                    candidates.stream().map(BeanDefinition::getBeanName).toList());

        return candidates.getFirst();
    }

    @Override
    public List<BeanDefinition> getBeanDefinitionsByType(Class<?> type) {
        return getBeanDefinitions().stream()
                .filter(def -> type.isAssignableFrom(def.getResolvableType()))
                .toList();
    }

    @Override
    public BeanDefinition resolveSingleBeanByType(Class<?> type) {
        List<BeanDefinition> candidates = getBeanDefinitionsByType(type);

        if(candidates.isEmpty())
            throw new NoSuchBeanDefinitionException(type.getSimpleName());

        if(candidates.size() == 1)
            return candidates.getFirst();

        List<BeanDefinition> primaries = candidates.stream().filter(BeanDefinition::isPrimary).toList();

        if(primaries.size() == 1)
            return primaries.getFirst();

        if(primaries.size() > 1)
            throw new IllegalStateException("Multiple @Primary beans found for type: " + type);

        candidates.sort(Comparator.comparingInt(BeanDefinition::getOrder));

        BeanDefinition first = candidates.get(0);
        BeanDefinition second = candidates.get(1);

        if (first.getOrder() == second.getOrder())
            throw new IllegalStateException("Multiple beans with same order for type: " + type);

        return first;
    }

    @Override
    public List<String> getBeanDefinitionNames() {
        return new ArrayList<>(beanDefinitions.keySet());
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanDefinitions.size();
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitions.containsKey(beanName);
    }

    @Override
    public Collection<BeanDefinition> getBeanDefinitions() {
        return beanDefinitions.values();
    }
}
