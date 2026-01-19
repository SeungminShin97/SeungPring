package org.example.test.web;

import org.example.framework.annotation.Controller;
import org.example.framework.annotation.RequestMapping;
import org.example.framework.was.protocol.model.HttpMethod;

@Controller
public class TestController {

    @RequestMapping(value = "/test", method = HttpMethod.GET)
    public String test() {
        return "ok";
    }

    @RequestMapping(value = "/test", method = HttpMethod.POST)
    public String testPost() {
        return "post";
    }
}
