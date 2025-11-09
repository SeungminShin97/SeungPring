package org.example.framework.context;

import org.example.test.DummyController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyApplicationContextTest {

    private final String BASE_PACKAGE = "org.example.test";
    private final String DUMMY_CLASS = "dummyController";
    private MyApplicationContext context;

    @BeforeEach
    void setup() {
        this.context = new MyApplicationContext(BASE_PACKAGE);
    }

    @Test
    @DisplayName("refresh() 호출 시 @Component 클래스는 Bean에 등록해야 한다")
    void should_Register_Bean_When_Refresh_Called() {
        // given
        // when
        assertFalse(context.containsSingleton(DUMMY_CLASS));

        context.refresh();
        Object obj = context.getBean(DUMMY_CLASS);

        // then
        assertNotNull(obj);
        assertTrue(context.containsSingleton(DUMMY_CLASS));
        assertSame(DummyController.class, obj.getClass());
    }
}
