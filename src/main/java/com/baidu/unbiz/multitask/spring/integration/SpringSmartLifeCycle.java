package com.baidu.unbiz.multitask.spring.integration;

import org.springframework.context.SmartLifecycle;

/**
 * Spring生命周期控制
 * 
 * @author wangchongjie
 * @since 2015-7-3 下午3:57:03
 */
public abstract class SpringSmartLifeCycle implements SmartLifecycle {

    protected boolean running = false;

    @Override
    public void start() {
        running = true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return -1;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void stop(Runnable callback) {
        if (callback != null) {
            callback.run();
        }
        running = false;
    }
}
