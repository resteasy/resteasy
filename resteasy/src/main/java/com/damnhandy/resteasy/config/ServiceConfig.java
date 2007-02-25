/**
 * 
 */
package com.damnhandy.resteasy.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ryan J. McDonough
 * Feb 13, 2007
 *
 */
public class ServiceConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3492491648306069160L;

	private Map<Class<?>,String> typePatterns = new HashMap<Class<?>,String>();
	
	

}
