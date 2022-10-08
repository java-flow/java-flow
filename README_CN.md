# Java Flow

采用流式编程、事件驱动的轻量级开发框架。

[English](./README.md)

## 安装

1. 克隆源码到本地 `git clone https://github.com/java-flow/java-flow.git`
2. 安装到本地 Maven 仓库 `gradle publishToMavenLocal`
3. 在项目中引入依赖

```xml
<dependency>
    <groupId>com.javaflow</groupId>
    <artifactId>java-flow</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## 示例

更多使用示例可以查看 [示例代码仓库](https://github.com/java-flow/java-flow-examples)

### Tcp 服务端

#### Echo 原样应答

```java
@AllArgsConstructor
public class EchoFlow extends FlowDefiner {

    private final Integer listenPort;

    @Override
    public Flow define() {
        return new Flow("Echo")
                .next(new TcpIn(listenPort, STRING))
                .next(new TcpOut());
    }

    public static void main(String[] args) {
        new EchoFlow(8080).deploy();
    }

}
```

#### Print Time 打印时间

```java
@AllArgsConstructor
public class PrintTimeFlow extends FlowDefiner {

    private final Integer listenPort;

    @Override
    public Flow define() {
        return new Flow("PrintTime")
                .next(new TcpIn(listenPort, STRING))
                .next(msg -> msg.payload(ZonedDateTime.now()))
                .next(new TcpOut());
    }

    public static void main(String[] args) {
        new PrintTimeFlow(8080).deploy();
    }

}
```

### Tcp 客户端

#### Request 请求

```java
@AllArgsConstructor
public class RequestFlow extends FlowDefiner {

    private final String host;

    private final Integer port;

    @Override
    public Flow define() {
        return new Flow("Request")
                .next(new TcpRequest(host, port))
                .next(msg -> msg);
    }

    public static void main(String[] args) {
        Flow flow = new RequestFlow("localhost", 8080).deploy();
        flow.invoke(new Msg().payload("hello"));
    }

}
```

### HTTP 服务端

#### Echo 原样应答

```java
@AllArgsConstructor
public class EchoFlow extends FlowDefiner {

    private final Integer listenPort;

    private final HttpMethod method;

    private final String url;

    @Override
    public Flow define() {
        return new Flow("Echo")
                .next(new HttpIn(listenPort, method, url))
                .next(new HttpOut());
    }

    public static void main(String[] args) {
        new EchoFlow(8080, HttpMethod.post, "/echo").deploy();
    }

}
```

#### Print Time 打印时间

```java
@AllArgsConstructor
public class PrintTimeFlow extends FlowDefiner {

    private final Integer listenPort;

    private final HttpMethod method;

    private final String url;

    @Override
    public Flow define() {
        return new Flow("PrintTime")
                .next(new HttpIn(listenPort, method, url))
                .next(msg -> msg.payload(ZonedDateTime.now()))
                .next(new HttpOut());
    }

    public static void main(String[] args) {
        new PrintTimeFlow(8080, HttpMethod.get, "/time").deploy();
    }

}

```

### HTTP 客户端

#### Request 请求

```java
@AllArgsConstructor
public class RequestFlow extends FlowDefiner {

    @Override
    public Flow define() {
        return new Flow("Request")
                .next(msg -> {
                    msg.put(HttpRequest.Fields.method, "POST");
                    msg.put(HttpRequest.Fields.url, "http://localhost:8080/echo");
                    return msg;
                })
                .next(new HttpRequest())
                .next(msg -> msg);
    }

    public static void main(String[] args) {
        Flow flow = new RequestFlow().deploy();
        flow.invoke(new Msg().payload("hello"));
    }

}
```

## 概念

主要分为以下几个概念：

- 对象
  - `Flow` 流程
  - `Node` 节点
    - `Network` 网络节点
    - `Function` 处理方法节点
  - `Msg` 消息
    - `payload` 消息主体
- 方法
  - `Flow.next(node)` 将节点插入到流程末尾
  - `Flow.deploy()` 部署流程，触发所有节点 onDeploy 方法
  - `Flow.destroy()` 销毁流程，触发所有节点 onDestroy 方法
  - `Flow.nextInvoke(node, msg)` 调用流程，以某个节点为起点，开始顺序调用后续节点
  - `Flow.invoke(msg)` 调用流程，从起点开始
  - `Node.invoke(msg)` 调用节点，输入为上一个节点返回的消息，输出的消息将传递给下一个节点
- 事件
  - `Node.onDeploy` 流程部署时触发，可进行节点初始化
  - `Node.onDestroy` 流程销毁时触发，可进行节点资源释放

## 依赖

- TcpIn、TcpOut 使用了 [Mina](https://mina.apache.org/mina-project/userguide/user-guide-toc.html) 框架实现
- HttpIn、HttpOut 使用了 [Spark](http://sparkjava.com/documentation.html) 框架实现

## 关于

该项目的灵感来源于 [Node-Red](https://nodered.org/)，试想如果将常见基础功能封装成一个个处理节点
，每个节点统一提供输入输出端口，将节点组成业务流程，将会是十分灵活简单的开发方式。对于简单应答式应用的开发会是一大利器。