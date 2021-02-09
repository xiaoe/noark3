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
    private final boolean printLog;

    public AsyncTask(TaskCallback taskCallback, Serializable playerId, boolean printLog) {
        super(null, playerId);
        this.taskCallback = taskCallback;
        this.printLog = printLog;
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
        return printLog;
    }
}