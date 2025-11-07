package org.example.framework.context;

import org.example.framework.annotation.Component;
import org.example.framework.core.ComponentScanner;
import org.example.framework.exception.ComponentScanException;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 지정된 패키지 경로에서 @Component 어노테이션이 붙은 클래스를 탐색하는 유틸리티
 * TODO: @ComponentScan(basePackage={"org.example.app"}) 방식의 어노테이션 기반으로 바꾸기
 */
public class MyComponentScanner implements ComponentScanner {



    /**
     * 지정된 모든 패키지를 순회하며 @Component 클래스들을 수집
     */
    @Override
    public Set<Class<?>> scan(String... basePackages) {
        Set<Class<?>> componentSet = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String currentPackage = "";

        try {
            for (String basePackage : basePackages) {
                currentPackage = basePackage;
                String path = basePackage.replace('.', '/');
                URL url = classLoader.getResource(path);
                if (url == null) continue;

                File dir = new File(url.toURI());
                collectClasses(dir, basePackage, componentSet, classLoader);
            }
        } catch (Exception e) {
            throw new ComponentScanException(currentPackage, e);
        }

        return componentSet;
    }


    /**
     * 디렉터리 내 .class 파일을 재귀적으로 탐색하고, @Component 클래스만 등록
     */
    private void collectClasses(File dir, String packageName, Set<Class<?>> components, ClassLoader loader)
            throws ClassNotFoundException {
        if (!dir.exists()) return;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                collectClasses(file, packageName + "." + file.getName(), components, loader);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                Class<?> clazz = loader.loadClass(className);
                if (clazz.isAnnotationPresent(Component.class)) {
                    components.add(clazz);
                }
            }
        }
    }
}