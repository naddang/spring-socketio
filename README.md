# 스프링부트 + Socket-IO 애플리케이션

기존 Sock.js만 이용해야해서 불편했던 부분들을 개선하기 위해 Socket-IO를 이용할 수 있도록 모듈을 만들었습니다.<br>
기존에 웹에 많이 떠돌아다니는 com.corundumstudio.socketio:netty-socketio 라이브러리를 이용한 socket-io서버의 경우 포트를 추가로 하나 더 뽑아서 사용해야하는 단점이 있기때문에,
socket-io 공식 라이브러리를 이용하여 하나의 포트에서 처리하는 모듈입니다.<br><br>
대부분의 중요 비즈니스 로직은 <a href="https://github.com/naddang/spring-socketio/blob/main/src/main/java/com/dev_cbj/springsocketio/socket/handler/EngineIoHandler.java">EngineIoHandler.java</a> 파일에 구현되어있습니다.

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
