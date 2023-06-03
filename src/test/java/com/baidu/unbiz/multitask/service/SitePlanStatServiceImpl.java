package com.baidu.unbiz.multitask.service;

import com.baidu.unbiz.multitask.vo.DeviceRequest;
import com.baidu.unbiz.multitask.vo.DeviceViewItem;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Created by wangchongjie on 16/4/29.
 */
@Service
public class SitePlanStatServiceImpl implements SitePlanStatService {

    public List<DeviceViewItem> queryPlanSiteData(DeviceRequest req) {
        return this.mockList1();
    }

    private List<DeviceViewItem> mockList1() {
        List<DeviceViewItem> list = new ArrayList<DeviceViewItem>();
        list.add(new DeviceViewItem());
        list.add(new DeviceViewItem());
        return list;
    }
}
