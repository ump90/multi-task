package com.baidu.unbiz.multitask.service;

import com.baidu.unbiz.multitask.spring.integration.TaskBeanContainer;
import com.baidu.unbiz.multitask.vo.DeviceRequest;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/**
 * Created by wangchongjie on 16/4/29.
 */
@Service
@DependsOn("simpleParallelExePool")
public class ParallelExePoolSupport {

    @Resource
    private SitePlanStatService siteService;

    @PostConstruct
    public void init() {
        TaskBeanContainer.registerTaskBean(siteService, "siteFetcher", "queryPlanSiteData", DeviceRequest.class);
    }
}
