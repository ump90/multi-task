package com.baidu.unbiz.multitask.policy;

/**
 * Created by wangchongjie on 15/12/22.
 */
public interface ExecutePolicy {
    /**
     * @return 任务超时时间，单位毫秒
     */
    long taskTimeout();
}
