package org.example.framework.context;

import org.example.framework.annotation.Autowired;
import org.example.framework.core.BeanFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MyDependencyInjectorTest {

    static class DummyService1 {}
    static class DummyService2 {}

    static class DummyController{
        @Autowired
        DummyService1 dummyService1;
        @Autowired
        DummyService2 dummyService2;
    }

    @Test
    @DisplayName("@Autowired 필드가 정상적으로 주입되어야 한다")
    void shouldInjectFieldsAnnotatedWithAutowired() {
        // given
        DummyController controller = new DummyController();
        BeanFactory mockFactory = Mockito.mock(BeanFactory.class);

        DummyService1 expectedService1 = new DummyService1();
        DummyService2 expectedService2 = new DummyService2();
        when(mockFactory.getBean("dummyService1")).thenReturn(expectedService1);
        when(mockFactory.getBean("dummyService2")).thenReturn(expectedService2);

        MyDependencyInjector injector = new MyDependencyInjector();

        // when
        injector.inject(controller, mockFactory);

        // then
        assertNotNull(controller.dummyService1);
        assertNotNull(controller.dummyService2);
        assertSame(expectedService1, controller.dummyService1);
        assertSame(expectedService2, controller.dummyService2);
        verify(mockFactory).getBean("dummyService1");
        verify(mockFactory).getBean("dummyService2");

    }

}
