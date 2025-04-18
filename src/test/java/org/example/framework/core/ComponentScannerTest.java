package org.example.framework.core;

import org.example.framework.core.ComponentScanner;

import java.util.Set;

public class ComponentScannerTest {


    /**
     * ComponentScanner가 지정된 패키지에서 .class 파일을 찾고
     * Component 어노테이션이 붙은 클래스들을 필터링 하는지 확인
     *  실제 컴파일 된 클래스 파일을 기반으로 작동
     */
    public static void main(String[] args) {

        try {
            ComponentScanner componentScanner = new ComponentScanner();

            Set<Class<?>> componentList = componentScanner.scan();
            if(componentList.isEmpty())
                System.out.println("Component가 비었습니다.");
            else {
                System.out.println("Component 출력");
                componentList.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Component scan 실패" + e.getMessage());
        }

    }



}
