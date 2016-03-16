package com.baidu.unbiz.multitask.common;

/**
 * 一个可并行的任务，<taskName, param></>
 * 
 * @author wangchongjie
 * @fileName InteralTaskPair.java
 * @dateTime 2015-7-15 下午7:10:47
 */
public class TaskPair extends Pair<String, Object> {

    public TaskPair(String taskName, Object param) {
        this.field1 = taskName;
        this.field2 = param;
    }
}
