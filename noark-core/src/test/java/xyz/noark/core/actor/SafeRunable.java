package xyz.noark.core.actor;

/**
 * 线程安全的SafeRunable
 * 
 * @author Jake
 */
public class SafeRunable implements Runnable {

	/** 后继节点 */
	private volatile SafeRunable next;

	/** 当前对象 */
	private final SafeType safeType;

	/** 当前任务 */
	private final SafeActor safeActor;

	protected SafeRunable(SafeType safeType, SafeActor safeActor) {
		this.safeType = safeType;
		this.safeActor = safeActor;
	}

	@Override
	public void run() {
		SafeRunable next = this;
		do {
			try {
				next.safeActor.run();
			} catch (Exception e) {
				next.safeActor.onException(e);
			}
		} while ((next = next.fetchNext()) != null);// 获取下一个任务
	}

	/**
	 * 执行
	 */
	public void execute() {

		// CAS loop
		for (SafeRunable tail = safeType.getTail();;) {

			// messages from the same client are handled orderly
			if (tail == null) { // No previous job
				if (safeType.casTail(null, this)) {
					this.run();
					return;
				}
				tail = safeType.getTail();
			} else if (tail.isHead() && safeType.casTail(tail, this)) {
				// previous message is handled, order is guaranteed.
				this.run();
				return;
			} else if (tail.casNext(this)) {
				safeType.casTail(tail, this);// fail is OK
				// successfully append to previous task
				return;
			} else {
				tail = safeType.getTail();
			}
		}

	}

	/**
	 * 获取下一个任务
	 */
	protected SafeRunable fetchNext() {
		if (!UNSAFE.compareAndSwapObject(this, nextOffset, null, this)) { // has
																			// more
																			// job
																			// to
																			// run
			return next;
		}
		return null;
	}

	boolean casNext(SafeRunable safeRunable) {
		return UNSAFE.compareAndSwapObject(this, nextOffset, null, safeRunable);
	}

	/**
	 * 判断节点是否为头节点
	 * 
	 * @return
	 */
	public boolean isHead() {
		return this.next == this;
	}

	public SafeType getSafeType() {
		return safeType;
	}

	// Unsafe mechanics
	private static final sun.misc.Unsafe UNSAFE;
	private static final long nextOffset;
	static {
		try {
			UNSAFE = getUnsafe();
			Class<?> sk = SafeRunable.class;
			nextOffset = UNSAFE.objectFieldOffset(sk.getDeclaredField("next"));
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	/**
	 * Returns a sun.misc.Unsafe. Suitable for use in a 3rd party package.
	 * Replace with a simple call to Unsafe.getUnsafe when integrating into a
	 * jdk.
	 *
	 * @return a sun.misc.Unsafe
	 */
	private static sun.misc.Unsafe getUnsafe() {
		try {
			return sun.misc.Unsafe.getUnsafe();
		} catch (SecurityException se) {
			try {
				return java.security.AccessController.doPrivileged(new java.security.PrivilegedExceptionAction<sun.misc.Unsafe>() {
					public sun.misc.Unsafe run() throws Exception {
						java.lang.reflect.Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
						f.setAccessible(true);
						return (sun.misc.Unsafe) f.get(null);
					}
				});
			} catch (java.security.PrivilegedActionException e) {
				throw new RuntimeException("Could not initialize intrinsics", e.getCause());
			}
		}
	}

	@Override
	public String toString() {
		return "SafeRunable [next=" + next + ", safeType=" + safeType + ", safeActor=" + safeActor + "]";
	}

}
