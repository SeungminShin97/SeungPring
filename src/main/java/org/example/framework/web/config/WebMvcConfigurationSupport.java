package org.example.framework.web.config;

import org.example.framework.annotation.Component;
import org.example.framework.core.ApplicationContext;
import org.example.framework.core.lifecycle.ApplicationContextAware;
import org.example.framework.web.interceptor.InterceptorRegistry;

import java.util.List;

/**
 * 웹 MVC 설정을 초기화하고 적용하는 내부 지원 클래스이다.
 *
 * <p>
 * 이 클래스는 {@link WebMvcConfigurer} 구현체들을 수집하여
 * 애플리케이션에 적용할 웹 관련 설정을 구성한다.
 * </p>
 *
 * <p>
 * 애플리케이션 컨텍스트 초기화 과정에서 한 번 실행되며,
 * 이후 요청 처리 과정에는 관여하지 않는다.
 * </p>
 */
@Component
public class WebMvcConfigurationSupport implements ApplicationContextAware {

    private ApplicationContext context;

    /**
     * 등록된 인터셉터들을 보관하는 레지스트리.
     *
     * <p>
     * 초기화 이후에는 읽기 전용으로 사용되며,
     * 요청 처리 과정에서 참조된다.
     * </p>
     */
    private final InterceptorRegistry interceptorRegistry = new InterceptorRegistry();

    /**
     * 애플리케이션 컨텍스트를 주입받고,
     * 웹 관련 설정 초기화를 수행한다.
     *
     * <p>
     * 이 메서드가 호출되는 시점에는
     * 모든 singleton Bean 생성이 완료된 상태를 전제로 한다.
     * </p>
     *
     * @param context 애플리케이션 컨텍스트
     */
    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
        initInterceptors();
    }

    /**
     * {@link WebMvcConfigurer} 구현체들을 조회하여
     * 인터셉터 등록 작업을 수행한다.
     */
    private void initInterceptors() {
        List<WebMvcConfigurer> configurers = context.getBeansOfType(WebMvcConfigurer.class);

        for(WebMvcConfigurer configurer : configurers)
            configurer.addInterceptors(interceptorRegistry);
    }

    /**
     * 초기화가 완료된 {@link InterceptorRegistry}를 반환한다.
     *
     * @return 인터셉터 레지스트리
     */
    public InterceptorRegistry getInterceptorRegistry() {
        return interceptorRegistry;
    }
}
