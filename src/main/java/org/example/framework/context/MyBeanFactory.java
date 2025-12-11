package org.example.framework.context;

import org.example.framework.annotation.Autowired;
import org.example.framework.core.BeanDefinitionRegistry;
import org.example.framework.core.BeanFactory;
import org.example.framework.core.DependencyInjector;
import org.example.framework.exception.bean.BeanCreationException;
import org.example.framework.exception.bean.NoSuchBeanDefinitionException;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBeanFactory implements BeanFactory {

    private final DependencyInjector injector;
    private final BeanDefinitionRegistry registry;

    /** Cache of singleton objects: bean name --> bean instance */
    private final Map<String, Object> singletonObjects = new HashMap<>();

    public MyBeanFactory(DependencyInjector injector, BeanDefinitionRegistry registry) {
        this.injector = injector;
        this.registry = registry;
    }

    /**
     * Bean이 존재하는지 확인<br>
     * {@link MyBeanFactory}의 {@code singletonObjects}와 {@link MyBeanDefinitionRegistry}의 {@code beanDefinitions}를 확인한다.
     * @param name 확인할 Bean 이름
     * @return {@code boolean}
     */
    @Override
    public boolean containsBean(String name) {
        return containsSingleton(name) || registry.containsBeanDefinition(name);
    }

    /**
     * {@code singletonObjects} 안에 해당 Bean이 존재하는지 검사
     */
    @Override
    public boolean containsSingleton(String name) {
        return singletonObjects.containsKey(name);
    }


    @Override
    public String[] getAliases(String name) {
        //TODO: 추후 alias 개발하면 구현 예정
        return new String[0];
    }

    /**
     * 주어진 타입과 일치하거나 호환되는 Bean을 반환한다.
     * <p>싱글톤 캐시에서 먼저 탐색하고, 없을 경우 등록된 BeanDefinition 중
     * 지정된 타입과 호환되는 Bean을 찾아 생성한다.</p>
     *
     * @param requiredType 찾을 Bean의 타입
     * @param <T> 반환 타입
     * @return 해당 타입의 Bean 인스턴스
     * @throws NoSuchBeanDefinitionException 일치하는 Bean 정의가 존재하지 않는 경우
     */
    @Override
    public <T> T getBean(Class<T> requiredType) {
        // 1. 싱글톤 캐시 검색
        for(Object obj : singletonObjects.values()) {
            if(requiredType.isInstance(obj))
                return requiredType.cast(obj);
        }

        // 2. BeanDefinition 검색 및 생성
        for (BeanDefinition def : registry.getBeanDefinitions()) {
            if (requiredType.isAssignableFrom(def.getBeanClass())) {
                Object instance = getBean(def.getBeanName());
                return requiredType.cast(instance);
            }
        }

        throw new NoSuchBeanDefinitionException(requiredType.getSimpleName());
    }

    /**
     * 지정된 이름의 Bean을 반환한다.
     * <p>싱글톤 캐시에 존재하면 재사용하고, 없으면 새로 생성하여 필요 시 캐시에 등록한다.</p>
     *
     * @param beanName 조회할 Bean의 이름
     * @return Bean 인스턴스
     * @throws NoSuchBeanDefinitionException 정의되지 않은 Bean 이름인 경우
     */
    @Override
    public Object getBean(String beanName) {
        if(containsSingleton(beanName))
            return singletonObjects.get(beanName);

        // 등록된 Bean 이 없으면 생성
        BeanDefinition beanDefinition = getBeanDefinitionOrThrow(beanName);
        Object obj = createBean(beanDefinition);

        if(beanDefinition.isSingleton())
            singletonObjects.put(beanName, obj);

        return obj;
    }

    /**
     * 지정된 Bean 이름에 해당하는 타입을 반환한다.<br>
     * 이미 생성된 Bean 인스턴스 또는 {@link BeanDefinition} 메타정보를 기반으로
     * Bean의 타입을 판별한다.
     * @param name 조회할 Bean 이름
     * @return Bean의 타입 (Class)
     * @throws NoSuchBeanDefinitionException 지정된 이름의 {@link BeanDefinition}이 존재하지 않을 때
     */
    @Override
    public Class<?> getType(String name) {
        if(containsSingleton(name))
            return singletonObjects.get(name).getClass();

        return getBeanDefinitionOrThrow(name).getBeanClass();
    }

    /**
     * 지정된 Bean의 {@link ScopeType}이 {@code prototype}인지 확인한다.<br>
     * {@link BeanDefinition}의 정보를 조회한다.
     * @param name 조회할 Bean 이름
     * @return {@code prototype} 여부
     * @throws NoSuchBeanDefinitionException 지정된 이름의 {@link BeanDefinition}이 존재하지 않을 때
     */
    @Override
    public boolean isPrototype(String name) {
        return getBeanDefinitionOrThrow(name).isPrototype();
    }

    /**
     * 지정된 Bean의 {@link ScopeType}이 {@code singleton}인지 확인한다.<br>
     * {@link BeanDefinition}의 정보를 조회한다.
     * @param name 조회할 Bean 이름
     * @return {@code singleton} 여부
     * @throws NoSuchBeanDefinitionException 지정된 이름의 {@link BeanDefinition}이 존재하지 않을 때
     */
    @Override
    public boolean isSingleton(String name) {
        return getBeanDefinitionOrThrow(name).isSingleton();
    }

    /**
     * 지정된 이름의 Bean이 주어진 타입과 호환되는지 확인한다.
     *
     * <p>등록된 {@link BeanDefinition}의 타입이 typeToMatch와
     * 동일하거나 하위 타입(또는 구현 클래스)일 경우 true를 반환한다.</p>
     *
     * @param name        검사할 Bean의 이름
     * @param typeToMatch 비교할 대상 타입
     * @return 타입이 일치하거나 호환되면 true, 그렇지 않으면 false
     * @throws NoSuchBeanDefinitionException Bean 정의가 존재하지 않을 경우
     */
    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) {
        BeanDefinition beanDefinition = getBeanDefinitionOrThrow(name);
        return typeToMatch.isAssignableFrom(beanDefinition.getBeanClass());
    }

    /**
     * 지정된 Bean의 존재 여부를 검사한다.<br>
     * {@link BeanDefinitionRegistry}의 {@code beanDefinitions}를 조회한다.
     * @param name 조회할 Bean의 이름
     * @throws NoSuchBeanDefinitionException 지정된 이름의 {@link BeanDefinition}이 존재하지 않을 때
     */
    private BeanDefinition getBeanDefinitionOrThrow(String name) {
        if(!registry.containsBeanDefinition(name))
            throw new NoSuchBeanDefinitionException(name);
        return registry.getBeanDefinition(name);
    }

    /**
     * 주어진 {@link BeanDefinition} 정보를 기반으로 새로운 Bean 인스턴스를 생성한다.
     *
     * <p>기본 생성자를 통해 객체를 생성하며, 이후 {@code injector}를 이용해
     * 필요한 의존성을 주입한다. 생성 과정에서 발생한 예외는
     * {@link BeanCreationException}으로 감싸서 던진다.</p>
     *
     * @param def Bean 정의 정보
     * @return 생성된 Bean 인스턴스
     * @throws BeanCreationException Bean 생성 중 예외가 발생한 경우
     */
    private Object createBean(BeanDefinition def) {
        try {
            Class<?> clazz = def.getBeanClass();

            Constructor<?> constructor = resolveConstructor(clazz);
            Object[] args = resolveConstructorArgs(constructor);

            constructor.setAccessible(true);
            Object instance = constructor.newInstance(args);

            // 의존성 주입
            injector.inject(instance, this);
            return instance;
        } catch (Exception e) {
            throw new BeanCreationException(def.getBeanName(), e);
        }
    }

    /**
     * Bean 생성 시 사용할 적절한 생성자를 선택한다.
     *
     * <p>생성자 선택 규칙은 Spring Framework의 의존성 주입 방식과 유사하게 동작한다.</p>
     *
     * <ul>
     *     <li>@Autowired가 지정된 생성자가 하나인 경우 해당 생성자를 사용한다.</li>
     *     <li>@Autowired가 여러 개 존재하면 명확한 생성자를 결정할 수 없으므로 예외를 발생시킨다.</li>
     *     <li>@Autowired 생성자가 없고, 공개(public) 생성자가 하나뿐이라면 해당 생성자를 사용한다.</li>
     *     <li>여러 생성자가 존재하지만 기본 생성자가 있는 경우 기본 생성자를 사용한다.</li>
     *     <li>위 조건을 만족하지 않으면 적절한 생성자를 찾을 수 없으므로 예외를 발생시킨다.</li>
     * </ul>
     *
     * @param clazz Bean을 생성할 대상 클래스
     * @return 선택된 {@link Constructor} 객체
     * @throws IllegalStateException 적절한 생성자를 결정할 수 없거나,
     *                               기본 생성자가 존재하지 않는 경우
     */
    private Constructor<?> resolveConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();

        // Autowired 생성자 추출
        List<Constructor<?>> autowiredConstructor =
                Arrays.stream(constructors)
                        .filter(t -> t.isAnnotationPresent(Autowired.class))
                        .toList();

        // Autowired 생성자 2개 이상일 경우 예외 발생
        if(autowiredConstructor.size() > 1)
            throw new IllegalStateException("Multiple @Autowired constructors not supported: " + clazz.getName());

        // Autowired 생성자가 1개 일 경우
        if(autowiredConstructor.size() == 1)
            return autowiredConstructor.getFirst();

        // Autowired 생성자가 없고 일반 생성자만 하나 있을 경우
        if(constructors.length == 1)
            return constructors[0];

        try {
            return clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("No @Autowired constructor and no default constructor found: " + clazz.getName());
        }
    }

    /**
     * 선택된 생성자의 파라미터 타입을 기반으로 필요한 의존성을 조회하여
     * 생성자 호출에 사용할 인자 배열을 생성한다.
     *
     * <p>각 파라미터 타입에 대해 {@link BeanFactory#getBean(Class)}를 호출하여
     * 해당 타입의 Bean 인스턴스를 가져오며, 생성자 기반 의존성 주입 시
     * 사용되는 인자 목록을 완성한다.</p>

     *
     * @param constructor 생성자 주입에 사용할 {@link Constructor} 객체
     * @return 생성자 호출 시 전달할 인자 배열
     */

    private Object[] resolveConstructorArgs(Constructor<?> constructor) {
        // 파리미터 타입 기반 bean 추출
        Class<?>[] paramType = constructor.getParameterTypes();
        Object[] args = new Object[paramType.length];

        for(int i = 0; i < paramType.length; i++)
            args[i] = getBean(paramType[i]);

        return args;
    }
}
