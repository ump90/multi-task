package com.baidu.unbiz.multitask.demo.test;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.service.MyThreadLocal;
import com.baidu.unbiz.multitask.task.ParallelExePool;
import com.baidu.unbiz.multitask.task.thread.MultiResult;
import com.baidu.unbiz.multitask.task.thread.TaskContext;
import com.baidu.unbiz.multitask.vo.DeviceRequest;
import com.baidu.unbiz.multitask.vo.DeviceViewItem;
import com.baidu.unbiz.multitask.vo.QueryParam;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext-test.xml")
public class ExplicitRegisterTaskBeanTest {

    @Resource(name = "simpleParallelExePool")
    private ParallelExePool parallelExePool;

    /**
     * 测试显示注册TaskBean
     */
    @Test
    public void testRegisterTaskBean() {
        QueryParam qp = new QueryParam();

        MyThreadLocal.set("msg from caller.");
        TaskContext.attachThreadLocal(MyThreadLocal.instance());

        MultiResult ctx =
                parallelExePool.submit(
                        new TaskPair("deviceStatFetcher", DeviceRequest.build(qp)),
                        new TaskPair("siteFetcher", DeviceRequest.build(qp)));

        List<DeviceViewItem> stat = ctx.getResult("deviceStatFetcher");
        List<DeviceViewItem> uv = ctx.getResult("siteFetcher");

        Assert.notEmpty(stat);
        Assert.notEmpty(uv);
        System.out.println(stat);
        System.out.println(uv);
    }

}
