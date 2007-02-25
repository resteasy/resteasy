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
 * 
 * @author Ryan J. McDonough
 * Jan 23, 2007
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Response {

	/**
	 * Specifies the media type of the response. By default, application/xml
	 * is used.
	 * @return
	 */
	public String mediaType() default "application/xml";
	
	/**
	 * 
	 * TODO: find a batter
	 * @return
	 */
	public boolean extendTxn() default false;
}
