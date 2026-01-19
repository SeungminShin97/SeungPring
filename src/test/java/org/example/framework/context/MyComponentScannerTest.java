package org.example.framework.context;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

class MyComponentScannerTest {

    @Test
    void scan_shouldFindAnnotatedComponents() throws Exception {
        // given
        MyComponentScanner scanner = new MyComponentScanner();

        // when
        Set<Class<?>> components = scanner.scan(
            "org.example.test"
        );

        // then
        assertFalse(components.isEmpty(), "컴포넌트가 비어 있으면 안 됨");
        assertTrue(
            components.stream().anyMatch(c -> c.getSimpleName().equals("DummyController")),
            "@Component 클래스(DummyController)가 포함되어야 함"
        );
    }

    @Test
    void scan_shouldNotIncludeNonComponentClasses() {
        MyComponentScanner scanner = new MyComponentScanner();

        Set<Class<?>> components = scanner.scan("org.example.test");

        assertFalse(
                components.stream().anyMatch(c -> c.getSimpleName().equals("NoComponentDummyClass")),
                "@Component 없는 클래스는 포함되면 안 됨"
        );
    }

    @Test
    void scan_shouldFindComponentsInSubPackages() {
        MyComponentScanner scanner = new MyComponentScanner();

        Set<Class<?>> components = scanner.scan("org.example.test");

        assertTrue(
                components.stream().anyMatch(c -> c.getName().contains("DummyClass")),
                "하위 패키지의 @Component도 스캔되어야 함"
        );
    }

    @Test
    void scan_shouldReturnEmptySetForInvalidPackage() {
        MyComponentScanner scanner = new MyComponentScanner();

        Set<Class<?>> components = scanner.scan("org.example.notexist");

        assertNotNull(components);
        assertTrue(components.isEmpty());
    }

    @Test
    void scan_shouldScanMultipleBasePackages() {
        MyComponentScanner scanner = new MyComponentScanner();

        Set<Class<?>> components = scanner.scan(
                "org.example.test",
                "org.example.app"
        );

        assertTrue(components.size() >= 2);
    }


}
