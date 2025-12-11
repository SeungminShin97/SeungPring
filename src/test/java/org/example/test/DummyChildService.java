package org.example.test;

import org.example.framework.annotation.Autowired;
import org.example.framework.annotation.Component;

@Component
public class DummyChildService extends DummyService{
    private final DummyBean bean;

    @Autowired
    public DummyChildService(DummyBean bean) {
        this.bean = bean;
    }

    public boolean hasDummyBean() {
        return bean != null;
    }
}
