package org.example.framework.context;

import org.example.framework.annotation.*;
import org.example.framework.context.beanDefinition.BeanDefinition;
import org.example.framework.context.beanDefinition.ClassBeanDefinition;
import org.example.framework.context.beanDefinition.ConfigurationBeanDefinition;
import org.example.framework.context.beanDefinition.MethodBeanDefinition;
import org.example.framework.context.capability.LazyProxyCapable;
import org.example.framework.context.processor.ApplicationContextAwareProcessor;
import org.example.framework.context.processor.InitializingBeanProcessor;
import org.example.framework.context.processor.PostConstructProcessor;
import org.example.framework.core.*;
import org.example.framework.core.lifecycle.SmartInitializingSingleton;
import org.example.framework.exception.ComponentScanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.example.framework.util.AnnotationUtils.hasAnnotation;


/**
 * <p>
 * 지정된 패키지를 스캔하여 {@code @Component}가 선언된 클래스를 탐색하고,
 * 각 클래스를 {@link BeanDefinition}으로 등록한 뒤 {@link MyBeanFactory}를 통해 관리한다.
 * </p>
 *
 * <p><b>주요 기능:</b></p>
 * <ul>
 *     <li>컴포넌트 스캔 및 BeanDefinition 등록</li>
 *     <li>BeanFactory 초기화 및 Bean 조회</li>
 *     <li>컨텍스트 메타데이터(id, 이름, 시작 시각) 관리</li>
 * </ul>
 *
 * <p>컨텍스트는 생성자 호출 시 즉시 초기화되며,
 * 등록된 Bean은 {@link #getBean(String)} 또는 {@link #getBean(Class)}를 통해 조회할 수 있다.</p>
 *
 * @see MyBeanFactory
 * @see MyComponentScanner
 * @see BeanDefinitionRegistry
 */
public class MyApplicationContext extends AbstractApplicationContext {

    private static final Logger log = LoggerFactory.getLogger(MyApplicationContext.class);

    private final BeanDefinitionRegistry registry;
    private final ConfigurableBeanFactory beanFactory;
    private final ListableBeanFactory listableBeanFactory;
    private final ComponentScanner scanner;
    // scan 대상 경로들
    private final String[] basePackages;

    /**
     * 지정된 패키지를 스캔하여 컨텍스트를 초기화한다.
     * <p>생성자 실행 시 {@link org.example.framework.core.ConfigurableApplicationContext#refresh()}가 호출되어 스캔 및 BeanDefinition 등록이 자동 수행된다.</p>
     *
     * @param basePackages 컴포넌트 스캔 대상 패키지 목록
     */
    public MyApplicationContext(String... basePackages) {
        this.registry = new MyBeanDefinitionRegistry();

        MyBeanFactory factory = new MyBeanFactory(new MyDependencyInjector(), registry);
        this.beanFactory = factory;
        this.listableBeanFactory = factory;
        this.scanner = new MyComponentScanner();
        this.basePackages = basePackages;
    }

    @Override
    protected void onRefresh() {
        // 1. 컴포넌트 스캔 + BeanDefinition 등록
        refreshBeanFactory();

        // 2. Bean 등록
        processConfigurationBeans();

        // 3. BeanPostProcessor 등록
        registerBeanPostProcessors();

        // 4. Lazy 제외 singleton Bean 생성
        preInstantiateSingletons();

        // 5. singleton 전체 초기화 완료 후 콜백
        invokeSmartInitializingSingletons();
    }

    /**
     * {@inheritDoc}
     * <p>컴포넌트 스캔과 BeanDefinition 등록을 수행한다.
     * BeanFactory 초기화나 Bean 생성을 추가하려면 이 메서드를 확장하면 된다.</p>
     */
    @Override
    protected void refreshBeanFactory() {
        Set<Class<?>> components = doScan(this.scanner, this.basePackages);
        register(components);
    }

    @Override
    protected void onClose() {
        beanFactory.destroySingletons();
    }

    /**
     * 기본 Scanner와 등록된 basePackages를 사용하여 컴포넌트를 스캔한다.
     */
    public void scan() {
        doScan(this.scanner, this.basePackages);
    }

    public void scan(String... basePackages) {
        doScan(this.scanner, basePackages);
    }

    public void scan(ComponentScanner scanner) {
        doScan(scanner, this.basePackages);
    }

    public void scan(ComponentScanner scanner, String... basePackages) {
        doScan(scanner, basePackages);
    }

    /**
     * 주어진 {@link ComponentScanner}로 지정된 패키지들을 스캔하여
     * {@code @Component} 클래스들을 탐색하고 BeanDefinition으로 등록할 준비를 한다.
     *
     * @param scanner 사용할 Scanner
     * @param basePackages 스캔 대상 경로 배열
     * @return 스캔된 컴포넌트 클래스 집합
     * @throws ComponentScanException 스캔 중 오류 발생 시
     */
    private Set<Class<?>> doScan(ComponentScanner scanner, String[] basePackages) {
        Objects.requireNonNull(basePackages, "Base packages cannot be null");

        // 지정된 패키지에서 @Component 클래스 스캔
        try{
            log.info("[Context] Scanning packages: {}", Arrays.toString(basePackages));
            Set<Class<?>> components = scanner.scan(basePackages);
            log.info("[Context] Registered {} beans", components.size());
            return components;
        } catch (ComponentScanException e) {
            log.error("[Context] Component scan failed", e);
            throw e;
        }
    }

    /**
     * 지정된 클래스들을 BeanDefinition으로 변환하여 등록한다.
     * <p>각 클래스는 단순명(decapitalized simple name)을 Bean 이름으로 사용하며,
     * {@code @Scope} 어노테이션이 지정되지 않은 경우 기본 스코프는 SINGLETON이다.</p>
     *
     * @param components 등록할 컴포넌트 클래스 집합
     */
    public void register(Set<Class<?>> components) {
        for (Class<?> clazz : components) {

            // Bean 생성 제외 규칙
            if(clazz.isAnnotation() || clazz.isInterface() || clazz.isEnum() || Modifier.isAbstract(clazz.getModifiers()))
                continue;

            // 클래스명에서 첫 글자 소문자로 변환하여 Bean 이름 생성
            String beanName = Introspector.decapitalize(clazz.getSimpleName());

            if(registry.containsBeanDefinition(beanName))
                continue;

            // 기본 스코프는 SINGLETON
            ScopeType scopeType = ScopeType.SINGLETON;

            // 클래스에 @Scope 어노테이션이 붙어 있으면 값 추출
            if (hasAnnotation(clazz, Scope.class)) {
                Scope scope = clazz.getAnnotation(Scope.class);
                scopeType = scope.value();
            }

            // @Lazy 어노테이션 검사
            boolean isLazyInit = hasAnnotation(clazz, Lazy.class);

            // Bean 메타정보(클래스, 이름, 스코프)를 담은 BeanDefinition 생성
            BeanDefinition beanDefinition;
            if(hasAnnotation(clazz, Configuration.class))
                beanDefinition = new ConfigurationBeanDefinition(clazz, beanName, scopeType, isLazyInit);
            else
                beanDefinition = new ClassBeanDefinition(clazz, beanName, scopeType, isLazyInit);

            // @LazyProxy 검사
            if(clazz.isAnnotationPresent(LazyProxy.class) && beanDefinition instanceof LazyProxyCapable capable)
                capable.setLazyProxy();

            // BeanDefinitionRegistry에 등록
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    /**
     * 지정된 클래스 가변인자를 BeanDefinition으로 등록한다.
     *
     * @param components 등록할 컴포넌트 클래스들
     */
    public void register(Class<?>... components) {
        register(new HashSet<>(List.of(components)));
    }

    /**
     * {@inheritDoc}
     * <p>Bean 이름 대신 타입으로 Bean을 조회한다.</p>
     *
     * @param requiredType 조회할 Bean 타입
     * @return 타입에 맞는 Bean 인스턴스
     */
    @Override
    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

    /**
     * {@inheritDoc}
     * <p>Bean 이름으로 Bean을 조회한다.</p>
     *
     * @param beanName 조회할 Bean 이름
     * @return 해당 이름의 Bean 인스턴스
     */
    @Override
    public Object getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }

    @Override
    public boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    /**
     * 해당 이름의 bean 인스턴스가 생성됐는지 확인한다.
     * @param beanName 조회할 bean 이름
     * @return 존재하면 true, 아니면 false
     */
    @Override
    public boolean containsSingleton(String beanName) {
        return beanFactory.containsSingleton(beanName);
    }

    @Override
    public String[] getAliases(String name) {
        return beanFactory.getAliases(name);
    }

    @Override
    public Class<?> getType(String name) {
        return beanFactory.getType(name);
    }

    @Override
    public boolean isPrototype(String name) {
        return beanFactory.isPrototype(name);
    }

    @Override
    public boolean isSingleton(String name) {
        return beanFactory.isSingleton(name);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) {
        return beanFactory.isTypeMatch(name, typeToMatch);
    }

    @Override
    public <T> List<T> getBeansOfType(Class<T> type) {
        return listableBeanFactory.getBeansOfType(type);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        Map<String, Object> result = new HashMap<>();

        for(String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);

            Class<?> beanClass = beanDefinition.getResolvableType();
            if(hasAnnotation(beanClass, annotationType))
                result.put(beanName, getBean(beanName));
        }

        return result;
    }

    /**
     * {@code @Configuration} 클래스로부터 {@code @Bean} 메서드들을 탐색하여
     * {@link MethodBeanDefinition}으로 변환 후 BeanDefinitionRegistry에 등록한다.
     *
     * <p>
     * 이 메서드는 ConfigurationBeanDefinition 자체를 순회 대상으로 하며,
     * registry에 이미 등록된 빈 이름과 충돌하는 @Bean 메서드는 무시한다.
     * </p>
     *
     * <p>
     * 주의:
     * registry 순회 중 신규 BeanDefinition 등록이 발생하므로,
     * ConcurrentModificationException 방지를 위해
     * ConfigurationBeanDefinition 목록을 스냅샷으로 분리한 후 처리한다.
     * </p>
     */
    private void processConfigurationBeans() {
        // ConfigurationBeanDefinition 스냅샷
        // 순회 중 registry 변경으로 인한 ConcurrentModificationException 방지
        List<ConfigurationBeanDefinition> configs =
                registry.getBeanDefinitions().stream()
                        .filter(def -> def instanceof ConfigurationBeanDefinition)
                        .map(def -> (ConfigurationBeanDefinition) def)
                        .toList();

        for(ConfigurationBeanDefinition configDef : configs) {
            Class<?> clazz = configDef.getResolvableType();

            for(Method method : clazz.getDeclaredMethods()) {
                if(!method.isAnnotationPresent(Bean.class))
                    continue;

                Bean bean = method.getAnnotation(Bean.class);

                String beanName = bean.name().isBlank() ? method.getName() : bean.name();

                if(registry.containsBeanDefinition(beanName))
                    continue;

                MethodBeanDefinition methodDef =
                        new MethodBeanDefinition(beanName, bean.scope(), bean.lazy(), method, configDef.getBeanName());

                registry.registerBeanDefinition(beanName, methodDef);
            }
        }
    }

    /**
     * Eager Init <br>
     *
     * Lazy, LazyProxy 어노테이션이 붙지 않은 Singleton Scope bean 을 로드하는 메서드
     */
    private void preInstantiateSingletons() {
        for(BeanDefinition definition : registry.getBeanDefinitions()) {
            String beanName = definition.getBeanName();
            boolean lazyProxy = definition instanceof LazyProxyCapable c && c.isLazyProxy();
            if(definition.isSingleton() && !definition.isLazyInit() && !lazyProxy)
                getBean(beanName);
        }
    }

    /**
     * Bean 생성 과정에 적용될 BeanPostProcessor들을 등록한다.
     *
     * - 등록 순서 = 실행 순서
     * - singleton 생성 이전에 반드시 호출되어야 한다
     */
    private void registerBeanPostProcessors() {
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
        beanFactory.addBeanPostProcessor(new PostConstructProcessor());
        beanFactory.addBeanPostProcessor(new InitializingBeanProcessor());
    }

    /**
     * 모든 singleton Bean 생성이 완료된 이후 호출되는 콜백.
     *
     * - SmartInitializingSingleton 구현 Bean에 대해
     *   afterSingletonsInstantiated()를 실행한다
     */
    private void invokeSmartInitializingSingletons() {
        for(String beanName : registry.getBeanDefinitionNames()) {
            Object bean = getBean(beanName);
            if(bean instanceof SmartInitializingSingleton smart)
                smart.afterSingletonsInstantiated();
        }
    }
}
