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
 * Java methods marked with this annotation within a class marked with 
 * a {@link WebResource} annotation will respond to this HTTP method.
 * 
 * @author Ryan J. McDonough
 * Nov 6, 2006
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HttpMethod {
	/**
	 * The value of the RESTEasy discrimantor value
	 */
	public static final String DISCRIMINATOR_KEY = "ACTION";
	/*
	 * 
	 */
	public static final String GET    = "GET";
	public static final String POST   = "POST";
	public static final String PUT    = "PUT";
	public static final String DELETE = "DELETE";
	
	/**
	 * Indicates the HTTP method used
	 * @return
	 */
	public String value();
	
	/**
	 * An identifier for the method, required for globally defined methods, 
	 * not allowed on locally embedded methods. Methods are identified by 
	 * an XML ID and are referred to using a URI reference. 
	 * (optional)
	 * @return
	 */
	public String id() default "";
	
	/**
	 * Specifies the WebResource that this method is bound to
	 * (optional)
	 * @return
	 */
	public String resourceId() default WebResource.DEFAULT_ID;
	
	/**
	 * (optional)
	 * @return
	 */
	public String discriminator() default "";
}
