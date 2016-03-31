package com.baidu.unbiz.multitask.service;

/**
 * Created by wangchongjie on 16/3/28.
 * 供测试ThreadLocal在MultiTask的任务中跨线程传递
 */
public class MyThreadLocal {

    private static ThreadLocal myThreadLocal = new ThreadLocal();

    public static Object get() {
        return myThreadLocal.get();
    }

    public static void set(Object obj) {
        myThreadLocal.set(obj);
    }

    public static ThreadLocal instance() {
        return myThreadLocal;
    }
}
