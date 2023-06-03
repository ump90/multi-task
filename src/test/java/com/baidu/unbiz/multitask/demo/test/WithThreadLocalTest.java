package com.baidu.unbiz.multitask.demo.test;

import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.service.MyThreadLocal;
import com.baidu.unbiz.multitask.task.ParallelExePool;
import com.baidu.unbiz.multitask.task.thread.MultiResult;
import com.baidu.unbiz.multitask.task.thread.TaskContext;
import com.baidu.unbiz.multitask.vo.DeviceRequest;
import com.baidu.unbiz.multitask.vo.DeviceViewItem;
import com.baidu.unbiz.multitask.vo.QueryParam;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public class WithThreadLocalTest {

    @Resource(name = "simpleParallelExePool")
    private ParallelExePool parallelExePool;

    /**
     * 测试ThreadLoal可正常传递至执行task中
     */
    @Test
    public void testFetchWithThreadLocal() {
        QueryParam qp = new QueryParam();

        MyThreadLocal.set("msg from caller.");
        TaskContext.attachThreadLocal(MyThreadLocal.instance());

        MultiResult ctx =
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

    @Test
    public void testThreadLocalSafty() {
        QueryParam qp = new QueryParam();

        MyThreadLocal.set("msg from caller.");
        TaskContext.attachThreadLocal(MyThreadLocal.instance());

        MultiResult ctx =
                parallelExePool.submit(
                        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                        new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)));
        List<DeviceViewItem> stat = ctx.getResult("deviceStatFetcher");
        List<DeviceViewItem> uv = ctx.getResult("deviceUvFetcher");

        TaskContext.detachThreadLocal(MyThreadLocal.instance());
        for (int i = 0; i < 10; i++) {
            parallelExePool.submit(
                    new TaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                    new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)));
            stat = ctx.getResult("deviceStatFetcher");
            uv = ctx.getResult("deviceUvFetcher");
        }

        TaskContext.attachThreadLocal(MyThreadLocal.instance());
        MyThreadLocal.set("new msg from caller.");
        for (int i = 0; i < 10; i++) {
            parallelExePool.submit(
                    new TaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                    new TaskPair("deviceUvFetcher", DeviceRequest.build(qp)));
            stat = ctx.getResult("deviceStatFetcher");
            uv = ctx.getResult("deviceUvFetcher");
        }

        Assert.notEmpty(stat);
        Assert.notEmpty(uv);
    }

}
