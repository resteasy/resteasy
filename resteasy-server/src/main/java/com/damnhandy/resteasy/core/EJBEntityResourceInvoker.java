/**
 * 
 */
package com.damnhandy.resteasy.core;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.damnhandy.resteasy.ConfigurationException;
import com.damnhandy.resteasy.entity.ResourceManager;
import com.damnhandy.resteasy.exceptions.HttpMethodInvocationException;

/**
 * @author ryan
 *
 */
public class EJBEntityResourceInvoker extends ResourceInvoker {


	private Class<?> beanClass;
	private String jndiName;
	private InitialContext ctx;
	
	public EJBEntityResourceInvoker(Class<?> beanClass, 
									Class<?> localInterface, 
									String jndiName) {
		if(localInterface == null) {
			throw new ConfigurationException("The LocalInterface value was null");
		}
		this.beanClass = beanClass;
		this.jndiName = jndiName;
		this.ctx = ResourceInvokerBuilder.getInitialContext();
		super.setTargetClass(localInterface);
	}

	/**
	 * 
	 * 
	 * @see com.damnhandy.resteasy.core.ResourceInvoker#getTargetInstance()
	 */
	@Override
	public ResourceManager getTargetInstance() {
		ResourceManager target = null;
		try {
			target = (ResourceManager) ctx.lookup(jndiName);
		} catch (NamingException e) {
			
		}
		return target;
	}

	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.core.ResourceInvoker#invoke(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void invoke(HttpServletRequest request, HttpServletResponse response) 
		throws HttpMethodInvocationException {
		if(request.getMethod().equals("GET")) {
			
		}
	}
	
	

}
