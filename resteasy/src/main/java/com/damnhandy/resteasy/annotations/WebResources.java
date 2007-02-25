/**
 * 
 */
package com.damnhandy.resteasy.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that is used to group several WebResource URIs 
 * into a single class.
 * 
 * @author Ryan J. McDonough
 * Jan 29, 2007
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WebResources {

	/**
	 * 
	 * @return
	 */
	public WebResource[] resources();
}
