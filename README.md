# 스프링부트 + Socket-IO 애플리케이션

기존 Sock.js만 이용해야해서 불편했던 부분들을 개선하기 위해 Socket-IO를 이용할 수 있도록 모듈을 만들었습니다.

## 목차

- [Install And Run](#install)
- [Reference](#reference)

## Install

```docker pull naddang2/spring-socketio:latest```<br/>
```docker run -p 8080:8080 naddang2/spring-socketio:latest```<br/>
<a href="http://localhost:8080">이후 localhost:8080으로 접속하여 확인 할 수 있습니다.</a><br>

혹은 git clone을 통해 직접 빌드하여 실행할 수 있습니다.

```git clone https://github.com/naddang/spring-socketio.git```<br/>
```cd spring-socketio```<br/>
```./gradlew bootRun```<br/>

## Reference
<a href="https://medium.com/@huyvu8051/setup-project-spring-boot-2-x-socket-io-client-4-x-cf10e0f86bbb">medium의 huyvu8051님 자료를 일부 참고하였음.</a>
