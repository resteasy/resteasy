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
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface RepresentationOut {

	/**
	 * Specifies the media type of the response. By default, application/xml
	 * is used.
	 * @return
	 */
	public String value() default "application/xml";
	
	/**
	 * An optional file extention that can be used to demand the
	 * media media type, bypassing content negotiation. By default,
	 * no value is specified.
	 * @return
	 */
	public String ext() default "";
	
	/**
	 * Defines the quality of source for this media type. The default 
	 * value is 0.8
	 * @return
	 */
	public float qs() default 0.8f;
	
	
}
