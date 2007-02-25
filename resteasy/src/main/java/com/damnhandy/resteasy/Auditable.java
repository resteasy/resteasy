/**
 * 
 */
package com.damnhandy.resteasy;

import java.util.Date;

/**
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
