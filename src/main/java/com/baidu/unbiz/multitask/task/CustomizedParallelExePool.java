package com.baidu.unbiz.multitask.task;

import java.util.concurrent.Executor;

import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.policy.ExecutePolicy;
import com.baidu.unbiz.multitask.task.thread.MultiResult;

public interface CustomizedParallelExePool extends ParallelExePool {

    MultiResult submit(Executor executor, TaskPair... taskPairs);

    MultiResult submit(Executor executor, ExecutePolicy policy, TaskPair... taskPairs);
}