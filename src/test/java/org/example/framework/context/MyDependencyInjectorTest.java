package org.example.framework.context;

import org.example.framework.annotation.Autowired;
import org.example.framework.core.BeanFactory;
import org.example.test.DummyController;
import org.example.test.DummyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MyDependencyInjectorTest {

    @Test
    @DisplayName("@Autowired 필드가 정상적으로 주입되어야 한다")
    void shouldInjectFieldsAnnotatedWithAutowired() {
        // given
        DummyController controller = new DummyController();
        BeanFactory mockFactory = Mockito.mock(BeanFactory.class);

        DummyService expectedService = new DummyService();
        when(mockFactory.getBean("dummyService")).thenReturn(expectedService);

        MyDependencyInjector injector = new MyDependencyInjector();

        // when
        injector.inject(controller, mockFactory);

        // then
        assertNotNull(controller.dummyService);
        assertSame(expectedService, controller.dummyService);
        verify(mockFactory).getBean("dummyService");
    }

}
