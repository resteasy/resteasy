/**
 * 
 */
package com.damnhandy.resteasy.core;

import javax.servlet.http.HttpServletResponse;

import com.damnhandy.resteasy.exceptions.HttpMethodInvocationException;

/**
 * The default ResourceInvoker for POJO-based services.
 * 
 * @author Ryan J. McDonough
 * @since 1.0
 * Jan 21, 2007
 *
 */
public class POJOResourceInvoker extends ResourceInvoker {

	/**
	 * 
	 * @param targetClass
	 */
	protected POJOResourceInvoker(Class<?> targetClass) {
		this.setTargetClass(targetClass);
	}
	
	/**
	 * 
	 *
	 * @see com.damnhandy.resteasy.core.ResourceInvoker#getTargetInstance()
	 */
	@Override
	public Object getTargetInstance() {
		try {
			return getTargetClass().newInstance();
		} catch (InstantiationException e) {
			 throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
		} catch (IllegalAccessException e) {
			 throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
		}
	}

}
