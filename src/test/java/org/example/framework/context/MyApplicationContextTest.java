package org.example.framework.context;

import org.example.test.DummyController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyApplicationContextTest {

    private final String BASE_PACKAGE = "org.example.test";
    private final String DUMMY_BEAN_NAME = "dummyController";

    private MyApplicationContext context;

    @BeforeEach
    void setUp() {
        // refresh를 호출하지 않은 '미초기화 컨텍스트'
        this.context = new MyApplicationContext(BASE_PACKAGE);
    }

    @Test
    @DisplayName("refresh 전에는 BeanDefinition이 등록되지 않는다")
    void beforeRefresh_noBeanDefinitions() {
        assertFalse(context.containsBean(DUMMY_BEAN_NAME));
        assertFalse(context.containsSingleton(DUMMY_BEAN_NAME));
    }

    @Test
    @DisplayName("refresh는 BeanDefinition만 등록하고 인스턴스는 생성하지 않는다")
    void refresh_registersDefinitions_only() {
        // when
        context.refresh();

        // then
        assertTrue(context.containsBean(DUMMY_BEAN_NAME));
        assertFalse(context.containsSingleton(DUMMY_BEAN_NAME));
    }

    @Test
    @DisplayName("getBean 호출 시 singleton 인스턴스가 생성된다")
    void getBean_triggersSingletonInstantiation() {
        // given
        context.refresh();

        // when
        Object bean = context.getBean(DUMMY_BEAN_NAME);

        // then
        assertNotNull(bean);
        assertTrue(context.containsSingleton(DUMMY_BEAN_NAME));
        assertSame(DummyController.class, bean.getClass());
    }

    @Test
    @DisplayName("refresh는 여러 번 호출되어도 중복 등록되지 않는다")
    void refresh_isIdempotent() {
        // when
        context.refresh();
        context.refresh();

        Object bean1 = context.getBean(DUMMY_BEAN_NAME);
        Object bean2 = context.getBean(DUMMY_BEAN_NAME);

        // then
        assertSame(bean1, bean2);
    }

    @Test
    @DisplayName("타입 기반 조회는 refresh 이후 정상 동작해야 한다")
    void getBeanByType_afterRefresh() {
        // given
        context.refresh();

        // when
        DummyController controller = context.getBean(DummyController.class);

        // then
        assertNotNull(controller);
        assertTrue(context.containsSingleton(DUMMY_BEAN_NAME));
    }
}
