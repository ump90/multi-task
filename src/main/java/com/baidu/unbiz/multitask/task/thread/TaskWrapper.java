package com.baidu.unbiz.multitask.task.thread;

import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.spring.integration.TaskBeanContainer;
import com.baidu.unbiz.multitask.task.Taskable;
import com.baidu.unbiz.multitask.utils.ArrayUtils;
import com.baidu.unbiz.multitask.utils.AssistUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 报表数据抓取类包装器，包装为可执行类
 *
 * @author wangchongjie
 * @since 2015-11-21 下午7:57:58
 */
public class TaskWrapper implements Runnable {

    /**
     * 可并行执行的fetcher
     */
    private Taskable<?> fetcher;

    /**
     * fetcher名称
     */
    private String fetcherName;

    /**
     * 参数封装
     */
    private Object args;

    /**
     * 并行执行上下文
     */
    private TaskContext context;

    /**
     * 将Fetcher包装，绑定执行上下文
     *
     * @param fetcher
     * @param args
     * @param context
     */
    public <T> TaskWrapper(Taskable<?> fetcher, Object args, TaskContext context) {
        this.fetcher = fetcher;
        // 此处参数有可能为null(暂不限制)，应用时需注意
        this.args = args;
        this.context = context;
    }

    public <T> TaskWrapper(Taskable<?> fetcher, Object args, TaskContext context, String fetcherName) {
        this.fetcher = fetcher;
        // 此处参数有可能为null(暂不限制)，应用时需注意
        this.args = args;
        this.context = context;
        this.fetcherName = fetcherName;
    }

    /**
     * 将Fetcher构建成包装类
     *
     * @param container 
     * @param context
     * @param queryPairs
     *
     * @return TaskWrapper List
     */
    public static List<TaskWrapper> wrapperFetcher(TaskBeanContainer container, TaskContext context,
                                             TaskPair... queryPairs) {

        List<TaskWrapper> fetchers = new ArrayList<TaskWrapper>();
        if (ArrayUtils.isArrayEmpty(queryPairs)) {
            return fetchers;
        }
        // wrap task
        for (TaskPair qp : queryPairs) {
            Taskable<?> fetcher = container.bean(AssistUtils.removeTaskVersion(qp.field1));
            fetchers.add(new TaskWrapper(fetcher, qp.field2, context, qp.field1));
        }
        return fetchers;
    }

    private String defaultName() {
        return fetcher.getClass().getName();
    }

    public String fetcherName() {
        return fetcherName != null ? fetcherName : defaultName();
    }

    public void setFetcherName(String fetcherName) {
        this.fetcherName = fetcherName;
    }

    @Override
    public String toString() {
        return fetcherName() + "\t" + args;
    }

    @Override
    public int hashCode() {
        int hashCode = fetcher.hashCode() + context.hashCode();
        if (this.args != null) {
            hashCode += args.hashCode();
        }
        return hashCode;
    }

    /**
     * 执行方法
     */
    @Override
    public void run() {
        try {
            Set<ThreadLocal> tls = context.attachedThreadLocals();
            for (ThreadLocal tl : tls) {
                tl.set(context.attachedthreadLocalValues().get(tl));
            }
            context.putResult(fetcherName(), fetcher.work(args));
        } catch (Exception ex) {
            context.throwException(fetcherName(), ex);
        }
    }
}
