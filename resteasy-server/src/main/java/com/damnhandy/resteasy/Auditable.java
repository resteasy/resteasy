/**
 * 
 */
package com.damnhandy.resteasy;

import java.util.Date;

/**
 * A helper interface that can be used by Entity beans or JAXB objects to
 * assist RESTEasy in providing a Last-Modified header value.
 * 
 * @author Ryan J. McDonough
 * Feb 22, 2007
 *
 */
public interface Auditable {

	/**
	 * 
	 * @return
	 */
	public Date getLastModified();
	
	/**
	 * 
	 * @return
	 */
	public Date getCreated();
}
