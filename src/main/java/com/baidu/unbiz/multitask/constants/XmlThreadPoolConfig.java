package com.baidu.unbiz.multitask.constants;

/**
 * Created by wangchongjie on 15/12/22.
 */
public class XmlThreadPoolConfig implements ThreadPoolConfig {

    private int coreTaskNum = CORE_TASK_NUM;

    private int maxTaskNum = MAX_TASK_NUM;

    private int maxCacheTaskNum = MAX_CACHE_TASK_NUM;

    private int queueFullSleepTime = QUEUE_FULL_SLEEP_TIME;

    private long taskTimeoutMillSeconds = TASK_TIMEOUT_MILL_SECONDS;

    public int coreTaskNum() {
        return coreTaskNum;
    }

    public int maxTaskNum() {
        return maxTaskNum;
    }

    public int maxCacheTaskNum() {
        return maxCacheTaskNum;
    }

    public int queueFullSleepTime() {
        return queueFullSleepTime;
    }

    public long taskTimeoutMillSeconds() {
        return taskTimeoutMillSeconds;
    }

    public long getTaskTimeoutMillSeconds() {
        return taskTimeoutMillSeconds;
    }

    public void setTaskTimeoutMillSeconds(long taskTimeoutMillSeconds) {
        this.taskTimeoutMillSeconds = taskTimeoutMillSeconds;
    }

    public int getCoreTaskNum() {
        return coreTaskNum;
    }

    public void setCoreTaskNum(int coreTaskNum) {
        this.coreTaskNum = coreTaskNum;
    }

    public int getMaxTaskNum() {
        return maxTaskNum;
    }

    public void setMaxTaskNum(int maxTaskNum) {
        this.maxTaskNum = maxTaskNum;
    }

    public int getMaxCacheTaskNum() {
        return maxCacheTaskNum;
    }

    public void setMaxCacheTaskNum(int maxCacheTaskNum) {
        this.maxCacheTaskNum = maxCacheTaskNum;
    }

    public int getQueueFullSleepTime() {
        return queueFullSleepTime;
    }

    public void setQueueFullSleepTime(int queueFullSleepTime) {
        this.queueFullSleepTime = queueFullSleepTime;
    }

}
