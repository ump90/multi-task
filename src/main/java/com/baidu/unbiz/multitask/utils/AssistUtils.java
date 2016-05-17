package com.baidu.unbiz.multitask.utils;

import com.baidu.unbiz.multitask.constants.TaskConfig;

/**
 * Created by wangchongjie on 15/12/21.
 */
public class AssistUtils {

    /**
     * 去除taskBean中的版本标识
     *
     * @param taskBean
     * @return
     */
    public static String removeTaskVersion(String taskBean) {
        return taskBean.replaceAll(TaskConfig.TASKNAME_SEPARATOR + ".*", "");
    }
}
