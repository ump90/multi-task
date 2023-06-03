package com.baidu.unbiz.multitask.policy;


/**
 * Created by wangchongjie on 15/12/22.
 */
public class TimeoutPolicy extends DefautExecutePolicy {

    private long timeout;

    /**
     * @return 任务超时时间，单位毫秒
     */
    public long taskTimeout() {
        return timeout;
    }

    /**
     * 单位毫秒
     * @param timeout
     */
    public TimeoutPolicy(long timeout) {
        this.timeout = timeout;
    }
}
