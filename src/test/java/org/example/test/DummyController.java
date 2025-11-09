package org.example.test;

import org.example.framework.annotation.Autowired;
import org.example.framework.annotation.Component;

@Component
public class DummyController {

    @Autowired
    public DummyService dummyService;

}
