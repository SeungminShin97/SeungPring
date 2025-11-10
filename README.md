# 🌀 SeungPring (승프링)

> **자바로 직접 구현한 Spring Framework 학습 프로젝트**  
> DI 컨테이너부터 DispatcherServlet, HandlerMapping까지 **스프링의 핵심 구조를 직접 설계 및 구현**했습니다.

---

## 📘 프로젝트 개요

스프링 프레임워크를 사용하면서,  
“의존성 주입(DI)과 요청 처리 흐름이 내부에서 어떻게 동작하는가?”  
라는 궁금증에서 출발한 **학습형 프레임워크 구현 프로젝트**입니다.

단순한 모방이 아니라,  
**Bean 등록 → 의존성 주입 → 요청 처리(DispatcherServlet)** 까지  
Spring의 핵심 동작 흐름을 실제 코드로 재현하는 것을 목표로 합니다.

---

## ⚙️ 주요 구현 기능

| 구분 | 구성 요소 | 설명 |
|------|------------|------|
| **Core (IOC)** | `ApplicationContext`, `BeanFactory`, `DependencyInjector` | 빈 등록, 의존성 주입, 라이프사이클 관리 |
| **Web (MVC)** | `DispatcherServlet`, `HandlerMapping`, `HandlerAdapter` | HTTP 요청 → 컨트롤러 실행 → JSON 응답 흐름 |
| **WAS (Tomcat 유사 계층)** | `Connector`, `ServletContainer`, `HttpRequest`, `HttpResponse` | Socket 통신 기반 요청 수신 및 서블릿 매핑 처리 |
| **App (사용자 계층)** | `PostController`, `PostService` | 실제 애플리케이션 로직 (테스트용 엔드포인트) |

---

## 🧩 아키텍처 개요

```text
[Client (Postman)]
   ↓
Connector (Socket)
   ↓
HttpRequest / HttpResponse
   ↓
ServletContainer
   ↓
FilterChain
   ↓
DispatcherServlet
   ↓
HandlerMapping → HandlerAdapter → Controller
   ↓
JSON Response
```

---

## 🗂️ 디렉터리 구조
```
org.example
├── Application.java
├── app/                # 실제 Controller, Service 구현
├── framework/          # 승프링 핵심 로직 (Spring Core + MVC)
│   ├── context/
│   ├── core/
│   ├── web/
│   └── exception/
└── was/                # Tomcat 유사 서버 구조 (Socket 기반)
├── connector/
└── container/
```

[//]: # (---)

[//]: # ()
[//]: # (## 📊 설계 문서 &#40;GitHub Pages&#41;)

[//]: # (/* 들어가서 한 번 클릭해주세요*/)

[//]: # (- [FLOW 다이어그램 보기]&#40;https://seungminshin97.github.io/SeungPring/SeungPring_FLOW.html&#41;)

[//]: # (- [UML 다이어그램 보기]&#40;https://seungminshin97.github.io/SeungPring/SeungPring_UML.html&#41;)

---

## 🧠 학습 포인트

- 프레임워크의 “동작 원리”를 직접 구현하며 구조적으로 이해
- IoC / DI, Servlet Pipeline, HTTP Request 흐름의 관계 파악
- Spring의 확장성 설계 철학(Component, Handler, Adapter)을 실전으로 재현

---

## 🚀 실행 목표
- Postman을 통한 JSON 기반 API 요청 및 응답 테스트
- View 렌더링은 제외하고, RESTful 구조 중심으로 구현 

---

## 📚 향후 계획
- Interceptor, ExceptionResolver 등 확장 기능 추가
- Thread Pool 기반 Connector 고도화
- ApplicationContext 로깅 및 Bean 라이프사이클 시각화

---

## 📑참고
- [스프링 공식 문서](https://docs.spring.io/spring-framework/docs/5.3.22/javadoc-api/allclasses-noframe.html)
- [톰캣 GITHUB](https://github.com/apache/tomcat/tree/main)