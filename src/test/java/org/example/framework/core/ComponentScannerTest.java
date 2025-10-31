package org.example.framework.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

class ComponentScannerTest {

    @Test
    void scan_shouldFindAnnotatedComponents() throws Exception {
        // given
        ComponentScanner scanner = new ComponentScanner(
            "org.example.app",
            "org.example.framework",
            "org.example.test"
        );

        // when
        Set<Class<?>> components = scanner.scan();

        // then
        assertFalse(components.isEmpty(), "컴포넌트가 비어 있으면 안 됨");
        assertTrue(
            components.stream().anyMatch(c -> c.getSimpleName().equals("DummyComponent")),
            "@Component 클래스(DummyComponent)가 포함되어야 함"
        );
    }
}
