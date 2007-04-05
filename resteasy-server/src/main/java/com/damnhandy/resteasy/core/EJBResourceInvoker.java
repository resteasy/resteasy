/**
 * 
 */
package com.damnhandy.resteasy.core;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.damnhandy.resteasy.ConfigurationException;
import com.damnhandy.resteasy.exceptions.HttpMethodInvocationException;

/**
 * @author Ryan J. McDonough
 * Jan 16, 2007
 *
 */
public class EJBResourceInvoker extends ResourceInvoker {
	private static final Logger logger = Logger.getLogger(EJBResourceInvoker.class);
	private Class<?> beanClass;
	private String jndiName;
	private InitialContext ctx;

	
	/**
	 * @param beanClass
	 * @param localInterface
	 * @param jndiName
	 * @param ctx
	 */
	public EJBResourceInvoker(Class<?> beanClass, Class<?> localInterface, String jndiName) {
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
	public Object getTargetInstance() {
		Object target = null;
		try {
			target = ctx.lookup(jndiName);
		} catch (NamingException e) {
			logger.error("NamingException while getting EJB ("+jndiName+") to invoke: "+e.getMessage(), e);
			throw new HttpMethodInvocationException("NamingException while getting EJB ("+jndiName+") to invoke: "+e.getMessage(),HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
		}
		return target;
	}



	/**
	 * @return the beanClass
	 */
	public Class<?> getBeanClass() {
		return beanClass;
	}



	/**
	 * @param beanClass the beanClass to set
	 */
	protected void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}



	/**
	 * @return the jndiName
	 */
	public String getJndiName() {
		return jndiName;
	}



	/**
	 * @param jndiName the jndiName to set
	 */
	protected void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}
}
