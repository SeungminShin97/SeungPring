package org.example.app.inbody;

import org.example.framework.annotation.Controller;
import org.example.framework.annotation.RequestMapping;
import org.example.framework.aop.profile.MethodProfile;
import org.example.framework.aop.profile.ProfileRepository;
import org.example.framework.was.protocol.model.HttpMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class InbodyController {

    @RequestMapping(value = "/inbody", method = HttpMethod.GET)
    public Map<String, Object> getInbody() {
        Map<Method, MethodProfile> snapshot = ProfileRepository.snapshot();
        Map<String, Object> results = new HashMap<>();

        for (Map.Entry<Method, MethodProfile> entry : snapshot.entrySet()) {
            Method m = entry.getKey();
            MethodProfile p = entry.getValue();

            String key = m.getDeclaringClass().getSimpleName() + "." + m.getName();

            Map<String, Object> stats = new HashMap<>();
            stats.put("count", p.getCount());
            stats.put("avgTimeNs", p.getAverageTimeNs());
            stats.put("totalTimeNs", p.getTotalTimeNs());

            results.put(key, stats);
        }

        return results;
    }
}
