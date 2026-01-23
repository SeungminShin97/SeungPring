package org.example.framework.context;

import org.example.framework.core.ConfigurableApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * ApplicationContext의 공통 템플릿을 제공하는 추상 클래스.
 *
 * - 컨텍스트 식별 정보(ID, 이름, 시작 시각) 관리
 * - refresh / close 라이프사이클 템플릿 제공
 * - 실제 BeanFactory 초기화/종료 로직은 하위 클래스에 위임
 *
 * Spring의 AbstractApplicationContext 설계를 단순화한 구조이다.
 */
public abstract class AbstractApplicationContext implements ConfigurableApplicationContext {

    private static final Logger log = LoggerFactory.getLogger(AbstractApplicationContext.class);

    // 컨텍스트 고유 ID
    private final String id = UUID.randomUUID().toString();
    // 컨텍스트 시작 시각 (밀리초 단위)
    private long startupDate;
    // 컨텍스트 표시 이름
    private final String displayName = "MyApplicationContext@" + id;
    // 컨텍스트 활성 여부
    private boolean active = false;

    /**
     * 컨텍스트를 시작한다.
     *
     * LifeCycle 인터페이스 계약에 따라
     * 내부적으로 refresh()를 호출한다.
     */
    @Override
    public void start() throws Exception {
        refresh();
    }

    /**
     * 컨텍스트를 중지한다.
     *
     * LifeCycle 인터페이스 계약에 따라
     * 내부적으로 close()를 호출한다.
     */
    @Override
    public void stop() throws Exception {
        close();
    }

    /**
     * ApplicationContext 초기화 템플릿 메서드.
     *
     * 호출 순서:
     * 1. prepareRefresh      : 컨텍스트 상태 초기화
     * 2. refreshBeanFactory  : BeanDefinition 로딩
     * 3. finishRefresh       : 초기화 완료 후처리
     *
     * ※ BeanPostProcessor, singleton 초기화는
     * 하위 Context에서 확장하여 수행해야 한다.
     */
    @Override
    public final void refresh() {
        prepareRefresh();
        onRefresh();
        finishRefresh();
    }

    /**
     * 실제 초기화 로직을 수행하는 훅.
     *
     * - BeanDefinition 로딩
     * - BeanPostProcessor 등록
     * - singleton 초기화 등
     */
    protected abstract void onRefresh();

    /**
     * 컨텍스트 초기화 전 공통 준비 작업.
     *
     * - startupDate 설정
     * - active 플래그 활성화
     */
    protected void prepareRefresh() {
        this.startupDate = System.currentTimeMillis();
        active = true;
    }

    /**
     * BeanFactory 초기화 훅.
     *
     * 하위 클래스에서 컴포넌트 스캔 및
     * BeanDefinition 등록을 수행한다.
     */
    protected abstract void refreshBeanFactory();

    /**
     * 컨텍스트 초기화 완료 후 후처리 훅.
     *
     * 기본 구현은 로그 출력만 수행한다.
     */
    protected void finishRefresh() {
        log.info("{} refreshed.", displayName);
    }

    /** 컨텍스트 종료 템플릿
     * closeBeanFactory()를 구현해 종료 수행
     * */
    public void close() {
        if(!active) {
            log.warn("{} is already closed.", displayName);
            return;
        }
        active = false;
        closeBeanFactory();
        log.info("{} closed.", displayName);
    }

    /**
     * 실제 종료 로직을 수행하는 훅.
     *
     * - singleton destroy
     * - @PreDestroy / DisposableBean 처리
     */
    protected abstract void onClose();

    protected void closeBeanFactory() {
        // TODO: 필요시 구현
    }

    /** 애플리케이션 이름 반환 */
    @Override
    public String getApplicationName() {
        return "SeungPringApp";
    }

    /** 컨텍스트 표시 이름 반환 */
    @Override
    public String getDisplayName() {
        return displayName;
    }

    /** 컨텍스트 고유 ID 반환 */
    @Override
    public String getId() {
        return id;
    }

    /** 컨텍스트 시작 시각 반환 */
    @Override
    public long getStartupDate() {
        return startupDate;
    }
}
