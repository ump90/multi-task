package com.baidu.unbiz.multitask.task;

import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.constants.ThreadPoolConfig;
import com.baidu.unbiz.multitask.log.AopLogFactory;
import com.baidu.unbiz.multitask.policy.ExecutePolicy;
import com.baidu.unbiz.multitask.spring.integration.TaskBeanContainer;
import com.baidu.unbiz.multitask.task.thread.TaskContext;
import com.baidu.unbiz.multitask.task.thread.TaskManager;
import javax.annotation.Resource;
import org.slf4j.Logger;

/**
 * 报表基础工具类
 *
 * @author wangchongjie
 * @since 2015-8-15 下午3:08:21
 */
public abstract class AbstractParallelExePool implements ParallelExePool {

    protected static final Logger LOG = AopLogFactory.getLogger(AbstractParallelExePool.class);

    @Resource(name = "taskBeanContainer")
    protected TaskBeanContainer container;


    public TaskContext beforeSubmit(TaskContext context, ExecutePolicy policy, TaskPair... taskPairs) {
        return context.putAttribute(TASK_PAIRS, taskPairs);
    }

    public TaskContext onSubmit(TaskContext context, ExecutePolicy policy, TaskPair... taskPairs) {
        return context;
    }

    public TaskContext postSubmit(TaskContext context, ExecutePolicy policy, TaskPair... taskPairs) {
        return context;
    }

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
