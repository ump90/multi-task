package com.baidu.unbiz.multitask.task;

import javax.annotation.Resource;

import org.slf4j.Logger;

import com.baidu.unbiz.multitask.constants.ThreadPoolConfig;
import com.baidu.unbiz.multitask.log.AopLogFactory;
import com.baidu.unbiz.multitask.task.thread.TaskManager;
import com.baidu.unbiz.multitask.spring.integration.TaskBeanContainer;

/**
 * 报表基础工具类
 *
 * @author wangchongjie
 * @fileName ParallelExePool.java
 * @dateTime 2015-1-15 下午3:08:21
 */
public abstract class AbstractParallelExePool implements ParallelExePool {

    protected static final Logger LOG = AopLogFactory.getLogger(AbstractParallelExePool.class);

    @Resource(name = "taskBeanContainer")
    protected TaskBeanContainer container;

    public AbstractParallelExePool() {
    }

    /**
     * 初始化线程池配置，缺省值为DefaultThreadPoolConfig
     *
     * @param tpConfig 线程池配置
     */
    public AbstractParallelExePool(ThreadPoolConfig tpConfig) {
        TaskManager.refreshConfig(tpConfig);
    }

}
