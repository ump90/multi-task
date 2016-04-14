package com.baidu.unbiz.multitask.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.constants.TaskConfig;
import com.baidu.unbiz.multitask.constants.ThreadPoolConfig;
import com.baidu.unbiz.multitask.forkjoin.ForkJoin;
import com.baidu.unbiz.multitask.log.AopLogFactory;
import com.baidu.unbiz.multitask.policy.DefautExecutePolicy;
import com.baidu.unbiz.multitask.policy.ExecutePolicy;
import com.baidu.unbiz.multitask.task.thread.MultiResult;
import com.baidu.unbiz.multitask.task.thread.TaskContext;
import com.baidu.unbiz.multitask.task.thread.TaskManager;
import com.baidu.unbiz.multitask.task.thread.TaskWrapper;
import com.baidu.unbiz.multitask.task.thread.WorkUnit;

/**
 * 报表基础工具类
 *
 * @author wangchongjie
 * @since 2015-1-15 下午3:08:21
 */
@Component
public class SimpleParallelExePool extends AbstractParallelExePool implements CustomizedParallelExePool {

    protected static final Logger LOG = AopLogFactory.getLogger(SimpleParallelExePool.class);


    /**
     * 并行获取报表数据，计算结果保存在context中
     *
     * @param taskPairs 查询方法及参数
     * @return TaskContext
     */
    public MultiResult submit(List<TaskPair> taskPairs) {
        // 使用默认超时时间fetch数据
        return submit(DefautExecutePolicy.instance(), taskPairs.toArray(new TaskPair[] {}));
    }

    /**
     * 并行获取报表数据，计算结果保存在context中
     *
     * @param taskPairs 查询方法及参数
     * @return TaskContext
     */
    public MultiResult submit(TaskPair... taskPairs) {
        // 使用默认超时时间fetch数据
        return submit(DefautExecutePolicy.instance(), taskPairs);
    }

    /**
     * 并行获取报表数据，计算结果保存在context中 可指定单个task的的timeout时间,单位为毫秒
     *
     * @param policy 执行策略，超时时间等
     * @param taskPairs 查询方法及参数
     * @return TaskContext
     */
    public MultiResult submit(ExecutePolicy policy, TaskPair... taskPairs) {
        return this.submit(null, policy, taskPairs);

    }

    /**
     * 并行获取报表数据，计算结果保存在context中 可指定单个task的的timeout时间,单位为毫秒
     *
     * @param executor 线程池
     * @param taskPairs 查询方法及参数
     * @return TaskContext
     */
    public MultiResult submit(Executor executor, TaskPair... taskPairs) {
        return this.submit(executor, DefautExecutePolicy.instance(), taskPairs);
    }

    /**
     * 并行获取报表数据，计算结果保存在context中 可指定单个task的的timeout时间,单位为毫秒
     *
     * @param policy 执行策略，超时时间等
     * @param taskPairs 查询方法及参数
     * @return TaskContext
     */
    public MultiResult submit(Executor executor, ExecutePolicy policy, TaskPair... taskPairs) {

        TaskContext context = TaskContext.newContext();
        beforeSubmit(context, policy, taskPairs);

        TaskPair[] lTaskPairs = context.getAttribute(TASK_PAIRS);
        List<TaskWrapper> fetchers = TaskWrapper.wrapperFetcher(container, context, lTaskPairs);

        WorkUnit workUnit = TaskManager.newWorkUnit(executor);
        context.copyAttachedthreadLocalValues();

        for (TaskWrapper fetcher : fetchers) {
            workUnit.submit(fetcher);
        }
        onSubmit(context, policy, taskPairs);

        workUnit.waitForCompletion(policy.taskTimeout());
        postSubmit(context, policy, taskPairs);
        return context;
    }

    /**
     * 简单fork join实现
     *
     * @param taskPair
     * @param forkJoin
     * @param <PARAM>
     * @param <RESULT>
     *
     * @return RESULT
     */
    public <PARAM, RESULT> RESULT submit(TaskPair taskPair, ForkJoin<PARAM, RESULT> forkJoin) {
        return submit(DefautExecutePolicy.instance(), taskPair, forkJoin);
    }

    /**
     * 简单fork join实现
     *
     * @param taskPair
     * @param forkJoin
     * @param <PARAM>
     * @param <RESULT>
     *
     * @return RESULT
     */
    public <PARAM, RESULT> RESULT submit(ExecutePolicy policy, TaskPair taskPair, ForkJoin<PARAM, RESULT> forkJoin) {

        List<PARAM> params = forkJoin.fork((PARAM) taskPair.field2);
        if (CollectionUtils.isEmpty(params)) {
            return null;
        }
        int version = 0;
        List<TaskPair> taskPairs = new ArrayList<TaskPair>();
        for (PARAM param : params) {
            taskPairs.add(new TaskPair(taskPair.field1 + TaskConfig.TASKNAME_SEPARATOR + version++, param));
        }
        TaskContext ctx = (TaskContext) this.submit(policy, taskPair);

        List<RESULT> results = new ArrayList<RESULT>();
        for (String taskName : ctx.getResult().keySet()) {
            results.add((RESULT) ctx.getResult(taskName));
        }
        return forkJoin.join(results);
    }

    /**
     * 初始化线程池配置，缺省值为DefaultThreadPoolConfig
     *
     * @param tpConfig 线程池配置
     */
    public SimpleParallelExePool(ThreadPoolConfig tpConfig) {
        super(tpConfig);
    }

    public SimpleParallelExePool() {
    }

}
