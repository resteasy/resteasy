package com.damnhandy.resteasy.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>
 * Represents a Query parameter in the URI. A resource to search for a contact
 * entity might be:
 * </p>
 * <pre>/contacts?firstName=ryan&lastName=mc</pre>
 * <p>
 * The Java methods parameters would then be annotated as:
 * </p>
 * <pre>
 * &#64;HttpMethod(GET)
 * public ContactList findContactsByName( &#64;QueryParam("firstName") String firstName, 
 *                                       &#64;QueryParam("lastName") String lastName) {
 *
 * </pre>
 * 
 * 
 * 
 * @author Ryan J. McDonough
 * @since 1.0
 * Nov 6, 2006
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface QueryParam {
	
	/**
	 * The parameter name
	 * @return
	 */
	public String value();
	
	/**
	 * Returns true if this parameter is required for execution
	 * @return
	 */
	public boolean required() default false;
	
	/**
	 * If no value is found for this parameter, the 
	 * default value will be used.
	 * @return
	 */
	public String defaultValue() default "";
}
