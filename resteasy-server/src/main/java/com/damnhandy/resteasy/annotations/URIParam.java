package com.damnhandy.resteasy.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>
 * Indicates this method parameter values or field valie that can be found 
 * in the WebResources path. By default, the application will attempt to
 * find a path param value which matches the method method parameter name. 
 * For example, if the WebResource path value is:
 * </p>
 * <pre>/contacts/{contactId}</pre>
 * <p>
 * The Java method's parameters shoudl be annotated as
 * </p>
 * <pre>public Contact getContactById(@URIParam("contactId") Long id);</pre>
 * <p>
 * The <code>{contactId}</code> value in the URI will be mapped to to the id parameter. 
 * </p>
 * 
 * @author Ryan J. McDonough
 * @since 1.0
 * Nov 6, 2006
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})
public @interface URIParam {
	
	/**
	 * 
	 * @return
	 */
	public String value();
	
	
}
