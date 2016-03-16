package com.baidu.unbiz.multitask.constants;

/**
 * Created by wangchongjie on 15/12/21.
 */
public interface TaskConfig {

    long TASK_TIMEOUT_MILL_SECONDS = 15 * 1000L;
    int QUEUE_FULL_SLEEP_TIME = 20;
    int MAX_CACHE_TASK_NUM = 2;
    int CORE_TASK_NUM = Math.min(15, Runtime.getRuntime().availableProcessors());
    int MAX_TASK_NUM = Math.max(20, Runtime.getRuntime().availableProcessors());

    char TASKNAME_SEPARATOR = '#';
    long NOT_LIMIT = -1;
}
