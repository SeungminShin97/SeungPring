package org.example.framework.web.mapping;

import org.example.framework.annotation.Component;
import org.example.framework.annotation.Controller;
import org.example.framework.annotation.RequestMapping;
import org.example.framework.core.ApplicationContext;
import org.example.framework.was.protocol.model.HttpRequest;
import org.example.framework.web.HandlerMethod;
import org.example.framework.web.RequestMappingInfo;

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

    private final Map<RequestMappingInfo, HandlerMethod> handlerMethods = new HashMap<>();

    public RequestMappingHandlerMapping(ApplicationContext context) {
        initHandlerMethods(context);
    }

    /**
     * 주어진 HTTP 요청에 대응하는 Handler를 조회한다.
     *
     * @param request 현재 HTTP 요청
     * @return 매칭되는 {@link HandlerMethod}, 없으면 {@code null}
     */
    @Override
    public Object getHandler(HttpRequest request) {
        RequestMappingInfo key = new RequestMappingInfo(request.getPath(), request.getMethod());

        return handlerMethods.get(key);
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
