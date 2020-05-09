package xyz.noark.core.thread;

/**
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
public class AsyncThreadCommand implements ThreadCommand {
    private final AsyncCallback callback;

    public AsyncThreadCommand(AsyncCallback callback) {
        this.callback = callback;
    }

    @Override
    public Object exec() {
        callback.doSomething();
        return null;
    }

    @Override
    public String code() {
        return "async";
    }

    @Override
    public boolean isPrintLog() {
        return false;
    }
}
