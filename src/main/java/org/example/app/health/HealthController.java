package org.example.app.health;

import org.example.framework.annotation.Autowired;
import org.example.framework.annotation.Controller;
import org.example.framework.annotation.RequestMapping;
import org.example.framework.was.protocol.model.HttpMethod;

@Controller
public class HealthController {

    @Autowired
    private final HealthService service;

    public HealthController(HealthService service) {
        this.service = service;
    }

    @RequestMapping(value = "/health", method = HttpMethod.GET)
    public String healthCheck() {
        return "good!";
    }

    @RequestMapping(value = "/health/sbd", method = HttpMethod.GET)
    public String sbd() {
        return service.sbdInfo();
    }
}
