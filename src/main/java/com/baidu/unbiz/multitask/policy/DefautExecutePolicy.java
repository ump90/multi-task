package com.baidu.unbiz.multitask.policy;

import com.baidu.unbiz.multitask.constants.TaskConfig;

/**
 * Created by wangchongjie on 15/12/22.
 */
public class DefautExecutePolicy implements ExecutePolicy {
    /**
     * @return 任务超时时间，单位毫秒
     */
    public long taskTimeout() {
        return TaskConfig.NOT_LIMIT;
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
