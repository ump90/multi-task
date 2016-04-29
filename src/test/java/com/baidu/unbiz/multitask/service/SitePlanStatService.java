package com.baidu.unbiz.multitask.service;

import java.util.List;

import com.baidu.unbiz.multitask.vo.DeviceRequest;
import com.baidu.unbiz.multitask.vo.DeviceViewItem;

/**
 * Created by wangchongjie on 16/4/29.
 */
public interface SitePlanStatService {

    List<DeviceViewItem> queryPlanSiteData(DeviceRequest req);
}
