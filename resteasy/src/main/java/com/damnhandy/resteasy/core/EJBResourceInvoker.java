/**
 * 
 */
package com.damnhandy.resteasy.core;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.damnhandy.resteasy.ConfigurationException;
import com.damnhandy.resteasy.HttpMethodInvocationException;
import com.damnhandy.resteasy.annotations.HttpMethod;
import com.damnhandy.resteasy.annotations.Response;

/**
 * @author Ryan J. McDonough
 * Jan 16, 2007
 *
 */
public class EJBResourceInvoker extends ResourceInvoker {
	
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
	 * @see com.damnhandy.resteasy.core.ResourceInvoker#invoke(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 *
	@Override
	public void invoke(HttpServletRequest request, HttpServletResponse response) throws HttpMethodInvocationException {
		UserTransaction ut = null;
		String discriminator = request.getParameter(HttpMethod.DISCRIMINATOR_KEY);
    	MethodKey key = new MethodKey(request.getMethod(),discriminator);
        MethodMapping mapping = getMethods().get(key);
		try {
			
			Response responseConfig = mapping.getMethod().getAnnotation(Response.class);
			if(responseConfig != null && responseConfig.extendTxn()) {
				ut = (UserTransaction) ctx.lookup("UserTransaction");	
				ut.begin();
			}
			super.invoke(request, response);
			
		} catch (SecurityException e) {
			 throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
		} catch (IllegalStateException e) {
			 throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
		} catch (NamingException e) {
			 throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
		} catch (NotSupportedException e) {
			 throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
		} catch (SystemException e) {
			 throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
		} finally {
			if(ut != null) {
				try {
					ut.commit();
				}  catch (IllegalStateException e) {
					 throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
				} catch (RollbackException e) {
					 throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
				} catch (HeuristicMixedException e) {
					 throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
				} catch (HeuristicRollbackException e) {
					 throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
				} catch (SystemException e) {
					 throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
				}
			}
		}
		
	}*/

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
