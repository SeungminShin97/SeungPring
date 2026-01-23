package org.example.framework.core.lifecycle;

import org.example.framework.core.ApplicationContext;

/**
 * 컨테이너로부터 {@link ApplicationContext}를
 * 주입받기 위한 콜백 인터페이스입니다.
 *
 * <p>
 * 이 인터페이스를 구현한 Bean은
 * 생성 및 의존성 주입 완료 이후,
 * 컨테이너에 의해 {@link #setApplicationContext(ApplicationContext)}가 호출됩니다.
 * </p>
 *
 * <p>
 * 컨테이너 인프라 객체에 접근해야 하는 Bean에 한해
 * 제한적으로 사용되어야 합니다.
 * </p>
 */
public interface ApplicationContextAware {

    /**
     * 현재 실행 중인 {@link ApplicationContext}를 주입합니다.
     *
     * @param context 활성화된 ApplicationContext
     */
    void setApplicationContext(ApplicationContext context);
}
