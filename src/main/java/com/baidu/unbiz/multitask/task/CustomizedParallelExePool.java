package com.baidu.unbiz.multitask.task;

import java.util.concurrent.Executor;

import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.policy.ExecutePolicy;
import com.baidu.unbiz.multitask.task.thread.TaskContext;

public interface CustomizedParallelExePool extends ParallelExePool {

    TaskContext submit(Executor executor, TaskPair... taskPairs);

    TaskContext submit(Executor executor, ExecutePolicy policy, TaskPair... taskPairs);
}