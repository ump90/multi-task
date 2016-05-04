package com.baidu.unbiz.multitask.policy;

import com.baidu.unbiz.multitask.constants.TaskConfig;

/**
 * Created by wangchongjie on 15/12/22.
 */
public class DefautExecutePolicy implements ExecutePolicy {

    private long taskTimeout = TaskConfig.NOT_LIMIT;

    /**
     * @return 任务超时时间，单位毫秒
     */
    public long taskTimeout() {
        return taskTimeout;
    }

    public DefautExecutePolicy setTaskTimeout(long taskTimeout) {
        this.taskTimeout = taskTimeout;
        return this;
    }

    public DefautExecutePolicy() {

    }

    public static DefautExecutePolicy instance() {
        return InstanceHolder.instance;
    }

    public static class InstanceHolder {
        public static DefautExecutePolicy instance = new DefautExecutePolicy();
    }
}
