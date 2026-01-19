package org.example.framework.context;

import org.example.framework.annotation.Autowired;
import org.example.framework.core.BeanDefinitionRegistry;
import org.example.framework.core.BeanFactory;
import org.example.framework.core.DependencyInjector;
import org.example.framework.core.ListableBeanFactory;
import org.example.framework.exception.bean.BeanCreationException;
import org.example.framework.exception.bean.CircularDependencyException;
import org.example.framework.exception.bean.NoSuchBeanDefinitionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.framework.util.AnnotationUtils.hasAnnotation;

public class MyBeanFactory implements BeanFactory, ListableBeanFactory {

    private final DependencyInjector injector;
    private final BeanDefinitionRegistry registry;

    /**
     * Cache of singleton objects: bean name --> bean instance
     */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    private final Set<String> inCreation = ConcurrentHashMap.newKeySet();

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
     * 지정된 타입에 할당 가능한 Bean 인스턴스를 반환한다.
     *
     * <p>
     * 등록된 {@link BeanDefinition} 중 요청된 타입과 호환되는 Bean을 탐색하여
     * 후보를 결정한 뒤, 단 하나의 후보만 존재할 경우 해당 Bean을 반환한다.
     * </p>
     *
     * <p>
     * 타입에 할당 가능한 Bean이 존재하지 않거나,
     * 둘 이상의 후보가 발견될 경우
     * 적절한 Bean을 결정할 수 없으므로 예외를 발생시킨다.
     * </p>
     *
     * @param requiredType 조회할 Bean의 타입
     * @param <T> 반환 타입
     * @return 요청된 타입에 해당하는 Bean 인스턴스
     * @throws NoSuchBeanDefinitionException
     *         해당 타입에 할당 가능한 Bean 정의가 존재하지 않는 경우
     * @throws IllegalStateException
     *         동일 타입의 Bean 후보가 둘 이상 존재하여 모호한 경우
     */
    @Override
    public <T> T getBean(Class<T> requiredType) {
        List<BeanDefinition> candidates = new ArrayList<>();

        // 후보 수집
        for(BeanDefinition def : registry.getBeanDefinitions()) {
            if(requiredType.isAssignableFrom(def.getBeanClass()))
                candidates.add(def);
        }

        // 후보가 없을 경우
        if(candidates.isEmpty())
            throw new NoSuchBeanDefinitionException(requiredType.getSimpleName());

        // 후보 1개
        if(candidates.size() == 1) {
            Object bean = getBean(candidates.getFirst().getBeanName());
            return requiredType.cast(bean);
        }

        // 후보 2개 이상
        throw new IllegalStateException(
                "Multiple beans found for type" + requiredType.getName() + ": " +
                        candidates.stream().map(BeanDefinition::getBeanName).toList()
        );
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
        BeanDefinition beanDefinition = getBeanDefinitionOrThrow(beanName);

        if (!beanDefinition.isSingleton())
            return createBean(beanDefinition);

        return singletonObjects.computeIfAbsent(beanName, name -> {
            if (inCreation.contains(name))
                throw new CircularDependencyException(name);

            try {
                inCreation.add(name);
                return createBean(beanDefinition);
            } finally {
                inCreation.remove(name);
            }
        });
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
     * 지정된 타입에 할당 가능한 모든 Bean 인스턴스를 조회합니다.<br>
     * 내부적으로 모든 {@code BeanDefinition}을 순회하며, 요청된 타입에 {@code isAssignableFrom} 관계가 성립하는 Bean 만을 수집합니다.
     *
     * @param type 조회 대상 Bean 타입
     * @return 해당 타입에 할당 가능한 모든 Bean 리스트
     */
    @Override
    public <T> List<T> getBeansOfType(Class<T> type) {
        List<BeanDefinition> candidates = new ArrayList<>();

        // 후보 BeanDefinition 수집
        for(BeanDefinition def : registry.getBeanDefinitions()) {
            if(type.isAssignableFrom(def.getBeanClass()))
                candidates.add(def);
        }

        // 인스턴스 생성
        List<T> result = new ArrayList<>(candidates.size());
        for(BeanDefinition def : candidates) {
            Object bean = getBean(def.getBeanName());
            result.add(type.cast(bean));
        }

        return result;
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
     * Bean 생성 시 사용할 생성자를 결정한다.
     *
     * <p>
     * 생성자 선택은 명시성 우선 원칙을 따르며,
     * 컨테이너가 임의로 추측하여 생성자를 선택하지 않도록 설계되어 있다.
     * </p>
     *
     * <p>선택 규칙은 다음 순서로 적용된다.</p>
     * <ul>
     *     <li>
     *         {@link Autowired}가 지정된 생성자가 정확히 하나 존재하면
     *         해당 생성자를 사용한다.
     *     </li>
     *     <li>
     *         {@link Autowired}가 둘 이상 존재하는 경우,
     *         생성자를 하나로 결정할 수 없으므로 예외를 발생시킨다.
     *     </li>
     *     <li>
     *         {@link Autowired} 생성자가 없는 경우,
     *         파라미터가 없는(public no-arg) 생성자가 존재하면 이를 사용한다.
     *     </li>
     *     <li>
     *         위 조건을 만족하지 않고 public 생성자가 하나뿐인 경우,
     *         해당 생성자를 사용한다.
     *     </li>
     *     <li>
     *         위 모든 조건을 만족하지 못하면
     *         적절한 생성자를 결정할 수 없으므로 예외를 발생시킨다.
     *     </li>
     * </ul>
     *
     * <p>
     * 이 구현은 Spring Framework의 생성자 주입 방식에서
     * <em>모호한 경우 즉시 실패(fail-fast)</em>하는 정책을 단순화하여 반영한다.
     * </p>
     *
     * @param clazz Bean을 생성할 대상 클래스
     * @return 선택된 {@link Constructor} 객체
     * @throws IllegalStateException 생성자를 하나로 결정할 수 없는 경우
     */

    private Constructor<?> resolveConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();

        if (constructors.length == 0) {
            throw new IllegalStateException("No public constructors found for " + clazz.getName());
        }

        // Autowired 생성자 추출
        List<Constructor<?>> autowiredConstructor =
                Arrays.stream(constructors)
                        .filter(c -> hasAnnotation(c, Autowired.class))
                        .toList();

        // Autowired 생성자 2개 이상일 경우 예외 발생
        if(autowiredConstructor.size() > 1)
            throw new IllegalStateException("Multiple @Autowired constructors not supported: " + clazz.getName());

        // Autowired 생성자가 1개 일 경우
        if(autowiredConstructor.size() == 1)
            return autowiredConstructor.getFirst();

        // 2. no-arg 생성자 우선
        List<Constructor<?>> noArgConstructors =
                Arrays.stream(constructors)
                        .filter(c -> c.getParameterCount() == 0)
                        .toList();

        if (noArgConstructors.size() == 1) {
            return noArgConstructors.getFirst();
        }

        // Autowired 생성자가 없고 일반 생성자만 하나 있을 경우
        if(constructors.length == 1)
            return constructors[0];

        throw new IllegalStateException("No @Autowired constructor, no single public constructor, and no public constructors found for " + clazz.getName());
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
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Type[] genericTypes = constructor.getGenericParameterTypes();

        Object[] args = new Object[paramTypes.length];

        for(int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];

            if(List.class.isAssignableFrom(paramType)) {
                Class<?> genericType = resolveGenericType(genericTypes[i]);
                args[i] = getBeansOfType(genericType);
                continue;
            }
            args[i] = getBean(paramType);
        }

        return args;
    }

    private Class<?> resolveGenericType(Type type) {
        if(!(type instanceof ParameterizedType parameterizedType))
            throw new IllegalStateException("List injection requires generic type information");

        Type actuallyType = parameterizedType.getActualTypeArguments()[0];

        if(!(actuallyType instanceof Class<?> clazz))
            throw new IllegalStateException("Unsupported generic type: " + actuallyType);

        return clazz;
    }
}
