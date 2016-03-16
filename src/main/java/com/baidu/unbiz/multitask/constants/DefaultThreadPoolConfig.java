package com.baidu.unbiz.multitask.constants;

/**
 * Created by wangchongjie on 15/12/22.
 */
public class DefaultThreadPoolConfig implements ThreadPoolConfig {

    public int coreTaskNum() {
        return CORE_TASK_NUM;
    }

    public int maxTaskNum() {
        return MAX_TASK_NUM;
    }

    public int maxCacheTaskNum() {
        return MAX_CACHE_TASK_NUM;
    }

    public int queueFullSleepTime() {
        return QUEUE_FULL_SLEEP_TIME;
    }

    public long taskTimeoutMillSeconds() {
        return TASK_TIMEOUT_MILL_SECONDS;
    }
}
