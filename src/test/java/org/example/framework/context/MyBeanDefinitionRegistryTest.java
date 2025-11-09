package org.example.framework.context;

import org.example.framework.annotation.Autowired;
import org.example.framework.exception.bean.BeanException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MyBeanDefinitionRegistryTest {

    private MyBeanDefinitionRegistry registry;

    private static class DummyService {}


    @BeforeEach
    void setUp() {
        registry = new MyBeanDefinitionRegistry();
    }

    private BeanDefinition createDummyBeanDefinition(String name, Class<?> clazz) {
        return new BeanDefinition(clazz, name, ScopeType.SINGLETON);
    }

    @Test
    void registerBeanDefinition_singleBean_shouldStore() {
        //Given
        BeanDefinition bean = createDummyBeanDefinition("test", DummyService.class);

        //When
        registry.registerBeanDefinition("test", bean);

        //Then
        assertTrue(registry.containsBeanDefinition("test"));
        assertEquals(1, registry.getBeanDefinitionCount());
        assertSame(bean, registry.getBeanDefinition("test"));
    }

    @Test
    void registerBeanDefinition_multipleBeans_shouldStoreAll() {
        //Given
        BeanDefinition bean1 = createDummyBeanDefinition("test1", DummyService.class);
        BeanDefinition bean2 = createDummyBeanDefinition("test2", DummyService.class);

        //When
        registry.registerBeanDefinition(("test1"), bean1);
        registry.registerBeanDefinition(("test2"), bean2);

        //Then
        assertEquals(2, registry.getBeanDefinitionCount());
        assertTrue(registry.containsBeanDefinition("test1"));
        assertTrue(registry.containsBeanDefinition("test2"));
        assertSame(bean1, registry.getBeanDefinition("test1"));
        assertSame(bean2, registry.getBeanDefinition("test2"));
    }

    @Test
    void registerBeanDefinition_duplicatedBeanName_shouldStoreFirstOne() {
        //Given
        BeanDefinition bean1 = createDummyBeanDefinition("test", DummyService.class);
        BeanDefinition bean2 = createDummyBeanDefinition("test", DummyService.class);

        //When
        registry.registerBeanDefinition(("test"), bean1);
        registry.registerBeanDefinition(("test"), bean2);

        //Then
        assertEquals(1, registry.getBeanDefinitionCount());
        assertTrue(registry.containsBeanDefinition("test"));
        assertSame(bean1, registry.getBeanDefinition("test"));
    }

    @Test
    void registerBeanDefinition_nullBeanName_shouldThrowException() {
        //Given
        BeanDefinition bean = createDummyBeanDefinition("test1", DummyService.class);

        //When & Then
        assertThrows(NullPointerException.class, () -> registry.registerBeanDefinition(null, bean));
        assertEquals(0, registry.getBeanDefinitionCount());
    }

    @Test
    void registerBeanDefinition_nullBeanDefinition_shouldThrowException() {
        //Given
        BeanDefinition bean = null;

        //When & Then
        assertThrows(NullPointerException.class, () -> registry.registerBeanDefinition("test", bean));
        assertEquals(0, registry.getBeanDefinitionCount());
    }

    @Test
    void getBeanDefinition_validBeanName_shouldReturnBeanDefinition() {
        // Given
        BeanDefinition bean = createDummyBeanDefinition("test",DummyService.class);

        //When
        registry.registerBeanDefinition("test", bean);

        //Then
        assertTrue(registry.containsBeanDefinition("test"));
        assertEquals(1, registry.getBeanDefinitionCount());
        assertSame(bean, registry.getBeanDefinition("test"));
    }

    @Test
    void getBeanDefinition_unRegisteredName_shouldThrowException() {
        // Given
        // When
        // Then
        assertEquals(0, registry.getBeanDefinitionCount());
        assertThrows(BeanException.class, () -> registry.getBeanDefinition("test"));
    }

    @Test
    void getBeanDefinitionNames_emptyMap_shouldReturnEmptyList() {
        // Given
        // When
        // Then
        assertEquals(0, registry.getBeanDefinitionCount());
        assertSame(ArrayList.class, registry.getBeanDefinitionNames().getClass());
        assertEquals(0, registry.getBeanDefinitionNames().size());
    }

    @Test
    void getBeanDefinitionCount_emptyMap_shouldReturnZero() {
        // Given
        // When
        // Then
        assertEquals(0, registry.getBeanDefinitionNames().size());
        assertEquals(0, registry.getBeanDefinitionCount());
    }

    @Test
    void getBeanDefinitionCount_multipleBeans_shouldReturnCorrectCount() {
        // Given
        BeanDefinition bean1 = createDummyBeanDefinition("test1", DummyService.class);
        BeanDefinition bean2 = createDummyBeanDefinition("test2", DummyService.class);

        // When
        registry.registerBeanDefinition("test1", bean1);
        registry.registerBeanDefinition("test2", bean2);

        // Then
        assertSame(bean1, registry.getBeanDefinition("test1"));
        assertSame(bean2, registry.getBeanDefinition("test2"));
        assertEquals(2, registry.getBeanDefinitionCount());
    }

    @Test
    void containsBeanDefinition_unRegisteredName_shouldReturnFalse() {
        // Given
        // When
        // Then
        assertEquals(0, registry.getBeanDefinitionCount());
        assertFalse(registry.containsBeanDefinition("test"));
    }

    @Test
    void containsBeanDefinition_singleBean_shouldReturnTrue() {
        // Given
        BeanDefinition bean = createDummyBeanDefinition("test", DummyService.class);

        // When
        registry.registerBeanDefinition("test", bean);

        // Then
        assertEquals(1, registry.getBeanDefinitionCount());
        assertSame(bean, registry.getBeanDefinition("test"));

        assertTrue(registry.containsBeanDefinition("test"));
    }
}