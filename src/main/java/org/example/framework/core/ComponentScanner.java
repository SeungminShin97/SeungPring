package org.example.framework.core;

import org.example.framework.annotation.Component;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComponentScanner {
    private final List<String> basePackage;

    public ComponentScanner() {
        // 스캔할 패키지 등록
        basePackage = new ArrayList<>();
        basePackage.add("org/example/app");
        basePackage.add("org/example/framework/web");
    }

    public Set<Class<?>> scan() throws URISyntaxException, ClassNotFoundException {
        Set<Class<?>> componentSet = new HashSet<>();
        // basePackage 순회
        for(String basePackageUrl : basePackage) {
            // basePackage 기반으로 클래스 패스 가져오기
            URL url = ClassLoader.getSystemClassLoader().getResource(basePackageUrl);

            if(url != null) {
                File dir = new File(url.toURI());
                File[] classFile = dir.listFiles(file -> file.getName().endsWith(".class"));

                if(classFile == null)
                    throw new ClassNotFoundException("클래스 파일을 읽을 수 없습니다." + dir.getPath());

                for(File file : classFile) {
                    String className = getClassName(file);

                    // 클래스 가져오기
                    Class<?> clazz = Class.forName(className);

                    if(clazz.isAnnotationPresent(Component.class))
                        componentSet.add(clazz);
                }
            }
        }
        return componentSet;
    }

    private String getClassName(File file) {
        String fullPath = file.getAbsolutePath();
        // 상대 경로 추출
        String basePath = new File("build/classes/java/main").getAbsolutePath();
        String relativePath = fullPath.substring(basePath.length() + 1);

        // 확장자 제거
        String className = relativePath.replace(".class", "");

        // 경로 구분 -> 패키지 구분
        return className.replace(File.separatorChar, '.');
    }

}
