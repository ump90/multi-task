package com.baidu.unbiz.multitask.task;

import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.policy.ExecutePolicy;
import com.baidu.unbiz.multitask.task.thread.MultiResult;
import java.util.concurrent.Executor;

public interface CustomizedParallelExePool extends ParallelExePool {

    MultiResult submit(Executor executor, TaskPair... taskPairs);

    MultiResult submit(Executor executor, ExecutePolicy policy, TaskPair... taskPairs);
}