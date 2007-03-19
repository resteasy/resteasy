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
 * @author Ryan J. McDonough
 * Mar 9, 2007
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MediaTypeVarient {

	/**
	 * The mime-type of this representation type
	 * @return
	 */
	public String contentType();
	
	/**
	 * The assocaited extention for this type
	 * @return
	 */
	public String extention() default "";
	
	/**
	 * 
	 * @return
	 */
	public float qsValue() default 1.0f;
}
