package xyz.noark.core.actor;

/**
 * 线程安全的Actor
 * <br/>支持单个对象操作的顺序执行,可并发提交。与提交线程的关系是同步或异步
 * <br/>序列执行子类型
 * <br/>需要覆盖run方法, 方法内可以调用super.afterExecute(Object[])执行结束后会进行回调
 * @author Jake
 */
public abstract class SafeActor implements Runnable {
	
	private final SafeRunable safeRunable;
	
	public SafeActor(SafeType safeType) {
		if (safeType == null) {
			throw new IllegalArgumentException("safeType");
		}
		this.safeRunable = new SafeRunable(safeType, this);
	}
	
	/**
     * 执行异常回调()
     * @param t Throwable
     */
    public void onException(Throwable t) {
    	t.printStackTrace();
    }
	
    /**
     * 开始执行Actor
     */
    public void start() {
    	// 执行SafeRunable序列
    	safeRunable.execute();
    }

	@Override
	public String toString() {
		return "SafeActor [" + this.hashCode() + "]";
	}
    
	
}
