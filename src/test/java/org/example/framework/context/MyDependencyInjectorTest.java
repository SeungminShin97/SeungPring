package org.example.framework.context;

import org.example.framework.annotation.Autowired;
import org.example.framework.core.BeanFactory;
import org.example.test.DummyController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MyDependencyInjectorTest {

    // ===== Dummy Services =====
    static class DummyService {}
    static class AnotherService {}

    // ===== Dummy Controllers =====

    static class AutowiredController {
        @Autowired
        DummyService dummyService;
    }

    static class NonAutowiredController {
        DummyService dummyService;
    }

    static class FinalFieldController {
        @Autowired
        final DummyService dummyService = null;
    }

    static class PrivateFieldController {
        @Autowired
        private DummyService dummyService;

        DummyService getDummyService() {
            return dummyService;
        }
    }

    static class ParentController {
        @Autowired
        DummyService parentService;
    }

    static class ChildController extends ParentController {
    }

    @Test
    @DisplayName("주입 대상 클래스에 선언된 @Autowired 필드는 정상적으로 주입된다")
    void shouldInjectFieldsAnnotatedWithAutowired() {
        // given
        ParentController controller = new ParentController();
        BeanFactory mockFactory = Mockito.mock(BeanFactory.class);

        DummyService expectedService = new DummyService();
        when(mockFactory.getBean("dummyService")).thenReturn(expectedService);

        MyDependencyInjector injector = new MyDependencyInjector();

        // when
        injector.inject(controller, mockFactory);

        // then
        assertNotNull(controller.parentService);
        assertSame(expectedService, controller.parentService);
        verify(mockFactory).getBean("dummyService");
    }

    @Test
    @DisplayName("@Autowired가 없는 필드는 의존성 주입 대상에서 제외된다")
    void shouldNotInjectFieldWithoutAutowired() {
        // given
        NonAutowiredController controller = new NonAutowiredController();
        BeanFactory factory = mock(BeanFactory.class);

        MyDependencyInjector injector = new MyDependencyInjector();

        // when
        injector.inject(controller, factory);

        // then
        assertNull(controller.dummyService);
        verify(factory, never()).getBean("nonAutowiredService");
    }

    @Test
    @DisplayName("final 필드는 @Autowired가 있어도 주입되지 않는다")
    void shouldSkipFinalFieldInjection() {
        // given
        FinalFieldController controller = new FinalFieldController();
        BeanFactory factory = mock(BeanFactory.class);

        MyDependencyInjector injector = new MyDependencyInjector();

        // when
        injector.inject(controller, factory);

        // then
        assertNull(controller.dummyService);
        verify(factory, never()).getBean(anyString());
    }

    @Test
    @DisplayName("의존성 조회에 실패하면 RuntimeException으로 래핑되어 던져진다")
    void shouldWrapExceptionWhenBeanFactoryFails() {
        // given
        AutowiredController controller = new AutowiredController();
        BeanFactory factory = mock(BeanFactory.class);

        when(factory.getBean("dummyService"))
                .thenThrow(new RuntimeException("no bean"));

        MyDependencyInjector injector = new MyDependencyInjector();

        RuntimeException e = assertThrows(
                RuntimeException.class,
                () -> injector.inject(controller, factory)
        );

        // when & then
        assertTrue(e.getMessage().contains("DI 실패"));
    }

    @Test
    @DisplayName("private 접근 제한자를 가진 필드에도 의존성이 주입된다")
    void shouldInjectPrivateField() {
        // given
        PrivateFieldController controller = new PrivateFieldController();
        BeanFactory factory = mock(BeanFactory.class);

        DummyService service = new DummyService();
        when(factory.getBean("dummyService")).thenReturn(service);

        MyDependencyInjector injector = new MyDependencyInjector();

        // when
        injector.inject(controller, factory);

        // then
        assertSame(service, controller.getDummyService()); // getter 필요
    }

    @Test
    @DisplayName("부모 클래스에 선언된 @Autowired 필드는 주입 대상이 아니다")
    void shouldNotInjectSuperclassFields() {
        // given
        ChildController controller = new ChildController();
        BeanFactory factory = mock(BeanFactory.class);

        MyDependencyInjector injector = new MyDependencyInjector();

        // when
        injector.inject(controller, factory);

        // then
        assertNull(controller.parentService);
    }


}
