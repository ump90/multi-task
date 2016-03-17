package com.baidu.unbiz.multitask.vo;

/**
 * 分设备维度模型
 *
 * @author wangchongjie
 * @fileName DeviceViewItem.java
 * @since 2015-7-3 上午10:52:25
 */
public class DeviceViewItem {

    private static final long serialVersionUID = 6132709579470894604L;

    private int planId;

    private String planName;

    private Integer deviceId;

    /** 无线出价比例 */
    private double bidRatio;

    /** 计划属性：1-所有功能，2-仅无线 */
    private Integer promotionType;

    // getter and setter
    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public double getBidRatio() {
        return bidRatio;
    }

    public void setBidRatio(double bidRatio) {
        this.bidRatio = bidRatio;
    }

    public Integer getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
    }

}
