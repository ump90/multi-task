package com.baidu.unbiz.multitask.constants;

/**
 * Created by wangchongjie on 15/12/22.
 */
public interface ThreadPoolConfig extends TaskConfig {

    long taskTimeoutMillSeconds();
    int queueFullSleepTime();
    int maxCacheTaskNum();
    int coreTaskNum();
    int maxTaskNum();
}
