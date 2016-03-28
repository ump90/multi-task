package com.baidu.unbiz.multitask.task.thread;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multitask.exception.TaskTimeoutException;
import com.sun.tools.javac.util.Assert;

/**
 * 一组工作单元,可视为一组原子操作
 *
 * @author wangchongjie
 * @since 2014-12-3 下午3:56:36
 */
public class WorkUnit {

    private static final Log LOG = LogFactory.getLog(TaskManager.class);

    private CompletionService<Void> completion;
    private Map<Future<?>, Runnable> futureMap = new ConcurrentHashMap<Future<?>, Runnable>();
    private int taskCount;
    private Thread parentThread;

    /**
     * 构造函数，线程池委托给调用方
     *
     * @param pool
     */
    public WorkUnit(Executor pool) {
        parentThread = Thread.currentThread();
        completion = new ExecutorCompletionService<Void>(pool);
    }

    /**
     * 提交可并行的任务
     *
     * @param runnable
     */
    public void submit(Runnable runnable) {
        futureMap.put(completion.submit(runnable, null), runnable);
        taskCount++;
    }

    /**
     * 等待结果返回，带默认超时时间
     */
    public void waitForCompletion() {
        this.waitForCompletion(TaskManager.config().taskTimeoutMillSeconds());
    }

    /**
     * 等待结果返回，显示指定超时时间，若超时则取消任务并释放资源
     */
    public void waitForCompletion(long timeoutMillSeconds) {
        for (int i = 0; i < taskCount; i++) {
            Future<Void> future;
            try {
                long timeout =
                        timeoutMillSeconds > 0 ? timeoutMillSeconds : TaskManager.config().taskTimeoutMillSeconds();
                future = completion.poll(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                LOG.error("wait for execute completion failed,e=" + e, e);
                throw new TaskTimeoutException(e);
            }

            if (future == null) {
                this.cancelAllTask();
                String errMsg = "wait for execute completion timeout: ";
                LOG.error(errMsg + this.futureInfo());
                this.cleanResource();
                throw new TaskTimeoutException(errMsg);
            } else {
                futureMap.remove(future);
            }
        }
        // 此处将Java引用置为null，JVM GC时能尽快回收，非操作系统层面的资源释放。
        // 未调用或未调用成功对系统及资源释放无影响，当前线程执行完后，JVM仍可回收。
        this.cleanResource();
    }

    /**
     * 取消所有任务
     */
    private void cancelAllTask() {
        for (Future<?> future : futureMap.keySet()) {
            boolean isCancel = future.cancel(true);
            LOG.info("cancel task success: " + isCancel + ": " + futureMap.get(future));
        }
    }

    /**
     * 任务信息，供异常时打印堆栈信息
     *
     * @return 全部任务信息
     */
    private String futureInfo() {
        StringBuilder sb = new StringBuilder();
        for (Runnable task : futureMap.values()) {
            sb.append(task.toString());
        }
        return sb.toString();
    }

    /**
     * 释放资源，供JVM回收
     */
    private void cleanResource() {
        this.futureMap = null;
        this.completion = null;
    }

}