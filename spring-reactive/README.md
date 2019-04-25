# 理解 Reactive

## 关于 Reactive 的一些讲法

- Reactive 是异步非阻塞编程
- Reactive 能够提升程序性能
- Reactive 解决传统编程模型遇到的困境

## Reactive 框架

- Java 9 Flow API
- RxJava
- Reactor

## 传统编程模型中的某些困境

### [Reactor](http://projectreactor.io/docs/core/release/reference/#_blocking_can_be_wasteful) 认为阻塞可能是浪费的

> ### 3.1. Blocking Can Be Wasteful
>
> Modern applications can reach huge numbers of concurrent users, and, even though the capabilities of modern hardware have continued to improve, performance of modern software is still a key concern.
>
> There are broadly two ways one can improve a program’s performance:
>
> 1. **parallelize**: use more threads and more hardware resources.
> 2. **seek more efficiency** in how current resources are used.
>
> Usually, Java developers write programs using blocking code. This practice is fine until there is a performance bottleneck, at which point the time comes to introduce additional threads, running similar blocking code. But this scaling in resource utilization can quickly introduce contention and concurrency problems.
>
> Worse still, blocking wastes resources. If you look closely, as soon as a program involves some latency (notably I/O, such as a database request or a network call), resources are wasted because a thread (or many threads) now sits idle, waiting for data.
>
> So the parallelization approach is not a silver bullet. It is necessary in order to access the full power of the hardware, but it is also complex to reason about and susceptible to resource wasting.

#### 观点归纳：

- 阻塞导致性能瓶颈和浪费资源
- 增加线程可能会引起资源竞争和并发问题
- 并行的方式不是银弹（不能解决所有问题）

#### 理解阻塞的弊端

##### 阻塞场景 - 数据顺序加载

加载流程如下图所示：

![blocking-loader](img\blocking-loader.png)

实现代码如下：

```java
public class DataLoader {
    public final void load() {
        long startTime = System.currentTimeMillis(); // 开始时间
        doLoad(); // 具体执行
        long costTime = System.currentTimeMillis() - startTime; // 消耗时间
        System.out.println("load() 总耗时：" + costTime + " 毫秒");
    }
    protected void doLoad() { // 串行计算
        loadConfigurations(); // 耗时 1s
        loadUsers(); // 耗时 2s
        loadOrders(); // 耗时 3s
    } // 总耗时 1s + 2s + 3s = 6s

    protected final void loadConfigurations() {
        loadMock("loadConfigurations()", 1);
    }
    protected final void loadUsers() {
        loadMock("loadUsers()", 2);
    }
    protected final void loadOrders() {
        loadMock("loadOrders()", 3);
    }
    private void loadMock(String source, int seconds) {
        try {
            long startTime = System.currentTimeMillis();
            long milliseconds = TimeUnit.SECONDS.toMillis(seconds);
            Thread.sleep(milliseconds);
            long costTime = System.currentTimeMillis() - startTime;
            System.out.printf("[线程 : %s] %s 耗时 : %d 毫秒\n",
                    Thread.currentThread().getName(), source, costTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        new DataLoader().load();
    }
}
```

输出为：

```
[线程 : main] loadConfigurations() 耗时 : 1002 毫秒
[线程 : main] loadUsers() 耗时 : 2000 毫秒
[线程 : main] loadOrders() 耗时 : 3001 毫秒
load() 总耗时：6015 毫秒
```

##### 结论

> 由于加载过程串行执行的关系，导致消耗实现线性累加。Blocking 模式即串行执行 。

#### 理解并行的复杂

##### 并行场景 - 并行数据加载

加载流程如下图所示：

![parallel-loader](img\parallel-loader.png)

```java
package cn.sjsdfg.spring.reactive.loader;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Joe on 2019/4/24.
 */
public class ParallelDataLoader extends DataLoader {
    protected void doLoad() { // 并行计算
        ExecutorService executorService = Executors.newFixedThreadPool(3); // 创建线程池
        CompletionService completionService = new
                ExecutorCompletionService(executorService);
        completionService.submit(super::loadConfigurations, null); // 耗时 >= 1s
        completionService.submit(super::loadUsers, null); // 耗时 >= 2s
        completionService.submit(super::loadOrders, null); // 耗时 >= 3s
        int count = 0;
        while (count < 3) { // 等待三个任务完成
            if (completionService.poll() != null) {
                count++;
            }
        }
        executorService.shutdown();
    } // 总耗时 max(1s, 2s, 3s) >= 3s
    public static void main(String[] args) {
        new ParallelDataLoader().load();
    }
}

```

输出为：

```java
[线程 : pool-1-thread-1] loadConfigurations() 耗时 : 1000 毫秒
[线程 : pool-1-thread-2] loadUsers() 耗时 : 2001 毫秒
[线程 : pool-1-thread-3] loadOrders() 耗时 : 3002 毫秒
load() 总耗时：3140 毫秒
```

##### 结论

> 明显地，程序改造为并行加载后，性能和资源利用率得到提升，消耗时间取最大者。

#### 延伸思考

1. 如果阻塞导致性能瓶颈和资源浪费的话，Reactive 也能解决这个问题？
2. 为什么不直接使用  Future#get() 方法强制所有任务执行完毕，然后再统计总耗时？
3. 由于以上三个方法之间没有数据依赖关系，所以执行方式由串行调整为并行后，能够达到性能提升的
    效果。如果方法之间存在依赖关系时，那么提升效果是否还会如此明显，并且如何确保它们的执行顺
    序？

### [Reactor](https://projectreactor.io/docs/core/release/reference/#_asynchronicity_to_the_rescue) 认为异步不一定能够救赎

> ### 3.2. Asynchronicity to the Rescue?
>
> The second approach (mentioned earlier), seeking more efficiency, can be a solution to the resource wasting problem. By writing *asynchronous*, *non-blocking* code, you let the execution switch to another active task **using the same underlying resources** and later come back to the current process when the asynchronous processing has finished.
>
> But how can you produce asynchronous code on the JVM? Java offers two models of asynchronous programming:
>
> - **Callbacks**: Asynchronous methods do not have a return value but take an extra `callback` parameter (a lambda or anonymous class) that gets called when the result is available. A well known example is Swing’s `EventListener`hierarchy.
> - **Futures**: Asynchronous methods return a `Future<T>` **immediately**. The asynchronous process computes a `T` value, but the `Future` object wraps access to it. The value is not immediately available, and the object can be polled until the value is available. For instance, `ExecutorService` running `Callable<T>` tasks use `Future` objects.
>
> Are these techniques good enough? Not for every use case, and both approaches have limitations.
>
> Callbacks are hard to compose together, quickly leading to code that is difficult to read and maintain (known as "Callback Hell").
>
> Consider an example: showing the top five favorites from a user on the UI or suggestions if she doesn’t have a favorite. This goes through three services (one gives favorite IDs, the second fetches favorite details, and the third offers suggestions with details):

#### 观点归纳

- Callbacks 是解决非阻塞的方案，然而他们之间很难组合，并且快速地将代码引导至 "Callback Hell"
  的不归路
- Futures 相对于 Callbacks 好一点，不过还是无法组合，不过   CompletableFuture 能够提升这方面
  的不足

#### 理解 "Callback Hell"

- Java GUI 示例

```java
package cn.sjsdfg.spring.reactive;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Joe on 2019/4/24.
 */
public class JavaGUI {
    public static void main(String[] args) {
        JFrame jFrame = new JFrame("GUI 示例");
        jFrame.setBounds(500, 300, 400, 300);
        LayoutManager layoutManager = new BorderLayout(400, 300);
        jFrame.setLayout(layoutManager);
        jFrame.addMouseListener(new MouseAdapter() { // callback 1
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.printf("[线程 : %s] 鼠标点击，坐标(X : %d, Y : %d)\n",
                        currentThreadName(), e.getX(), e.getY());
            }
        });
        jFrame.addWindowListener(new WindowAdapter() { // callback 2
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.printf("[线程 : %s] 清除 jFrame... \n", currentThreadName());
                jFrame.dispose(); // 清除 jFrame
            }
            @Override
            public void windowClosed(WindowEvent e) {
                System.out.printf("[线程 : %s] 退出程序... \n", currentThreadName());
                System.exit(0); // 退出程序
            }
        });
        System.out.println("当前线程：" + currentThreadName());
        jFrame.setVisible(true);
    }
    private static String currentThreadName() { // 当前线程名称
        return Thread.currentThread().getName();
    }
}
```

##### 结论

> Java GUI 以及事件/监听模式基本采用匿名内置类实现，即回调实现。从本例可以得出，鼠标的点击
> 确实没有被其他线程给阻塞。不过当监听的维度增多时，Callback 实现也随之增多。同时，事件/监
> 听者模式的并发模型可为同步或异步。
>
> > 回顾
> > Spring 事件/监听器（同步/异步）：
> > - 事件：  ApplicationEvent
> > - 事件监听器： ApplicationListener
> > - 事件广播器： ApplicationEventMulticaster
> > - 事件发布器： ApplicationEventPublisher
> > 
> > Servlet 事件/监听器
> > - 同步
> >   - 事件： ServletContextEvent
> >   - 事件监听器： ServletContextListener
> > - 异步
> >   - 事件： AsyncEvent
> >   - 事件监听器： AsyncListener

#### 理解 Future 阻塞问题

```java
public class FutureBlockingDataLoader extends DataLoader {
    protected void doLoad() {
        ExecutorService executorService = Executors.newFixedThreadPool(3); // 创建线程池
        runCompletely(executorService.submit(super::loadConfigurations));
        runCompletely(executorService.submit(super::loadUsers));
        runCompletely(executorService.submit(super::loadOrders));
        executorService.shutdown();
    }
    private void runCompletely(Future<?> future) {
        try {
            future.get();
        } catch (Exception e) {
        }
    }
    public static void main(String[] args) {
        new FutureBlockingDataLoader().load();
    }
}
```

##### 结论

> Future#get() 方法不得不等待任务执行完成，换言之，如果多个任务提交后，返回的多个 Future
> 逐一调用  get() 方法时，将会依次 blocking，任务的执行从并行变为串行。这也是之前 ”“延伸思考”
> 问答 2 的答案：
>
> > 为什么不直接使用  Future#get() 方法强制所有任务执行完毕，然后再统计总耗时？

#### 理解 Future 链式问题

由于  Future 无法实现异步执行结果链式处理，尽管  FutureBlockingDataLoader 能够解决方法数据依赖
以及顺序执行的问题，不过它将并行执行带回了阻塞（串行）执行。所以，它不是一个理想实现。不过
CompletableFuture 可以帮助提升  Future 的限制：

```java
public class ChainDataLoader extends DataLoader {
    protected void doLoad() {
        CompletableFuture
                .runAsync(super::loadConfigurations)
                .thenRun(super::loadUsers)
                .thenRun(super::loadOrders)
                .whenComplete((result, throwable) -> { // 完成时回调
                    System.out.println("加载完成");
                })
                .join(); // 等待完成
    }
    public static void main(String[] args) {
        new ChainDataLoader().load();
    }
}
```

输出为：

```java
[线程 : ForkJoinPool.commonPool-worker-1] loadConfigurations() 耗时 : 1001 毫秒
[线程 : ForkJoinPool.commonPool-worker-1] loadUsers() 耗时 : 2000 毫秒
[线程 : ForkJoinPool.commonPool-worker-1] loadOrders() 耗时 : 3001 毫秒
加载完成
load() 总耗时：6080 毫秒
```

**图解**

![chain-loader](img\chain-loader.png)

##### 结论

1. 如果阻塞导致性能瓶颈和资源浪费的话，Reactive 也能解决这个问题？
2. CompletableFuture 属于异步操作，如果强制等待结束的话，又回到了阻塞编程的方式，那么
   Reactive 也会面临同样的问题吗？
3. CompletableFuture 让我们理解到非阻塞不一定提升性能，那么 Reactive 也会这样吗？

#### [Reactive Streams JVM](https://github.com/reactive-streams/reactive-streams-jvm#goals-design-and-scope) 认为异步系统和资源消费需要特殊处理

> Handling streams of data—especially “live” data whose volume is not predetermined—requires
> special care in an asynchronous system. The most prominent issue is that resource
> consumption needs to be carefully controlled such that a fast data source does not overwhelm
> the stream destination. Asynchrony is needed in order to enable the parallel use of computing
> resources, on collaborating network hosts or multiple CPU cores within a single machine.

观点归纳：

- 流式数据容量难以预判
- 异步编程复杂
- 数据源和消费端之间资源消费难以平衡

思考

- Reactive 到底是什么？
- Reactive 的使用场景在哪里？
- Reactive 存在怎样限制/不足？

### Reactive Programming 定义

#### [The Reactive Manifesto](https://www.reactivemanifesto.org/)

> Reactive Systems are: Responsive, Resilient, Elastic and Message Driven.
>
> > https://www.reactivemanifesto.org/

关键字：

- 响应的（Responsive）

- 适应性强的（Resilient）

- 弹性的（Elastic）

- 消息驱动的（Message Driven）

侧重点：

- 面向 Reactive 系统
- Reactive 系统原则

#### [维基百科](https://en.wikipedia.org/wiki/Reactive_programming)

> Reactive programming is a declarative programming paradigm concerned with data streams
> and the propagation of change. With this paradigm it is possible to express static (e.g. arrays)
> or dynamic (e.g. event emitters) data streams with ease, and also communicate that an
> inferred dependency within the associated execution model exists, which facilitates the
> automatic propagation of the changed data flow.
>
> > https://en.wikipedia.org/wiki/Reactive_programming

关键字：

- 数据流（data streams ）
- 传播变化（ propagation of change）

侧重点：

- 数据结构

  - 数组（arrays）

  - 事件发射器（event emitters）

- 数据变化

技术连接：

- 数据流：Java 8  Stream
- 传播变化：Java  Observable / Observer
- 事件：Java  EventObject / EventListener

#### [Spring Framework](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-why-reactive)

> The term "reactive" refers to programming models that are built around reacting to change
> — network component reacting to I/O events, UI controller reacting to mouse events, etc. In
> that sense non-blocking is reactive because instead of being blocked we are now in the mode
> of reacting to notifications as operations complete or data becomes available.
>
> > https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#
> > webflux-why-reactive

关键字：

- 变化响应（reacting to change ）
- 非阻塞（non-blocking）

侧重点：

- 响应通知
  - 操作完成（operations complete）
  - 数据可用（data becomes available）

技术连接：

- 非阻塞：Servlet 3.1 ReadListener / WriteListener
- 响应通知：Servlet 3.0  AsyncListener

#### [ReactiveX](http://reactivex.io/intro.html)

> ReactiveX extends the observer pattern to support sequences of data and/or events and adds
> operators that allow you to compose sequences together declaratively while abstracting away
> concerns about things like low-level threading, synchronization, thread-safety, concurrent data
> structures, and non-blocking I/O.
>
> > http://reactivex.io/intro.html

关键字：

- 观察者模式（Observer pattern ）
- 数据/事件序列（Sequences of data and/or events )
- 序列操作符（Opeators）
- 屏蔽并发细节（abstracting away…）

侧重点：

- 设计模式
- 数据结构
- 数据操作
- 并发模型

技术连接：

- 观察者模式：Java  Observable / Observer
- 数据/事件序列：Java 8  Stream
- 数据操作：Java 8  Stream
- 屏蔽并发细节（abstracting away…）： Exectuor 、 Future 、 Runnable

#### [Reactor](https://projectreactor.io/docs/core/release/reference/#intro-reactive)

>  The reactive programming paradigm is often presented in object-oriented languages as an
> extension of the Observer design pattern. One can also compare the main reactive streams
> pattern with the familiar Iterator design pattern, as there is a duality to the Iterable-Iterator
> pair in all of these libraries. One major difference is that, while an Iterator is pull-based,
> reactive streams are push-based.
>
> > http://projectreactor.io/docs/core/release/reference/#intro-reactive

关键字：

- 观察者模式（Observer pattern ）
- 响应流模式（Reactive streams pattern ）
- 迭代器模式（Iterator pattern）
- 拉模式（pull-based）
- 推模式（push-based）

侧重点：

- 设计模式
- 数据获取方式

技术连接：

- 观察者模式：Java  Observable / Observer
- 响应流模式：Java 8  Stream
- 迭代器模式：Java Iterator

#### [@andrestaltz](https://twitter.com/andrestaltz)

> [Reactive programming is programming with asynchronous data streams.](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754#reactive-programming-is-programming-with-asynchronous-data-streams)
>
> In a way, this isn't anything new. Event buses or your typical click events are really an
> asynchronous event stream, on which you can observe and do some side effects. Reactive is
> that idea on steroids. You are able to create data streams of anything, not just from click and
> hover events. Streams are cheap and ubiquitous, anything can be a stream: variables, user
> inputs, properties, caches, data structures, etc.
>
> > https://gist.github.com/staltz/868e7e9bc2a7b8c1f754#what-is-reactive-programming

关键字：

- 异步（asynchronous ）
- 数据流（data streams）
- 并非新鲜事物（not anything new）
- 过于理想化（idea on steroids）

侧重点：

- 并发模型
- 数据结构
- 技术本质

技术连接：

- 异步：Java  Future
- 数据流：Java 8  Stream

