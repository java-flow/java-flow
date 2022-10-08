# Java Flow

Streaming programming and event driven lightweight development framework.

[中文文档](./README_CN.md)

## Installing

1. Clone project `git clone https://github.com/java-flow/java-flow.git`
2. Publish to local Maven repository `gradle publishToMavenLocal`
3. To add a dependency using Maven, use the following:

```xml
<dependency>
    <groupId>com.javaflow</groupId>
    <artifactId>java-flow</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Example

More examples can be viewed: [Examples](https://github.com/java-flow/java-flow-examples)

### TCP Server

#### Echo Server

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

#### Print Time Server

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

### TCP Client

#### Request

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

### HTTP Server

#### Echo Server

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

#### Print Time Server

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

### HTTP Client

#### Request

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

## Concept

There are mainly the following concepts:

- Object
    - `Flow`
    - `Node`
        - `Network`
        - `Function`
    - `Msg`
        - `payload`
- Method
    - `Flow.next(node)` Insert a node at the end of the flow.
    - `Flow.deploy()` Deploy flow，Trigger all nodes `onDeploy` method.
    - `Flow.destroy()` Destroy flow，Trigger all nodes `onDestroy` method.
    - `Flow.nextInvoke(node, msg)` Invoke flow，Start from the specified node.
    - `Flow.invoke(msg)` Invoke flow. Start from the first node.
    - `Node.invoke(msg)` Call the node. The input is the message returned by the previous node, and the output message will be passed to the next node.
- Event
    - `Node.onDeploy` Triggered during flow deployment. The node resource can be initialized at this time.
    - `Node.onDestroy` Triggered during flow destruction. The node resources can be released at this time.

## Dependency

- TcpIn、TcpOut used: [Mina](https://mina.apache.org/mina-project/userguide/user-guide-toc.html)
- HttpIn、HttpOut used: [Spark](http://sparkjava.com/documentation.html)

## About

The inspiration of this project comes from [Node-Red](https://nodered.org/),
Imagine that it would be a very flexible and simple development method if common 
basic functions were encapsulated into processing nodes, and each node provided 
unified input and output ports to form a business process. This will be suitable 
for simple application development.