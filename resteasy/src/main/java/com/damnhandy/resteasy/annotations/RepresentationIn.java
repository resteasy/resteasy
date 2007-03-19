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
 * Nov 6, 2006
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RepresentationIn {
    
	/**
	 * 
	 * @return
	 */
    public String value();

    /**
     * Specified the media type of the representation. By default,
     * the value is <code>application/xml</code>. If you need a 
     * custom media type, you will specify it here. 
     * 
     * @return the media type for the representation
     */
    public String mediaType() default "application/xml";
    
    /**
     * Used by MessageDriven WebResources to declare the type of the Object
     * in the ObjectMessage. This property is not utilized for any other type.
     * 
     * @return
     */
    public Class<?> type() default RepresentationIn.class;
}
