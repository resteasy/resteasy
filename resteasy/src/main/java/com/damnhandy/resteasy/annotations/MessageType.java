package com.damnhandy.resteasy.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * TODO: Implement this usage in the ResourceInvoker
 * @author Ryan J. McDonough
 * Feb 23, 2007
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MessageType {

	/**
	 * 
	 * @return
	 */
	public MessageTypes value() default MessageTypes.TEXT;
}
