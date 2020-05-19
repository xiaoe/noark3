package xyz.noark.core.thread;

import xyz.noark.core.network.NetworkListener;

/**
 * 一种普通的异步任务.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class AsyncTask implements Runnable {
    private final NetworkListener networkListener;
    private final TaskCallback taskCallback;

    public AsyncTask(NetworkListener networkListener, TaskCallback taskCallback) {
        this.networkListener = networkListener;
        this.taskCallback = taskCallback;
    }

    @Override
    public void run() {
        taskCallback.doSomething();
    }
}