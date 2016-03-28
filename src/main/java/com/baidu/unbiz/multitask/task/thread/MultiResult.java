package com.baidu.unbiz.multitask.task.thread;

import java.util.Map;

/**
 * 执行结果上下文
 */
public interface MultiResult {

    /**
     * 结果数据存储
     *
     * @param key
     * @param value
     */
    <E> void putResult(String key, E value);

    /**
     * 获取并行执行的数据结果
     *
     * @param taskName
     *
     * @return fetcher对应的数据结果
     */
    <E> E getResult(String taskName);

    /**
     * 获取所有结果
     *
     * @return 所有并行结果
     */
    Map<String, Object> getResult();

    /**
     * 资源清理，尽早释放，非必须
     */
    void clean();

}
