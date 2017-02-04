package com.baidu.unbiz.multitask.common;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 可转型基类
 * 
 * @author wangchongjie
 * @since 2015-11-21 下午7:59:21
 */
public class EnableCast {

    @SuppressWarnings("unchecked")
    public <T> T cast() {
        return (T) this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
