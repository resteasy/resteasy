/**
 * 
 */
package com.damnhandy.resteasy.helper;

import javax.ejb.MessageDriven;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

import com.damnhandy.resteasy.annotations.WebResource;
import com.damnhandy.resteasy.annotations.WebResources;

/**
 * @author Ryan J. McDonough
 * Jan 16, 2007
 *
 */
public class ClassUtils {

	/**
	 * 
	 * @param targetClass
	 * @return
	 */
	public static boolean isEJB(Class<?> targetClass) {
		if(targetClass.isAnnotationPresent(Stateless.class) ||
		   targetClass.isAnnotationPresent(Stateful.class)) {
			return true;
		}    
		return false;
	}
	
	/**
	 * 
	 * @param targetClass
	 * @return
	 */
	public static boolean isMDB(Class<?> targetClass) {
		return targetClass.isAnnotationPresent(MessageDriven.class);
	}
	
	/**
	 * 
	 * @param targetClass
	 * @return
	 */
	public static boolean isHttpResource(Class<?> targetClass) {
		return (targetClass.isAnnotationPresent(WebResource.class) || 
				targetClass.isAnnotationPresent(WebResources.class));
	}
	
	
}
