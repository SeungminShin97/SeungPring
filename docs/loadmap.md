# 승프링(SeungSpring) 개발 로드맵

> 기준: 현재 구현 상태(IoC + BIO 기반 WAS + HTTP/1.1 파서/라이터 보유)  
> 목표: “HTTP 서버 + IoC 실험”이 아니라 **프레임워크로서 설득력 있는 미니 Spring 구현**

---

## 0. 현재 상태 요약 (전제)

### 이미 구현됨
- IoC 컨테이너
    - Component Scan
    - BeanDefinition / BeanFactory
    - Singleton 관리
    - Constructor / Field 기반 DI
    - 순환 참조 감지
- BIO 기반 WAS
    - Socket accept + ThreadPool
    - HTTP/1.1 파싱 / 응답 작성
    - HTTP 프로토콜 감지 구조(1.1 / 2.0 분기)

### 미완 / 공백
- DispatcherServlet 계열 없음
- 요청 → 컨트롤러 매핑 로직 없음
- MVC 파이프라인 없음
- HTTP/2는 전부 TODO 상태

---

## Phase 1. MVC MVP (최우선 단계)

> 목표: “요청 → 컨트롤러 메서드 실행 → 응답”까지 흐름 완성

### 구현 항목
1. 내부 표준 요청/응답 객체 정비
    - HttpRequest → FrameworkRequest
    - HttpResponse → FrameworkResponse

2. DispatcherServlet 도입
    - `service(request, response)`
    - `doDispatch()` 중심 구조

3. HandlerMapping
    - (HTTP Method + Path) → HandlerMethod
    - Controller Bean + Method 매핑

4. HandlerAdapter
    - Reflection 기반 메서드 호출
    - 파라미터 최소 지원
        - HttpRequest / HttpResponse
        - Path, Query 중 1개만 우선

5. HttpProtocolHandler 연결
    - `parser.parse()`
    - `dispatcher.service()`
    - `writer.write()`

### 완료 기준
- 브라우저/HTTP 클라이언트에서 요청 시
- `@Controller` + `@RequestMapping` 메서드가 실제 실행됨

---

## Phase 2. IoC 컨테이너 고도화

> 목표: “작동하는 컨테이너” → “프레임워크급 컨테이너”

### 우선순위
1. Eager Singleton 초기화
    - refresh 종료 시 singleton 전부 생성

2. @ComponentScan 지원
    - Application 진입 클래스 기반 basePackages 지정

3. Bean Lifecycle Hook
    - 초기화: @PostConstruct 또는 InitializingBean
    - 종료: @PreDestroy 또는 DisposableBean

4. BeanPostProcessor
    - Bean 생성 전/후 가로채기
    - 이후 AOP / Proxy 확장 기반

---

## Phase 3. 웹 파이프라인 완성도 상승

> 목표: Spring MVC와 구조적으로 유사한 체인 확보

### 구현 항목
1. FilterChain
    - DispatcherServlet 앞단 전처리

2. HandlerInterceptor
    - preHandle / postHandle / afterCompletion

3. Exception Resolver
    - Controller 예외 → HTTP 응답 매핑
    - 공통 에러 응답 포맷 정의

---

## Phase 4. WAS 현실성 보완 (마무리 단계)

> 목표: “돌아가는 서버” → “의도된 서버 동작”

### 개선 항목
1. Connection 정책 명확화
    - keep-alive 지원 여부 결정
    - 기본은 close 고정 권장

2. Thread Pool Saturation 전략
    - 큐 초과 시 거절 / 로그 정책

3. BIO Endpoint 안정화
    - 소켓 종료 시점 명확화
    - 예외 발생 시 자원 정리

4. (선택) NIO Endpoint
    - 구조 증명용
    - 포트폴리오 필수 아님

---

## 추천 완성 루트 (취준 기준)

1. Phase 1 MVC MVP 완성
2. Phase 2 중 핵심 2~3개만 선택 구현
    - Eager Init
    - ComponentScan
    - BeanPostProcessor
3. Phase 3에서 Filter + Interceptor + Exception 처리로 마무리
