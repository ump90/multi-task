package com.baidu.unbiz.multitask.common;

/**
 * 转型接口
 * 
 * @author wangchongjie
 * @fileName Castable.java
 * @dateTime 2014-11-21 下午7:59:52
 */
public interface Castable {

    /**
     * 转型
     * 
     * @return
     * @since 2015-7-3 by wangchongjie
     */
    <T> T cast();
}
