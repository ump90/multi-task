package com.baidu.unbiz.multitask.forkjoin;

import java.util.List;

/**
 * Created by wangchongjie on 15/12/21.
 */
public interface ForkJoin<PARAM, RESULT> {

    List<PARAM> fork(PARAM param);
    RESULT join(List<RESULT> results);
}
