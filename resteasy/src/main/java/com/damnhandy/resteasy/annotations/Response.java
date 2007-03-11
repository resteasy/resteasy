/**
 * 
 */
package com.damnhandy.resteasy.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.damnhandy.resteasy.ResponseCodes;

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
	
	/**
	 * Returns the correct HTTP response code. The default value is 200 (OK),
	 * which should be appriate for most cases. This value should be overriden
	 * in situations where you are creating resource. 
	 * @return
	 */
	public int responseCode() default ResponseCodes.OK;
	
	/**
	 * 
	 * @return
	 */
	public int failureResponseCode() default ResponseCodes.OK;
}
