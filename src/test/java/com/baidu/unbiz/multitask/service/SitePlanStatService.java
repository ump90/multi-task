package com.baidu.unbiz.multitask.service;

import com.baidu.unbiz.multitask.vo.DeviceRequest;
import com.baidu.unbiz.multitask.vo.DeviceViewItem;
import java.util.List;

/**
 * Created by wangchongjie on 16/4/29.
 */
public interface SitePlanStatService {

    List<DeviceViewItem> queryPlanSiteData(DeviceRequest req);
}
