/*
 * Copyright © 2015 www.noark.xyz All Rights Reserved.
 * 
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package xyz.noark.reflectasm;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.WeakHashMap;

/**
 * ClassLoader。
 *
 * @since 3.0
 * @author 小流氓(176543888@qq.com)
 */
class AccessClassLoader extends ClassLoader {
	// Weak-references to class loaders, to avoid perm gen memory leaks, for
	// example in app servers/web containters if the
	// reflectasm library (including this class) is loaded outside the deployed
	// applications (WAR/EAR) using ReflectASM/Kryo (exts,
	// user classpath, etc).
	// The key is the parent class loader and the value is the
	// AccessClassLoader, both are weak-referenced in the hash table.
	static private final WeakHashMap<ClassLoader, WeakReference<AccessClassLoader>> accessClassLoaders = new WeakHashMap<>();

	// Fast-path for classes loaded in the same ClassLoader as this class.
	static private final ClassLoader selfContextParentClassLoader = getParentClassLoader(AccessClassLoader.class);
	static private volatile AccessClassLoader selfContextAccessClassLoader = new AccessClassLoader(selfContextParentClassLoader);

	static AccessClassLoader get(Class<?> type) {
		ClassLoader parent = getParentClassLoader(type);
		// 1. fast-path:
		if (selfContextParentClassLoader.equals(parent)) {
			if (selfContextAccessClassLoader == null) {
				synchronized (accessClassLoaders) { // DCL with volatile
													// semantics
					if (selfContextAccessClassLoader == null) {
						selfContextAccessClassLoader = new AccessClassLoader(selfContextParentClassLoader);
					}
				}
			}
			return selfContextAccessClassLoader;
		}
		// 2. normal search:
		synchronized (accessClassLoaders) {
			WeakReference<AccessClassLoader> ref = accessClassLoaders.get(parent);
			if (ref != null) {
				AccessClassLoader accessClassLoader = ref.get();
				if (accessClassLoader != null) {
					return accessClassLoader;
				} else {
					accessClassLoaders.remove(parent);
				}
			}
			AccessClassLoader accessClassLoader = new AccessClassLoader(parent);
			accessClassLoaders.put(parent, new WeakReference<AccessClassLoader>(accessClassLoader));
			return accessClassLoader;
		}
	}

	public static void remove(ClassLoader parent) {
		// 1. fast-path:
		if (selfContextParentClassLoader.equals(parent)) {
			selfContextAccessClassLoader = null;
		} else {
			// 2. normal search:
			synchronized (accessClassLoaders) {
				accessClassLoaders.remove(parent);
			}
		}
	}

	public static int activeAccessClassLoaders() {
		int sz = accessClassLoaders.size();
		if (selfContextAccessClassLoader != null) {
			sz++;
		}
		return sz;
	}

	private AccessClassLoader(ClassLoader parent) {
		super(parent);
	}

	protected synchronized java.lang.Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		// These classes come from the classloader that loaded
		// AccessClassLoader.
		if (name.equals(MethodAccess.class.getName())) {
			return MethodAccess.class;
		}
		// All other classes come from the classloader that loaded the type we
		// are accessing.
		return super.loadClass(name, resolve);
	}

	Class<?> defineClass(String name, byte[] bytes) throws ClassFormatError {
		try {
			// Attempt to load the access class in the same loader, which makes
			// protected and default access members accessible.
			Method method = ClassLoader.class.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, int.class, int.class, ProtectionDomain.class });
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			return (Class<?>) method.invoke(getParent(), new Object[] { name, bytes, Integer.valueOf(0), Integer.valueOf(bytes.length), getClass().getProtectionDomain() });
		} catch (Exception ignored) {}
		return defineClass(name, bytes, 0, bytes.length, getClass().getProtectionDomain());
	}

	private static ClassLoader getParentClassLoader(Class<?> type) {
		ClassLoader parent = type.getClassLoader();
		if (parent == null) {
			parent = ClassLoader.getSystemClassLoader();
		}
		return parent;
	}
}