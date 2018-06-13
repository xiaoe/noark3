package xyz.noark.core.actor;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class TestTypeSafe {
	
	public static void main(String[] args) {
		
//		while(true) {
		
		final A a = new A();
		final B b = new B();
		final C c = new C();
				
		final int TEST_LOOP = 10;
		
		final CountDownLatch ct = new CountDownLatch(1);
		
		for (int j = 0; j < 100;j++) {
			
			final int l = j;
			new Thread() {
				
				@Override
				public void run() {
					
					try {
						ct.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					try {
						Thread.sleep(new Random().nextInt(10));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if( l % 2 == 0) {
					
						for (int k = 0;k < TEST_LOOP;k++) {
							
							new SafeActor(a) {
								
								@Override
								public void run() {
									int i = a.i;
									i += 1;
									a.i = i;
									
									try {
										Thread.sleep(3);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									
									int j = b.j;
									j += 1;
									b.j = j;
									
									c.k++;
								}
								
							}.start();
						}
						
					} else {
						
						for (int k = 0;k < TEST_LOOP;k++) {
							
							new SafeActor(a) {
								
								@Override
								public void run() {
									int i = a.i;
									i += 1;
									a.i = i;
									
									try {
										Thread.sleep(1);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									
									c.k++;
									int j = b.j;
									j += 1;
									b.j = j;
								}
								
							}.start();
						}
						
					}
				}
			}.start();
		}
		
		try {
			ct.countDown();
			Thread.sleep(5000);
			System.out.println("a.i:" + a.i);
			System.out.println("b.j:" + b.j);
			System.out.println("c.k:" + c.k);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		}
		
	}

	static class A extends SafeType {
		
		int i = 0;

		@Override
		public String toString() {
			return "A [i=" + i + "]";
		}
		
	}
	
	static class B extends SafeType {
		
		int j = 0;

		@Override
		public String toString() {
			return "B [j=" + j + "]";
		}
		
	}
	
	static class C extends SafeType {
		
		int k = 0;

		@Override
		public String toString() {
			return "C [k=" + k + "]";
		}
		
	}
	
}
