/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LogUtils {

	/**
	 * A method for triggering the LogFactory.release method on the commons logging library (http://commons.apache.org/logging/) without having compile time
	 * dependency to the library itself. Trigger of the release method can be nessecary sometimes in order to avoid memory leaks, see
	 * http://wiki.apache.org/jakarta-commons/Logging/UndeployMemoryLeak for more information.
	 */
	public static void releaseCommonsLogging() {

		try {
			Class<?> logFactoryClass = Class.forName("org.apache.commons.logging.LogFactory");

			Method releaseMethod = logFactoryClass.getMethod("release", ClassLoader.class);

			releaseMethod.invoke(null, Thread.currentThread().getContextClassLoader());

		} catch (ClassNotFoundException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
	}
}
