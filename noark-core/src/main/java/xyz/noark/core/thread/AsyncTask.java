package xyz.noark.core.thread;

/**
 * 一种普通的异步任务.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class AsyncTask implements Runnable {
    private final TaskCallback taskCallback;

    public AsyncTask(TaskCallback taskCallback) {
        this.taskCallback = taskCallback;
    }

    @Override
    public void run() {
        taskCallback.doSomething();
    }
}