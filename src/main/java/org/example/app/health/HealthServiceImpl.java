package org.example.app.health;

import org.example.framework.annotation.Component;
import org.example.framework.aop.annotation.Profiled;
import org.example.framework.aop.annotation.Retry;

@Component
public class HealthServiceImpl implements HealthService {

    private int count = 0;

    @Override
    public String sbdInfo() {
        return "3대 500";
    }

    @Override
    @Retry(maxAttempts = 3, delayMs = 100)
    public String benchPress() {
        System.out.println("벤치 100KG 시작!");
        count++;
        System.out.println(count + "개!");

        if(count < 3)
            throw new RuntimeException("최소 " + count + "번은 들자!");

        return count + "개 성공!";
    }

    @Override
    @Profiled
    public String slowPushUp() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return "슬로우 푸쉬업 성공!";
    }

    @Override
    @Retry(maxAttempts = 5)
    @Profiled
    public String bench1rm() {
        System.out.println("벤치 1rm 시작");

        if(Math.random() < 0.5)
            throw new RuntimeException("실패!");

        return "성공";
    }
}
