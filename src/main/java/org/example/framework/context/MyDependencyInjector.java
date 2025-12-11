package org.example.framework.context;

import org.example.framework.annotation.Autowired;
import org.example.framework.core.BeanFactory;
import org.example.framework.core.DependencyInjector;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class MyDependencyInjector implements DependencyInjector {

    /**
     * @implNote
     * 대상 객체의 모든 필드 중 @Autowired가 붙은 필드에 대해,
     * {@link BeanFactory}에서 의존 {@link BeanDefinition}을 찾아 주입한다.
     *
     * <p>현재 클래스의 필드만 대상으로 하며,
     * private 필드 접근을 위해 setAccessible(true)를 사용한다.</p>
     */
    @Override
    public void inject(Object target, BeanFactory beanFactory) {
        // getDeclaredFields : 현재 클래스의 모든 필드(public, protected, private) / 부모 클래스 필드는 제외
        for(Field field : target.getClass().getDeclaredFields()) {
            if(!field.isAnnotationPresent(Autowired.class)) continue;

            // final 필드의 경우 생성자 주입
            if(Modifier.isFinal(field.getModifiers())) continue;

            field.setAccessible(true);
            String dependencyName = resolveBeanName(field);

            try {
                Object dependency = beanFactory.getBean(dependencyName);
                field.set(target, dependency);
            } catch (Exception e) {
                throw new RuntimeException("DI 실패 : " + field.getName(), e);
            }
        }
    }

    /**
     * 해당 {@link Field}의 타입 이름을 decapitalize 하여 추론한다. <br>
     * 예시: UserService → userService
     * @param field 추론할 타입 이름의 필드
     * @return 타입 이름
     */
    private String resolveBeanName(Field field) {
        return Introspector.decapitalize(field.getType().getSimpleName());
    }
}
