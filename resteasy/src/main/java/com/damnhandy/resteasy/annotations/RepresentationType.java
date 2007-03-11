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
public @interface RepresentationType {

	/**
	 * The mime-type of this representation type
	 * @return
	 */
	public String type();
	
	/**
	 * The assocaited extention for this type
	 * @return
	 */
	public String[] ext();
	
	/**
	 * 
	 * @return
	 */
	public boolean defaultType() default false;
}
