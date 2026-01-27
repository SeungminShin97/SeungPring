package org.example.framework.web.mapping;

import org.example.framework.annotation.Component;
import org.example.framework.annotation.Controller;
import org.example.framework.annotation.RequestMapping;
import org.example.framework.core.ApplicationContext;
import org.example.framework.core.lifecycle.ApplicationContextAware;
import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.web.HandlerMethod;
import org.example.framework.web.RequestMappingInfo;
import org.example.framework.web.config.WebMvcConfigurationSupport;
import org.example.framework.web.interceptor.HandlerExecutionChain;
import org.example.framework.web.interceptor.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code RequestMappingHandlerMapping}은 {@link Controller}로 등록된 Bean을 대상으로
 * {@link RequestMapping} 어노테이션이 선언된 메서드를 분석하여
 * 요청 조건({@link RequestMappingInfo})과 실행 대상({@link HandlerMethod}) 간의
 * 매핑 정보를 생성하고 보관한다.
 *
 * <p>매핑 정보는 컨텍스트 초기화 시점에 한 번 수집되며,
 * 요청 처리 시에는 캐시된 정보를 기반으로 빠르게 Handler를 조회한다.</p>
 */
@Component
public class RequestMappingHandlerMapping implements HandlerMapping{

    /**
     * 요청 조건({@link RequestMappingInfo})과 실행 대상({@link HandlerMethod}) 간의
     * 매핑 정보를 보관하는 내부 캐시이다.
     *
     * <p>
     * 이 Map은 컨테이너 초기화 과정에서
     * {@link #afterSingletonsInstantiated()} 단계에 한 번 채워지며,
     * 이후 요청 처리 시에는 읽기 전용으로 사용된다.
     * </p>
     */
    private final Map<RequestMappingInfo, HandlerMethod> handlerMethods = new HashMap<>();

    /**
     * 컨테이너로부터 주입받은 {@link ApplicationContext}.
     *
     * <p>
     * 이 필드는 {@link ApplicationContextAware} 콜백을 통해 설정되며,
     * 모든 singleton Bean 생성이 완료된 이후 수행되는
     * 초기화 단계에서 사용된다.
     * </p>
     */
    private ApplicationContext context;

    private final WebMvcConfigurationSupport mvcConfig;

    public RequestMappingHandlerMapping(WebMvcConfigurationSupport mvcConfig) {
        this.mvcConfig = mvcConfig;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Bean 생성 및 의존성 주입 완료 이후,
     * 컨테이너에 의해 호출되어 현재 {@link ApplicationContext}를 주입받는다.
     * </p>
     */
    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * 모든 singleton Bean 인스턴스 생성이 완료된 이후 호출되며,
     * 컨트롤러 및 요청 매핑 정보를 수집하는
     * 초기화 작업을 수행한다.
     * </p>
     */
    @Override
    public void afterSingletonsInstantiated() {
        initHandlerMethods(context);
    }

    /**
     * 주어진 HTTP 요청에 대응하는 {@link HandlerExecutionChain}을 조회한다.
     *
     * <p>
     * 요청의 경로와 HTTP 메서드를 기반으로
     * 사전에 등록된 {@link RequestMappingInfo} → {@link HandlerMethod} 매핑을 조회한 뒤,
     * 매칭되는 Handler가 존재하는 경우
     * 해당 Handler와 적용 가능한 {@link HandlerInterceptor} 목록을 포함한
     * 실행 체인을 생성하여 반환한다.
     * </p>
     *
     * <p>
     * 매칭되는 Handler가 없는 경우 {@code null}을 반환하며,
     * 이후 처리 여부는 호출자(DispatcherServlet)에 위임된다.
     * </p>
     *
     * @param request 현재 HTTP 요청
     * @return 요청에 매핑된 {@link HandlerExecutionChain}, 없으면 {@code null}
     */
    @Override
    public HandlerExecutionChain getHandler(HttpRequest request) {
        RequestMappingInfo key = new RequestMappingInfo(request.getPath(), request.getMethod());
        HandlerMethod handlerMethod = handlerMethods.get(key);

        if(handlerMethod == null)
            return null;

        return new HandlerExecutionChain(handlerMethod, mvcConfig.getInterceptorRegistry().getInterceptors());
    }

    /**
     * 컨텍스트 초기화 시점에 {@link Controller} Bean을 탐색하여
     * {@link RequestMapping}이 선언된 메서드들을
     * {@link RequestMappingInfo} → {@link HandlerMethod} 매핑으로 등록한다.
     *
     * <p>이 과정은 한 번만 수행되며,
     * 요청 처리 시에는 캐시된 매핑 정보를 그대로 사용한다.</p>
     */
    private void initHandlerMethods(ApplicationContext context) {
        // Controller 어노테이션이 있는 클래스 조회
        Map<String, Object> controllers = context.getBeansWithAnnotation(Controller.class);

        for(Object controller: controllers.values()) {
            Class<?> clazz = controller.getClass();

            for(Method method : clazz.getDeclaredMethods()) {
                // RequestMapping 어노테이션만 선택
                if(!method.isAnnotationPresent(RequestMapping.class))
                    continue;

                // RequestMappingInfo 생성
                RequestMapping rm = method.getAnnotation(RequestMapping.class);
                RequestMappingInfo info = new RequestMappingInfo(rm.value(), rm.method());

                // HandlerMethod 생성
                HandlerMethod handlerMethod = new HandlerMethod(controller, method);

                if(handlerMethods.containsKey(info))
                    throw new IllegalStateException("Duplicate mapping" + info);

                handlerMethods.put(info, handlerMethod);
            }
        }
    }
}
