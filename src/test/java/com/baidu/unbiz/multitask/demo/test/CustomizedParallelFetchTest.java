package com.baidu.unbiz.multitask.demo.test;

import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.task.CustomizedParallelExePool;
import com.baidu.unbiz.multitask.task.thread.MultiResult;
import com.baidu.unbiz.multitask.vo.DeviceRequest;
import com.baidu.unbiz.multitask.vo.DeviceViewItem;
import com.baidu.unbiz.multitask.vo.QueryParam;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public class CustomizedParallelFetchTest {

    @Resource(name = "simpleParallelExePool")
    private CustomizedParallelExePool parallelExePool;

    /**
     * 通过客户端线程池并行查询测试
     */
    @Test
    public void testParallelFetchByClientExecutorPool() {

        ExecutorService threadPool =
                new ThreadPoolExecutor(2, 5, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        QueryParam qp = new QueryParam();
        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp));

        MultiResult ctx =
                parallelExePool.submit(
                        threadPool,
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
     * 通过客户端线程池并行查询测试,同时框架自带线程池也执行
     */
    @Test
    public void testParallelFetchByClientExecutorPoolWithOrigalPoolToghther() {

        ExecutorService threadPool =
                new ThreadPoolExecutor(2, 5, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        QueryParam qp = new QueryParam();

        MultiResult ctx1 =
                parallelExePool.submit(
                        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                        new TaskPair("deviceUvFetcher#1", DeviceRequest.build(qp)),
                        new TaskPair("deviceUvFetcher#2", DeviceRequest.build(qp)),
                        new TaskPair("deviceUvFetcher#3", DeviceRequest.build(qp)));

        MultiResult ctx2 =
                parallelExePool.submit(
                        threadPool,
                        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                        new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)));

        List<DeviceViewItem> stat1 = ctx1.getResult("deviceStatFetcher");
        List<DeviceViewItem> uv11 = ctx1.getResult("deviceUvFetcher#1");
        List<DeviceViewItem> uv12 = ctx1.getResult("deviceUvFetcher#2");
        List<DeviceViewItem> uv13 = ctx1.getResult("deviceUvFetcher#3");
        List<DeviceViewItem> stat2 = ctx2.getResult("deviceStatFetcher");
        List<DeviceViewItem> uv2 = ctx2.getResult("deviceUvFetcher");

        Assert.notEmpty(stat1);
        Assert.notEmpty(uv11);
        Assert.notEmpty(uv12);
        Assert.notEmpty(uv13);
        Assert.notEmpty(stat2);
        Assert.notEmpty(uv2);
        System.out.println(stat2);
        System.out.println(uv2);
    }
}
