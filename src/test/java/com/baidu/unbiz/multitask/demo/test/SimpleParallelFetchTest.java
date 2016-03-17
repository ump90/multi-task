package com.baidu.unbiz.multitask.demo.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.exception.BusinessException;
import com.baidu.unbiz.multitask.exception.TaskTimeoutException;
import com.baidu.unbiz.multitask.forkjoin.ForkJoin;
import com.baidu.unbiz.multitask.policy.DefautExecutePolicy;
import com.baidu.unbiz.multitask.policy.ExecutePolicy;
import com.baidu.unbiz.multitask.policy.TimeoutPolicy;
import com.baidu.unbiz.multitask.task.ParallelExePool;
import com.baidu.unbiz.multitask.task.Params;
import com.baidu.unbiz.multitask.task.thread.TaskContext;
import com.baidu.unbiz.multitask.vo.DeviceRequest;
import com.baidu.unbiz.multitask.vo.DeviceViewItem;
import com.baidu.unbiz.multitask.vo.QueryParam;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public class SimpleParallelFetchTest {

    @Resource(name = "simpleParallelExePool")
    private ParallelExePool parallelExePool;

    /**
     * 正常并行查询测试
     */
    @Test
    public void testParallelFetch() {
        QueryParam qp = new QueryParam();

        TaskContext ctx =
                parallelExePool.submit(
                        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                        new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)));

        List<DeviceViewItem> stat = ctx.getResult("deviceStatFetcher");
        List<DeviceViewItem> uv = ctx.getResult("deviceUvFetcher");

        Assert.notEmpty(stat);
        Assert.notEmpty(uv);
        System.out.println(stat);
        System.out.println(uv);
    }

    /**
     * 带显式定义的task的并行查询测试
     */
    @Test
    public void testParallelFetchWithExplicitDefTask() {
        QueryParam qp = new QueryParam();
        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp));

        TaskContext ctx =
                parallelExePool.submit(
                        new TaskPair("explicitDefTask", DeviceRequest.build(qp)),
                        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                        new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)));

        List<DeviceViewItem> def = ctx.getResult("explicitDefTask");
        List<DeviceViewItem> stat = ctx.getResult("deviceStatFetcher");
        List<DeviceViewItem> uv = ctx.getResult("deviceUvFetcher");

        Assert.notEmpty(def);
        Assert.notEmpty(stat);
        Assert.notEmpty(uv);
        System.out.println(def);
    }

    /**
     * 带显式定义的task的并行查询测试
     */
    @Test
    public void testParallelFetchWithMultiParamTask() {
        QueryParam qp = new QueryParam();
        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp));

        Params params = Params.with(3).add("str").add(1).add(2);

        TaskContext ctx =
                parallelExePool.submit(
                        new TaskPair("multiParamFetcher", params.use()),
                        new TaskPair("voidParamFetcher", null),
                        new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)));

        List<DeviceViewItem> def = ctx.getResult("multiParamFetcher");
        List<DeviceViewItem> stat = ctx.getResult("voidParamFetcher");
        List<DeviceViewItem> uv = ctx.getResult("deviceUvFetcher");

        Assert.notEmpty(def);
        Assert.notEmpty(stat);
        Assert.notEmpty(uv);
        System.out.println(def);
    }

    /**
     * 同一个task不同参数并行查询测试
     */
    @Test
    public void testParallelFetchWithSameTaskByDifferentParams() {
        QueryParam qp1 = new QueryParam();
        QueryParam qp2 = new QueryParam();

        TaskContext ctx =
                parallelExePool.submit(
                        new TaskPair("deviceStatFetcher#1", DeviceRequest.build(qp1)),
                        new TaskPair("deviceStatFetcher#2", DeviceRequest.build(qp2)));

        List<DeviceViewItem> stat = ctx.getResult("deviceStatFetcher#1");
        List<DeviceViewItem> uv = ctx.getResult("deviceStatFetcher#2");

        Assert.notEmpty(stat);
        Assert.notEmpty(uv);
        System.out.println(stat);
        System.out.println(uv);
    }

    /**
     * 简单ForkJoin并行查询测试
     */
    @Test
    public void testParallelForkJoinFetch() {
        TaskPair taskPair = new TaskPair("deviceStatFetcher", DeviceRequest.build(new QueryParam()));

        ForkJoin<DeviceRequest, List<DeviceViewItem>> forkJoin = new ForkJoin<DeviceRequest, List<DeviceViewItem>>() {

            public List<DeviceRequest> fork(DeviceRequest deviceRequest) {
                List<DeviceRequest> reqs = new ArrayList<DeviceRequest>();
                reqs.add(deviceRequest);
                reqs.add(deviceRequest);
                reqs.add(deviceRequest);
                return reqs;
            }

            public List<DeviceViewItem> join(List<List<DeviceViewItem>> lists) {
                List<DeviceViewItem> result = new ArrayList<DeviceViewItem>();
                if (CollectionUtils.isEmpty(lists)) {
                    return result;
                }
                for (List<DeviceViewItem> res : lists) {
                    result.addAll(res);
                }
                return result;
            }
        };

        List<DeviceViewItem> result = parallelExePool.submit(taskPair, forkJoin);

        Assert.notEmpty(result);
        System.out.println(result);
    }

    /**
     * 模拟业务异常测试
     */
    @Test(expected = BusinessException.class)
    public void testParallelFetchWithBusinessException() {
        QueryParam qp = new QueryParam();

        TaskContext ctx =
                parallelExePool.submit(
                        new TaskPair("doSthFailWithExceptionFetcher", DeviceRequest.build(qp)),
                        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                        new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)));

        List<DeviceViewItem> out = ctx.getResult("doSthFailWithExceptionFetcher");
        Assert.isNull(out);
    }


    /**
     * 模拟网络异常超时测试
     */
    @Test(expected = TaskTimeoutException.class)
    public void testParallelFetchWithTimeOutExceptionByTimeoutPolicy() {
        QueryParam qp = new QueryParam();

        TaskContext ctx =
                parallelExePool.submit(
                        new TimeoutPolicy(1),
                        new TaskPair("doSthVerySlowFetcher", DeviceRequest.build(qp)),
                        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                        new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)));

        List<DeviceViewItem> out = ctx.getResult("doSthVerySlowFetcher");
        Assert.isNull(out);
    }

    /**
     * 模拟网络异常超时测试
     */
    @Test(expected = TaskTimeoutException.class)
    public void testParallelFetchWithTimeOutExceptionByMyExecutePolicy() {
        QueryParam qp = new QueryParam();

        ExecutePolicy policy = new  DefautExecutePolicy() {
            public long taskTimeout() {
                return 2;
            }
        };

        TaskContext ctx =
                parallelExePool.submit(
                        policy,
                        new TaskPair("doSthVerySlowFetcher", DeviceRequest.build(qp)),
                        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                        new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)));

        List<DeviceViewItem> out = ctx.getResult("doSthVerySlowFetcher");
        Assert.isNull(out);
    }

    /**
     * 模拟网络异常超时测试
     */
    @Test(expected = TaskTimeoutException.class)
    public void testParallelFetchWithTimeOutException() {
        QueryParam qp = new QueryParam();

        TaskContext ctx =
                parallelExePool.submit(
                        new TaskPair("doSthVerySlowFetcher", DeviceRequest.build(qp)),
                        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                        new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)));

        List<DeviceViewItem> out = ctx.getResult("doSthVerySlowFetcher");
        Assert.isNull(out);
    }
}
