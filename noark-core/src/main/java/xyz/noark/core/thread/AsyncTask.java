package xyz.noark.core.thread;

import java.io.Serializable;

/**
 * 一种普通的异步任务.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class AsyncTask extends AbstractAsyncTask implements Runnable {
    private final TaskCallback taskCallback;

    public AsyncTask(TaskCallback taskCallback, Serializable playerId) {
        super(null, playerId);
        this.taskCallback = taskCallback;
    }

    @Override
    protected void doSomething() {
        taskCallback.doSomething();
    }

    @Override
    protected String logCode() {
        return "async task";
    }

    @Override
    protected boolean isPrintLog() {
        return true;
    }
}