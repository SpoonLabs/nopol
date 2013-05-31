/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.jsemfix.conditional;

import java.lang.reflect.Constructor;
import java.util.concurrent.ThreadFactory;

/**
 * @author Favio D. DeMarco
 *
 */
final class ProvidedClassLoaderThreadFactory implements ThreadFactory {

	private final ClassLoader classLoader;

	private final Constructor<Thread> constructor;

	/**
	 * @param classLoader
	 */
	@SuppressWarnings("unchecked")
	ProvidedClassLoaderThreadFactory(final ClassLoader classLoader) {
		this.classLoader = classLoader;
		try {
			this.constructor = (Constructor<Thread>) classLoader.loadClass(Thread.class.getName()).getConstructor(
					Runnable.class);
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 */
	@Override
	public Thread newThread(final Runnable r) {
		Thread newThread;
		try {
			newThread = this.constructor.newInstance(r);
		} catch (ReflectiveOperationException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		newThread.setContextClassLoader(this.classLoader);
		return newThread;
	}
}
