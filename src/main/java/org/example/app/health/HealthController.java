package org.example.app.health;

import org.example.framework.annotation.Controller;
import org.example.framework.annotation.Lazy;
import org.example.framework.annotation.RequestMapping;
import org.example.framework.was.protocol.model.HttpMethod;

@Controller
@Lazy
public class HealthController {

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

    @RequestMapping(value = "/health/benchPress", method = HttpMethod.GET)
    public String benchPress() {
        return service.benchPress();
    }

    @RequestMapping(value = "/health/slowPushUp", method = HttpMethod.GET)
    public String slowPushUp() {
        return service.slowPushUp();
    }

    @RequestMapping(value = "/health/bench1rm", method = HttpMethod.GET)
    public String bench1rm() {
        return service.bench1rm();
    }
}
