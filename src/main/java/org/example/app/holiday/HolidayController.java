package org.example.app.holiday;

import org.example.framework.annotation.Autowired;
import org.example.framework.annotation.Controller;
import org.example.framework.annotation.RequestMapping;
import org.example.framework.was.protocol.model.HttpMethod;

@Controller
public class HolidayController {

    @Autowired
    private final HolidayService service;

    public HolidayController(HolidayService service) {
        this.service = service;
    }

    @RequestMapping(value = "/holiday", method = HttpMethod.GET)
    public String getHoliday() {
        return service.getHoliday();
    }
}
