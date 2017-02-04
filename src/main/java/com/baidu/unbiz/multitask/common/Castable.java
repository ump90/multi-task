package com.baidu.unbiz.multitask.common;

/**
 * 转型接口
 * 
 * @author wangchongjie
 * @since 2015-11-21 下午7:59:52
 */
public interface Castable {

    /**
     * 转型
     * 
     * @return T
     * @since 2015-7-3 by wangchongjie
     */
    <T> T cast();
}
