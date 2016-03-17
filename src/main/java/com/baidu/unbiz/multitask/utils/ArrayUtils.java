package com.baidu.unbiz.multitask.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayUtils {
    /**
     * 判断数组是否为空
     * 
     * @param array
     * @return boolean
     */
    public static <T> boolean isArrayEmpty(T[] array) {
        if (null == array || array.length <= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 数据是否非空
     * 
     * @param array 数组
     * @since 2015-7-28 by wangchongjie
     * @return boolean
     */
    public static <T> boolean isArrayNotEmpty(T[] array) {
        return !isArrayEmpty(array);
    }

    /**
     * 数组转列表
     * 
     * @param array
     * @since 2015-7-28
     * @return List
     */
    public static <T> List<T> arrayToList(T[] array) {
        return isArrayEmpty(array) ? new ArrayList<T>(1) : Arrays.asList(array);
    }
}
