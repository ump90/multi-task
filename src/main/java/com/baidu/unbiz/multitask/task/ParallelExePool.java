package com.baidu.unbiz.multitask.task;

import java.util.List;

import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.forkjoin.ForkJoin;
import com.baidu.unbiz.multitask.task.thread.TaskContext;
import com.baidu.unbiz.multitask.policy.ExecutePolicy;

public interface ParallelExePool {

    TaskContext submit(List<TaskPair> taskPairs);

    TaskContext submit(TaskPair... taskPairs);

    TaskContext submit(ExecutePolicy policy, TaskPair... taskPairs);

    <PARAM, RESULT> RESULT submit(TaskPair taskPair, ForkJoin<PARAM, RESULT> forkJoin);

    <PARAM, RESULT> RESULT submit(ExecutePolicy policy, TaskPair taskPair, ForkJoin<PARAM, RESULT> forkJoin);
}