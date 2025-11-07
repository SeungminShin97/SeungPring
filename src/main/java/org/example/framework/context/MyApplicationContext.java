package org.example.framework.context;

import org.example.framework.annotation.Scope;
import org.example.framework.core.BeanDefinitionRegistry;
import org.example.framework.core.BeanFactory;
import org.example.framework.core.ComponentScanner;
import org.example.framework.exception.ComponentScanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.util.*;

// TODO: eager init 기능 구현

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
    private final BeanFactory factory;
    private final ComponentScanner scanner;
    // scan 대상 경로들
    private final String[] basePackages;

    /**
     * 지정된 패키지를 스캔하여 컨텍스트를 초기화한다.
     * <p>생성자 실행 시 {@link #refresh()}가 호출되어 스캔 및 BeanDefinition 등록이 자동 수행된다.</p>
     *
     * @param basePackages 컴포넌트 스캔 대상 패키지 목록
     */
    public MyApplicationContext(String... basePackages) {
        this.registry = new MyBeanDefinitionRegistry();
        this.factory = new MyBeanFactory(new MyDependencyInjector(), registry);
        this.scanner = new MyComponentScanner();
        this.basePackages = basePackages;

        // 컨택스트 초기화
        refresh();
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
            // 클래스명에서 첫 글자 소문자로 변환하여 Bean 이름 생성
            String beanName = Introspector.decapitalize(clazz.getSimpleName());

            if(registry.containsBeanDefinition(beanName))
                continue;

            // 기본 스코프는 SINGLETON
            ScopeType scopeType = ScopeType.SINGLETON;

            // 클래스에 @Scope 어노테이션이 붙어 있으면 값 추출
            if (clazz.isAnnotationPresent(Scope.class))
                scopeType = clazz.getAnnotation(Scope.class).value();

            // Bean 메타정보(클래스, 이름, 스코프)를 담은 BeanDefinition 생성
            BeanDefinition beanDefinition = new BeanDefinition(clazz, beanName, scopeType);

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
     * <p>컴포넌트 스캔과 BeanDefinition 등록을 수행한다.
     * BeanFactory 초기화나 Bean 생성을 추가하려면 이 메서드를 확장하면 된다.</p>
     */
    @Override
    protected void refreshBeanFactory() {
        Set<Class<?>> components = doScan(this.scanner, this.basePackages);
        register(components);
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
        return factory.getBean(requiredType);
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
        return factory.getBean(beanName);
    }
}
