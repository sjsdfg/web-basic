package cn.sjsdfg.web.controller;

import cn.sjsdfg.service.DeferredResultQueue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by Joe on 2019/3/27.
 */
@RestController
public class AsyncController {
    @GetMapping("async01")
    public Callable<String> async01() {
        System.out.println(Thread.currentThread() + " 主线程开始 --> " + System.currentTimeMillis());
        Callable<String> callable = () -> {
            System.out.println(Thread.currentThread() + " 子线程正在处理 --> " + System.currentTimeMillis());
            return "Callable<String> async01()";
        };
        System.out.println(Thread.currentThread() + " 主线程结束 --> " + System.currentTimeMillis());
        return callable;
    }

    @GetMapping("/createOrder")
    public DeferredResult<Object> createOrder() {
        DeferredResult<Object> result = new DeferredResult<>(3000L, "create failure");
        DeferredResultQueue.save(result);
        return result;
    }

    @GetMapping("/create")
    public String create() {
        // 创建订单
        String order = UUID.randomUUID().toString();
        DeferredResult<Object> objectDeferredResult = DeferredResultQueue.get();
        objectDeferredResult.setResult(order);
        return "success ===》" + order;
    }

    @GetMapping("/cs")
    public CompletionStage<String> completionStage() {
        final long startTime = System.currentTimeMillis();
        System.out.println(Thread.currentThread() + " 主线程开始 --> " + startTime);

        return CompletableFuture.supplyAsync(() -> {
            long costTime = System.currentTimeMillis() - startTime;
            System.out.println(Thread.currentThread() + " 异步线程 --> " + System.currentTimeMillis());
            System.out.println("Cost time = " + costTime);
            return "Hello CompletionStage";
        });
    }
}
