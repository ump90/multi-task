package com.baidu.unbiz.multitask.task.thread;

import com.baidu.unbiz.multitask.exception.TaskBizException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 并行抓取上线文环境
 *
 * @author wangchongjie
 * @since 2015-11-21 下午7:58:49
 */
public class TaskContext implements MultiResult {

    private static final Log LOG = LogFactory.getLog(TaskContext.class);

    /**
     * 启用的ThreadLocal
     * 将在task执行时生效
     */
    private static Set<ThreadLocal> threadLocalSet = new HashSet<ThreadLocal>();
    /**
     * 一组结果数据仓库
     */
    private Map<String, Object> result = new ConcurrentHashMap<String, Object>();
    /**
     * 一组结果Exception仓库
     */
    private Map<String, Exception> exception = new ConcurrentHashMap<String, Exception>();
    /**
     * 启用ThreadLocal后，一组任务Task执行前的镜像值
     */
    private Map<ThreadLocal, Object> threadLocalValues = new HashMap<ThreadLocal, Object>();

    /**
     * 执行环境上下文属性
     */
    private Map<String, Object> attribute = new ConcurrentHashMap<String, Object>();

    /**
     * 结果数据存储
     *
     * @param key
     * @param value
     */
    public <E> void putResult(String key, E value) {
        if (value != null) {
            result.put(key, value);
        }
    }

    /**
     * 并行执行时上抛Exception
     *
     * @param key
     * @param value
     */
    public void throwException(String key, Exception value) {
        exception.put(key, value);
    }

    /**
     * 获取并行执行的数据结果
     *
     * @param taskName
     *
     * @return fetcher对应的数据结果
     */
    @SuppressWarnings("unchecked")
    public <E> E getResult(String taskName) {
        Exception ex = exception.get(taskName);
        if (ex != null) {
            LOG.error("Execute fail:", ex);
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new TaskBizException(ex);
            }
        }
        return (E) result.get(taskName);
    }

    /**
     * 获取所有结果
     *
     * @return 所有并行结果
     */
    public Map<String, Object> getResult() {
        return result;
    }

    /**
     * 资源清理，非必须
     */
    public void clean() {
        if (result != null) {
            result.clear();
            result = null;
            exception.clear();
            exception = null;
            threadLocalValues.clear();
            threadLocalValues = null;
        }
    }

    /**
     * 禁止直接实例化，以便后续内部扩展
     */
    private TaskContext() {
    }

    /**
     * 工厂方法，方便后续组装扩展
     *
     * @return TaskContext
     */
    public static TaskContext newContext() {
        return new TaskContext();
    }

    public static void attachThreadLocal(ThreadLocal threadLocal) {
        threadLocalSet.add(threadLocal);
    }

    public static void detachThreadLocal(ThreadLocal threadLocal) {
        threadLocalSet.remove(threadLocal);
    }

    public static Set<ThreadLocal> attachedThreadLocals() {
        return threadLocalSet;
    }

    public Map<ThreadLocal, Object> attachedthreadLocalValues() {
        return threadLocalValues;
    }

    public void copyAttachedthreadLocalValues() {
        for (ThreadLocal tl : threadLocalSet) {
            threadLocalValues.put(tl, tl.get());
        }
    }

    public <T> T getAttribute(String key) {
        return (T) attribute.get(key);
    }

    public <T> TaskContext putAttribute(String key, T value) {
        attribute.put(key, value);
        return this;
    }

}
