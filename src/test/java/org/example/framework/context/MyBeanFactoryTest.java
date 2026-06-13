package org.example.framework.context;

import org.example.framework.annotation.Autowired;
import org.example.framework.context.beanDefinition.BeanDefinition;
import org.example.framework.context.beanDefinition.ClassBeanDefinition;
import org.example.framework.core.BeanDefinitionRegistry;
import org.example.framework.core.BeanFactory;
import org.example.framework.exception.bean.BeanCreationException;
import org.example.framework.exception.bean.NoSuchBeanDefinitionException;
import org.example.test.DummyBean;
import org.example.test.DummyChildService;
import org.example.test.DummyController;
import org.example.test.DummyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyBeanFactoryTest {

    private final String DUMMY_CONTROLLER = "dummyController";
    private final String DUMMY_SERVICE = "dummyService";

    private BeanFactory factory;
    private BeanDefinitionRegistry registry;

    static class MultiAutowiredConstructorBean {
        @Autowired
        public MultiAutowiredConstructorBean(DummyService a) {}

        @Autowired
        public MultiAutowiredConstructorBean(DummyService a, DummyService b) {}
    }

    static class ListInjectionBean {
        private final List<DummyService> services;

        @Autowired
        public ListInjectionBean(List<DummyService> services) {
            this.services = services;
        }

        int size() {
            return services.size();
        }
    }

    static class A {
        @Autowired
        public A(B b) {}
    }

    static class B {
        @Autowired
        public B(A a) {}
    }

    @BeforeEach
    void given() {
        registry = new MyBeanDefinitionRegistry();
        registry.registerBeanDefinition(DUMMY_SERVICE, new ClassBeanDefinition(DummyService.class, ScopeType.SINGLETON));
        registry.registerBeanDefinition(DUMMY_CONTROLLER, new ClassBeanDefinition(DummyController.class, ScopeType.SINGLETON));

        factory = new MyBeanFactory(new MyDependencyInjector(), registry);
    }

    @Nested
    @DisplayName("getBean(String beanName) 테스트")
    class GetBeanTests {
        @Test
        @DisplayName("존재하지 않는 bean인 경우 NoSuchBeanDefinitionException 예외 발생")
        void should_Throw_When_Bean_Not_Exist() {
            // when
            // then
            assertThrows(NoSuchBeanDefinitionException.class,
                    () -> factory.getBean("IllegalBeanName"));
        }

        @Test
        @DisplayName("bean 이름으로 인스턴스를 조회할 수 있다")
        void should_Return_Bean_When_BeanName_Given() {
            // when
            Object bean = factory.getBean(DUMMY_SERVICE);

            // then
            assertNotNull(bean);
            assertInstanceOf(DummyService.class, bean);
        }

        @Test
        @DisplayName("생성되지 않은 bean은 생성 후 캐시에 저장되어야 한다")
        void should_Create_And_Cache_Bean_If_Not_Exist() {
            // when
            Object first = factory.getBean(DUMMY_CONTROLLER);
            Object second = factory.getBean(DUMMY_CONTROLLER);

            // then
            assertNotNull(first);
            assertSame(first, second);
            assertEquals(DummyController.class, first.getClass());
        }
    }

    @Test
    @DisplayName("Bean이 정상적으로 생성되고 캐시에 저장되어야 한다")
    void should_Create_And_Cache_Singleton_Bean() {
        // when
        Object bean1 = factory.getBean(DUMMY_SERVICE);
        Object bean2 = factory.getBean(DUMMY_SERVICE);

        // then
        assertNotNull(bean1);
        assertNotNull(bean2);
        assertSame(bean1, bean2);
    }
    
    @Test
    @DisplayName("@Autowired 기반 생성자 DI가 정상적으로 이루어져야 한다.")
    public void should_inject_dependency_via_autowired_constructor() {
        //given
        BeanDefinition childBean = new ClassBeanDefinition(DummyChildService.class, ScopeType.SINGLETON);
        BeanDefinition dummyBean = new ClassBeanDefinition(DummyBean.class, ScopeType.SINGLETON);
        registry.registerBeanDefinition(childBean);
        registry.registerBeanDefinition(dummyBean);
        
        //when
        DummyChildService service = factory.getBean(DummyChildService.class);

        //then
        assertNotNull(service);
        assertTrue(service.hasDummyBean());
    }

    @Nested
    @DisplayName("getType() 테스트")
    class GetTypeTests {
        @Test
        @DisplayName("존재하지 않는 bean인 경우 NoSuchBeanDefinitionException 예외 발생")
        void should_Throw_When_Bean_Not_Exist() {
            // when
            // then
            assertThrows(NoSuchBeanDefinitionException.class,
                    () -> factory.getType("IllegalBeanName"));
        }

        @Test
        @DisplayName("bean 이름으로 타입(class)를 조회할 수 있다")
        void should_Return_Type_When_BeanName_given() {
            // when
            Class<?> clazz = factory.getType(DUMMY_CONTROLLER);

            // then
            assertNotNull(clazz);
            assertEquals(DummyController.class, clazz);
        }

        @Test
        @DisplayName("아직 생성되지 않은 prototype bean도 타입 조회는 가능하다")
        void should_Return_Type_For_Prototype_Before_Creation() {
            registry.registerBeanDefinition("proto",
                    new ClassBeanDefinition(DummyController.class, "proto", ScopeType.PROTOTYPE));

            Class<?> type = factory.getType("proto");

            assertEquals(DummyController.class, type);
        }

    }

    @Nested
    @DisplayName("isSingleton() 테스트")
    class IsSingletonTests {
        @Test
        @DisplayName("존재하지 않는 bean인 경우 NoSuchBeanDefinitionException 예외 발생")
        void should_Throw_When_Bean_Not_Exist() {
            // when
            // then
            assertThrows(NoSuchBeanDefinitionException.class,
                    () -> factory.getBean("IllegalBeanName"));
        }

        @Test
        @DisplayName("singleton scope의 경우 true 반환")
        void should_Return_True_When_Singleton() {
            // when
            // then
            assertNotNull(factory.getBean(DUMMY_CONTROLLER));
            assertTrue(factory.isSingleton(DUMMY_CONTROLLER));
        }

        @Test
        @DisplayName("protoType scope의 경우 false 반환")
        void should_Return_False_When_ProtoType() {
            // when
            registry.registerBeanDefinition("protoController",
                    new ClassBeanDefinition(DummyController.class, "protoController", ScopeType.PROTOTYPE));

            // then
            assertNotNull(factory.getBean("protoController"));
            assertFalse(factory.isSingleton("protoController"));
        }
    }

    @Nested
    @DisplayName("isPrototype() 테스트")
    class IsPrototypeTests {
        @Test
        @DisplayName("존재하지 않는 bean인 경우 NoSuchBeanDefinitionException 예외 발생")
        void should_Throw_When_Bean_Not_Exist() {
            // when
            // then
            assertThrows(NoSuchBeanDefinitionException.class,
                    () -> factory.getBean("IllegalBeanName"));
        }

        @Test
        @DisplayName("prototype scope의 경우 true 반환")
        void should_Return_True_When_Prototype() {
            // when
            registry.registerBeanDefinition("protoController",
                    new ClassBeanDefinition(DummyController.class, "protoController", ScopeType.PROTOTYPE));

            // then
            assertNotNull(factory.getBean("protoController"));
            assertTrue(factory.isPrototype("protoController"));
        }

        @Test
        @DisplayName("singleton scope의 경우 false 반환")
        void should_Return_False_When_ProtoType() {
            // when
            // then
            assertNotNull(factory.getBean(DUMMY_CONTROLLER));
            assertFalse(factory.isPrototype(DUMMY_CONTROLLER));
        }
    }

    @Nested
    @DisplayName("isTypeMatch() 테스트")
    class IsTypeMatchTests {
        @Test
        @DisplayName("존재하지 않는 bean 이름인 경우 NoSuchBeanDefinitionException 예외 발생")
        void should_Throw_When_Bean_Not_Exist() {
            // when
            // then
            assertThrows(NoSuchBeanDefinitionException.class,
                    () -> factory.isTypeMatch("IllegalClass", Object.class));
        }

        @Test
        @DisplayName("호환되는 타입인 경우 true 반환")
        void should_Return_True_When_Type_Is_Assignable() {
            // given
            registry.registerBeanDefinition("dummyService2",
                    new ClassBeanDefinition(DummyService.class, "dummyService2"));
            registry.registerBeanDefinition("childService",
                    new ClassBeanDefinition(DummyChildService.class, "childService"));

            // when
            // then
            assertTrue(factory.isTypeMatch(DUMMY_SERVICE, DummyService.class));
            assertTrue(factory.isTypeMatch("childService", DummyService.class));
        }

        @Test
        @DisplayName("호환되지 않는 타입인 경우 false 반환")
        void should_Return_False_When_Type_Not_Assignable() {
            // given
            registry.registerBeanDefinition("childService",
                    new ClassBeanDefinition(DummyChildService.class, "childService"));

            // when
            // then
            assertFalse(factory.isTypeMatch(DUMMY_SERVICE, DummyController.class));
            assertFalse(factory.isTypeMatch(DUMMY_SERVICE, DummyChildService.class));
        }
    }

    @Test
    @DisplayName("@Autowired 생성자가 둘 이상이면 예외가 발생한다")
    void should_Throw_When_Multiple_Autowired_Constructors() {
        registry.registerBeanDefinition("multi",
                new ClassBeanDefinition(MultiAutowiredConstructorBean.class, ScopeType.SINGLETON));

        assertThrows(BeanCreationException.class,
                () -> factory.getBean("multi"));
    }

    @Test
    @DisplayName("List<T> 생성자 파라미터에는 해당 타입의 모든 하위 Bean이 주입된다")
    void should_Inject_List_Of_Beans() {
        registry.registerBeanDefinition("s1",
                new ClassBeanDefinition(DummyService.class, "s1"));
        registry.registerBeanDefinition("s2",
                new ClassBeanDefinition(DummyService.class, "s2"));

        registry.registerBeanDefinition("listBean",
                new ClassBeanDefinition(ListInjectionBean.class, "listBean"));

        ListInjectionBean bean = factory.getBean(ListInjectionBean.class);

        assertEquals(3, bean.size());
    }

    @Test
    @DisplayName("순환 참조가 발생하면 CircularDependencyException이 발생한다")
    void should_Throw_When_Circular_Dependency() {
        registry.registerBeanDefinition("a", new ClassBeanDefinition(A.class, "a"));
        registry.registerBeanDefinition("b", new ClassBeanDefinition(B.class, "b"));

        assertThrows(BeanCreationException.class, () -> factory.getBean("a"));
    }
}