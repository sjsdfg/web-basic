package cn.sjsdfg.spring.reactive.reactor;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Created by Joe on 2019/4/25.
 */
public class FluxDemo {
    public static void main(String[] args) throws Exception {
        Flux.just("A", "B", "C") // 发布数据 A -> B -> C
                .publishOn(Schedulers.elastic()) // 线程池切换
                .map(old -> "+" + old) // 转换
                .subscribe(FluxDemo::print, // 消费 = onNext
                        FluxDemo::print, // 异常 = onError
                        () -> // 完成回调 = onComplete
                                System.out.println("[线程：" + Thread.currentThread().getName() + "] " + "操作完成")
                        , subscription ->  {
                            subscription.request(2); // 参数为请求的元素数量
                        }// 背压操作
                );

        Thread.sleep(1000);
        print("hello");
    }

    private static void print(Object object) {
        String threadName = Thread.currentThread().getName();
        System.out.println("[线程：" + threadName + "] " + object);
    }
}
