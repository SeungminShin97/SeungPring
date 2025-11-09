package org.example.framework.context;

import org.example.framework.core.BeanDefinitionRegistry;
import org.example.framework.core.BeanFactory;
import org.example.framework.exception.bean.NoSuchBeanDefinitionException;
import org.example.test.DummyChildService;
import org.example.test.DummyController;
import org.example.test.DummyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MyBeanFactoryTest {

    private final String DUMMY_CONTROLLER = "dummyController";
    private final String DUMMY_SERVICE = "dummyService";

    private BeanFactory factory;
    private BeanDefinitionRegistry registry;

    @BeforeEach
    void given() {
        registry = new MyBeanDefinitionRegistry();
        registry.registerBeanDefinition(DUMMY_SERVICE, new BeanDefinition(DummyService.class, ScopeType.SINGLETON));
        registry.registerBeanDefinition(DUMMY_CONTROLLER, new BeanDefinition(DummyController.class, ScopeType.SINGLETON));

        factory = new MyBeanFactory(new MyDependencyInjector(), registry);
    }

    @Nested
    @DisplayName("getBean() 테스트")
    class GetBeanTests {

        @Nested
        @DisplayName("getBean(Class<T> requiredType) 테스트")
        class GetBeanByTypeTests {
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

        @Nested
        @DisplayName("getBean(String beanName) 테스트")
        class GetBeanByNameTests {
            @Test
            @DisplayName("존재하지 않는 bean인 경우 NoSuchBeanDefinitionException 예외 발생")
            void should_Throw_When_Bean_Not_Exist() {
                // when
                // then
                assertThrows(NoSuchBeanDefinitionException.class,
                        () -> factory.getBean(MyBeanFactory.class));
            }

            @Test
            @DisplayName("bean 타입으로 인스턴스를 조회할 수 있다")
            void should_Return_Bean_When_Type_Given() {
                // when
                DummyService bean = factory.getBean(DummyService.class);

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
                    () -> factory.getBean("IllegalClass"));
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
                    new BeanDefinition(DummyController.class, "protoController", ScopeType.PROTOTYPE));

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
                    () -> factory.getBean("IllegalClass"));
        }

        @Test
        @DisplayName("prototype scope의 경우 true 반환")
        void should_Return_True_When_Prototype() {
            // when
            registry.registerBeanDefinition("protoController",
                    new BeanDefinition(DummyController.class, "protoController", ScopeType.PROTOTYPE));

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
                    new BeanDefinition(DummyService.class, "dummyService2"));
            registry.registerBeanDefinition("childService",
                    new BeanDefinition(DummyChildService.class, "childService"));

            // when
            Object dummyService2 = factory.getBean("dummyService2");
            Object childService = factory.getBean("childService");

            // then
            assertNotNull(dummyService2);
            assertNotNull(childService);
            assertTrue(factory.isTypeMatch(DUMMY_SERVICE, dummyService2.getClass()));
            assertTrue(factory.isTypeMatch("childService", DummyService.class));
        }

        @Test
        @DisplayName("호환되지 않는 타입인 경우 false 반환")
        void should_Return_False_When_Type_Not_Assignable() {
            // given
            registry.registerBeanDefinition("childService",
                    new BeanDefinition(DummyChildService.class, "childService"));

            // when
            Object bean = factory.getBean(DUMMY_CONTROLLER);
            Object childService = factory.getBean("childService");

            // then
            assertFalse(factory.isTypeMatch(DUMMY_SERVICE, bean.getClass()));
            // 부모가 자식 타입으로 호환 가능한지? -> false
            assertFalse(factory.isTypeMatch(DUMMY_SERVICE, childService.getClass()));
        }
    }
}