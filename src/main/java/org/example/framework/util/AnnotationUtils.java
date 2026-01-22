package org.example.framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Set;

public final class AnnotationUtils {
    private AnnotationUtils() {}

    /**
     * 대상 클래스에 특정 어노테이션이 직접 또는 메타 어노테이션으로
     * 존재하는지 검사한다.
     */
    public static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return hasAnnotation(element.getAnnotations(), annotationType, new HashSet<>());
    }

    private static boolean hasAnnotation(
            Annotation[] annotations, Class<? extends Annotation> targetType, Set<Class<? extends Annotation>> visited
    ) {
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> currentType = annotation.annotationType();

            // 순환 방지
            if (!visited.add(currentType)) {
                continue;
            }

            // 직접 매칭
            if (currentType.equals(targetType)) {
                return true;
            }

            // Java 어노테이션 제외
            if(currentType.getPackageName().startsWith("java.lang"))
                continue;

            // 메타 어노테이션 재귀 탐색
            if (hasAnnotation(currentType.getAnnotations(), targetType, visited)) {
                return true;
            }
        }
        return false;
    }
}
