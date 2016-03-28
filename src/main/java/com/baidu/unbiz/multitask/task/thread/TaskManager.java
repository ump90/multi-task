package com.baidu.unbiz.multitask.task.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import com.baidu.unbiz.multitask.constants.DefaultThreadPoolConfig;
import com.baidu.unbiz.multitask.constants.ThreadPoolConfig;

/**
 * 报表可并行处理的任务管理器
 *
 * @author wangchongjie
 * @since 2014-11-21 下午7:57:37
 */
public class TaskManager {

    private static final Log LOG = LogFactory.getLog(TaskManager.class);

    private static volatile ThreadPoolConfig config = new DefaultThreadPoolConfig();

    private static volatile BlockingQueue<Runnable> taskQueue =
            new ArrayBlockingQueue<Runnable>(config.maxCacheTaskNum());
    // 此处可以使用信号量来控制饱和策略，以释放资源，但在未饱和状态时也会引入同步开销，且task需配合信号量，
    // 可预期模块大部分时间运行是不饱和的，所以折中考虑，采用线程自旋方式等待队列可用，降低运行开销
    // protected final Semaphore semaphore = new Semaphore(MaxCacheTaskNum);

    private static volatile ThreadPoolExecutor threadPool =
            new CleanableThreadPoolExecutor(config.coreTaskNum(), config.maxTaskNum(), 10, TimeUnit.SECONDS, taskQueue,
                    new CustomizableThreadFactory("multi-task-pool-"), new ThreadPoolExecutor.CallerRunsPolicy());

    static {
        logThreadPoolInfo();
    }

    private static AtomicBoolean refreshed = new AtomicBoolean(false);

    /**
     * 只允许启动前刷新一次，或使用默认配置
     *
     * @param tpConfig
     */
    public static void refreshConfig(ThreadPoolConfig tpConfig) {
        if (refreshed.compareAndSet(false, true)) {
            threadPool.shutdown();
            config = tpConfig;
            taskQueue = new ArrayBlockingQueue<Runnable>(config.maxCacheTaskNum());
            threadPool =
                    new ThreadPoolExecutor(config.coreTaskNum(), config.maxTaskNum(), 10, TimeUnit.SECONDS, taskQueue,
                            new CustomizableThreadFactory("multi-task-pool-"),
                            new ThreadPoolExecutor.CallerRunsPolicy());
            logThreadPoolInfo();
        }
    }

    public static void logThreadPoolInfo() {
        String logFormat =
                "CORE_TASK_NUM:%s MAX_TASK_NUM:%s MAX_CACHE_TASK_NUM:%s "
                        + "TASK_TIMEOUT_MILL_SECONDS:%s AVAILABLE_PROCESSORs:%s";
        LOG.info(String.format(logFormat, config.coreTaskNum(), config.maxTaskNum(), config.maxCacheTaskNum(),
                config.taskTimeoutMillSeconds(), Runtime.getRuntime().availableProcessors()));
    }

    public static ThreadPoolConfig config() {
        return config;
    }

    /**
     * 启动可并行Task
     *
     * @param task
     *
     * @return Task执行的Future
     */
    public <T> Future<T> invoke(Callable<T> task) {
        if (taskQueue.size() > 1) {
            LOG.debug("ReportTaskManager current taskQueue size is:" + taskQueue.size());
        }
        this.checkQueueFullThenSleep();
        // threadPool.execute(task);
        Future<T> result = threadPool.submit(task);
        return result;
    }

    /**
     * 启动可并行Task,不关心结果
     *
     * @param task
     */
    public void invoke(Runnable task) {
        if (taskQueue.size() > 1) {
            LOG.debug("ReportTaskManager current taskQueue size is:" + taskQueue.size());
        }
        this.checkQueueFullThenSleep();
        threadPool.execute(task);
    }

    private void checkQueueFullThenSleep() {
        // 避免queue满后抛异常
        while (taskQueue.size() >= config.maxCacheTaskNum()) {
            LOG.info("TaskPoolManager thread pool blocking queue is full.");
            sleep(config.queueFullSleepTime());
        }
    }

    /**
     * 封装异常
     *
     * @param miliSeconds
     */
    public static final void sleep(int miliSeconds) {
        try {
            Thread.sleep(miliSeconds);
        } catch (InterruptedException e) {
            LOG.error("Thread sleep is interrupted", e);
        }
    }

    /**
     * 构建一组工作单元，为并行执行的原子单位，均运行完成（成功或失败）后返回
     *
     * @return 一组工作单元
     */
    public static WorkUnit newWorkUnit() {
        if (taskQueue.size() > 0) {
            LOG.debug("ReportTaskManager current taskQueue size is:" + taskQueue.size());
        }
        return new WorkUnit(threadPool);
    }

    /**
     * 构建一组工作单元，为并行执行的原子单位，均运行完成（成功或失败）后返回
     *
     * @param executor
     * @return 一组工作单元
     */
    public static WorkUnit newWorkUnit(Executor executor) {
        if (executor == null) {
            return newWorkUnit();
        }
        return new WorkUnit(executor);
    }
}
