/**
 * 
 */
package com.damnhandy.resteasy.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author ryan
 *
 */
public class TypeConverter {

	/**
	 * 
	 * @param <T>
	 * @param source
	 * @param targetType
	 * @return
	 */
	public static <T> T getType(String source, Class<T> targetType) {
		T result;
		try {
			result = getTypeViaValueOfMethod(source,targetType);
		} catch (NoSuchMethodException e) {
			result = getTypeViaStringConstructor(source,targetType);
		}
		return result;
	}
	
	/**
	 * 
	 * @param <T>
	 * @param source
	 * @param targetType
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static <T> T getTypeViaValueOfMethod(String source, Class<T> targetType) throws NoSuchMethodException {
		T result = null;
		try {
			Method valueOf = targetType.getDeclaredMethod("valueOf",new Class[] { String.class });
			Object value = valueOf.invoke(null, source);
			if(targetType.isInstance(value)) {
				result = targetType.cast(value);
			}
		}  
		catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 
	 * @param <T>
	 * @param source
	 * @param targetType
	 * @return
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static <T> T getTypeViaStringConstructor(String source,Class<T> targetType) {
		T result = null;
		Constructor<T> c = null;
		try {
			c = targetType.getDeclaredConstructor(new Class[] { String.class });
		} catch (Exception ex) {
			throw new IllegalArgumentException((new StringBuilder()).append(targetType.getName()).append(" has no String constructor").toString(), ex);
		}
		try {
			result = c.newInstance(new Object[] { source });
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
