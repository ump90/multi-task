package com.baidu.unbiz.multitask.task;

/**
 * 并行数据抓取通用接口
 * 
 * @author wangchongjie
 * @since 2014-11-21 下午7:50:10
 */
public interface Taskable<T> {

    /**
     * 执行数据获取
     * 
     * @param request
     * @return 返回结果
     * @since 2015-7-3 by wangchongjie
     */
    <E> T work(E request);

}
