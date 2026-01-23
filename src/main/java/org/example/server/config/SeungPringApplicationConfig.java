package org.example.server.config;

import org.example.framework.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;

/**
 * SeungPring 서버 실행에 필요한 설정 정보.
 *
 * <p>
 * 커맨드라인 인자를 기반으로 WAS 실행 옵션을 구성한다.
 * </p>
 *
 * <pre>
 * --port=8080
 * --threads=10
 * --base-package=org.example.app
 * --base-package=org.example.web
 * </pre>
 */
public record SeungPringApplicationConfig(

        // WAS
        int port,
        int workerThreads,

        // IoC
        String[] basePackages

) {

    // -------------------------
    // factory
    // -------------------------

    public static SeungPringApplicationConfig from(
            Class<?> source,
            String[] args
    ) {
        int port = 8080;
        int workerThreads = 10;
        List<String> basePackages = new ArrayList<>();

        // 1. args 우선 파싱
        for (String arg : args) {
            if (arg.startsWith("--port=")) {
                port = Integer.parseInt(arg.substring("--port=".length()));
            }
            else if (arg.startsWith("--threads=")) {
                workerThreads = Integer.parseInt(arg.substring("--threads=".length()));
            }
            else if (arg.startsWith("--base-package=")) {
                basePackages.add(arg.substring("--base-package=".length()));
            }
        }

        // 2. args에 base-package가 없으면 @ComponentScan 사용
        if (basePackages.isEmpty()) {
            ComponentScan scan = source.getAnnotation(ComponentScan.class);
            if (scan != null) {
                basePackages.addAll(List.of(scan.value()));
            }
        }

        // 3. 그래도 없으면 실패
        if (basePackages.isEmpty()) {
            throw new IllegalStateException(
                    "No base package specified. Use --base-package or @ComponentScan"
            );
        }

        return new SeungPringApplicationConfig(
                port,
                workerThreads,
                basePackages.toArray(String[]::new)
        );
    }

}
