package org.example.framework.context;

import org.example.framework.core.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


public abstract class AbstractApplicationContext implements ApplicationContext {

    private static final Logger log = LoggerFactory.getLogger(AbstractApplicationContext.class);

    // 컨텍스트 고유 ID
    private final String id = UUID.randomUUID().toString();
    // 컨텍스트 시작 시각 (밀리초 단위)
    private final long startupDate = System.currentTimeMillis();
    // 컨텍스트 표시 이름
    private final String displayName = "MyApplicationContext@" + id;
    // 컨텍스트 활성 여부
    private boolean active = false;

    /**
     * 컨텍스트 초기화 템플릿. <br>
     * refreshBeanFactory() 를 구현해 초기화 수행
     */
    public void refresh() {
        prepareRefresh();
        refreshBeanFactory();
        finishRefresh();
    }

    /** 컨텍스트 활성화 */
    protected void prepareRefresh() {
        active = true;
    }

    /** BeanFactory 초기화 */
    protected abstract void refreshBeanFactory();

    /** 초기화 완료 시 후처리 */
    protected void finishRefresh() {
        log.info(displayName + " refreshed.");
    }

    /** 컨텍스트 종료 템플릿
     * closeBeanFactory()를 구현해 종료 수행
     * */
    public void close() {
        if(!active) {
            log.warn(displayName + " is already closed.");
            return;
        }
        active = false;
        log.info(displayName + "closed.");
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
