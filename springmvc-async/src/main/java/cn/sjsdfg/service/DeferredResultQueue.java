package cn.sjsdfg.service;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Joe on 2019/3/28.
 */
public class DeferredResultQueue {
    private static Queue<DeferredResult<Object>> queue = new ConcurrentLinkedDeque<>();

    public static void save(DeferredResult<Object> result) {
        queue.add(result);
    }

    public static DeferredResult<Object> get() {
        return queue.poll();
    }
}
