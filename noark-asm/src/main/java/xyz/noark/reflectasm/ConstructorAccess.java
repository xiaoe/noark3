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

import static xyz.noark.asm.Opcodes.ACC_PUBLIC;
import static xyz.noark.asm.Opcodes.ACC_SUPER;
import static xyz.noark.asm.Opcodes.ALOAD;
import static xyz.noark.asm.Opcodes.ARETURN;
import static xyz.noark.asm.Opcodes.ATHROW;
import static xyz.noark.asm.Opcodes.CHECKCAST;
import static xyz.noark.asm.Opcodes.DUP;
import static xyz.noark.asm.Opcodes.INVOKESPECIAL;
import static xyz.noark.asm.Opcodes.INVOKEVIRTUAL;
import static xyz.noark.asm.Opcodes.NEW;
import static xyz.noark.asm.Opcodes.POP;
import static xyz.noark.asm.Opcodes.RETURN;
import static xyz.noark.asm.Opcodes.V1_8;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import xyz.noark.asm.ClassWriter;
import xyz.noark.asm.MethodVisitor;

public abstract class ConstructorAccess<T> {
	boolean isNonStaticMemberClass;

	public boolean isNonStaticMemberClass() {
		return isNonStaticMemberClass;
	}

	/**
	 * Constructor for top-level classes and static nested classes.
	 * <p>
	 * If the underlying class is a inner (non-static nested) class, a new
	 * instance will be created using <code>null</code> as the this$0 synthetic
	 * reference. The instantiated object will work as long as it actually don't
	 * use any member variable or method fron the enclosing instance.
	 */
	abstract public T newInstance();

	/**
	 * Constructor for inner classes (non-static nested classes).
	 * 
	 * @param enclosingInstance The instance of the enclosing type to which this
	 *            inner instance is related to (assigned to its synthetic this$0
	 *            field).
	 */
	abstract public T newInstance(Object enclosingInstance);

	static public <T> ConstructorAccess<T> get(Class<T> type) {
		Class<?> enclosingType = type.getEnclosingClass();
		boolean isNonStaticMemberClass = enclosingType != null && type.isMemberClass() && !Modifier.isStatic(type.getModifiers());

		String className = type.getName();
		String accessClassName = className + "ConstructorAccess";
		if (accessClassName.startsWith("java."))
			accessClassName = "reflectasm." + accessClassName;
		Class<?> accessClass = null;

		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			try {
				accessClass = loader.loadClass(accessClassName);
			} catch (ClassNotFoundException ignored) {
				String accessClassNameInternal = accessClassName.replace('.', '/');
				String classNameInternal = className.replace('.', '/');
				String enclosingClassNameInternal;

				boolean isPrivate = false;
				if (!isNonStaticMemberClass) {
					enclosingClassNameInternal = null;
					try {
						Constructor<T> constructor = type.getDeclaredConstructor((Class[]) null);
						isPrivate = Modifier.isPrivate(constructor.getModifiers());
					} catch (Exception ex) {
						throw new RuntimeException("Class cannot be created (missing no-arg constructor): " + type.getName(), ex);
					}
					if (isPrivate) {
						throw new RuntimeException("Class cannot be created (the no-arg constructor is private): " + type.getName());
					}
				} else {
					enclosingClassNameInternal = enclosingType.getName().replace('.', '/');
					try {
						Constructor<T> constructor = type.getDeclaredConstructor(enclosingType); // Inner
																									// classes
																									// should
																									// have
																									// this.
						isPrivate = Modifier.isPrivate(constructor.getModifiers());
					} catch (Exception ex) {
						throw new RuntimeException("Non-static member class cannot be created (missing enclosing class constructor): " + type.getName(), ex);
					}
					if (isPrivate) {
						throw new RuntimeException("Non-static member class cannot be created (the enclosing class constructor is private): " + type.getName());
					}
				}

				ClassWriter cw = new ClassWriter(0);
				cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null, "xyz/noark/reflectasm/ConstructorAccess", null);

				insertConstructor(cw);
				insertNewInstance(cw, classNameInternal);
				insertNewInstanceInner(cw, classNameInternal, enclosingClassNameInternal);

				cw.visitEnd();
				accessClass = loader.defineClass(accessClassName, cw.toByteArray());
			}
		}
		try {
			@SuppressWarnings("unchecked")
			ConstructorAccess<T> access = (ConstructorAccess<T>) accessClass.newInstance();
			access.isNonStaticMemberClass = isNonStaticMemberClass;
			return access;
		} catch (Exception ex) {
			throw new RuntimeException("Error constructing constructor access class: " + accessClassName, ex);
		}
	}

	@SuppressWarnings("deprecation")
	static private void insertConstructor(ClassWriter cw) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "xyz/noark/reflectasm/ConstructorAccess", "<init>", "()V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	@SuppressWarnings("deprecation")
	static void insertNewInstance(ClassWriter cw, String classNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "()Ljava/lang/Object;", null, null);
		mv.visitCode();
		mv.visitTypeInsn(NEW, classNameInternal);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "()V");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
	}

	@SuppressWarnings("deprecation")
	static void insertNewInstanceInner(ClassWriter cw, String classNameInternal, String enclosingClassNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitCode();
		if (enclosingClassNameInternal != null) {
			mv.visitTypeInsn(NEW, classNameInternal);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, enclosingClassNameInternal);
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "(L" + enclosingClassNameInternal + ";)V");
			mv.visitInsn(ARETURN);
			mv.visitMaxs(4, 2);
		} else {
			mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
			mv.visitInsn(DUP);
			mv.visitLdcInsn("Not an inner class.");
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V");
			mv.visitInsn(ATHROW);
			mv.visitMaxs(3, 2);
		}
		mv.visitEnd();
	}
}